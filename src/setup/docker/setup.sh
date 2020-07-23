#!/bin/sh

# Install packages
apt-get update
apt-get -y install apache2 mariadb-server vim

# Create directory tree
mkdir -p /var/opt/archie/{assetstore,import,backup,logs,work}
mkdir -p /var/opt/archie/assetstore/{public,private,secret}
mkdir -p /var/opt/archie/assetstore/public/{originals,thumbnails,text}
mkdir -p /var/opt/archie/assetstore/private/{originals,thumbnails,text}
mkdir -p /var/opt/archie/assetstore/secret/{originals,thumbnails,text}
chown -R www-data /var/opt/archie

# Install Apache AcitiveMQ
tar xf ./activemq


# Configure Apache server
mv /root/httpd/archie.conf /etc/apache2/sites-enabled/