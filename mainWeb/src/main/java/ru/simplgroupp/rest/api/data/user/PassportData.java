package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Documents;

/**
 * Основные паспортные данные человека
 */
public class PassportData {
    private String series;
    private String number;
    private String docDate;
    private String docOrg;
    private String docOrgCode;


    public PassportData() {
    }

    public PassportData(Documents passport) {
        this.series = passport.getSeries();
        this.number = passport.getNumber();
        this.docDate = Convertor.dateToString(passport.getDocDate(), "dd / MM / yyyy");
        this.docOrg = passport.getDocOrg();
        this.docOrgCode = passport.getDocOrgCode();
    }

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
