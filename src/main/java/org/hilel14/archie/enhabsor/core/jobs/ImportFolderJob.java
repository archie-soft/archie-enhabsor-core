package org.hilel14.archie.enhabsor.core.jobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.tasks.FileValidator;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFolderForm;
import org.hilel14.archie.enhabsor.core.jobs.tools.DatabaseTool;

/**
 *
 * @author hilel14
 */
public class ImportFolderJob {

    static final Logger LOGGER = LoggerFactory.getLogger(ImportFolderJob.class);
    Config config;
    DatabaseTool databaseTool;

    public static void main(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            Config config = new Config();
            CommandLine cmd = parser.parse(options, args);
            String jobSpec = cmd.getOptionValue("job").trim();
            ImportFolderForm form = ImportFolderForm.unmarshal(jobSpec);
            ImportFolderJob importFolderJob = new ImportFolderJob(config);
            importFolderJob.run(form);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("import-folder-job", options);
            System.exit(1);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    public ImportFolderJob(Config config) throws Exception {
        this.config = config;
        databaseTool = new DatabaseTool(config);
    }

    public void run(ImportFolderForm form) throws Exception {
        LOGGER.info("Importing folder {}", form.getFolderName());
        List<String> items = config.getStorageConnector().listFiles("import", form.getFolderName());
        LOGGER.debug("Folder {} contains {} items, textAction = {}, addFileNamesTo = {}",
                form.getFolderName(), items.size(), form.getTextAction(), form.getAddFileNamesTo());
        databaseTool.createImportFolderRecord(form, items.size());
        for (String item : items) {
            // prepare
            ImportFileTicket ticket = new ImportFileTicket(item, form);
            databaseTool.createImportFileRecord(ticket);
            // download
            Path source = config.getStorageConnector().download("import", form.getFolderName(), ticket.getFileName());
            Path target = config.getWorkFolder().resolve("import").resolve(ticket.getUuid() + "." + ticket.getFormat());
            Files.move(source, target);
            // import
            importFile(ticket, target);
            databaseTool.updateImportFileRecord(ticket);
        }
        LOGGER.info("Import job completed successfully for folder {}", form.getFolderName());
    }

    private void importFile(ImportFileTicket ticket, Path path) throws Exception {
        FileValidator validator = new FileValidator(config);
        validator.process(ticket, path);
        ticket.finalizeStatus();
        cleanup(ticket);
    }

    private void cleanup(ImportFileTicket ticket) throws IOException {
        Path path = config.getWorkFolder().resolve("import").resolve(ticket.getUuid() + "." + ticket.getFormat());
        Files.deleteIfExists(path);
        path = config.getWorkFolder().resolve("import").resolve(ticket.getUuid() + ".png");
        Files.deleteIfExists(path);
        path = config.getWorkFolder().resolve("import").resolve(ticket.getUuid() + ".txt");
        Files.deleteIfExists(path);
    }

    static Options createOptions() {
        Options options = new Options();
        Option option;
        // job-spec
        option = new Option("j", "Job spec. Json string representation of ImportFolderJob.");
        option.setLongOpt("job");
        option.setArgs(1);
        option.setRequired(true);
        options.addOption(option);
        // return
        return options;
    }

}
