#!/usr/bin/env python

from cmath import exp, pi
from random import randint

from opencensus.trace import tracer as tracer_module
from opencensus.trace.exporters import jaeger_exporter
from opencensus.trace import execution_context


# access token for SignalFx
access_token = ""

# Constants for signal generation
X_LENGTH = 10
X_MIN = 0
X_MAX = 10


def fft(x):
    # retrieve the stored tracer
    tracer = execution_context.get_opencensus_tracer()

    with (tracer.span(name="fft")) as span_fft:
        # tag the span with the signal being processed
        span_fft.add_attribute("x", ' '.join(str(x_i) for x_i in x))

        n = len(x)
        t = exp(-2 * pi * 1j / n)

        if n > 1:
            even = fft(x[::2])
            odd = fft(x[1::2])
            x = even + odd

            for k in range(n // 2):
                x_k = x[k]
                x[k] = x_k + t ** k * x[k + n // 2]
                x[k + n // 2] = x_k - t ** k * x[k + n // 2]

        return x


def gen_x():
    # retrieve the stored tracer
    tracer = execution_context.get_opencensus_tracer()

    with (tracer.span(name="generate signal")) as span_gen_x:
        return [ randint(X_MIN, X_MAX) for i in range(X_LENGTH) ]


def mag_fft(x):
    # retrieve the stored tracer
    tracer = execution_context.get_opencensus_tracer()

    with (tracer.span(name="magnitude of fft")) as print_span:
        # print(' '.join("%5.3f" % abs(f) for f in x))
        return ' '.join("%5.3f" % abs(f) for f in x)


def main():
    # create the exporter and set it in the tracer
    exporter = jaeger_exporter.JaegerExporter(
            service_name="test_python_service",
            host_name="ingest.signalfx.com",
            username="auth",
            password=access_token,
            endpoint="/v1/trace"
            )
    tracer = tracer_module.Tracer(exporter=exporter)

    # set the tracer for this context so it can be retrieved elsewhere
    tracer.store_tracer()
    # The same result can also be achieved with
    # execution_context.set_opencensus_tracer(tracer)

    # start the span using "with", and it will automatically close and export the
    # span as you leave scope
    with (tracer.span(name="main")) as span_main:
        x = fft(gen_x())

        # tag the main span with the result
        span_main.add_attribute("fft(x)", mag_fft(x))

        # simple rectangular pulse to verify that the generated transform is correct
        # output should be [ 4.000 2.613 0.000 1.082 0.000 1.082 0.000 2.613 ]
        x = fft([1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0])
        span_main.add_attribute("fft(rect((x - 2)/4))", mag_fft(x))


if __name__ == "__main__":
    main()
