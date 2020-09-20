#!/bin/bash

# cd to root of maven project

function setup {
    mvn deploy
    mv target/lib/ /opt/hilel14/archie/enhabsor/
    cp -R src/main/scripts/ /opt/hilel14/archie/enhabsor/bin
    chmod 755 /opt/hilel14/archie/enhabsor/bin/*
    cp -R src/main/resources/ /opt/hilel14/archie/enhabsor/
    cp -R src/test/resources/tessdata /opt/hilel14/archie/enhabsor/resources
}

function update {
    kill `cat /var/opt/archie/enhabsor/logs/grizzly.pid`
    kill `cat /var/opt/archie/enhabsor/logs/jobs-consumer.pid`
    mvn clean install -DskipTests
    mv target/archie-enhabsor-core*.jar /opt/hilel14/archie/enhabsor/lib
    /opt/hilel14/archie/enhabsor/bin/start-grizzly-server.sh
    /opt/hilel14/archie/enhabsor/bin/start-jobs-consumer.sh
}

case "$1" in
    setup)
        echo setup
    ;;
    
    update)
        echo update
    ;;
    
    *)
        echo $"Usage: $0 {setup|update}"
        exit 1
esac