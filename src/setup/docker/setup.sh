#!/bin/sh
# Exit on first error
set -e
# config components
exec ./config/packages.sh
exec ./config/asset-store.sh
exec ./config/solr.sh
exec ./config/activemq.sh
exec ./config/mariadb.sh  
exec ./config/apache2.sh
exec ./config/archie-core.sh
exec ./config/archie-ui.sh
# additional configuration
ln --symbolic --force /usr/share/zoneinfo/Asia/Jerusalem  /etc/localtime