import json

print('Loading the function')

def lambda_handler(event, context):
    print("Received event: " + json.dumps(event, indent=2))
    return "Hello from Python function!"