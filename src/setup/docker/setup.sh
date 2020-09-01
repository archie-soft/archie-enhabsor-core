#!/bin/bash

function installPackages {
    apt-get update
    apt-get -y install \
        apache2 mariadb-server openjdk-11-jre  \
        lsof util-linux vim bash-completion iproute2 curl \
        openjdk-11-jdk maven nodejs npm
}

function createAssetStore {
    mkdir -p /var/opt/archie/enhabsor
    for d in assetstore import logs work; do
        mkdir /var/opt/archie/enhabsor/$d
    done
    for d1 in public private secret; do
        mkdir /var/opt/archie/enhabsor/assetstore/$d1
        for d2 in originals thumbnails text; do
            mkdir /var/opt/archie/enhabsor/assetstore/$d1/$d2
        done
    done
    chown -R www-data /var/opt/archie/enhabsor
}

function installSolr {
    mkdir -p /opt/apache
    tar xf ./download/solr-7.7.3.tgz
    mv ./solr-7.7.3 /opt/apache/solr
    chown -R www-data /opt/apache/solr
    runuser -u www-data -- /opt/apache/solr/bin/solr start
    runuser -u www-data -- /opt/apache/solr/bin/solr create -c enhabsor
    curl http://localhost:8983/solr/enhabsor/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
    curl http://localhost:8983/solr/enhabsor/config -H 'Content-type:application/json' -d @./archie.solr.config.json
    curl http://localhost:8983/solr/enhabsor/schema -H 'Content-type:application/json' -d @./archie.solr.schema.json
    # optionally: restore data from backup snapshot
}

function installActivemq {
    mkdir -p /opt/apache
    tar xf ./download/activemq-5.16.0-bin.tar.gz
    mv ./apache-activemq-5.16.0 /opt/apache/activemq
    chown -R www-data /opt/apache/activemq
}

function configMariadb {
    service mysql start
    mysqladmin -u root create enhabsor
    cat ./archie.mariadb.sql | mysql -u root enhabsor
    mysql -u root -e "GRANT ALL ON enhabsor.* TO 'archie'@'%' IDENTIFIED BY '1234'"
}

function configApache2 {
    # Apache http server configuration
    mkdir -p /var/www/archie/enhabsor
    mv ./archie.virtual-host.conf /etc/apache2/sites-available/
    chown -R www-data /var/opt/archie/enhabsor
    a2enmod proxy
    a2enmod proxy_http
    a2enmod headers
    a2enmod rewrite
    a2dissite 000-default
    a2ensite archie.virtual-host
}

function otherTasks {
    ln --symbolic --force /usr/share/zoneinfo/Asia/Jerusalem  /etc/localtime
}

installPackages
createAssetStore
installSolr
installActivemq
configMariadb
configApache2
otherTasks