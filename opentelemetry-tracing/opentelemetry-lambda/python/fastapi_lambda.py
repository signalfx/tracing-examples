import logging
from fastapi import FastAPI
from mangum import Mangum
from pymongo import MongoClient

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

print('Loading FastAPI Python function')

app = FastAPI(title="MyAwesomeApp")

@app.get("/hello")
def hello():
    return {"message": "Hello World"}

@app.get("/mongo")
def mongo():
    mongo_uri = "mongodb+srv://<USER>:<PASSWORD>@<MONGO URL>?retryWrites=true&w=majority";
    client = MongoClient(mongo_uri)
    db = client.test_db
    collection = db.test_collection
    new_id = collection.insert_one({"message": "hello"}).inserted_id
    return {"created hello message with id: ": str(new_id)}

handler = Mangum(app)


