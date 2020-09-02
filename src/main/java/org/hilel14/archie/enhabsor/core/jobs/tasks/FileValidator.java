package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class FileValidator {

    static final Logger LOGGER = LoggerFactory.getLogger(FileValidator.class);
    final Pattern startsWithWordCharacter = Pattern.compile("[a-zA-Z_0-9_א-ת].+");
    final Config config;
    final DigestCalculator next;

    public FileValidator(Config config) throws Exception {
        this.config = config;
        this.next = new DigestCalculator(config);
    }

    public void process(ImportFileTicket ticket, Path path) throws Exception {
        LOGGER.debug("Validating file {}", ticket.getFileName());
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("File not found");
            return;
        }
        if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("Not a regular file");
            return;
        }
        if (!startsWithWordCharacter.matcher(ticket.getFileName()).matches()) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("File name not starting with word character");
            return;
        }
        String extension = FilenameUtils.getExtension(ticket.getFileName());
        if (extension.trim().isEmpty()) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("File name without extention");
            return;
        }
        if (!config.getValidFileFormats().contains(extension.toLowerCase())) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("Unknown file name extention: " + extension);
            return;
        }
        // call next processor
        next.process(ticket, path);
    }
}
