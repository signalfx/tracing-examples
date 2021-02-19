from splunk_otel.tracing import start_tracing

def post_fork(server, worker):
    start_tracing()
