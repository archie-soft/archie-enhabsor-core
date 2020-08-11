package org.hilel14.archie.enhabsor.core.jobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ArchieItem;

/**
 *
 * @author hilel14
 */
public class UpdateDocumentsJob {

    static final Logger LOGGER = LoggerFactory.getLogger(UpdateDocumentsJob.class);
    final Config config;
    final DateFormat iso8601TimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    final String[] previewFileNameExtentions = new String[]{"png", "txt"};

    public UpdateDocumentsJob(Config config) {
        this.config = config;
    }

    public void run(List<ArchieItem> items) throws Exception {
        for (ArchieItem item : items) {
            update(item, config.getSolrClient());
        }
        config.getSolrClient().commit();
    }

    public void runCsv(Reader in) throws Exception {
        LOGGER.info("Updating Solr index from CSV file...");
        int count = 0;
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            count++;
            ArchieItem item = ArchieItem.fromMap(record.toMap());
            update(item, config.getSolrClient());
        }
        LOGGER.info("Commiting changes to {} documents", count);
        config.getSolrClient().commit();
        LOGGER.info("The operation completed successfully");
    }

    public void update(ArchieItem item, SolrClient solrClient)
            throws Exception {
        // Create Solr Document
        SolrInputDocument doc = item.toSolrUpdate();
        if (item.containsKey("dcFormat")) {
            moveFiles(item);
        }
        // add to solr
        solrClient.add(doc);
    }

    private void moveFiles(ArchieItem item) throws Exception {
        String sourceRepository = findSourceRepository("originals", item.getOriginalFileName());
        if (sourceRepository.isEmpty()) {
            return;
        }
        String targetRepository = item.get("dcAccessRights").toString();
        if (sourceRepository.equalsIgnoreCase(targetRepository)) {
            return;
        }
        LOGGER.debug("Moving originals/{} from {} to {}", item.getOriginalFileName(), sourceRepository, targetRepository);
        config.getStorageConnector().move(sourceRepository, targetRepository, "originals", item.getOriginalFileName());
        // thumbnails
        if (config.getStorageConnector().exist(sourceRepository, "thumbnails", item.getThumbnailFileName())) {
            config.getStorageConnector().move(sourceRepository, targetRepository, "thumbnails", item.getThumbnailFileName());
        }
        // text
        if (config.getStorageConnector().exist(sourceRepository, "text", item.getTextFileName())) {
            config.getStorageConnector().move(sourceRepository, targetRepository, "text", item.getTextFileName());
        }
    }

    private String findSourceRepository(String container, String file) {
        for (String repository : config.getRepositories()) {
            if (config.getStorageConnector().exist(repository, container, file)) {
                return repository;
            }
        }
        return "";
    }
}
