#!/bin/sh

export POD_HOST_IP=${POD_HOST_IP:-$(curl http://169.254.169.254/latest/meta-data/local-ipv4)}

python -m ${APP}.${APP}
