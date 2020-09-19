package org.hilel14.archie.enhabsor.core.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import net.lingala.zip4j.ZipFile;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFolderForm;
import org.hilel14.archie.enhabsor.core.jobs.tasks.ThumbnailGeneratorTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel
 */
public class TestimSetup {

    static final Logger LOGGER = LoggerFactory.getLogger(TestimSetup.class);

    public static ImportFolderForm getImportFolderForm() throws IOException {
        String attributes;
        try (InputStream in = ThumbnailGeneratorTest.class.getResourceAsStream("/data/folder-1.json");) {
            attributes = new String(in.readAllBytes(), Charset.forName("utf-8"));
        }
        ImportFolderForm form = ImportFolderForm.unmarshal(attributes);
        return form;
    }

    public static Config initTestEvn() throws Exception {
        Path base = Files.createTempDirectory("archie_");
        createSubFolders(base);
        initWorkFolder(base);
        Properties properties = createProperties(base);
        Config config = new Config(properties);
        SolrClient solrClient = createSolrClient(base);
        config.setSolrClient(solrClient);
        return config;
    }

    private static void createSubFolders(Path base) throws IOException {
        LOGGER.info("Creating sub folders in {}", base);
        for (String folder : new String[]{"assetstore", "import", "logs", "work"}) {
            Files.createDirectories(base.resolve(folder));
        }
        for (String store : new String[]{"public", "private", "secret"}) {
            Files.createDirectories(base.resolve("assetstore").resolve(store));
            for (String section : new String[]{"originals", "thumbnails", "text"}) {
                Files.createDirectories(base.resolve("assetstore").resolve(store).resolve(section));
            }
        }
    }

    private static Properties createProperties(Path base) throws IOException {
        Properties properties = new Properties();
        String name = "/archie.enhabsor.properties";
        try (InputStream in = Config.class.getResourceAsStream(name);) {
            properties.load(in);
        }
        properties.put("import.folder", base.resolve("import").toString());
        properties.put("work.folder", base.resolve("work").toString());
        properties.put("public.assets", base.resolve("assetstore").resolve("public").toString());
        properties.put("private.assets", base.resolve("assetstore").resolve("private").toString());
        properties.put("secret.assets", base.resolve("assetstore").resolve("secret").toString());
        properties.put("tesseract.train.data.path", "src/test/resources/tessdata/best");
        return properties;
    }

    private static void initWorkFolder(Path base) throws IOException {
        Path workFolder = base.resolve("work");
        LOGGER.info("Creating and populating work folder {}", workFolder);
        //Files.createDirectory(workFolder);
        copyToWorkFolder(workFolder, "dog.jpg");
        copyToWorkFolder(workFolder, "ganan-gidel-dagan.jpg");
        copyToWorkFolder(workFolder, "git-cheat-sheet.pdf");
    }

    private static void copyToWorkFolder(Path importFolder, String fileName) throws IOException {
        try (InputStream in = TestimSetup.class.getResourceAsStream("/data/folder-1/" + fileName);) {
            Path target = importFolder.resolve(fileName);
            Files.write(target, in.readAllBytes(), StandardOpenOption.CREATE);
        }
    }

    static SolrClient createSolrClient(Path base) throws IOException {
        //Files.createDirectory(home);
        // copy zip file to temp folder
        Path targetFile = base.resolve("solr.zip");
        try (InputStream in = TestimSetup.class.getResourceAsStream("/solr.zip");) {
            Files.write(targetFile, in.readAllBytes(), StandardOpenOption.CREATE);
        }
        // extract zip
        ZipFile zipFile = new ZipFile(targetFile.toString());
        zipFile.extractAll(base.toString());
        // create the client
        SolrClient client = new EmbeddedSolrServer(base.resolve("solr"), "enhabosr");
        return client;
    }

}
