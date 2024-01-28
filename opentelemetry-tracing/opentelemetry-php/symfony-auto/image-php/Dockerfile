FROM php:8.2-apache

COPY files /files
RUN chmod +x /files/*.sh

RUN /files/setup-php.sh
RUN /files/setup-application.sh

ENTRYPOINT ["apache2-foreground"]
