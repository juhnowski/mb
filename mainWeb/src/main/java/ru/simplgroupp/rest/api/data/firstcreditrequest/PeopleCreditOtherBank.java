package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeopleCreditOtherBank implements Serializable {
	private String creditsumprev;
	private String creditcardlimit;
	private String creditdate;
	private String overdue;
    private Integer prevcredits;
    private Integer credittype;
    private Integer currencytype;
    private Integer creditisover;
    private Integer creditOrganization;
    private String credittypeTitle;
    private String currencytypeTitle;
    private String creditOrganizationTitle;
    private String overdueTitle;
    private String creditSumMonth;
    private String creditclosedate;
    private String factcreditclosedate;
    
    
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
	public String getOverdue() {
		return overdue;
	}
	public void setOverdue(String overdue) {
		this.overdue = overdue;
	}
	public Integer getPrevcredits() {
		return prevcredits;
	}
	public void setPrevcredits(Integer prevcredits) {
		this.prevcredits = prevcredits;
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
	public Integer getCreditOrganization() {
		return creditOrganization;
	}
	public void setCreditOrganization(Integer creditOrganization) {
		this.creditOrganization = creditOrganization;
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
	public String getOverdueTitle() {
		return overdueTitle;
	}
	public void setOverdueTitle(String overdueTitle) {
		this.overdueTitle = overdueTitle;
	}
	public String getCreditSumMonth() {
		return creditSumMonth;
	}
	public void setCreditSumMonth(String creditSumMonth) {
		this.creditSumMonth = creditSumMonth;
	}
	public String getCreditclosedate() {
		return creditclosedate;
	}
	public void setCreditclosedate(String creditclosedate) {
		this.creditclosedate = creditclosedate;
	}
	public String getFactcreditclosedate() {
		return factcreditclosedate;
	}
	public void setFactcreditclosedate(String factcreditclosedate) {
		this.factcreditclosedate = factcreditclosedate;
	}
}
