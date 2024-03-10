mkdir -p /var/www/html
cd /var/www/html
composer create-project symfony/symfony-demo example 2.5.0
cd example
composer update
composer require -n "open-telemetry/sdk:^1.0"
composer require -n "open-telemetry/exporter-otlp:^1.0.3"
composer require -n "php-http/guzzle7-adapter": "^1.0"
composer require -n "open-telemetry/opentelemetry-auto-symfony:1.0.0beta22"
composer require -n "open-telemetry/opentelemetry-auto-psr3:^0.0.6"
composer require -n "open-telemetry/opentelemetry-propagation-server-timing:^0.0.2"
cp /files/DemoController.php /var/www/html/example/src/Controller/
chmod -R 0777 /var/www/html/example/var/log
