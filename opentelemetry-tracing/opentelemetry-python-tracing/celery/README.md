# Instrumenting Celery worker and producer with Splunk OpenTelemetry

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

## 5. Run message broker (redis)

```
docker run -d -p 6379:6379 redis
```

## 6. Run the workers

```
splunk-py-trace celery -A tasks worker --loglevel=DEBUG -c 4
```

## 7. Run the producer to send tasks to the workers

```
splunk-py-trace python producer.py
```
