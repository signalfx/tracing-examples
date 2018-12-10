from six.moves import range
import opentracing


def leibniz_approx(partials=4, partial_terms=25000000):
    """Approximate the value of Pi using Leibniz's formula."""
    # Creates and activates a root span for automatic parentage in subsequent
    # creations.  Since opentracing.tracer returns a noop tracer by default, no
    # configured tracing is required to use this function.
    with opentracing.tracer.start_active_span('leibniz_approx') as root_scope:
        root_span = root_scope.span  # obtain the span from the active scope
        # Set function level tags for debugging purposes
        root_span.set_tag('leibniz_approx.partials', partials)
        root_span.set_tag('leibniz_approx.partial_terms', partial_terms)
        approx = 0
        start = 0

        for _ in range(partials):
            approx += calculate_partial(start, start + partial_terms)
            start += partial_terms

        return approx


def calculate_partial(start, end):
    """Calculate partial approximation of Pi.
    Distributable by summing partitioned approximations [start, end)
    """
    # start_active_span will automatically establish child reference with active span of existing trace,
    # if one provided by scope manager.  If none determined to be active, this will create a new root span.
    # If no opentracing-compatible tracer has been initialized, this will be a noop span.
    with opentracing.tracer.start_active_span('calculate_partial') as scope:
        span = scope.span
        span.set_tag('calculate_partial.start', start)
        span.set_tag('calculate_partial.end', end)

        partial_sum = sum((4 * (-1.0) ** i / ((2 * i) + 1) for i in range(start, end)))
        span.set_tag('calculate_partial.partial_sum', partial_sum)
        return partial_sum
