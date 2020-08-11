package org.hilel14.archie.enhabsor.core.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.hilel14.archie.enhabsor.core.Config;
import static org.hilel14.archie.enhabsor.core.Config.LOGGER;

/**
 *
 * @author hilel
 */
public class TestimSetup {

    public static Config initTestEvn() throws Exception {
        Path base = Files.createTempDirectory("archie_");
        createSubFolders(base);
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
        Config config = new Config(properties);
        return config;
    }

    private static void createSubFolders(Path base) throws IOException {
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
}
