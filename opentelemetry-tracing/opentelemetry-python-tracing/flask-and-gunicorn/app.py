from flask import Flask

app = Flask(__name__)

@app.route('/hello/')
def hello_world():
    return 'Hello, World!'


# running with gunicorn:
# gunicorn -b 127.0.0.1:8000 -c gunicorn.config.py --threads 2 --workers 4 app:app