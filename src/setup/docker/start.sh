#!/bin/bash
service apache2 start
service mysql start
runuser -u archie -- /opt/apache/solr/bin/solr start
runuser -u archie -- /opt/apache/activemq/bin/activemq start
runuser -u archie -- /opt/hilel14/archie/enhabsor/bin/start-grizzly-server.sh
/bin/bash