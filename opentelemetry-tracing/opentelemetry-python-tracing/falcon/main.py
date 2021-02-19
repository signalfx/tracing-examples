#!/usr/bin/env python

import json
import sys
import logging

import falcon
from wsgiref import simple_server
from opentelemetry import trace
from opentelemetry.trace import SpanKind


tracer = trace.get_tracer(__name__)

class HelloWorldResource(object):
    def on_get(self, req, resp):
        """Handles GET requests"""
        with tracer.start_as_current_span('internal_span', kind=SpanKind.INTERNAL) as span:
            resp.body = json.dumps({'ok': True})


app = falcon.API()

app.add_route('/hello', HelloWorldResource())

if __name__ == '__main__':
    port = 8000
    print('starting server: ', port)
    httpd = simple_server.make_server('127.0.0.1', port, app)
    httpd.serve_forever()
