package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import static org.bytedeco.leptonica.global.lept.pixDestroy;
import static org.bytedeco.leptonica.global.lept.pixRead;
import org.bytedeco.tesseract.TessBaseAPI;
import org.hilel14.archie.enhabsor.core.Config;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;

/**
 *
 * @author hilel14
 */
public class ContentExtractor {

    static final Logger LOGGER = LoggerFactory.getLogger(ContentExtractor.class);
    final Config config;
    final DocumentCreator next;

    public ContentExtractor(Config config) throws Exception {
        this.config = config;
        this.next = new DocumentCreator(config);
    }

    public void process(ImportFileTicket ticket, Path path) throws Exception {
        switch (ticket.getImportFolderForm().getTextAction()) {
            case "recognize":
                recognize(ticket, path);
                break;
            case "extract":
                extract(ticket, path);
                break;
            default:
                LOGGER.debug("Skipping text action for file {}", ticket.getFileName());
        }
        next.process(ticket, path);
    }

    private void recognize(ImportFileTicket ticket, Path source) throws IOException {
        LOGGER.debug("Using OCR to recognize text from file {}", ticket.getFileName());
        if (config.getValidOcrFormats().contains(ticket.getFormat().toLowerCase())) {
            ocrImage(ticket, source);
        } else if (ticket.getFormat().equalsIgnoreCase("pdf")) {
            ocrPdf(ticket, source);
        } else {
            LOGGER.warn("{} is not a valid OCR file", ticket.getFileName());
        }
    }

    private void extract(ImportFileTicket ticket, Path path) throws IOException {
        if (ticket.getFormat().equalsIgnoreCase("pdf")) {
            LOGGER.debug("Extracting text from PDF file {}", ticket.getFileName());
            try (PDDocument doc = PDDocument.load(path.toFile())) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                Writer writer = new StringWriter();
                stripper.writeText(doc, writer);
                String result = writer.toString();
                if (result != null) {
                    result = result.trim();
                    if (!result.isEmpty()) {
                        ticket.setContent(result.trim());
                    }
                }
            }
        } else {
            LOGGER.warn("{} is not a PDF file", ticket.getFileName());
        }
    }

    private void ocrImage(ImportFileTicket ticket, Path source) throws IOException {
        TessBaseAPI api = new TessBaseAPI();
        String trainDatePath = "src/main/resources/tessdata/best";
        String lang = "heb";
        if (api.Init(trainDatePath, lang) != 0) {
            throw new IOException("Unable to init tesseract api with train data files from " + trainDatePath);
        }
        // Open input image with leptonica library
        PIX image = pixRead(source.toString());
        api.SetImage(image);
        // Get OCR result
        BytePointer outText = api.GetUTF8Text();
        String result = outText.getString("utf-8");
        if (result != null) {
            result = result.trim();
            if (!result.isEmpty()) {
                String content = ticket.getContent() == null
                        ? result
                        : ticket.getContent().concat(" ").concat(result);
                ticket.setContent(content);
            }
        }
        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);
    }

    private void ocrPdf(ImportFileTicket ticket, Path source) throws IOException {
        try (PDDocument document = PDDocument.load(source.toFile())) {
            PDFRenderer renderer = new PDFRenderer(document);
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300, ImageType.RGB);
                Path target
                        = source.getParent()
                                .resolve(source.getFileName().toString()
                                        .concat(String.valueOf(i)).concat(".png"));
                //ImageIO.write(image, "png", target.toFile());
                ImageIOUtil.writeImage(image, target.toString(), 300);
                ocrImage(ticket, target);
            }
        }
    }

    private void saveAs(ImportFileTicket ticket, Path source) throws IOException {
        Path target = source.getParent().resolve(ticket.getUuid());
        Files.writeString(target, ticket.getContent(), StandardOpenOption.CREATE_NEW);
    }

}
