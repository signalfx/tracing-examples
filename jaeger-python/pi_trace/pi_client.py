#!/usr/bin/env python
from argparse import ArgumentParser
from queue import Queue
from time import sleep
import logging

from concurrent.futures import ThreadPoolExecutor
from opentracing.ext import tags
from jaeger_client import Config
from requests import post
import opentracing

from propagation import context_headers


logging.basicConfig(format='%(asctime)s %(levelname)s %(message)s', level='DEBUG')


def approximate_pi(partials, partial_terms, batch_size, url):
    """An instrumented Pi approximation client that propagates its span context to
    partial sum workers via B3.
    """
    # opentracing.tracer is a previously initialized tracer or a noop tracer if not globally initialized.
    # Noop tracer provides noop tracer and span implementations for all high-level interfaces.
    with opentracing.tracer.start_span('pi_approximation') as root_span:
        # Set function-level tags for debugging and trace context
        root_span.set_tag('pi_approximation.total_terms', partials * partial_terms)

        # Nesting span context managers and using the child_of references allows
        # parent spans to persist through their children's duration
        with opentracing.tracer.start_span('pi_map', child_of=root_span) as map_span:
            # Set sequential phase level tags for child context
            map_span.set_tag('pi_map.partials', partials)
            map_span.set_tag('pi_map.partial_terms', partial_terms)
            map_span.set_tag('pi_map.batch_size', batch_size)

            queue = Queue()

            # Log events for annotations in SignalFx
            map_span.log_kv(dict(event='Scheduling requests.'))

            with ThreadPoolExecutor(batch_size) as exe:
                start = 0
                for _ in range(partials):
                    # Pass map_span w/ executor submission as opentracing_instrumentation
                    # utils use threading.local(), so get_current_span() will return None
                    # when invoked on separate thread.
                    exe.submit(request_partial, start, start + partial_terms, queue, url, map_span)
                    start += partial_terms

            map_span.log_kv(dict(event='Received all responses.'))

        # Simple child span without additional tags or logs.
        with opentracing.tracer.start_span('pi_reduce', child_of=root_span):
            approx = 0
            for _ in range(partials):
                approx += queue.get()

        root_span.set_tag('pi_approximation.pi', approx)
        logging.info('Pi approximation: {}'.format(approx))


def request_partial(start, end, queue, url, parent_span=None):
    tracer = opentracing.tracer

    # Set informative span tags from opentracing semantic conventions
    # https://github.com/opentracing/specification/blob/master/semantic_conventions.md
    # https://github.com/opentracing/opentracing-python/blob/master/opentracing/ext/tags.py
    span_tags = {tags.SPAN_KIND: tags.SPAN_KIND_RPC_CLIENT,
                 tags.HTTP_URL: url,
                 tags.HTTP_METHOD: 'POST'}
    with tracer.start_span('request_partial', child_of=parent_span, tags=span_tags) as span:
        span.set_tag('request_partial.start', start)
        span.set_tag('request_partial.end', end)

        # Propagate span context to remote service
        headers = context_headers(tracer, span)

        span.log_kv(dict(event='request_partial beginning request'))
        r = post(url, json=dict(term_start=start, term_end=end), headers=headers)
        span.log_kv(dict(event='request_partial completed request'))

        span.set_tag(tags.HTTP_STATUS_CODE, r.status_code)
        if r.status_code != 200:
            span.set_tag(tags.ERROR, True)
            # Log unexpected conditions and state information for debugging
            span.log_kv({'event': 'error', 'event.object': r.content.decode()})
            queue.put(0)
            return

        queue.put(float(r.content))


if __name__ == '__main__':
    ap = ArgumentParser()
    ap.add_argument('-a', '--access-token', dest='access_token', type=str, default='',
                    help='Your SignalFx access token.')
    ap.add_argument('-i', '--ingest', dest='ingest', type=str, default='https://ingest.signalfx.com/v1/trace',
                    help='SignalFx ingest url (default https://ingest.signalfx.com/v1/trace)')
    ap.add_argument('-p', '--partials', dest='partials', type=int, default=20,
                    help='Number of partial requests to make (default 20).')
    ap.add_argument('-t', '--partial-terms', dest='partial_terms', type=int, default=500000,
                    help='Number of terms for each partial request (default 500000).')
    ap.add_argument('-b', '--batch-size', dest='batch_size', type=int, default=5,
                    help='Maximum number of requests to make at given moment (default 5).')
    ap.add_argument('-P', '--port', dest='port', type=int, default=9090,
                    help='Port for running PiServer (default 9090)')
    args = ap.parse_args()

    config = Config(
        config={
            'sampler': {
                'type': 'const',  # Report all spans.  Not recommended for production environments
                'param': 1,
            },
            'propagation': 'b3',
            'jaeger_endpoint': args.ingest,
            'jaeger_format_params': '',
            'jaeger_user': 'auth',
            'jaeger_password': args.access_token,
            'logging': True,
        },
        service_name='PiClient',
        validate=True,
    )

    # this call also sets opentracing.tracer
    tracer = config.initialize_tracer()

    url = 'http://127.0.0.1:{}'.format(args.port)
    approximate_pi(args.partials, args.partial_terms, args.batch_size, url)

    # Needed to allow jaeger_client's reporter to queue any remaining spans
    sleep(2)  # https://github.com/jaegertracing/jaeger-client-python/issues/50
    tracer.close()  # Flush any queued spans and teardown
