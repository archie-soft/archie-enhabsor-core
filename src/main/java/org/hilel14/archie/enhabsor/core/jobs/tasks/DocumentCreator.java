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
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFolderForm;

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
        ImportFolderForm form = ticket.getImportFolderForm();
        if (form.getIsadFonds() != null) {
            doc.addField("isadFonds", form.getIsadFonds());
        }
        if (form.getIsadSubFonds() != null) {
            doc.addField("isadSubFonds", form.getIsadSubFonds());
        }
        if (form.getIsadSeries() != null) {
            doc.addField("isadSeries", form.getIsadSeries());
        }
        if (form.getIsadFile() != null) {
            doc.addField("isadFile", form.getIsadFile());
        }
        doc.addField("dcAccessRights", form.getDcAccessRights());
        if (form.getDcTitle() != null) {
            doc.addField("dcTitle", form.getDcTitle());
        }
        if (form.getDcCreator() != null) {
            doc.addField("dcCreator", form.getDcCreator());
        }
        if (form.getDcDateStart() != null) {
            doc.addField("dcDateStart", form.getDcDateStart());
        }
        if (form.getDcDateEnd() != null) {
            doc.addField("dcDateEnd", form.getDcDateEnd());
        }
        if (form.getDcDescription() != null) {
            doc.addField("dcDescription", form.getDcDescription());
        }
        doc.addField("dcType", form.getDcType());
        if (form.getDcSource() != null) {
            doc.addField("dcSource", form.getDcSource());
        }
        if (form.getDcIdentifier() != null) {
            doc.addField("dcIdentifier", form.getDcIdentifier());
        }
        doc.addField("localStoragePermanent", form.isLocalStoragePermanent());
        if (form.getLocalStorageCabinet() != null) {
            doc.addField("localStorageCabinet", form.getLocalStorageCabinet());
        }
        if (form.getLocalStorageShelf() != null) {
            doc.addField("localStorageShelf", form.getLocalStorageShelf());
        }
        if (form.getLocalStorageContainer() != null) {
            doc.addField("localStorageContainer", form.getLocalStorageContainer());
        }
        addFileNames(ticket.getBaseName(), form, doc);
        config.getSolrClient().add(doc);
        config.getSolrClient().commit();
    }

    private void addFileNames(String baseFileName, ImportFolderForm form, SolrInputDocument doc) {
        if (form.getAddFileNamesTo() == null) {
            return;
        }
        switch (form.getAddFileNamesTo()) {
            case "dcTitle":
                if (form.getDcTitle() == null) {
                    doc.addField("dcTitle", baseFileName);
                } else {
                    String dcTitle = form.getDcTitle().trim() + " " + baseFileName;
                    doc.getField("dcTitle").setValue(dcTitle);
                }
                break;
            case "dcDescription":
                if (form.getDcDescription() == null) {
                    doc.addField("dcDescription", baseFileName);
                } else {
                    String dcDescription = form.getDcDescription().trim() + " " + baseFileName;
                    doc.getField("dcDescription").setValue(dcDescription);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown addFileNamesTo field: " + form.getAddFileNamesTo());
        }
    }

}
