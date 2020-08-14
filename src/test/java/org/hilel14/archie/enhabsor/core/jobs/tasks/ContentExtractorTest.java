package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Path;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.TestimSetup;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFolderForm;
import static org.hilel14.archie.enhabsor.core.jobs.tasks.ThumbnailGeneratorTest.LOGGER;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hilel
 */
public class ContentExtractorTest {

    public ContentExtractorTest() {
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
     * Test of process method, of class ContentExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcess() throws Exception {
        LOGGER.info("Testing process method of class ContentExtractor");
        // prepare test env
        Config config = TestimSetup.initTestEvn();
        ContentExtractor instance = new ContentExtractor(config);
        ImportFolderForm form = TestimSetup.getImportFolderForm();
        form.setTextAction("recognize");
        // jpg test
        String fileName = "ganan-gidel-dagan.jpg";
        ImportFileTicket ticket = new ImportFileTicket(fileName, form);
        Path original = config.getWorkFolder().resolve(fileName);
        instance.process(ticket, original);
        assertFalse(ticket.getContent().isEmpty());
        // pdf test
        fileName = "git-cheat-sheet.pdf";
        ticket = new ImportFileTicket(fileName, form);
        original = config.getWorkFolder().resolve(fileName);
        instance.process(ticket, original);
        assertFalse(ticket.getContent().isEmpty());
    }

}
