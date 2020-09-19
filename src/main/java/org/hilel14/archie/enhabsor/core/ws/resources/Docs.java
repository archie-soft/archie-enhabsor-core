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
    final JmsProducer jmsProducer;

    public Docs(@Context Configuration configuration) throws Exception {
        this.config = (Config) configuration.getProperty("archie.config");
        this.jmsProducer = new JmsProducer(config.getJmsFactory(), config.getJmsQueueName());
    }

    @GET
    @Path("folders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getFolders() throws Exception {
        return config.getStorageConnector().listFolders("import", "");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("folder")
    public int importFolder(ImportFolderForm form) throws Exception {
        LOGGER.debug("Importing files from {}", form.getFolderName());
        jmsProducer.produceJsonMessage(form, "import-folder");
        return 0;
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
    public void deleteDocs(List<String> docs) throws Exception {
        LOGGER.debug("Deleting {} documents", docs.size());
        jmsProducer.produceJsonMessage(docs, "delete-documents");
    }

    @DELETE
    @Path("delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteDoc(@PathParam("id") String id) throws Exception {
        LOGGER.debug("Deleting document {}", id);
        List<String> docs = new ArrayList<>();
        docs.add(id);
        new DeleteDocumentsJob(config).run(docs);
    }

}
