# Instrumenting Falcon with Splunk OpenTelemetry

## 1. Setup virtual env

```
python -m venv venv
```

## 2. Activate the virtual env

```
source venv/bin/activate
```

## 3. Install Falcon and Splunk OpenTelemetry

```
pip install -r requirements.txt
```

## 4. Install instrumentation packages

```
splunk-py-trace-bootstrap
```

## 5. Instrument and run the falcon app

```
export OTEL_SERVICE_NAME=my-falcon-service
splunk-py-trace python main.py
```

Open http://localhost:8000/hello to access the app.



