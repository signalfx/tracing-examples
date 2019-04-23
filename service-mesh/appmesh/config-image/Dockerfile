FROM alpine

ARG ENDPOINT_URL
ENV ENDPOINT=$ENDPOINT_URL

RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/* 

ADD tracing_envoy.yaml /envoy_config/tracing.yaml
ADD startup.sh /startup.sh

RUN chmod +x /startup.sh

VOLUME /envoy_config

CMD /startup.sh $ENDPOINT
# ENTRYPOINT [ "/bin/sh", "-c", "sed", "-i", "'s/@@HOST_IP@@/$(curl http://169.254.169.254/latest/meta-data/local-ipv4)/g'", "/tmp/envoy.yaml" ]
