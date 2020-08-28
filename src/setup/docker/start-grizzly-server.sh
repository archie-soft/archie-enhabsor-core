#!/bin/sh

APP_HOME=`dirname $0`
APP_HOME=`dirname $APP_HOME`
echo $APP_HOME

java \
-cp "$APP_HOME/resources/:$APP_HOME/lib/*" \
org.hilel14.archie.enhabsor.core.ws.ArchieServer &

echo $! > /var/opt/archie/enhabsor/logs/grizzly.pid