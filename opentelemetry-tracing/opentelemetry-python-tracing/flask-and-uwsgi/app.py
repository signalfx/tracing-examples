from flask import Flask

app = Flask(__name__)

@app.route('/hello/')
def hello_world():
    return 'Hello, World!'


# running with uWSGI:
# opentelemetry-instrument uwsgi --http 127.0.0.1:8000 --wsgi-file app.py --callable app --master --processes 4 --enable-threads
