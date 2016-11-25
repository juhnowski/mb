package ru.simplgroupp.data;

/**
 * Step3Data
 * Данные передаваемые на пятом шаге в json
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 23.04.14.
 */
public class Step5Data {
    private String id, creditsumprev,creditcardlimit,creditdate, overdue;
    private Integer prevcredits,credittype,currencytype,creditisover, creditOrganization;
    private String credittypeTitle,currencytypeTitle,creditOrganizationTitle,overdueTitle;

    public Step5Data(){
        creditOrganizationTitle = id = creditsumprev = creditcardlimit = creditdate = overdueTitle = credittypeTitle = currencytypeTitle = overdue = "";
        prevcredits = credittype = currencytype = creditisover = creditOrganization = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreditsumprev() {
        return creditsumprev;
    }

    public void setCreditsumprev(String creditsumprev) {
        this.creditsumprev = creditsumprev;
    }

    public String getCreditcardlimit() {
        return creditcardlimit;
    }

    public void setCreditcardlimit(String creditcardlimit) {
        this.creditcardlimit = creditcardlimit;
    }

    public String getCreditdate() {
        return creditdate;
    }

    public void setCreditdate(String creditdate) {
        this.creditdate = creditdate;
    }

    public Integer getPrevcredits() {
        return prevcredits;
    }

    public void setPrevcredits(Integer prevcredits) {
        this.prevcredits = prevcredits;
    }

    public Integer getCreditOrganization() {
        return creditOrganization;
    }

    public void setCreditOrganization(Integer creditOrganization) {
        this.creditOrganization = creditOrganization;
    }

    public Integer getCredittype() {
        return credittype;
    }

    public void setCredittype(Integer credittype) {
        this.credittype = credittype;
    }

    public Integer getCurrencytype() {
        return currencytype;
    }

    public void setCurrencytype(Integer currencytype) {
        this.currencytype = currencytype;
    }

    public Integer getCreditisover() {
        return creditisover;
    }

    public void setCreditisover(Integer creditisover) {
        this.creditisover = creditisover;
    }

    public String getCredittypeTitle() {
        return credittypeTitle;
    }

    public void setCredittypeTitle(String credittypeTitle) {
        this.credittypeTitle = credittypeTitle;
    }

    public String getCurrencytypeTitle() {
        return currencytypeTitle;
    }

    public void setCurrencytypeTitle(String currencytypeTitle) {
        this.currencytypeTitle = currencytypeTitle;
    }

    public String getCreditOrganizationTitle() {
        return creditOrganizationTitle;
    }

    public void setCreditOrganizationTitle(String creditOrganizationTitle) {
        this.creditOrganizationTitle = creditOrganizationTitle;
    }

    public String getOverdue() {
        return overdue;
    }

    public void setOverdue(String overdue) {
        this.overdue = overdue;
    }

    public String getOverdueTitle() {
        return overdueTitle;
    }

    public void setOverdueTitle(String overdueTitle) {
        this.overdueTitle = overdueTitle;
    }
}
