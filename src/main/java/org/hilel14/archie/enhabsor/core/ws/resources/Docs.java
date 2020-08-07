package org.hilel14.archie.enhabsor.core.ws.resources;

import org.hilel14.archie.enhabsor.core.ws.JmsProducer;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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
import org.hilel14.archie.enhabsor.core.jobs.model.ArchieDocument;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFolderForm;

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
    @RolesAllowed("manager")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getFolders() throws Exception {
        return config.getStorageConnector().listFolders("import", "");
    }

    @POST
    @PermitAll
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("folder")
    //public int importFolder(ImportAttributes importAttributes) throws Exception {
    public int importFolder(ImportFolderForm importAttributes) throws Exception {
        LOGGER.debug("Importing files from {}", importAttributes.getFolderName());
        jmsProducer.produceJsonMessage(importAttributes, "import-folder");
        return 0;
    }

    @PUT
    @RolesAllowed("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateDocs(List<ArchieDocument> docs) throws Exception {
        LOGGER.debug("Updating {} documents", docs.size());
        if (docs.size() == 1) {
            UpdateDocumentsJob job = new UpdateDocumentsJob(config);
            job.run(docs);
        } else {
            jmsProducer.produceJsonMessage(docs, "update-documents");
        }
    }

    @DELETE
    @RolesAllowed("manager")
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
