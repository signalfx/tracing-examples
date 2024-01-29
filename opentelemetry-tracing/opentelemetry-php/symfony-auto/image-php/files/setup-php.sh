apt-get update
apt-get install -y nano git gcc make autoconf zlib1g-dev libpng-dev unzip libicu-dev gdb
pecl config-set preferred_state beta
pecl install opentelemetry
pecl install protobuf-3.25.1
a2enmod rewrite
cp /files/extension.ini /usr/local/etc/php/conf.d/otel.ini
cp /files/vhost.conf /etc/apache2/sites-available/000-default.conf
php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');"
php -r "if (hash_file('sha384', 'composer-setup.php') === 'e21205b207c3ff031906575712edab6f13eb0b361f2085f1f1237b7126d785e826a450292b6cfd1d64d92e6563bbde02') { echo 'Installer verified'; } else { echo 'Installer corrupt'; unlink('composer-setup.php'); } echo PHP_EOL;"
php composer-setup.php
php -r "unlink('composer-setup.php');"
mv composer.phar /usr/local/bin/composer
