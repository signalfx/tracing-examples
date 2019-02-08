FROM ubuntu:18.04

WORKDIR /example

RUN apt update &&\
    DEBIAN_FRONTEND=noninteractive apt install -y composer unzip php7.2-xml php7.2-sqlite3 php7.2-mbstring php7.2-intl php7.2-curl

RUN phpenmod pdo xml pdo_sqlite json mbstring intl curl
COPY ./ /example/
RUN composer install &&\
    composer --working-dir=symfony_demo install
