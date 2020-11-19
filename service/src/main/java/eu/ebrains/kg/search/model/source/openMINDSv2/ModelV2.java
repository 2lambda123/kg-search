package eu.ebrains.kg.search.model.source.openMINDSv2;

import eu.ebrains.kg.search.model.source.commons.ExternalReference;
import eu.ebrains.kg.search.model.source.commons.InternalReference;
import eu.ebrains.kg.search.model.source.commons.Publication;

import java.util.Date;
import java.util.List;

public class ModelV2 {
    private List<ExternalReference> fileBundle;
    private List<InternalReference> custodian;
    private List<InternalReference> mainContact;
    private String title;
    private List<String> modelFormat;
    private List<String> cellularTarget;
    private String editorId;
    private String version;
    private List<String> studyTarget;
    private List<InternalReference> usedDataset; //TODO: validate the type.
    private String description;
    private List<ExternalReference> license;
    private List<String> modelScope;
    private List<String> abstractionLevel;
    private List<InternalReference> producedDataset;
    private List<Publication> publications; //TODO: Check why doi is a List ?
    private List<String> brainStructure;
    private List<InternalReference> contributors;
    private List<String> embargo;
    private String identifier;
    private Date lastReleaseAt;
    private Date firstReleaseAt;

    public Date getLastReleaseAt() { return lastReleaseAt; }

    public void setLastReleaseAt(Date lastReleaseAt) { this.lastReleaseAt = lastReleaseAt; }

    public Date getFirstReleaseAt() { return firstReleaseAt; }

    public void setFirstReleaseAt(Date firstReleaseAt) { this.firstReleaseAt = firstReleaseAt; }

    public List<ExternalReference> getFileBundle() { return fileBundle; }

    public void setFileBundle(List<ExternalReference> fileBundle) { this.fileBundle = fileBundle; }

    public List<InternalReference> getCustodian() { return custodian; }

    public void setCustodian(List<InternalReference> custodian) { this.custodian = custodian; }

    public List<InternalReference> getMainContact() { return mainContact; }

    public void setMainContact(List<InternalReference> mainContact) { this.mainContact = mainContact; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<String> getModelFormat() { return modelFormat; }

    public void setModelFormat(List<String> modelFormat) { this.modelFormat = modelFormat; }

    public List<String> getCellularTarget() { return cellularTarget; }

    public void setCellularTarget(List<String> cellularTarget) { this.cellularTarget = cellularTarget; }

    public String getEditorId() { return editorId; }

    public void setEditorId(String editorId) { this.editorId = editorId; }

    public String getVersion() { return version; }

    public void setVersion(String version) { this.version = version; }

    public List<String> getStudyTarget() { return studyTarget; }

    public void setStudyTarget(List<String> studyTarget) { this.studyTarget = studyTarget; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public List<ExternalReference> getLicense() { return license; }

    public void setLicense(List<ExternalReference> license) { this.license = license; }

    public List<String> getModelScope() { return modelScope; }

    public void setModelScope(List<String> modelScope) { this.modelScope = modelScope; }

    public List<String> getAbstractionLevel() { return abstractionLevel; }

    public void setAbstractionLevel(List<String> abstractionLevel) { this.abstractionLevel = abstractionLevel; }

    public List<InternalReference> getProducedDataset() { return producedDataset; }

    public void setProducedDataset(List<InternalReference> producedDataset) { this.producedDataset = producedDataset; }

    public List<Publication> getPublications() { return publications; }

    public void setPublications(List<Publication> publications) { this.publications = publications; }

    public List<String> getBrainStructure() { return brainStructure; }

    public void setBrainStructure(List<String> brainStructure) { this.brainStructure = brainStructure; }

    public List<InternalReference> getContributors() { return contributors; }

    public void setContributors(List<InternalReference> contributors) { this.contributors = contributors; }

    public List<String> getEmbargo() { return embargo; }

    public void setEmbargo(List<String> embargo) { this.embargo = embargo; }

    public String getIdentifier() { return identifier; }

    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public List<InternalReference> getUsedDataset() { return usedDataset; }

    public void setUsedDataset(List<InternalReference> usedDataset) { this.usedDataset = usedDataset; }

}
