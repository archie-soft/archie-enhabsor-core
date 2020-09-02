package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.io.FileInputStream;
import java.io.InputStream;
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
public class DigestCalculator {

    static final Logger LOGGER = LoggerFactory.getLogger(DigestCalculator.class);
    final Config config;
    final DuplicateFinder next;

    public DigestCalculator(Config config) throws Exception {
        this.config = config;
        this.next = new DuplicateFinder(config);
    }

    public void process(ImportFileTicket ticket, Path path) throws Exception {
        LOGGER.debug("Calculating digest for file {}", ticket.getFileName());
        try (InputStream in = new FileInputStream(path.toFile())) {
            String digest = DigestUtils.md5Hex(in);
            ticket.setFileDigest(digest);
        }
        // call next calculator
        next.process(ticket, path);
    }

}
