#!/usr/bin/env python
from argparse import ArgumentParser
import traceback
import logging
import json

from opentracing_instrumentation import span_in_context
from six.moves import BaseHTTPServer as http_server
from six.moves import socketserver
from jaeger_client import Config
from opentracing.ext import tags
import opentracing

from propagation import propagated_context
from leibniz import calculate_partial


logging.basicConfig(format='%(asctime)s %(levelname)s %(message)s', level='DEBUG')


class PiHandler(http_server.BaseHTTPRequestHandler):
    """An instrumented request handler that calculates partial pi approximations
    based on the "term_start" and "term_end" json field values of the POST request body
    """
    def do_POST(self):
        # Get previously initialized tracer or a noop tracer if not globally initialized.
        # Noop tracer provides noop tracer and span implementations for all high-level interfaces.
        tracer = opentracing.tracer

        headers = dict(self.headers.items())

        # Retrieve span context from RPC
        span_ctx = propagated_context(tracer, headers)

        # Set informative span tags from opentracing semantic conventions
        # https://github.com/opentracing/specification/blob/master/semantic_conventions.md
        # https://github.com/opentracing/opentracing-python/blob/master/opentracing/ext/tags.py
        span_tags = {tags.SPAN_KIND: tags.SPAN_KIND_RPC_SERVER,
                     tags.HTTP_URL: headers.get('Host', headers.get('host')) + self.path,
                     tags.HTTP_METHOD: 'POST',
                     tags.PEER_ADDRESS: ':'.join(map(str, self.client_address))}

        # Opentracing spans provide helpful context manager for transparent span scoping
        with tracer.start_span('post_handler', child_of=span_ctx, tags=span_tags) as span:
            # Set request-level tags
            span.set_tag('post_handler.headers', json.dumps(headers))

            try:
                body = self.rfile.read(int(self.headers.get('Content-Length', '0')))
                body = json.loads(body.decode())
                start = body['term_start']
                end = body['term_end']

                # Log events for annotation creation in SignalFx
                span.log_kv(dict(event='calculation beginning.'))

                # Provide subsequent, shared thread invocations of
                # opentracing_instumentation.get_current_span() with this span
                with span_in_context(span):
                    partial_sum = calculate_partial(start, end)

                span.log_kv(dict(event='calculation finished.'))
            except Exception as e:
                tb = traceback.format_exc()
                span.log_kv({'event': 'error',
                             'error.kind': str(type(e)),
                             'error.object': tb})
                span.set_tag(tags.HTTP_STATUS_CODE, 500)
                self.write_response('Error occurred while processing request:\n{}' .format(tb), 500)
            else:
                span.set_tag(tags.HTTP_STATUS_CODE, 200)
                self.write_response(str(partial_sum), 200)

    def write_response(self, content, status_code):
        self.send_response(status_code)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        self.wfile.write(content.encode())


class PiServer(socketserver.ThreadingMixIn, http_server.HTTPServer):
    pass


if __name__ == '__main__':
    ap = ArgumentParser()
    ap.add_argument('-a', '--access-token', dest='access_token', type=str, default='',
                    help='Your SignalFx access token.')
    ap.add_argument('-P', '--port', dest='port', type=int, default=9090,
                    help='Listening port (default 9090)')
    ap.add_argument('-i', '--ingest', dest='ingest', type=str, default='https://ingest.signalfx.com/v1/trace',
                    help='SignalFx ingest url (default https://ingest.signalfx.com/v1/trace)')
    args = ap.parse_args()

    # Establish a jaeger-client-python configuration to report spans to SignalFx
    config = Config(
        config={
            'sampler': {
                'type': 'const',
                'param': 1,
            },
            'propagation': 'b3',  # Interoperable with Zipkin and Jaeger traces
            'jaeger_endpoint': args.ingest,
            'jaeger_format_params': '',  # No format query parameters required by SignalFx
            'jaeger_user': 'auth',  # Required static username for Access Token authentication via Basic Access
            'jaeger_password': args.access_token,  # Your organization's Access Token
            'logging': True,  # Report helpful span submission statements and errors to log handler
        },
        service_name='PiServer',  # Service name (low cardinality)
        validate=True,  # Have jaeger_client fail quickly on invalid configuration
    )

    # Globally sets opentracer.tracer to use jaeger_client over default noop tracer
    tracer = config.initialize_tracer()

    server = PiServer(('127.0.0.1', args.port), PiHandler)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
