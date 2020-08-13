#!/bin/sh

# wget solr-7.7.3.tgz
tar xvf solr-7.7.3.tgz
cd solr-7.7.3/
bin/solr start
bin/solr create -c enhabsor
curl http://localhost:8983/solr/enhabsor/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
curl http://localhost:8983/solr/enhabsor/config -H 'Content-type:application/json' -d @./archie.solr.config.json
curl http://localhost:8983/solr/enhabsor/schema -H 'Content-type:application/json' -d @./archie.solr.schema.json
bin/solr stop
