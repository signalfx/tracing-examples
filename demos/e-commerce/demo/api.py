#!/usr/bin/env python

# API backend for the website.

from signalfx_tracing import auto_instrument
from signalfx_tracing.libraries import requests_config
from flask import Flask, abort, jsonify, request
import requests

from . import utils

tracer = utils.get_tracer('api')
requests_config.propagate = True
auto_instrument(tracer)
app = Flask(__name__)


@app.after_request
def inject_trace_id(response):
    scope = tracer.scope_manager.active
    traceId = '{0:x}'.format(scope.span.context.trace_id)
    response.headers['X-SF-TraceId'] = traceId
    return response


@app.route("/checkout", methods=["POST"])
def checkout():
    data = request.get_json()
    if not utils.validate_and_set_tags(data, 'customerId', 'cartId'):
        abort(400)

    r = requests.post(
            url='http://localhost:5001/start_checkout',
            json={'customerId': data['customerId'],
                  'cartId': data['cartId']})
    if r.status_code != requests.codes.ok:
        abort(500)

    payment = r.json()
    if not utils.validate_and_set_tags(payment,
            'processor', 'transactionId', 'currency', 'amount'):
        abort(500)

    resp = {
        'customerId': data['customerId'],
        'cartId': data['cartId'],
        'processor': payment['processor'],
        'transactionId': payment['transactionId'],
        'currency': payment['currency'],
        'amount': payment['amount']
    }

    return jsonify(resp)

if __name__ == '__main__':
    app.run()
