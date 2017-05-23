package com.redaril.dmptf.tests.support.gsqualifiers;

public class GeneralSearchDomain {
    private String id;
    private String regex;
    private String requestUrl;
    private String baseUrl;
    private String searchRegex;

    public String getSearchRegex() {
        return searchRegex;
    }

    public void setSearchRegex(String searchRegex) {
        this.searchRegex = searchRegex;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
