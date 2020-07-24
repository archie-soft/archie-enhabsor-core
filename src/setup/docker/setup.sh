#!/bin/sh

# Exit on first error
set -e

# Create directories
mkdir /opt/apache
for d in assetstore import logs work; do
    mkdir -p /var/opt/archie/enhabsor/$d
done
for d1 in public private secret; do
    mkdir /var/opt/archie/enhabsor/assetstore/$d1;
    for d2 in originals thumbnails text; do
        mkdir /var/opt/archie/enhabsor/assetstore/$d1/$d2;
    done
done

# Install core packages
apt-get update
apt-get -y install apache2 mariadb-server openjdk-11-jre lsof util-linux imagemagick ghostscript tesseract-ocr tesseract-ocr-heb
# Install optional packages
apt-get -y install vim bash-completion iproute2 curl

# Solr configuration
tar xf ./download/solr-7.7.3.tgz
mv ./solr-7.7.3 /opt/apache/solr
chown -R www-data /opt/apache/solr
runuser -u www-data -- /opt/apache/solr/bin/solr start
runuser -u www-data -- /opt/apache/solr/bin/solr create -c enhabsor
curl http://localhost:8983/solr/enhabsor/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
curl http://localhost:8983/solr/enhabsor/config -H 'Content-type:application/json' -d @./archie.solr.config.json
curl http://localhost:8983/solr/enhabsor/schema -H 'Content-type:application/json' -d @./archie.solr.schema.json
# optionally: restore data freom backup snapshot

# AcitiveMQ configuration
tar xf ./download/activemq-5.16.0-bin.tar.gz
mv ./apache-activemq-5.16.0 /opt/apache/activemq
chown -R www-data /opt/apache/activemq

# MariaDB configuration
service mysql start
mysqladmin -u root create enhabsor
cat ./archie.mariadb.sql | mysql -u root enhabsor
mysql -u root -e "GRANT ALL ON enhabsor.* TO 'archie'@'%' IDENTIFIED BY '1234'"

# Apache http server configuration
mkdir -p /var/www/archie/enhabsor
mv ./index.html /var/www/archie/enhabsor
mv ./archie.virtual-host.conf /etc/apache2/sites-available/
chown -R www-data /var/opt/archie/enhabsor
a2enmod proxy
a2enmod proxy_http
a2dissite 000-default
a2ensite archie.virtual-host

# Move start script to the root of the file system and make sure it is executable
mv ./start.sh /
chmod 755 /start.sh
