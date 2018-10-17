#!/usr/bin/env python

# Checkout service.

from signalfx_tracing import auto_instrument
from signalfx_tracing.libraries import requests_config
from flask import Flask, abort, jsonify, request
import requests

from . import utils

tracer = utils.get_tracer('checkout')
requests_config.propagate = True
auto_instrument(tracer)
app = Flask(__name__)


@app.route("/checkout", methods=["POST"])
def checkout():
    scope = tracer.scope_manager.active

    data = request.get_json()
    if not utils.validate_and_set_tags(data, 'customerId', 'items'):
        abort(400)

    amount = 0
    with tracer.start_active_span('price_items', finish_on_close=True) as s:
        for item in data['items']:
            r = requests.get('http://localhost:5004/item/' + item[0])
            if r.status_code != requests.codes.ok:
                abort(500)
            price = item[1] * r.json()['price']
            amount += price


    payment = {
        'customerId': data['customerId'],
        'currency': 'USD',
        'amount': amount
    }

    r = requests.post(
            url='http://localhost:5003/process',
            json=payment)
    if r.status_code != requests.codes.ok:
        abort(500)
    return jsonify(r.json())

if __name__ == '__main__':
    app.run(port=5002)
