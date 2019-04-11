import asyncio
from aiohttp import ClientSession
import json
import os
import random
import requests
import time


HEADERS = {'Content-Type': 'application/json'}
search_keywords = ['red', 'blue', 'yellow', 'orange', 'green', 'purple', 'couch', 'chair', 'ottoman', 'sofa', 'plate', 'fork']
bad_customers = ['19d69ba226b2','ebaa9aeec82d','9973b84285f8']


async def hello(url,Headers,Data):
    async with ClientSession() as session:
        async with session.post(url,headers=Headers,data=Data) as response:
            response = await response.read()
            print(response)


def get_api_service():
    return 'http://' + os.getenv('API_SERVICE')


while True:
    iter = 0
    while iter < 30:
        loop = asyncio.get_event_loop()
        tasks = []
        customerId = '%012x' % random.randrange(16**12)
        cartId = random.randint(1,10)
        if cartId==2:
                cartId = random.randint(1,10)
        if cartId==2:
                customerId = bad_customers[random.randint(0,2)]

        search_query = {}
        search_query['customerId'] = customerId
        search_string = ''
        for i in range(0,random.randint(1,10)):
                search_string += random.choice(search_keywords) + " "
        search_query['query'] = search_string
        
        task = asyncio.ensure_future(hello(get_api_service() + "/search",HEADERS,json.dumps(search_query)))
        tasks.append(task)
        request_data = {}
        request_data['customerId'] = customerId
        # Reduce error rate by a factor of 10, thus 1% chance of error now

        request_data['cartId'] = "cart-" + str(cartId)
        task = asyncio.ensure_future(hello(get_api_service() + "/checkout",HEADERS, json.dumps(request_data)))
        tasks.append(task)
        iter += 1
    iter = 0
    loop.run_until_complete(asyncio.wait(tasks))
    time.sleep(3)
