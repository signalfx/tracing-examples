apt-get update
apt-get install -y nano git gcc make autoconf zlib1g-dev libpng-dev unzip libicu-dev gdb
pecl config-set preferred_state beta
pecl install opentelemetry
pecl install protobuf-3.25.1
a2enmod rewrite
cp /files/extension.ini /usr/local/etc/php/conf.d/otel.ini
cp /files/vhost.conf /etc/apache2/sites-available/000-default.conf
php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');"
php composer-setup.php
php -r "unlink('composer-setup.php');"
mv composer.phar /usr/local/bin/composer
