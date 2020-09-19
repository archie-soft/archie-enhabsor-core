package org.hilel14.archie.enhabsor.core.jobs.model;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    // item attributes
    String isadFonds;
    String isadSubFonds;
    String isadSeries;
    String isadFile;
    String dcAccessRights;
    String dcTitle;
    Set<String> dcCreator;
    String dcDateStart;
    String dcDateEnd;
    String dcDescription;
    String dcType;
    String dcSource;
    String dcIdentifier;
    boolean localStoragePermanent;
    String localStorageCabinet;
    String localStorageShelf;
    String localStorageContainer;

    public static ImportFolderForm unmarshal(String attributes) throws IOException {
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

    public String getIsadFonds() {
        return isadFonds;
    }

    public void setIsadFonds(String isadFonds) {
        this.isadFonds = isadFonds;
    }

    public String getIsadSubFonds() {
        return isadSubFonds;
    }

    public void setIsadSubFonds(String isadSubFonds) {
        this.isadSubFonds = isadSubFonds;
    }

    public String getIsadSeries() {
        return isadSeries;
    }

    public void setIsadSeries(String isadSeries) {
        this.isadSeries = isadSeries;
    }

    public String getIsadFile() {
        return isadFile;
    }

    public void setIsadFile(String isadFile) {
        this.isadFile = isadFile;
    }

    public String getDcAccessRights() {
        return dcAccessRights;
    }

    public void setDcAccessRights(String dcAccessRights) {
        this.dcAccessRights = dcAccessRights;
    }

    public String getDcTitle() {
        return dcTitle;
    }

    public void setDcTitle(String dcTitle) {
        this.dcTitle = dcTitle;
    }

    public Set<String> getDcCreator() {
        return dcCreator;
    }

    public void setDcCreator(Set<String> dcCreator) {
        this.dcCreator = dcCreator;
    }

    public String getDcDateStart() {
        return dcDateStart;
    }

    public void setDcDateStart(String dcDateStart) {
        this.dcDateStart = dcDateStart;
    }

    public String getDcDateEnd() {
        return dcDateEnd;
    }

    public void setDcDateEnd(String dcDateEnd) {
        this.dcDateEnd = dcDateEnd;
    }

    public String getDcDescription() {
        return dcDescription;
    }

    public void setDcDescription(String dcDescription) {
        this.dcDescription = dcDescription;
    }

    public String getDcType() {
        return dcType;
    }

    public void setDcType(String dcType) {
        this.dcType = dcType;
    }

    public String getDcSource() {
        return dcSource;
    }

    public void setDcSource(String dcSource) {
        this.dcSource = dcSource;
    }

    public String getDcIdentifier() {
        return dcIdentifier;
    }

    public void setDcIdentifier(String dcIdentifier) {
        this.dcIdentifier = dcIdentifier;
    }

    public boolean isLocalStoragePermanent() {
        return localStoragePermanent;
    }

    public void setLocalStoragePermanent(boolean localStoragePermanent) {
        this.localStoragePermanent = localStoragePermanent;
    }

    public String getLocalStorageCabinet() {
        return localStorageCabinet;
    }

    public void setLocalStorageCabinet(String localStorageCabinet) {
        this.localStorageCabinet = localStorageCabinet;
    }

    public String getLocalStorageShelf() {
        return localStorageShelf;
    }

    public void setLocalStorageShelf(String localStorageShelf) {
        this.localStorageShelf = localStorageShelf;
    }

    public String getLocalStorageContainer() {
        return localStorageContainer;
    }

    public void setLocalStorageContainer(String localStorageContainer) {
        this.localStorageContainer = localStorageContainer;
    }

}
