#!/bin/sh

# Exit on first error
set -e

# delete assets and work files
rm -rf /var/opt/archie/enhabsor/work/*
find /var/opt/archie/enhabsor/assetstore -type f -exec rm -rf {} \;

# recreate solr collection
runuser -u www-data -- /opt/apache/solr/bin/solr delete -c enhabsor
runuser -u www-data -- /opt/apache/solr/bin/solr create -c enhabsor
curl http://localhost:8983/solr/enhabsor/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
curl http://localhost:8983/solr/enhabsor/config -H 'Content-type:application/json' -d @./archie.solr.config.json
curl http://localhost:8983/solr/enhabsor/schema -H 'Content-type:application/json' -d @./archie.solr.schema.json
