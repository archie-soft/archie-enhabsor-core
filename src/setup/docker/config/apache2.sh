# Apache http server configuration
mkdir -p /var/www/archie/enhabsor
mv ./index.html /var/www/archie/enhabsor
mv ./logo.png /var/opt/archie/enhabsor/assetstore/public/originals/
mv ./archie.virtual-host.conf /etc/apache2/sites-available/
chown -R www-data /var/opt/archie/enhabsor
a2enmod proxy
a2enmod proxy_http
a2enmod headers
a2enmod rewrite
a2dissite 000-default
a2ensite archie.virtual-host
