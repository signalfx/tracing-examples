#!/usr/bin/env python

# Checkout service.

from flask import Flask, abort, jsonify, request
import os
import requests
from signalfx_tracing import auto_instrument
from signalfx_tracing.libraries import requests_config
from . import utils


tracer = utils.get_tracer(os.getenv('SERVICE_NAME', 'checkout'))
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
            r = requests.get(utils.get_service('CATALOG') + '/item/' + item[0].strip('item-'))
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
            url=utils.get_service('PAYMENT') + '/process',
            json=payment)
    if r.status_code != requests.codes.ok:
        abort(500)
    return jsonify(r.json())


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002)
