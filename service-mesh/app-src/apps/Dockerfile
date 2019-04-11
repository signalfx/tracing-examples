FROM ecommerce-base:latest

ARG APP
ENV APP ${APP}

RUN mkdir /app/${APP}
RUN touch /app/${APP}/__init__.py
COPY demo/${APP}.py /app/${APP}/
COPY demo/utils.py /app/${APP}/
WORKDIR /app/

CMD python -m ${APP}.${APP}
