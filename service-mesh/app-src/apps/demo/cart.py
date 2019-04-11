#!/usr/bin/env python

# Cart service.

from flask import Flask, abort, jsonify, request
import os
import requests
from signalfx_tracing import auto_instrument
from signalfx_tracing.libraries import requests_config
from . import utils


tracer = utils.get_tracer(os.getenv('SERVICE_NAME', 'cart'))
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
    'cart-3': [
        ('item-1', 3),
        ('item-2', 1),
        ('item-3', 2),
        ('item-4', 1),
        ('item-5', 9),
        ('item-6', 2),
        ('item-7', 5),
        ('item-8', 1),
        ('item-9', 6),
        ('item-10', 3),
        ('item-11', 8),
        ('item-12', 1),
        ('item-13', 3),
        ('item-14', 1),
    ],
    'cart-4': [
        ('item-10', 1),
    ],
    'cart-5': [
        ('item-7', 5),
        ('item-8', 1),
        ('item-9', 6),
        ('item-10', 3),
    ],
    'cart-6': [
        ('item-3', 2),
        ('item-4', 1),
        ('item-5', 9),
        ('item-6', 2),
        ('item-7', 5),
        ('item-8', 1),
        ('item-9', 6),
    ],
    'cart-7': [
        ('item-3', 4),
        ('item-1', 1),
    ],
    'cart-8': [
        ('item-5', 99),
        ('item-13', 99),
    ],
    'cart-9': [
        ('item-8', 1),
        ('item-9', 6),
        ('item-10', 3),
        ('item-11', 8),
        ('item-12', 1),
        ('item-13', 3),
        ('item-1', 1),
        ('item-2', 6),
        ('item-3', 3),
        ('item-4', 8),
        ('item-5', 1),
        ('item-6', 3),
    ],
    'cart-10': [
        ('item-1', 3),
        ('item-2', 1),
        ('item-3', 2),
        ('item-4', 1),
        ('item-5', 9),
        ('item-6', 2),
        ('item-7', 5),
        ('item-8', 1),
        ('item-9', 6),
        ('item-10', 3),
        ('item-11', 8),
        ('item-12', 1),
        ('item-13', 3),
        ('item-14', 1),
        ('item-15', 6),
        ('item-16', 3),
        ('item-17', 8),
        ('item-18', 1),
        ('item-19', 3),
        ('item-20', 1),
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
            url=utils.get_service('CHECKOUT') + '/checkout',
            json=checkout)
    if r.status_code != requests.codes.ok:
        abort(500)
    return jsonify(r.json())


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
