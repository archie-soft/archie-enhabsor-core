package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.solr.common.SolrInputDocument;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ArchieItem;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;

/**
 *
 * @author hilel14
 */
public class DocumentCreator {

    static final Logger LOGGER = LoggerFactory.getLogger(DocumentCreator.class);
    final Config config;
    final FileInstaller next;
    final DateFormat iso8601TimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public DocumentCreator(Config config) {
        this.config = config;
        this.next = new FileInstaller(config);
    }

    public void process(ImportFileTicket ticket, Path path) throws Exception {
        LOGGER.debug("Adding {} to Solr", ticket.getFileName());
        ArchieItem item = new ArchieItem();
        item.setId(ticket.getUuid());
        item.put("importTime", iso8601TimeFormat.format(Calendar.getInstance().getTime()));
        item.putAll(ticket.getImportFolderForm().getItemAttributes());
        SolrInputDocument doc = item.toSolrCreate();
        config.getSolrClient().add(doc);
        config.getSolrClient().commit();
        next.process(ticket, path);
    }

}
