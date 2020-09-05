#!/bin/bash

set -e

APP_HOME=`dirname $0`
APP_HOME=`dirname $APP_HOME`

java \
-cp $APP_HOME/resources:$APP_HOME/lib/* \
-Duser.timezone="Asia/Jerusalem" \
org.hilel14.archie.enhabsor.core.jobs.ImportFolderJob "$@"

# cp -R src/test/resources/data/folder-1 /var/opt/archie/enhabsor/import/
# /opt/hilel14/archie/enhabsor/bin/import-folder.sh -i src/test/resources/data/folder-1.json 
