#!/usr/bin/env python

# Cart service.

from signalfx_tracing import auto_instrument
from signalfx_tracing.libraries import requests_config
from flask import Flask, abort, jsonify, request
import requests

from . import utils

tracer = utils.get_tracer('cart')
requests_config.propagate = True
auto_instrument(tracer)
app = Flask(__name__)

CARTS = {
    'cart-1': [
        ('item-1', 2),
        ('item-2', 2),
        ('item-3', 4),
    ],
    'cart-2': [
        ('item-1', 4),
        ('item-0', 1),
    ],
}


@app.route("/start_checkout", methods=["POST"])
def start_checkout():
    scope = tracer.scope_manager.active

    data = request.get_json()
    if not utils.validate_and_set_tags(data, 'customerId', 'cartId'):
        abort(400)

    with tracer.start_active_span('load_cart', finish_on_close=True) as s:
        items = CARTS[data['cartId']]
        if not items:
            abort(404)
        s.span.set_tag('num.items', len(items))

    checkout = {
        'customerId': data['customerId'],
        'items': items
    }

    r = requests.post(
            url='http://localhost:5002/checkout',
            json=checkout)
    if r.status_code != requests.codes.ok:
        abort(500)
    return jsonify(r.json())

if __name__ == '__main__':
    app.run(port=5001)
