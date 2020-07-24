## Requirements
* Download Apache Solr and save in download folder as solr.tgz
* Download Apache ActiveMQ and save in download folder as activemq.tar. gz

## Build
sudo docker build --rm --tag local/archie.enhabsor:2 .

## Run
sudo docker run  --interactive --detach --tty --name=archie.enhabsor.2 local/archie.enhabsor:2

## Connect
sudo docker exec -it archie.enhabsor.2 /bin/bash

## Optional configuration
* Edit /etc/mysql/mariadb.conf.d/50-server.cnf : set bind-address to 0.0.0.0
* Edit /opt/apache/activemq/conf/jetty.xml > bean id="jettyPort" >  property name="host" : set value to 0.0.0.0

## Test
Replace 172.17.0.2 with real container ip
* http://172.17.0.2
* http://172.17.0.2:8983/solr
* http://172.17.0.2:8161/admin/ (user: admin, password: admin)