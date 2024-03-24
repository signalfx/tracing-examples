#!/bin/sh
/files/setup-application.sh
sed -i 's/\/var\/www\/html\//\/var\/www\/html\/example\/public\//g' /etc/apache2/sites-available/000-default.conf
apache2-foreground
