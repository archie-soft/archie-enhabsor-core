#!/bin/bash

function setup {
    mkdir -p /opt/hilel14/archie/enhabsor
    mkdir /var/opt/maven
    mvn deploy
    mv target/lib/ /opt/hilel14/archie/enhabsor/
    cp -R src/main/scripts/ /opt/hilel14/archie/enhabsor/bin
    cp -R src/main/resources/ /opt/hilel14/archie/enhabsor/
    chown -R archie /opt/hilel14/archie/enhabsor
}

function update {
    mvn clean install
    mv target/archie-enhabsor-core*.jar /opt/hilel14/archie/enhabsor/lib
}
