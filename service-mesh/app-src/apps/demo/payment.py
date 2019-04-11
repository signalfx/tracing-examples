#!/usr/bin/env python

# Payment service.

from flask import Flask, abort, jsonify, request
import os
import random
import time
from signalfx_tracing import auto_instrument
from . import utils
import uuid


tracer = utils.get_tracer(os.getenv('SERVICE_NAME', 'payment'))
auto_instrument(tracer)
app = Flask(__name__)


@app.route("/process", methods=["POST"])
def process():
    scope = tracer.scope_manager.active

    data = request.get_json()
    if not utils.validate_and_set_tags(data, 'customerId', 'currency', 'amount'):
        abort(400)

    with tracer.start_active_span('process_payment', finish_on_close=True) as s:
        transactionId = uuid.uuid4()
        processor = 'PayPal'
        s.span.set_tag('processor', processor)
        s.span.set_tag('transactionId', transactionId)
        s.span.set_tag('error', 'true')

        # Fake API request to a payment service
        payment_api_tags={
                'span.kind': 'CLIENT',
                'http.method': 'POST',
                'http.url': 'https://api.paypal.com/payment',
                'http.status_code': 504,
                'error': 'true',
                'message': 'Request timeout'
                }
        with tracer.start_active_span('http.post https://api.paypal.com/payment', tags=payment_api_tags, finish_on_close=True) as child_scope:
            time.sleep(int(os.getenv('DELAY', 0)))

    return jsonify({
        'transactionId': transactionId,
        'processor': processor,
        'currency': data['currency'],
        'amount': data['amount']
    })


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5003)
