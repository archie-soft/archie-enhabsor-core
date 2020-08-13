# Setup 

cd src/setup/docker/
tar xvf download/solr-7.7.3.tgz
cd solr-7.7.3/

bin/solr start
bin/solr create -c enhabsor
curl http://localhost:8983/solr/enhabsor/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
curl http://localhost:8983/solr/enhabsor/config -H 'Content-type:application/json' -d @./archie.solr.config.json
curl http://localhost:8983/solr/enhabsor/schema -H 'Content-type:application/json' -d @./archie.solr.schema.json
bin/solr stop

SOLR_HOME=src/test/resources/server
mkdir $SOLR_HOME
mv server/solr/solr.xml $SOLR_HOME
mkdir $SOLR_HOME/books
mv server/solr/books/conf $SOLR_HOME/books
mv server/solr/books/core.properties $SOLR_HOME/books
echo "solr.data.dir=/tmp/solr-test" >> $SOLR_HOME/books/core.properties

