mkdir -p /opt/apache
tar xf ./download/activemq-5.16.0-bin.tar.gz
mv ./apache-activemq-5.16.0 /opt/apache/activemq
chown -R www-data /opt/apache/activemq
