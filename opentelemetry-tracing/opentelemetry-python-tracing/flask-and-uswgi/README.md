# Instrumenting Flask and uWSGI with Splunk OpenTelemetry

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
uwsgi --http 127.0.0.1:8000 --wsgi-file app.py --callable app --master --processes 4 --enable-threads
```

Open http://localhost:8000/hello/ to access the app.

