package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;

/**
 *
 * @author hilel14
 */
public class FileInstaller implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(FileInstaller.class);
    final Config config;

    public FileInstaller(Config config) {
        this.config = config;
    }

    @Override
    public void process(ImportFileTicket ticket, Path original) throws Exception {
        String repository = ticket.getImportFolderForm().getDcAccessRights();
        // original
        LOGGER.debug("Uploading file {} to asset-store as {}", ticket.getFileName(),ticket.getAssetName());
        config.getStorageConnector().upload(original, repository, "originals");
        // thumbnail
        Path thumbnail = config.getWorkFolder().resolve("import").resolve(ticket.getUuid() + ".png");
        if (Files.exists(thumbnail)) {
            LOGGER.debug("Uploading thumbnail for file {} to asset-store as {}", ticket.getFileName(), thumbnail.getFileName());
            config.getStorageConnector().upload(thumbnail, repository, "thumbnails");
        }
        // text
        Path text = config.getWorkFolder().resolve("import").resolve(ticket.getUuid() + ".txt");
        if (Files.exists(text)) {
            LOGGER.debug("Uploading text content for file {} to asset-store", ticket.getFileName());
            config.getStorageConnector().upload(text, repository, "text");
        }
        // cleanup
        config.getStorageConnector().delete(
                "import",
                ticket.getImportFolderForm().getFolderName(),
                ticket.getFileName()
        );
    }

}
