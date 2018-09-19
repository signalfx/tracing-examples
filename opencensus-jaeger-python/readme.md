# Opencensus Jaeger Exporter for Python

This is an example Python app using the Opencensus Jaeger exporter. It calculates a fast Fourier Transform and annotates the parent span with the result. The code is in [fft.py](fft.py)


# Setup and Usage

To install the required packages, `opencensus` and `thrift`, using pip.
```
pip install opencensus thrift
```

Include these imports to use the tracer:
```
from opencensus.trace import execution_context
from opencensus.trace import tracer as tracer_module
from opencensus.trace.exporters import jaeger_exporter
```

## Configure a tracer

```
exporter = jaeger_exporter.JaegerExporter(
    service_name="",
    host_name="",
    port="",
    username="auth",
    password=auth_token,
    endpoint="/v1/trace"
    )

tracer = tracer_module.Tracer(exporter=exporter)
```

Now the tracer can be saved so that it can be called from other functions.
```
tracer.store_tracer()
```

Retrieve the tracer from the execution context:
```
tracer = execution_context.get_opencensus_tracer()
```

## Create Spans

To create a span, simply call `tracer.span(name=span_name)`. Starting and closing a span can be simplified by using Python's `with` statement:
```
with (tracer.span(name=span_name)) as span:
    some_function()

# the span is automatically closed and exported
```

Alternatively, the span can be manually managed.
```
span = tracer.span(name=span_name)
span.start()
do_some_work()
span.finish()
```

The tracer is able to automatically manage parent/child spans if a child span is started while a parent span is still active.
```
with (tracer.span(name="parent_span")) as parent_span:
    with (tracer.span(name="child_span")) as child_span:
        do_some_work()
```
`child_span` will be exported as a child span of `parent_span`

A child span can also be manually created.
```
child_span = parent_span.span(name="child_span")
```


# Running the example

Install the pip packages from the setup section.

Then run the script:
```
./fft.py
```


# Resources

- https://opencensus.io/api/python/trace/usage.html

