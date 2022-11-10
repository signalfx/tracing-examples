#!/usr/bin/env python

import json

import falcon
from wsgiref import simple_server


class HelloWorldResource(object):
    def on_get(self, req, resp):
        """Handles GET requests"""
        resp.body = json.dumps({"ok": True})


app = falcon.App()

app.add_route("/hello", HelloWorldResource())

if __name__ == "__main__":
    port = 8000
    print("starting server: ", port)
    httpd = simple_server.make_server("127.0.0.1", port, app)
    httpd.serve_forever()
