## Requirements
* Download Apache Solr and save in download folder
* Download Apache ActiveMQ and save in download folder

## Build
sudo docker build --rm --tag local/archie.enhabsor:2 .

## Run
sudo docker run -idt --name=archie.enhabsor.2 -p 80:80 -p 8983:8983 -p 8161:8161 local/archie.enhabsor:2
-i, --interactive 
-d, --detach 
-t, --tty 
-p, --publish

## Connect
sudo docker exec -it archie.enhabsor.2 /bin/bash

## Optional configuration
* Edit /etc/mysql/mariadb.conf.d/50-server.cnf : set bind-address to 0.0.0.0
* Edit /opt/apache/activemq/conf/jetty.xml > bean id="jettyPort" >  property name="host" : set value to 0.0.0.0

## Test

Proxy
* http://localhost/api
* http://localhost/docs

Docker published ports
* http://localhost:8983/solr
* http://localhost:8161/admin/ (user: admin, password: admin)

Direct access (replace 172.17.0.2 with real container ip)
* http://172.17.0.2
* http://172.17.0.2:8983/solr
* http://172.17.0.2:8161/admin/
* mysql -h 172.17.0.2 -u archie -p enhabsor

## Deploy Angular
ng build --prod --base-href / --i18n-file src/locale/messages.he.xlf --i18n-format xlf --i18n-locale he
sudo docker exec archie.enhabsor.2 rm -rf /var/www/archie/enhabsor
sudo docker cp dist/archie-enhabsor-ui archie.enhabsor.2:/var/www/archie/enhabsor
sudo docker exec archie.enhabsor.2 chown -R 0.0 /var/www/archie/

## Deploy
mvn clean install
sudo docker cp target/archie-enhabsor-core-*.jar archie.enhabsor.2:/opt/hilel14/archie/enhabsor/lib
optional: copy dependencies to lib folder
optional: copy grizzly launch script bin folder