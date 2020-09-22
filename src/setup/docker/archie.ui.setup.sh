#!/bin/bash

# cd to root of angular project
# run npm install

ng build --prod --base-href /
rm -rf /var/www/archie/enhabsor/*
mv dist/archie-enhabsor-ui/* /var/www/archie/enhabsor

