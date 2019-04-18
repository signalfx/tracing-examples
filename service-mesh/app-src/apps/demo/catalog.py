#!/usr/bin/env python

# Item catalog service.

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


ITEMS = {
    'item-1': ('Fork',       1.99),
    'item-2': ('Knife',      1.99),
    'item-3': ('Glass',      2.99),
    'item-4': ('Wine glass', 4.99),
    'item-5': ('Sheet',      2.99),
    'item-6': ('Sofa',    1000.00),
    'item-7': ('Plate',     22.99),
    'item-8': ('Chair',     84.99),
    'item-9': ('Rack',      12.99),
    'item-10': ('Pan',      14.99),
    'item-11': ('Spoon',     0.99),
    'item-12': ('Plant',     7.99),
    'item-13': ('Table',   500.00),
    'item-14': ('Bowl',      8.99),
    'item-15': ('Vase',     18.99),
    'item-16': ('Napkin',    1.99),
    'item-17': ('Ottoman', 200.00),
    'item-18': ('Bench',   350.00),
    'item-19': ('Armchair',250.00),
    'item-20': ('Stool',    15.00),
}


@app.route("/item/<item_id>")
def item_info(item_id):
    if not random.randint(0,25000):
        r = requests.get(url=utils.get_service('REFRESHDB') + '/refreshDB')

    item = ITEMS.get('item-' + item_id)

    if not item:
        abort(404)

    r = requests.get(url=utils.get_service('CATALOG') + '/stock_db/item/'+item_id)    

    return jsonify({
        'id': item_id,
        'name': item[0],
        'price': item[1]
    })


@app.route("/search",methods=["POST"])
def search_catalog():
    data= request.get_json()

    if not utils.validate_and_set_tags(data, 'query'):
        abort(400)

    resp = {}
    resp['results'] = []
    for token in data['query'].split():
        for item in ITEMS:
            if ITEMS.get(item)[0].lower() == token.lower():
                r = requests.get(url=utils.get_service('CATALOG') + '/stock_db/item/' + item.strip('item-'))
                resp['results'].append(ITEMS.get(item))
                time.sleep(.01)

    resp['result_count'] = len(resp['results'])

    if not utils.validate_and_set_tags(resp, 'result_count'):
        abort(400)

    return jsonify(resp)


@app.route("/stock_db/item/<item_id>")
def stock_db(item_id):
    data = {}
    data['query'] = 'SELECT ' + item_id + ' FROM STOCK'
    data['deployment']='prod'
    
    if not random.randint(0,500):
        data['query'] = 'SELECT * FROM STOCK'
        data['deployment'] = 'canary'
        time.sleep(random.randint(5,8)+float(random.randint(0,45)/1000))

    if not utils.validate_and_set_tags(data, 'query', 'deployment'):
        abort(400)

    resp = {}
    resp['results'] = []
    resp['results'].append(ITEMS.get('item-'+item_id))

    return jsonify(resp)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5004)

