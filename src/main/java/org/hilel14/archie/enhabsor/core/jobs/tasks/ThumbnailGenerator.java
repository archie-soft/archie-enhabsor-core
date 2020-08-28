package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.hilel14.archie.enhabsor.core.Config;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel
 */
public class ThumbnailGenerator implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(ThumbnailGenerator.class);
    final Config config;

    public ThumbnailGenerator(Config config) {
        this.config = config;
    }

    @Override
    public void process(ImportFileTicket ticket, Path original) throws Exception {
        LOGGER.debug("Generating preview for file {}", ticket.getFileName());
        switch (ticket.getFormat()) {
            case "pdf":
                convertPdf(ticket, original);
                break;
            case "jpg":
            case "jpeg":
            case "gif":
            case "tif":
            case "tiff":
            case "png":
                convertImage(ticket, original);
                break;
            default:
                LOGGER.debug("Unable to create preview for {} files", ticket.getFormat());
        }
    }

    private void convertPdf(ImportFileTicket ticket, Path original) throws Exception {
        Path target = original.getParent().resolve(ticket.getUuid() + ".png");
        try (PDDocument document = PDDocument.load(original.toFile())) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 72, ImageType.RGB);
            ImageIO.write(image, "png", target.toFile());
        }
    }

    private void convertImage(ImportFileTicket ticket, Path original) throws Exception {
        BufferedImage sourceImage = ImageIO.read(original.toFile());
        Dimension scaledDimension = calculateDimension(sourceImage.getWidth(), sourceImage.getHeight(), 550, 380);
        Image scaledImage = sourceImage.getScaledInstance(scaledDimension.width, scaledDimension.height, Image.SCALE_SMOOTH);
        BufferedImage targetImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        targetImage.createGraphics().drawImage(scaledImage, 0, 0, null);
        Path target = original.getParent().resolve(ticket.getUuid() + ".png");
        ImageIO.write(targetImage, "png", target.toFile());

    }

    private Dimension calculateDimension(int sourceWidth, int sourceHeight, int maxWidth, int maxHeight) {
        Dimension result = new Dimension(-1, -1);
        if (sourceWidth <= maxWidth && sourceHeight <= maxHeight) {
            return result;
        }
        if (sourceWidth > maxWidth && sourceHeight <= maxHeight) {
            result.width = maxWidth;
            return result;
        }
        if (sourceHeight > maxHeight && sourceWidth <= maxWidth) {
            result.height = maxHeight;
            return result;
        }
        int widthDiff = sourceWidth - maxWidth;
        int heightDiff = sourceHeight - maxHeight;
        if (widthDiff > heightDiff) {
            result.width = maxWidth;
            return result;
        } else {
            result.height = maxHeight;
            return result;
        }
    }

}
