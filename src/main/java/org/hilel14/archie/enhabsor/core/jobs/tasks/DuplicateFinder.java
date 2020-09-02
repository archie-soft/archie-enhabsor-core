package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Path;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class DuplicateFinder {

    static final Logger LOGGER = LoggerFactory.getLogger(DuplicateFinder.class);
    final Config config;
    final ContentExtractor next;

    public DuplicateFinder(Config config) throws Exception {
        this.config = config;
        this.next = new ContentExtractor(config);
    }

    public void process(ImportFileTicket ticket, Path path) throws Exception {
        LOGGER.debug("Checking if file {} already exist", ticket.getFileName());
        SolrQuery query = new SolrQuery();
        query.set("q", "fileDigest:" + ticket.getFileDigest());
        query.setFields("id");
        QueryResponse response = config.getSolrClient().query(query);
        SolrDocumentList list = response.getResults();
        if (list.getNumFound() > 0) {
            Object id = list.get(0).getFieldValue("id");
            LOGGER.warn("Digest {} of file {} already exist in document {}",
                    ticket.getFileDigest(), ticket.getFileName(), id);
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("duplicate of " + id);
            return;
        }
        next.process(ticket, path);
    }

}
