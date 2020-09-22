Archie EnHabsor setup

# Docker

## Requirements
* Download Apache Solr and save in download folder
* Download Apache ActiveMQ and save in download folder

## Build
docker build --rm --tag local/archie.enhabsor:2 .

## Run

Development

<pre>
docker run --interactive --detach --tty --mount type=bind,source=/home/hilel/Projects/archie-soft,target=/home/archie/archie-soft --name=archie.enhabsor.2 --publish 80:80 --publish 8983:8983 --publish 8161:8161 --publish 4200:4200 local/archie.enhabsor:2
</pre>

Production

<pre>
docker run -idt --mount type=bind,source=/var/opt/archie/enhabsor/,target=/var/opt/archie/enhabsor/ --name=archie.enhabsor.2 --publish 80:80 local/archie.enhabsor:2
</pre>

## Connect
docker exec -it archie.enhabsor.2 /bin/bash

## Optional configuration
* Edit /etc/mysql/mariadb.conf.d/50-server.cnf : set bind-address to 0.0.0.0

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

## Java notes

Test
<pre>
/opt/hilel14/archie/enhabsor/bin/start-grizzly-server.sh
curl http://localhost:8080/archie-enhabsor-ws/about
</pre>

## Angular notes

Development

* Make sure port 4200 is published
* Add the relevant Access-Control headers to Apache2 configuration?
* Run inside docker container
<pre>
ng serve --host=0.0.0.0
</pre>

Production
<pre>
ng build --prod --base-href /
rm -rf /var/www/archie/enhabsor
mv dist/archie-enhabsor-ui /var/www/archie/enhabsor
</pre>

# General Notes

## Solr schema
* localTextActionCode: extract, recognize, extracted, recognized
