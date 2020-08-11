package org.hilel14.archie.enhabsor.core.jobs.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author hilel14
 */
public class ImportFolderForm {

    // global attributes
    private String folderName;
    private String textAction; // recognize (ocr), extrat (pdf), skip
    private String addFileNamesTo; // dcTitle, dcDescription    
    // database fields
    private long importFolderId;
    // document fields
    private Map<String, Object> itemAttributes;

    public static ImportFolderForm unmarshal(String attributes)
            throws IOException {
        return new ObjectMapper().readValue(attributes, ImportFolderForm.class);
    }

    /**
     * @return the folderName
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * @param folderName the folderName to set
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * @return the textAction
     */
    public String getTextAction() {
        return textAction;
    }

    /**
     * @param textAction the textAction to set
     */
    public void setTextAction(String textAction) {
        this.textAction = textAction;
    }

    /**
     * @return the addFileNamesTo
     */
    public String getAddFileNamesTo() {
        return addFileNamesTo;
    }

    /**
     * @param addFileNamesTo the addFileNamesTo to set
     */
    public void setAddFileNamesTo(String addFileNamesTo) {
        this.addFileNamesTo = addFileNamesTo;
    }

    /**
     * @return the importFolderId
     */
    public long getImportFolderId() {
        return importFolderId;
    }

    /**
     * @param importFolderId the importFolderId to set
     */
    public void setImportFolderId(long importFolderId) {
        this.importFolderId = importFolderId;
    }

    /**
     * @return the itemAttributes
     */
    public Map<String, Object> getItemAttributes() {
        return itemAttributes;
    }

    /**
     * @param itemAttributes the itemAttributes to set
     */
    public void setItemAttributes(Map<String, Object> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

}
