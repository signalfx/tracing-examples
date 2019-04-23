FROM ecommerce-base:latest

ARG APP
ENV APP ${APP}

RUN mkdir /app/${APP}
RUN touch /app/${APP}/__init__.py
COPY demo/${APP}.py /app/${APP}/
COPY demo/utils.py /app/${APP}/
COPY startup.sh /app/startup.sh
WORKDIR /app/

CMD ./startup.sh
