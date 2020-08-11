package org.hilel14.archie.enhabsor.core.jobs.tasks;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import java.nio.file.Path;
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

    private Path convertPdf(ImportFileTicket ticket, Path original) throws Exception {
        return original;
    }

    private void convertImage(ImportFileTicket ticket, Path original) throws Exception {
        Path target = original.getParent().resolve(ticket.getUuid() + ".png");
        ImagePlus imagePlus = IJ.openImage(original.toString());
        ImageProcessor imageProcessor = imagePlus.getProcessor();
        //imageProcessor.scale(0.5, 0.5);
        int w = imageProcessor.getWidth(); // max 550
        int h = imageProcessor.getHeight(); // max 380
        imageProcessor = imageProcessor.resize(550);
        imagePlus.setProcessor(imageProcessor);
        IJ.saveAs(imagePlus, "png", target.toString());
    }

}