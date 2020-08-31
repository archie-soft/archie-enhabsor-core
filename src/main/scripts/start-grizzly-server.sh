#!/bin/bash

APP_HOME=`dirname $0`
APP_HOME=`dirname $APP_HOME`
echo $APP_HOME

java \
-cp "$APP_HOME/resources/:$APP_HOME/lib/*" \
-Duser.timezone="Asia/Jerusalem" \
org.hilel14.archie.enhabsor.core.cli.GrizzlyServer &

echo $! > /var/opt/archie/enhabsor/logs/grizzly.pid