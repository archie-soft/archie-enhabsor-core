package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.solr.common.SolrInputDocument;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;

/**
 *
 * @author hilel14
 */
public class DocumentCreator implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(DocumentCreator.class);
    final Config config;
    final DateFormat iso8601TimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public DocumentCreator(Config config) {
        this.config = config;
    }

    @Override
    public void process(ImportFileTicket ticket, Path path) throws Exception {
        LOGGER.debug("Adding {} to Solr", ticket.getFileName());
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", ticket.getUuid());
        doc.addField("importTime", iso8601TimeFormat.format(Calendar.getInstance().getTime()));
        doc.addField("dcFormat", ticket.getFormat());
        doc.addField("fileDigest", ticket.getFileDigest());
        if (ticket.getContent() != null) {
            doc.addField("content", ticket.getContent());
        }
        Map<String, Object> item = ticket.getImportFolderForm().getItemAttributes();
        item.keySet().forEach(key -> {
            doc.addField(key, item.get(key)
            );
        });
        addFileNames(ticket, doc);
        config.getSolrClient().add(doc);
        config.getSolrClient().commit();
    }

    private void addFileNames(ImportFileTicket ticket, SolrInputDocument doc) {
        String addFileNamesTo = ticket.getImportFolderForm().getAddFileNamesTo();
        if (addFileNamesTo == null) {
            return;
        }
        Map<String, Object> item = ticket.getImportFolderForm().getItemAttributes();
        switch (addFileNamesTo) {
            case "dcTitle":
                if (item.get("dcTitle") == null) {
                    doc.addField("dcTitle", ticket.getBaseName());
                } else {
                    String dcTitle = item.get("dcTitle").toString().trim() + " " + ticket.getBaseName();
                    doc.getField("dcTitle").setValue(dcTitle);
                }
                break;
            case "dcDescription":
                if (item.get("dcDescription") == null) {
                    doc.addField("dcDescription", ticket.getBaseName());
                } else {
                    String dcDescription = item.get("dcDescription").toString().trim() + " " + ticket.getBaseName();
                    doc.getField("dcDescription").setValue(dcDescription);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown addFileNamesTo field: " + addFileNamesTo);
        }
    }

}
