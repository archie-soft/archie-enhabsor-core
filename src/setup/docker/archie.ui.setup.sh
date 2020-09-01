#!/bin/bash

function setup {
    mkdir /var/www/archie/
    npm install
    npm install -g @angular/cli
}

function update {
    ng build --prod --base-href /
    rm -rf /var/www/archie/enhabsor
    mv dist/archie-enhabsor-ui /var/www/archie/enhabsor
    chown -R 0.0 /var/www/archie/
}