## Requirements
* Download Apache Solr and save in download folder as solr.tgz
* Download Apache ActiveMQ and save in download folder as activemq.tar. gz

## Build
sudo docker build --rm --tag local/archie.enhabsor:2 .

## Run
sudo docker run  --interactive --detach --tty --name=archie.enhabsor.2 local/archie.enhabsor:1

## Connect
sudo docker exec -it archie-enhabsor /bin/bash

## Manual configuration
* Edit /etc/mysql/mariadb.conf.d/50-server.cnf : set bind-address to 0.0.0.0
* Create db and user
  mysqladmin -u root create archiedb
  mysql -u root -e "GRANT ALL ON archiedb.* to 'archie'@'%' IDENTIFIED BY '1234'"
