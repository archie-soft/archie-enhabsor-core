package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class DigestCalculator implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(DigestCalculator.class);
    final Config config;

    public DigestCalculator(Config config) {
        this.config = config;
    }

    @Override
    public void process(ImportFileTicket ticket, Path path) throws Exception {
        LOGGER.debug("Calculating digest for file {}", ticket.getFileName());
        byte[] data = Files.readAllBytes(path);
        ticket.setFileDigest(DigestUtils.md5Hex(data));
    }

}
