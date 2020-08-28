mkdir -p /var/opt/archie/enhabsor

for d in assetstore import logs work; do
    mkdir /var/opt/archie/enhabsor/$d
done

for d1 in public private secret; do
    mkdir /var/opt/archie/enhabsor/assetstore/$d1
    for d2 in originals thumbnails text; do
        mkdir /var/opt/archie/enhabsor/assetstore/$d1/$d2
    done
done

chown -R www-data /var/opt/archie/enhabsor