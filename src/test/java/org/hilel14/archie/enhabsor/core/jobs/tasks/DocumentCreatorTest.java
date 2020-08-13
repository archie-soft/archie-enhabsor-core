package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Path;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;
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
public class DocumentCreatorTest {
    
    public DocumentCreatorTest() {
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
     * Test of process method, of class DocumentCreator.
     */
    @Test
    public void testProcess() throws Exception {
        System.out.println("process");
        ImportFileTicket ticket = null;
        Path path = null;
        DocumentCreator instance = null;
        instance.process(ticket, path);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
