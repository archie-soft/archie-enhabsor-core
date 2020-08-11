package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
        // prepare test
        Config config = TestimSetup.initTestEvn();
        ThumbnailGenerator instance = new ThumbnailGenerator(config);
        // Create ImportFolderForm
        String attributes;
        try (InputStream in = ThumbnailGeneratorTest.class.getResourceAsStream("/data/folder-1.json");) {
            attributes = new String(in.readAllBytes(), Charset.forName("utf-8"));
        }
        ImportFolderForm form = ImportFolderForm.unmarshal(attributes);
        // init work folder
        Path workFolder = config.getWorkFolder().resolve(form.getFolderName());
        Files.createDirectory(workFolder);
        copyToWorkFolder(workFolder, "dog.jpg");
        copyToWorkFolder(workFolder, "git.pdf");
        // jpg test
        String fileName = "dog.jpg";
        ImportFileTicket ticket = new ImportFileTicket(fileName, form);
        Path original = workFolder.resolve(fileName);
        instance.process(ticket, original);
        Path target = original.getParent().resolve(ticket.getUuid() + ".png");
        assertTrue(Files.exists(target));
        // pdf test
        fileName = "git.pdf";
        ticket = new ImportFileTicket(fileName, form);
        original = workFolder.resolve(fileName);
        instance.process(ticket, original);
        target = original.getParent().resolve(ticket.getUuid() + ".png");
        assertTrue(Files.exists(target));
    }

    private void copyToWorkFolder(Path importFolder, String fileName) throws IOException {
        try (InputStream in = ThumbnailGeneratorTest.class.getResourceAsStream("/data/folder-1/" + fileName);) {
            Path target = importFolder.resolve(fileName);
            Files.write(target, in.readAllBytes(), StandardOpenOption.CREATE);
        }
    }

}
