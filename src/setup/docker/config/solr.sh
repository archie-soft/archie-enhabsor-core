mkdir -p /opt/apache
tar xf ./download/solr-7.7.3.tgz
mv ./solr-7.7.3 /opt/apache/solr
chown -R www-data /opt/apache/solr
runuser -u www-data -- /opt/apache/solr/bin/solr start
runuser -u www-data -- /opt/apache/solr/bin/solr create -c enhabsor
curl http://localhost:8983/solr/enhabsor/config -d '{"set-user-property": {"update.autoCreateFields":"false"}}'
curl http://localhost:8983/solr/enhabsor/config -H 'Content-type:application/json' -d @./archie.solr.config.json
curl http://localhost:8983/solr/enhabsor/schema -H 'Content-type:application/json' -d @./archie.solr.schema.json
# optionally: restore data from backup snapshot
