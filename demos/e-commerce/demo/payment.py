#!/usr/bin/env python

# Payment service.

from signalfx_tracing import auto_instrument
from flask import Flask, abort, jsonify, request
import uuid

from . import utils

tracer = utils.get_tracer('payment')
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

    return jsonify({
        'transactionId': transactionId,
        'processor': processor,
        'currency': data['currency'],
        'amount': data['amount']
    })

if __name__ == '__main__':
    app.run(port=5003)
