package ru.simplgroupp.rest.api.data;

/**
 * Created by PARFENOV on 29.05.2015.
 */
public class PassportData extends AbstractUserData{

    private String series;
    private String number;
    private String docDate;
    private String docOrgCode;
    private String docOrg;


    public String getSeries() {
        return series;
    }


    public void setSeries(String series) {
        this.series = series;
    }


    public String getNumber() {
        return number;
    }


    public void setNumber(String number) {
        this.number = number;
    }


    public String getDocDate() {
        return docDate;
    }


    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }


    public String getDocOrgCode() {
        return docOrgCode;
    }


    public void setDocOrgCode(String docOrgCode) {
        this.docOrgCode = docOrgCode;
    }


    public String getDocOrg() {
        return docOrg;
    }


    public void setDocOrg(String docOrg) {
        this.docOrg = docOrg;
    }
}
