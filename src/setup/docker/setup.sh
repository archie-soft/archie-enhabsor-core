#!/bin/sh
# Exit on first error
set -e
# config components
exec ./config/folder.sh
exec ./config/packages.sh
exec ./config/solr.sh
exec ./config/activemq.sh
exec ./config/mariadb.sh  
exec ./config/apache2.sh