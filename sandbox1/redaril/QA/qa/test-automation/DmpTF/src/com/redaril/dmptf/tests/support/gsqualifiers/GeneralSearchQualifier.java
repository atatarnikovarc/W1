package com.redaril.dmptf.tests.support.gsqualifiers;

public class GeneralSearchQualifier {
    private long id;
    private String parameter;
    private long externalId;
    private String regex;
    private long dataSourceId;
    private boolean stemmed;
    private long linkedCategoryId;
    private String linkedCategoryName;
    private long pixelId;

    public long getPixelId() {
        return pixelId;
    }

    public void setPixelId(long pixelId) {
        this.pixelId = pixelId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public long getExternalId() {
        return externalId;
    }

    public void setExternalId(long externalId) {
        this.externalId = externalId;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public boolean isStemmed() {
        return stemmed;
    }

    public void setStemmed(boolean stemmed) {
        this.stemmed = stemmed;
    }

    public long getLinkedCategoryId() {
        return linkedCategoryId;
    }

    public void setLinkedCategoryId(long linkedCategoryId) {
        this.linkedCategoryId = linkedCategoryId;
    }

    public String getLinkedCategoryName() {
        return linkedCategoryName;
    }

    public void setLinkedCategoryName(String linkedCategoryName) {
        this.linkedCategoryName = linkedCategoryName;
    }
}
