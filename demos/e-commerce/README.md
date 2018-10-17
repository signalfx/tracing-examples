# Istio Python Microservices E-commerce Demo

## Services

* `api`: API server
* `cart`: Cart service
* `checkout`: Checkout service
* `catalog`: Item catalog servic
* `payment`: Payment service

## Running it

Make sure you have the right libraries:

```
$ git clone git+ssh://git@github.com/signalfx/signalfx-python-tracing
$ cd signalfx-python-tracing
$ ./bootstrap.py --jaeger
$ cd ~/.../tracing-examples/demos/e-commerce/
$ pip install -r requirements.txt
```

Then run all 5 services with:

```
$ python -m demo.<service>
```

## TODO

* Run in Istio
* Replace direct URLs (`localhost:500x`) to service-discovery powered
  endpoints like `http://checkout/...`
* Add understanding of a `IS_CANARY` environment variable to the
  `catalog` service that makes it take longer and/or throw errors
* Add service to automatically exercise the demo environment by
  generating requests
