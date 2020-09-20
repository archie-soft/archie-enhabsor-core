package org.hilel14.archie.enhabsor.core.ws.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.DeleteDocumentsJob;
import org.hilel14.archie.enhabsor.core.jobs.UpdateDocumentsJob;
import org.hilel14.archie.enhabsor.core.jobs.model.ArchieItem;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFolderForm;
import org.hilel14.archie.enhabsor.core.ws.JmsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Documents and files related operations
 *
 * @author hilel14
 */
@Path("docs")
public class Docs {

    static final Logger LOGGER = LoggerFactory.getLogger(Docs.class);
    final Config config;
    JmsProducer jmsProducer = null;

    public Docs(@Context Configuration configuration) {
        this.config = (Config) configuration.getProperty("archie.config");
        try {
            this.jmsProducer = new JmsProducer(config.getJmsFactory(), config.getJmsQueueName());
        } catch (Exception ex) {
            LOGGER.error("Docs class initialization failed!", ex);
        }
    }

    @GET
    @Path("folders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getFolders() {
        List<String> folders = null;
        try {
            folders = config.getStorageConnector().listFolders("import", "");
        } catch (Exception ex) {
            LOGGER.error("List folders operation failed!", ex);
        }
        return folders;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("folder")
    public void importFolder(ImportFolderForm form) {
        LOGGER.debug("Importing files from {}", form.getFolderName());
        try {
            jmsProducer.produceJsonMessage(form, "import-folder");
        } catch (Exception ex) {
            LOGGER.error("Import folder operation failed!", ex);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateDocs(List<ArchieItem> items) {
        LOGGER.debug("Updating {} documents", items.size());
        try {
            if (items.size() == 1) {
                UpdateDocumentsJob job = new UpdateDocumentsJob(config);
                job.run(items);
                LOGGER.info("Update operation completed successfully");
            } else {
                jmsProducer.produceJsonMessage(items, "update-documents");
            }
        } catch (Exception ex) {
            LOGGER.error("Update operation failed!", ex);
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteDocs(List<String> docs) {
        LOGGER.debug("Deleting {} documents", docs.size());
        try {
            jmsProducer.produceJsonMessage(docs, "delete-documents");
        } catch (Exception ex) {
            LOGGER.error("Delete docs operation failed!", ex);
        }
    }

    @DELETE
    @Path("delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteDoc(@PathParam("id") String id) {
        LOGGER.debug("Deleting document {}", id);
        List<String> docs = new ArrayList<>();
        docs.add(id);
        try {
            new DeleteDocumentsJob(config).run(docs);
        } catch (Exception ex) {
            LOGGER.error("Delete doc operation failed!", ex);
        }
    }
}
