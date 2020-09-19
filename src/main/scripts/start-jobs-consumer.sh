#!/bin/sh

set -e

APP_HOME=`dirname $0`
APP_HOME=`dirname $APP_HOME`

export LC_ALL=en_US.UTF-8

java \
-cp "$APP_HOME/resources/:$APP_HOME/lib/*" \
-Duser.timezone="Asia/Jerusalem" \
org.hilel14.archie.enhabsor.core.jobs.JobsConsumer &

echo $! > /var/opt/archie/enhabsor/logs/jobs-consumer.pid
