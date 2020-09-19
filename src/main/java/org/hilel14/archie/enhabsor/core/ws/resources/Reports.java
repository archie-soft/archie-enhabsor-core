package org.hilel14.archie.enhabsor.core.ws.resources;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.reports.ImportFolderRecord;
import org.hilel14.archie.enhabsor.core.reports.ReportGenerator;

@Path("reports")
public class Reports {
    final Config config;
    final ReportGenerator reporter;

    public Reports(@Context Configuration configuration) throws Exception {
        this.config = (Config) configuration.getProperty("archie.config");
        reporter = new ReportGenerator(config.getDataSource());
    }

    @GET
    @Path("import-folders")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<ImportFolderRecord> getImportFolders() throws SQLException {
        return reporter.getImportFolders();
    }

    @GET
    @Path("import-folder/{id}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public ImportFolderRecord getImportFolder(@PathParam("id") long importFolderId) throws SQLException {
        return reporter.getImportFolder(importFolderId);
    }
    
}
