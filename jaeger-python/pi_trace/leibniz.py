from opentracing_instrumentation import get_current_span, span_in_context
from six.moves import range
import opentracing


def leibniz_approx(partials=4, partial_terms=25000000):
    """Approximate the value of Pi using Leibniz's formula."""
    # Since opentracing.tracer returns a noop tracer by default, no configured tracing is
    # required to use this function.
    with opentracing.tracer.start_span('leibniz_approx') as root_span:
        # Set function level tags for debugging purposes
        root_span.set_tag('leibniz_approx.partials', partials)
        root_span.set_tag('leibniz_approx.partial_terms', partial_terms)
        approx = 0
        start = 0

        # opentracing_instrumentation.span_in_context will propagate
        # the existing span for retrieval in threading.local(). This
        # way, in single-threaded utilization, spans need not be referenced
        # by function argument.
        with span_in_context(root_span):
            for _ in range(partials):
                approx += calculate_partial(start, start + partial_terms)
                start += partial_terms

        return approx


def calculate_partial(start, end):
    """Calculate partial approximation of Pi.
    Distributable by summing partitioned approximations [start, end)
    """
    # If get_current_span() finds no existing span in thread-local storage, this will be a root
    # span.  If no opentracing compatible tracer has been initialized, this will be a noop span.
    with opentracing.tracer.start_span('calculate_partial', child_of=get_current_span()) as span:
        span.set_tag('calculate_partial.start', start)
        span.set_tag('calculate_partial.end', end)

        partial_sum = sum((4 * (-1.0) ** i / ((2 * i) + 1) for i in range(start, end)))
        span.set_tag('calculate_partial.partial_sum', partial_sum)
        return partial_sum
