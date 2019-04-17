FROM ubuntu:18.04

RUN apt-get update -y && \
    apt-get install -y python-pip python-dev curl

COPY . /app/
WORKDIR /app/
RUN pip install -r ./requirements.txt

WORKDIR /app/signalfx-python-tracing
RUN ./scripts/bootstrap.py --jaeger

WORKDIR /app
