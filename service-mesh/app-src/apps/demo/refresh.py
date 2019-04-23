#!/usr/bin/env python

# Catalog refresh service

from flask import Flask, abort, jsonify, request
import os
import random
import requests
from signalfx_tracing import auto_instrument
from signalfx_tracing.libraries import requests_config
import time
from . import utils
import uuid


tracer = utils.get_tracer()
requests_config.propagate = True
auto_instrument(tracer)
app = Flask(__name__)


@app.route("/refreshDB")
def refreshDB():
    data = {}
    data['http.url'] = 'http://dbquery:9000/refresh/items'
    data['http.status_code'] = 200
    data['http.method']='GET'

    sleepTime = random.random()+random.randint(0,4)

    if sleepTime > 2:
    	data['region'] = 'apac'
    	if not utils.validate_and_set_tags(data, 'http.url','http.status_code','http.method','region'):
        	abort(400)
    else:
    	if not utils.validate_and_set_tags(data, 'http.url','http.status_code','http.method'):
        	abort(400)

    time.sleep(sleepTime)
    return "OK"


if __name__ == '__main__':
    app.run(host='0.0.0.0',port=5008)
