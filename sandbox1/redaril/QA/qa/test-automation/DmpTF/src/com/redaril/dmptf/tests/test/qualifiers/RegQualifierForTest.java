package com.redaril.dmptf.tests.test.qualifiers;

/**
 * Created with IntelliJ IDEA.
 * User: yksenofontov
 * Date: 25.04.13
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public class RegQualifierForTest {
    private String id;//parameter of test
    private String url;//parameter of test
    private String interestId;//parameter of test
    private String interestName;//parameter of test
    private String interestSource;//parameter of test


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInterestId() {
        return interestId;
    }

    public void setInterestId(String interestId) {
        this.interestId = interestId;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public String getInterestSource() {
        return interestSource;
    }

    public void setInterestSource(String interestSource) {
        this.interestSource = interestSource;
    }
}
