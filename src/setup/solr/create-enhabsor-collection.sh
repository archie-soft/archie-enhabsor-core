#!/bin/sh
set -e
#su -c "/opt/apache/solr/bin/solr delete -c archie_enhabsor" archie
#su -c "/opt/apache/solr/bin/solr create -c archie_enhabsor" archie
CONFIG_FOLDER=`dirname $0`
curl http://localhost:8983/solr/archie_enhabsor/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
curl http://localhost:8983/solr/archie_enhabsor/config -H 'Content-type:application/json' -d @$CONFIG_FOLDER/solr.enhabsor.config.json
curl http://localhost:8983/solr/archie_enhabsor/schema -H 'Content-type:application/json' -d @$CONFIG_FOLDER/solr.enhabsor.schema.json
