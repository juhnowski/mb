package ru.simplgroupp.webapp.analysis.plugin.controller;

import java.io.Serializable;

import ru.simplgroupp.ejb.RusStandartPluginConfig;

public class EditPluginRsController extends EditPluginExtController  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4369668962258273247L;
	
	protected boolean useWork;
	protected boolean requestPayment;
	protected boolean requestFMS;
	protected boolean requestFSSP;
	protected boolean requestFNS;
	protected boolean requestVerify;
	protected boolean requestScoring;
	protected boolean requestAntifrod;
	protected String fakeXmlAnswer;
	protected int cacheDays;
	protected int creditReportDetalization;
	protected int numberRepeats;
	
	@Override
	void reload() {
		RusStandartPluginConfig rcfg = (RusStandartPluginConfig) editCtl.plugin;
		useWork = rcfg.isUseWork();
		requestPayment = rcfg.getRequestPayment();
		requestFMS = rcfg.getRequestFMS();
		requestFSSP = rcfg.getRequestFSSP();
		requestFNS = rcfg.getRequestFNS();
		requestVerify = rcfg.getRequestVerify();
		requestScoring = rcfg.getRequestScoring();
		requestAntifrod=rcfg.getRequestAntifrod();
		cacheDays=rcfg.getCacheDays();
		creditReportDetalization=rcfg.getCreditReportDetalization();
		numberRepeats=rcfg.getNumberRepeats();
	}
	
	public String getFakeXmlAnswer() {
		return fakeXmlAnswer;
	}
	
	public void setFakeXmlAnswer(String value) {
		fakeXmlAnswer = value;
	}

	@Override
	void save() {
		RusStandartPluginConfig rcfg = (RusStandartPluginConfig) editCtl.plugin;
		rcfg.setUseWork(useWork);
		rcfg.setRequestPayment(requestPayment);
		rcfg.setRequestFMS(requestFMS);
		rcfg.setRequestFNS(requestFNS);
		rcfg.setRequestFSSP(requestFSSP);
		rcfg.setRequestVerify(requestVerify);
		rcfg.setRequestScoring(requestScoring);
	    rcfg.setRequestAntifrod(requestAntifrod);
	    rcfg.setCacheDays(cacheDays);
	    rcfg.setCreditReportDetalization(creditReportDetalization);
	    rcfg.setNumberRepeats(numberRepeats);
			
	}

	public boolean isUseWork() {
		return useWork;
	}

	public void setUseWork(boolean useWork) {
		this.useWork = useWork;
	}

	public boolean isRequestPayment() {
		return requestPayment;
	}

	public void setRequestPayment(boolean requestPayment) {
		this.requestPayment = requestPayment;
	}

	public boolean isRequestFMS() {
		return requestFMS;
	}

	public void setRequestFMS(boolean requestFMS) {
		this.requestFMS = requestFMS;
	}

	public boolean isRequestFSSP() {
		return requestFSSP;
	}

	public void setRequestFSSP(boolean requestFSSP) {
		this.requestFSSP = requestFSSP;
	}

	public boolean isRequestFNS() {
		return requestFNS;
	}

	public void setRequestFNS(boolean requestFNS) {
		this.requestFNS = requestFNS;
	}

	public boolean isRequestVerify() {
		return requestVerify;
	}

	public void setRequestVerify(boolean requestVerify) {
		this.requestVerify = requestVerify;
	}

	public boolean isRequestScoring() {
		return requestScoring;
	}

	public void setRequestScoring(boolean requestScoring) {
		this.requestScoring = requestScoring;
	}

	public boolean isRequestAntifrod() {
		return requestAntifrod;
	}

	public void setRequestAntifrod(boolean requestAntifrod) {
		this.requestAntifrod = requestAntifrod;
	}

	public int getCacheDays() {
		return cacheDays;
	}

	public void setCacheDays(int cacheDays) {
		this.cacheDays = cacheDays;
	}

	public int getCreditReportDetalization() {
		return creditReportDetalization;
	}

	public void setCreditReportDetalization(int creditReportDetalization) {
		this.creditReportDetalization = creditReportDetalization;
	}

	public int getNumberRepeats() {
		return numberRepeats;
	}

	public void setNumberRepeats(int numberRepeats) {
		this.numberRepeats = numberRepeats;
	}

	
	
}
