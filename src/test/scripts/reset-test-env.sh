#!/bin/bash

# Exit on first error
set -e

# delete assets and work files
rm -rf /var/opt/archie/enhabsor/work/import/*
find /var/opt/archie/enhabsor/assetstore -type f -exec rm -rf {} \;

# copy sample files to import folder
mkdir -p /var/opt/archie/enhabsor/import/folder-1
cp src/test/resources/data/folder-1/* /var/opt/archie/enhabsor/import/folder-1/

# delete solr documents
curl http://localhost:8983/solr/enhabsor/update --data '<delete><query>*:*</query></delete>' -H 'Content-type:text/xml; charset=utf-8'
curl http://localhost:8983/solr/enhabsor/update --data '<commit/>' -H 'Content-type:text/xml; charset=utf-8'
