import opentracing
from signalfx_tracing.utils import create_tracer

def _get_token():
    with open('.sftoken.sfdemo') as f:
        return f.read().strip()


def get_tracer(service):
    return create_tracer(_get_token(),
        config={'sampler': {'type': 'const', 'param': 1},
                'logging': True,
                'propagation': 'b3'},
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
