#!/bin/sh
service apache2 start
service mysql start
runuser -u www-data -- /opt/apache/solr/bin/solr start
runuser -u www-data -- /opt/apache/activemq/bin/activemq start
/bin/bash