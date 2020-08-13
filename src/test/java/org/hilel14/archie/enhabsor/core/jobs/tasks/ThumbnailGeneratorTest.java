package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Files;
import java.nio.file.Path;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.TestimSetup;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFolderForm;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel
 */
public class ThumbnailGeneratorTest {

    static final Logger LOGGER = LoggerFactory.getLogger(ThumbnailGeneratorTest.class);

    public ThumbnailGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of process method, of class ThumbnailGenerator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProccess() throws Exception {
        LOGGER.info("Testing process method of class ThumbnailGenerator");
        // prepare test env
        Config config = TestimSetup.initTestEvn();
        ThumbnailGenerator instance = new ThumbnailGenerator(config);
        ImportFolderForm form = TestimSetup.getImportFolderForm();
        // jpg test
        String fileName = "dog.jpg";
        ImportFileTicket ticket = new ImportFileTicket(fileName, form);
        Path original = config.getWorkFolder().resolve(fileName);
        instance.process(ticket, original);
        Path target = original.getParent().resolve(ticket.getUuid() + ".png");
        assertTrue(Files.exists(target));
        // pdf test
        fileName = "git-cheat-sheet.pdf";
        ticket = new ImportFileTicket(fileName, form);
        original = config.getWorkFolder().resolve(fileName);
        instance.process(ticket, original);
        target = original.getParent().resolve(ticket.getUuid() + ".png");
        assertTrue(Files.exists(target));
    }

}
