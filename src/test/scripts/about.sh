mvn clean install
mvn dependency:copy-dependencies
java -cp target/dependency/*:target/archie-enhabsor-core-1.0.0-SNAPSHOT.jar org.hilel14.archie.enhabsor.core.About
