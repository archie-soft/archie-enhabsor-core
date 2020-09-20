#!/bin/bash

APP_HOME=`dirname $0`
APP_HOME=`dirname $APP_HOME`
echo $APP_HOME

export LC_ALL=en_US.UTF-8

java \
-cp "$APP_HOME/resources/:$APP_HOME/lib/*" \
-Duser.timezone="Asia/Jerusalem" \
-Djava.util.logging.config.file=$APP_HOME/resources/logging.properties \
org.hilel14.archie.enhabsor.core.ws.GrizzlyServer &

echo $! > /var/opt/archie/enhabsor/logs/grizzly.pid