mkdir /var/www/archie/
git clone https://github.com/archie-soft/archie-enhabsor-ui.git
cd archie-enhabsor-ui
npm install
npm install -g @angular/cli
ng build --prod --base-href /
mv dist/archie-enhabsor-ui /var/www/archie/enhabsor
chown -R 0.0 /var/www/archie/