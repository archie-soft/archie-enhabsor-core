#!/bin/bash

function installPackages {
    apt-get update
    apt-get -y install apache2 mariadb-server openjdk-11-jre openjdk-11-jdk maven git \
    lsof util-linux vim bash-completion iproute2 curl xmlstarlet locales
    # nodeps
    curl -sL https://deb.nodesource.com/setup_14.x | bash -
    apt-get update
    apt-get install -y nodejs
    yes | npm install --silent -g @angular/cli
    # add regular user
    adduser --disabled-password --gecos "" archie
}

function createFolders {
    # asset-store
    mkdir -p /var/opt/archie/enhabsor
    for d in assetstore import logs work; do
        mkdir /var/opt/archie/enhabsor/$d
    done
    mkdir /var/opt/archie/enhabsor/work/import
    for d1 in public private secret; do
        mkdir /var/opt/archie/enhabsor/assetstore/$d1
        for d2 in originals thumbnails text; do
            mkdir /var/opt/archie/enhabsor/assetstore/$d1/$d2
        done
    done
    chown -R archie /var/opt/archie/enhabsor
    # java application
    mkdir -p /opt/hilel14/archie/enhabsor
    chown -R archie /opt/hilel14/archie/enhabsor
    mkdir /var/opt/maven
    chown -R archie /var/opt/maven
}

function installSolr {
    mkdir -p /opt/apache
    tar xf ./download/solr-7.7.3.tgz
    mv ./solr-7.7.3 /opt/apache/solr
    chown -R archie /opt/apache/solr
    runuser -u archie -- /opt/apache/solr/bin/solr start
    runuser -u archie -- /opt/apache/solr/bin/solr create -c enhabsor
    curl http://localhost:8983/solr/enhabsor/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
    curl http://localhost:8983/solr/enhabsor/config -H 'Content-type:application/json' -d @./archie.solr.config.json
    curl http://localhost:8983/solr/enhabsor/schema -H 'Content-type:application/json' -d @./archie.solr.schema.json
    # optionally: restore data from backup snapshot
}

function installActivemq {
    mkdir -p /opt/apache
    tar xf ./download/activemq-5.16.0-bin.tar.gz
    mv ./apache-activemq-5.16.0 /opt/apache/activemq
    xmlstarlet edit --inplace -N my=http://www.springframework.org/schema/beans --update "/my:beans/my:bean[@id='jettyPort']/my:property[@name='host']/@value" --value "0.0.0.0" /opt/apache/activemq/conf/jetty.xml
    chown -R archie /opt/apache/activemq
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
    chown -R archie /var/www/archie/enhabsor
    mv ./archie.virtual-host.conf /etc/apache2/sites-available/
    a2enmod proxy
    a2enmod proxy_http
    a2enmod headers
    a2enmod rewrite
    a2dissite 000-default
    a2ensite archie.virtual-host
}

function otherTasks {
    # date and time
    ln --symbolic --force /usr/share/zoneinfo/Asia/Jerusalem  /etc/localtime
    # character encoding
    echo "LC_ALL=en_US.UTF-8" >> /etc/environment
    echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen
    echo "LANG=en_US.UTF-8" > /etc/locale.conf
    locale-gen en_US.UTF-8
    # also add to Dockerfile ?
        # ENV LANG en_US.UTF-8
        # ENV LANGUAGE en_US:en
        # ENV LC_ALL en_US.UTF-8
}

installPackages
createFolders
installSolr
installActivemq
configMariadb
configApache2
otherTasks