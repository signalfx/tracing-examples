#!/usr/bin/env python

# API backend for the website.

from flask import Flask, abort, jsonify, request
import os
import requests
from signalfx_tracing import auto_instrument
from signalfx_tracing.libraries import requests_config
from . import utils


tracer = utils.get_tracer()
requests_config.propagate = True
auto_instrument(tracer)
app = Flask(__name__)


@app.route("/checkout", methods=["POST"])
def checkout():
    data = request.get_json()
    if not utils.validate_and_set_tags(data, 'customerId', 'cartId'):
        abort(400)

    r = requests.post(
            # headers=utils.forward_headers(request),
            url=utils.get_service('CART') + '/start_checkout',
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


@app.route("/search", methods=["POST"])
def site_search():
    data = request.get_json()
    if not utils.validate_and_set_tags(data, 'customerId', 'query'):
        abort(400)

    r = requests.post(url=utils.get_service('CATALOG') + '/search',
        json={'query': data['query']})
        
    if r.status_code != requests.codes.ok:
        abort(500)

    resp = r.json()
    if not utils.validate_and_set_tags(resp, 'result_count'):
        abort(400)

    return jsonify(resp)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
