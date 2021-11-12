# Instrumenting Flask and Gunicorn with Splunk OpenTelemetry

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
splunk-py-trace-bootstrap
```

## 5. Run the Django app with Gunicorn

```
export OTEL_SERVICE_NAME=my-flask-service
splunk-py-trace gunicorn -b 127.0.0.1:8000 --threads 2 --workers 4 app:app
```

Open http://localhost:8000/hello/ to access the app.

