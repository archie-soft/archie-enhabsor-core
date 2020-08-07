service mysql start
mysqladmin -u root create enhabsor
cat ./archie.mariadb.sql | mysql -u root enhabsor
mysql -u root -e "GRANT ALL ON enhabsor.* TO 'archie'@'%' IDENTIFIED BY '1234'"
