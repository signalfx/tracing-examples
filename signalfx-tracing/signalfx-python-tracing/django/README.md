
# Django Auto-Instrumentation Example

The aim of this example is to show how to automatically produce distributed traces using the 
[SignalFx Tracing Library for Python](https://github.com/signalfx/signalfx-python-tracing).
For this example, using the [Django REST Framework](https://www.django-rest-framework.org/), 
we have created a client/server interaction in the form of a basic blog service and consumer.



## Installing and running the example app and client
To run this example locally, and send traces to your available Smart Agent or Gateway, you may first clone this repository. 
Next, navigate into the `django` directory and run the following commands:

```bash
$ pip install -r requirements.txt
# install supported tracer and library instrumentation
$ sfx-py-trace-bootstrap
$ cd blog
$ python manage.py makemigrations posts
$ python manage.py migrate
$ python manage.py createsuperuser --username <USERNAME> --email <EMAIL>
$ sfx-py-trace manage.py runserver localhost:<PORT>
```
To confirm everything is set up as expected, you may navigate to `http://localhost:<PORT>` 
and sign in with the `username` and `password` you created above.


### Interactions with the blog includes:
    - Creating an article (user log in necessary)
    - Viewing articles created
    - Viewing list of contributing authors and their articles
    - Updating/Editing an article  (user log in necessary)
    - Deleting an article (user log in necessary)
    

### Trace identification:
To clearly identify your traces, you may choose to name your server services by setting the
 `SIGNALFX_SERVICE_NAME` environment variable to your preferred name.  <br/>
 
For example: 
```bash
$ export SIGNALFX_SERVICE_NAME='<YOUR_APP_NAME>'
```
(__Note__: This should be done in the same terminal in which your server is running).

The client's service name may be set in `create_tracer()` as follows:
```python

create_tracer(service_name='<APP_NAME>')
```
If not set, the default service name is: `SignalFx-Tracing`<br/><br/>
 
### Running the example
In the directory containing `manage.py` (in this example:`django/blog`), start the server by using:

```bash
$ sfx-py-trace manage.py runserver <PORT>

```

In a different terminal, and in the directory containing `client.py` (in this case, `django/`), run the client using:

```bash
$ python client.py --username <USERNAME> --password <PASSWORD> --port <PORT>
```

And that's it!


### Extra Notes
1. In this example, we used the SignalFx trace decorator `@trace` to automatically create spans 
for tracing custom logic. Please see [the tracer decoration documentation](https://github.com/signalfx/signalfx-python-tracing#trace-decorator) 
for more information. <br/>

2. The `signalfx-tracing` module and this application configuration assume that your Smart Agent
or Gateway is accepting traces at http://localhost:9080/v1/trace.  If this is not the case,
you can set the `SIGNALFX_ENDPOINT_URL` environment variable to the desired url to suit your
environment before launching the server and client.


