#!/usr/bin/env python

# Item catalog service.

from signalfx_tracing import auto_instrument
from flask import Flask, abort, jsonify, request
import uuid

from . import utils

tracer = utils.get_tracer('catalog')
auto_instrument(tracer)

app = Flask(__name__)

ITEMS = {
    'item-1': ('Fork',       1.99),
    'item-2': ('Knife',      1.99),
    'item-3': ('Glass',      2.99),
    'item-4': ('Wine glass', 4.99),
}

@app.route("/item/<item_id>")
def item_info(item_id):
    item = ITEMS.get(item_id)
    if not item:
        abort(404)
    return jsonify({
        'id': item_id,
        'name': item[0],
        'price': item[1]
    })

if __name__ == '__main__':
    app.run(port=5004)

