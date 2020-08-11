package org.hilel14.archie.enhabsor.core.jobs.model;

import java.util.UUID;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author hilel14
 */
public class ImportFileTicket {

    public static final int IMPORT_IN_PROGRESS = 0;
    public static final int INVALID_FILE = -1;
    public static final int IMPORT_COMPLETED_SUCCESSFULLY = 1;

    private final String fileName;
    private final String uuid;
    private String fileDigest;
    private String content;
    private int importStatusCode;
    private String importStatusText;
    private ImportFolderForm importFolderForm;

    public ImportFileTicket(String fileName, ImportFolderForm form) {
        this.fileName = fileName;
        this.importFolderForm = form;
        this.uuid = UUID.randomUUID().toString();
        this.importStatusCode = IMPORT_IN_PROGRESS;
    }

    public void finalizeStatus() {
        if (getImportStatusCode() == IMPORT_IN_PROGRESS) {
            setImportStatusCode(IMPORT_COMPLETED_SUCCESSFULLY);
        }
    }

    public String getBaseName() {
        return FilenameUtils.getBaseName(getFileName());
    }

    public String getFormat() {
        return FilenameUtils.getExtension(getFileName()).toLowerCase();
    }

    public String getAssetName() {
        return getUuid() + "." + getFormat();
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return the fileDigest
     */
    public String getFileDigest() {
        return fileDigest;
    }

    /**
     * @param fileDigest the fileDigest to set
     */
    public void setFileDigest(String fileDigest) {
        this.fileDigest = fileDigest;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the importStatusCode
     */
    public int getImportStatusCode() {
        return importStatusCode;
    }

    /**
     * @param importStatusCode the importStatusCode to set
     */
    public void setImportStatusCode(int importStatusCode) {
        this.importStatusCode = importStatusCode;
    }

    /**
     * @return the importStatusText
     */
    public String getImportStatusText() {
        return importStatusText;
    }

    /**
     * @param importStatusText the importStatusText to set
     */
    public void setImportStatusText(String importStatusText) {
        this.importStatusText = importStatusText;
    }

    /**
     * @return the importFolderForm
     */
    public ImportFolderForm getImportFolderForm() {
        return importFolderForm;
    }

    /**
     * @param importFolderForm the importFolderForm to set
     */
    public void setImportFolderForm(ImportFolderForm importFolderForm) {
        this.importFolderForm = importFolderForm;
    }

}
