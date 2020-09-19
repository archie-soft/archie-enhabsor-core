package org.hilel14.archie.enhabsor.core.jobs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ArchieItem;

/**
 *
 * @author hilel14
 */
public class UpdateDocumentsJob {

    static final Logger LOGGER = LoggerFactory.getLogger(UpdateDocumentsJob.class);
    final Config config;
    final DateFormat iso8601TimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    final String[] previewFileNameExtentions = new String[] { "png", "txt" };

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        try {
            CommandLine cmd = parser.parse(options, args);
            String input = cmd.getOptionValue("input").trim();
            Config config = new Config();
            UpdateDocumentsJob job = new UpdateDocumentsJob(config);
            Path inFile = Paths.get(input);
            String format = FilenameUtils.getExtension(inFile.getFileName().toString());
            switch (format) {
                case "json":
                    processJson(job, inFile);
                    break;
                case "csv":
                    processCsv(job, inFile);
                    break;
                default:
                    System.out.println("Invalid input: " + input);
            }
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("update-documents", options);
            System.exit(1);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    static void processCsv(UpdateDocumentsJob job, Path inFile) throws Exception {
        LOGGER.info("Updating Solr index from csv file {}", inFile);
        try (Reader in = new FileReader(inFile.toFile())) {
            job.runCsv(in);
        }
    }

    static void processJson(UpdateDocumentsJob job, Path inFile) throws Exception {
        LOGGER.info("Updating Solr index from json file {}", inFile);
        String attributes = new String(Files.readAllBytes(inFile));
        List<ArchieItem> docs = new ObjectMapper().readValue(attributes, new TypeReference<List<ArchieItem>>() {
        });
        job.run(docs);
    }

    static Options createOptions() {
        Options options = new Options();
        Option option;
        // input
        option = new Option("i", "Input. Path to json or csv input file.");
        option.setLongOpt("input");
        option.setArgs(1);
        option.setRequired(true);
        options.addOption(option);
        // return
        return options;
    }

    public UpdateDocumentsJob(Config config) {
        this.config = config;
    }

    public void run(List<ArchieItem> items) throws Exception {
        LOGGER.debug("Updating items {}", items);
        for (ArchieItem item : items) {
            LOGGER.debug("Updating item {}", item.get("id"));
            update(item, config.getSolrClient());
        }
        config.getSolrClient().commit();
    }

    public void runCsv(Reader in) throws Exception {
        LOGGER.info("Updating Solr index from CSV file...");
        int count = 0;
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            count++;
            ArchieItem item = ArchieItem.fromMap(record.toMap());
            update(item, config.getSolrClient());
        }
        LOGGER.info("Commiting changes to {} documents", count);
        config.getSolrClient().commit();
        LOGGER.info("The operation completed successfully");
    }

    public void update(ArchieItem item, SolrClient solrClient) throws Exception {
        LOGGER.debug("Converting item {} to Solr document", item.get("id"));
        SolrInputDocument doc = item.toSolrUpdate();
        if (item.containsKey("dcFormat")) {
            LOGGER.debug("Checking asset-store files related to item {}", item.get("id"));
            moveFiles(item);
        }
        // add to solr
        LOGGER.debug("Adding doc {} to Solr index", doc.get("id"));
        solrClient.add(doc);
        LOGGER.debug("Update of item {} completed successfully", item.get("id"));
    }

    private void moveFiles(ArchieItem item) throws Exception {
        String sourceRepository = findSourceRepository("originals", item.getOriginalFileName());
        if (sourceRepository.isEmpty()) {
            return;
        }
        String targetRepository = item.get("dcAccessRights").toString();
        if (sourceRepository.equalsIgnoreCase(targetRepository)) {
            return;
        }
        LOGGER.debug("Moving originals/{} from {} to {}", item.getOriginalFileName(), sourceRepository,
                targetRepository);
        config.getStorageConnector().move(sourceRepository, targetRepository, "originals", item.getOriginalFileName());
        // thumbnails
        if (config.getStorageConnector().exist(sourceRepository, "thumbnails", item.getThumbnailFileName())) {
            config.getStorageConnector().move(sourceRepository, targetRepository, "thumbnails",
                    item.getThumbnailFileName());
        }
        // text
        if (config.getStorageConnector().exist(sourceRepository, "text", item.getTextFileName())) {
            config.getStorageConnector().move(sourceRepository, targetRepository, "text", item.getTextFileName());
        }
    }

    private String findSourceRepository(String container, String file) {
        for (String repository : config.getRepositories()) {
            if (config.getStorageConnector().exist(repository, container, file)) {
                return repository;
            }
        }
        return "";
    }
}
