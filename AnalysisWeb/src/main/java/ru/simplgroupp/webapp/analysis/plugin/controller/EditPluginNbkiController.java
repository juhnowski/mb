package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.NBKIPluginConfig;

public class EditPluginNbkiController extends EditPluginExtController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4874825785472001810L;
	protected boolean useWork;
	protected int cacheDays;
	protected boolean requestScoring;
	protected String urlCommon;
	protected String urlScoring;
	protected boolean useSSL;
	protected int numberRepeats;
	
	@Override
	void reload() {
		NBKIPluginConfig cfg= (NBKIPluginConfig) editCtl.plugin;
		useWork = cfg.isUseWork();
		cacheDays=cfg.getCacheDays();
		requestScoring=cfg.getRequestScoring();
		urlCommon=cfg.getUrlCommon();
		urlScoring=cfg.getUrlScoring();
		useSSL=cfg.getUseSSL();
		numberRepeats=cfg.getNumberRepeats();
	}

	@Override
	void save() {
		NBKIPluginConfig cfg= (NBKIPluginConfig) editCtl.plugin;
		cfg.setUrlCommon(urlCommon);
		cfg.setUrlScoring(urlScoring);
		cfg.setUseWork(useWork);
		cfg.setCacheDays(cacheDays);
		cfg.setRequestScoring(requestScoring);
		cfg.setUseSSL(useSSL);
		cfg.setNumberRepeats(numberRepeats);
	}

	public boolean isUseWork() {
		return useWork;
	}

	public void setUseWork(boolean useWork) {
		this.useWork = useWork;
	}

	public int getCacheDays() {
		return cacheDays;
	}

	public void setCacheDays(int cacheDays) {
		this.cacheDays = cacheDays;
	}

	public boolean isRequestScoring() {
		return requestScoring;
	}

	public void setRequestScoring(boolean requestScoring) {
		this.requestScoring = requestScoring;
	}

	public String getUrlCommon() {
		return urlCommon;
	}

	public void setUrlCommon(String urlCommon) {
		this.urlCommon = urlCommon;
	}

	public String getUrlScoring() {
		return urlScoring;
	}

	public void setUrlScoring(String urlScoring) {
		this.urlScoring = urlScoring;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	public int getNumberRepeats() {
		return numberRepeats;
	}

	public void setNumberRepeats(int numberRepeats) {
		this.numberRepeats = numberRepeats;
	}

	
	
	
}
