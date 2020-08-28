mkdir -p /opt/hilel14/archie/enhabsor

for d in bin lib resources; do
    mkdir /opt/hilel14/archie/enhabsor/$d
done

chown -R www-data /opt/hilel14/archie/enhabsor

mkdir /var/www/archie/
