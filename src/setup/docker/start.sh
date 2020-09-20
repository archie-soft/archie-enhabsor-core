#!/bin/bash
service apache2 start
service mysql start
runuser -u archie -- /opt/apache/solr/bin/solr start
runuser -u archie -- /opt/apache/activemq/bin/activemq start
sleep 10
#runuser -u archie -- /opt/hilel14/archie/enhabsor/bin/start-grizzly-server.sh
#runuser -u archie -- /opt/hilel14/archie/enhabsor/bin/start-jobs-consumer.sh
/bin/bash