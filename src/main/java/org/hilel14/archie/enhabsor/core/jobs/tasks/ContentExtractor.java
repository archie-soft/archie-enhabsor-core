package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.io.IOException;
import java.nio.file.Path;
import org.hilel14.archie.enhabsor.core.Config;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;
import org.hilel14.archie.enhabsor.core.jobs.tools.OcrTool;
import org.hilel14.archie.enhabsor.core.jobs.tools.PdfTool;

/**
 *
 * @author hilel14
 */
public class ContentExtractor implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(ContentExtractor.class);
    final Config config;
    PdfTool pdfTool;
    OcrTool ocrTool;

    public ContentExtractor(Config config) throws IOException {
        this.config = config;
        pdfTool = new PdfTool(config);
        ocrTool = new OcrTool(config);
    }

    @Override
    public void process(ImportFileTicket ticket, Path path) throws Exception {
        switch (ticket.getImportFolderForm().getTextAction()) {
            case "recognize":
                ocrTool.recognizeTextWithOcr(ticket, path);
                break;
            case "extract":
                pdfTool.extractTextFromPdf(ticket, path);
                break;
            default:
                LOGGER.debug("Skipping text action for file {}", ticket.getFileName());
        }
    }

}
