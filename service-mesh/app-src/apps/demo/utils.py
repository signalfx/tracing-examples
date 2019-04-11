# Common utilities for the application

import opentracing
from opentracing.propagation import Format
import os
from signalfx_tracing.utils import create_tracer


def _get_token():
    return os.getenv('SIGNALFX_ACCESS_TOKEN', "")


def get_ingest():
    endpoint = os.getenv('SIGNALFX_ENDPOINT_URL', 'https://ingest.signalfx.com')
    host_ip = os.getenv('POD_HOST_IP')

    endpoint = endpoint.replace('POD_HOST_IP', host_ip)

    if not endpoint.startswith('http'):
        endpoint = 'http://' + endpoint

    return endpoint


def get_tracer(service):
    endpoint = get_ingest()
    print(endpoint)
    return create_tracer(_get_token(),
        config={'sampler': {'type': 'const', 'param': 1},
                'logging': True,
                'propagation': 'b3',
                'jaeger_endpoint': endpoint + '/v1/trace'},
        service_name=service)


def validate_and_set_tags(data, *args):
    span = opentracing.tracer.scope_manager.active.span
    for arg in args:
        value = data.get(arg)
        if value is None:
            return False

        if type(value) is list or type(value) is dict:
            span.set_tag('num.' + arg, len(value))
        else:
            span.set_tag(arg, value)
    return True


def get_service(name):
    return 'http://' + os.getenv(name + '_SERVICE')

