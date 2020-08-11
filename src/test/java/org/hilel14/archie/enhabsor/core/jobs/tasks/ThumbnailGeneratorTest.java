package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
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
     */
    @Test
    public void testProccess() throws Exception {
        LOGGER.info("Testing process method of class ThumbnailGenerator");
        // init test env
        Config config = TestimSetup.initTestEvn();
        // Create ImportFolderForm and ImportFileTicket
        String fileName = "dog.jpg";
        String attributes;
        try (InputStream in = ThumbnailGeneratorTest.class.getResourceAsStream("/data/folder-1.json");) {
            attributes = new String(in.readAllBytes(), Charset.forName("utf-8"));
        }
        ImportFolderForm form = ImportFolderForm.unmarshal(attributes);
        ImportFileTicket ticket = new ImportFileTicket(fileName, form);
        // init work folder
        Path original = config.getWorkFolder().resolve(form.getFolderName()).resolve(fileName);
        Files.createDirectories(original.getParent());
        try (InputStream in = ThumbnailGeneratorTest.class.getResourceAsStream("/data/folder-1/dog.jpg");) {
            Files.write(original, in.readAllBytes(), StandardOpenOption.CREATE);
            in.readAllBytes();
        }
        // perform the test
        ThumbnailGenerator instance = new ThumbnailGenerator(config);
        instance.process(ticket, original);
        Path target = original.getParent().resolve(ticket.getUuid() + ".png");
        assertTrue(Files.exists(target));
    }

}
