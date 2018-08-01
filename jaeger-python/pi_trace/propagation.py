from opentracing.propagation import Format, SpanContextCorruptedException


def context_headers(tracer, span):
    # Propagate B3 context via request headers
    # https://github.com/openzipkin/b3-propagation/blob/master/README.md
    headers = {}
    tracer.inject(span, Format.HTTP_HEADERS, headers)
    return headers


def propagated_context(tracer, headers):
    # Retrieve propagated B3 context from request headers
    # https://github.com/openzipkin/b3-propagation/blob/master/README.md
    try:
        span_ctx = tracer.extract(Format.HTTP_HEADERS, headers)
    except SpanContextCorruptedException:  # Requests without these headers should be anticipated.
        span_ctx = None
    return span_ctx
