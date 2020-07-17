# Archie En-Habsor Installation and configuration

## Initial setup

Install the latest version of [CentOS](https://www.centos.org/) Linux.

Install some usefull packages

```bash
#!/bin/bash
sudo yum install epel-release
sudo yum install java-1.8.0-openjdk-devel git maven lsof curl wget ImageMagick ghostscript tesseract-langpack-heb bash-completion

```

Clone `archie enhabsor core` repository from GitHub:

```bash
#!/bin/bash
git clone https://github.com/archie-soft/archie-enhabsor-core.git
```

Create dedicated user:

```bash
#!/bin/bash
sudo useradd archie
sudo passwd archie
```

Create application folders

```bash
#!/bin/bash
sudo mkdir -p /var/opt/archie/enhabsor/{assetstore,import,backup,logs,work}
sudo mkdir -p /var/opt/archie/enhabsor/assetstore/{public,private,secret}
sudo mkdir -p /var/opt/archie/enhabsor/assetstore/public/{originals,thumbnails,text}
sudo mkdir -p /var/opt/archie/enhabsor/assetstore/private/{originals,thumbnails,text}
sudo mkdir -p /var/opt/archie/enhabsor/assetstore/secret/{originals,thumbnails,text}
sudo chown -R archie.apache /var/opt/archie/enhabsor
sudo chcon -R -t httpd_sys_content_t /var/opt/archie/enhabsor/assetstore/public
sudo chcon -R -t httpd_sys_content_t /var/opt/archie/enhabsor/assetstore/private
sudo mkdir -p /opt/hilel14/archie/enhabsor/{bin,lib,resources}
sudo chown -R archie /opt/hilel14/archie/enhabsor
```
## Install and configure MariaDB

Iinital setup:

```bash
#!/bin/bash
sudo yum install mariadb-server
sudo systemctl enable mariadb
sudo systemctl start mariadb
mysql_secure_installation
```

Create application database and user:

```bash
#!/bin/bash
mysqladmin -u root -p create archie_enhabsor
mysql -u root -p -e "GRANT ALL ON archie_enhabsor.* TO 'archie'@'localhost' IDENTIFIED BY '12345678'";
cat src/setup/mariadb/create-archie-enhabsor-tables.sql | mysql -u archie -p archie_enhabsor
```

## Install and configure Apache Solr

Download [Apache Solr](http://lucene.apache.org/solr) version 7.7.1 and extract to `/opt/apache/solr`

Make the dedicated system user archie the owner of solr folder:

```bash
#!/bin/bash
sudo chown -R archie /opt/apache/solr
```

Install Systemd service unit file:

```bash
#!/bin/bash
sudo cp src/setup/systemd/solr.service /etc/systemd/system
sudo systemctl enable solr
sudo systemctl start solr
```

Test Solr installation:

```bash
#!/bin/bash
systemctl status solr
sudo jps -l
```

* Open browser and go to [administration console](http://localhost:8983/solr)

Create and configure `archie_enhabsor` collection

```bash
#!/bin/bash
sh src/setup/solr/create-enhabsor-collection.sh
```

Test Solr configuration:

* Open terminal and execute:
`curl "http://localhost:8983/solr/archie_enhabsor/select?wt=json&indent=on&q=*:*"`

## Install and configure Apache ActiveMQ

Download the latest version of [Apache ActiveMQ](https://activemq.apache.org) and extract to `/opt/apache/activemq`

Make the dedicated system user archie the owner of activemq folder:

```bash
#!/bin/bash
sudo chown -R archie /opt/apache/activemq
```

Install Systemd service unit file:

```bash
#!/bin/bash
sudo cp src/setup/systemd/activemq.service /etc/systemd/system
sudo systemctl enable activemq
sudo systemctl start activemq
```

Test ActiveMQ installation:

```bash
#!/bin/bash
systemctl status activemq
sudo jps -l
```

* Open browser and go to [administration console](http://127.0.0.1:8161/admin/)

## Install and configure Apache HTTPd

Install HTTPd package:

```bash
#!/bin/bash
sudo yum install httpd
```

Copy virtual host file to configuration folder:

```bash
#!/bin/bash
sudo cp src/setup/httpd/archie.enhabsor.virtual-host.conf /etc/httpd/conf.d
```

Set the correct ServerName in /etc/httpd/conf.d/archie.enhabsor.virtual-host.conf

Start HTTPd service:

```bash
#!/bin/bash
sudo systemctl enable httpd
sudo systemctl start httpd
```
## Build and deploy Archie core

```bash
#!/bin/bash
mvn clean install
sudo mv target/archie-enhabsor-core-${version}/* /opt/hilel14/archie/enhabsor/lib/
```

Copy configuration files to application folder:

```bash
#!/bin/bash
sudo cp ~/archie-enhabsor/api/src/setup/archie/jobs-consumer.sh /opt/hilel14/archie/enhabsor/bin
sudo cp ~/archie-enhabsor/api/src/setup/archie/users-admin.sh /opt/hilel14/archie/enhabsor/bin
sudo chown -R archie /opt/hilel14/archie
```

Test Archie API installation:

```bash
#!/bin/bash
su -c "/opt/hilel14/archie/enhabsor/bin/jobs-consumer.sh" archie
```

Install Systemd service unit file:

```bash
#!/bin/bash
sudo cp src/setup/archie/archie-enhabsor.service  /etc/systemd/system
sudo systemctl enable archie-enhabsor
sudo systemctl start archie-enhabsor
```

## Build and deploy Archie GUI

