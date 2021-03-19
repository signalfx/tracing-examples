# Instrumenting Django and Gunicorn with Splunk OpenTelemetry

## 1. Setup virtual env

```
python -m venv venv
```

## 2. Activate the virtual env

```
source venv/bin/activate
```

## 3. Install dependencies into virtual env

```
pip install -r requirements.txt
```

## 4. Install instrumentation packages

```
splk-py-trace-bootstrap
```

## 5. Run the Django app with Gunicorn

```
export DJANGO_SETTINGS_MODULE=djtest.settings
gunicorn -b 127.0.0.1:8000 -c gunicorn.config.py --threads 2 --workers 4 djtest.wsgi
```

Open http://localhost:8000/hello to access the app.


Refer to `gunicorn.config.py` to see how tracing is setup.



