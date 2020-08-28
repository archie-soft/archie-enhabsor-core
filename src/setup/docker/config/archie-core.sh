mkdir -p /opt/hilel14/archie/enhabsor

for d in bin lib resources; do
    mkdir /opt/hilel14/archie/enhabsor/$d
done

git clone https://github.com/archie-soft/archie-enhabsor-core.git
cd archie-enhabsor-corecd archie-enhabsor-core
mvn clean install

mvn dependency:copy-dependencies