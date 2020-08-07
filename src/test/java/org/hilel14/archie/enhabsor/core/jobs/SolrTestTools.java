package org.hilel14.archie.enhabsor.core.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.hilel14.archie.enhabsor.core.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel
 */
public class SolrTestTools {

    static final Logger LOGGER = LoggerFactory.getLogger(SolrTestTools.class);

    static Config getConfig() throws Exception {
        Config config = new Config();
        return config;
    }

    static SolrClient getClient() throws IOException {
        String solrHome = "src/test/resources/server";
        String defaultCoreName = "books";
        Path dataDir = Paths.get("/tmp/solr-test");
        initDataDir(dataDir);
        SolrClient client = new EmbeddedSolrServer(Paths.get(solrHome), defaultCoreName);
        LOGGER.info("Solr home: {}, core name: {}", solrHome, defaultCoreName);
        return client;
    }

    static void initDataDir(Path dataDir) throws IOException {
        LOGGER.info("Preparing data dir " + dataDir);
        if (Files.exists(dataDir)) {
            try (Stream<Path> walk = Files.walk(dataDir)) {
                walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        //.peek(System.out::println)
                        .forEach(File::delete);
            }
        }
        Files.createDirectories(dataDir);
    }

}
