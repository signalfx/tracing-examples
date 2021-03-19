import uwsgidecorators
from splunk_otel.tracing import start_tracing
from opentelemetry.instrumentation.flask import FlaskInstrumentor
from flask import Flask

app = Flask(__name__)

@uwsgidecorators.postfork
def setup_tracing():
    start_tracing()
    FlaskInstrumentor().instrument_app(app)

@app.route('/hello/')
def hello_world():
    return 'Hello, World!'


# running with uWSGI:
# uwsgi --http 127.0.0.1:8000 --wsgi-file app.py --callable app --master --processes 4 --enable-threads