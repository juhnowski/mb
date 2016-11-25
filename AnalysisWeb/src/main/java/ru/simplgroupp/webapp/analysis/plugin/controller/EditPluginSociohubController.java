package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.SociohubPluginConfig;

public class EditPluginSociohubController extends EditPluginExtController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2536160350841546553L;
	protected boolean useWork;
	protected int cacheDays;
	protected int minCorrectnessScore;
	protected int numberRepeats;
	
	@Override
	void reload() {
		SociohubPluginConfig cfg=(SociohubPluginConfig) editCtl.plugin;
		useWork = cfg.isUseWork();
		cacheDays=cfg.getCacheDays();
		minCorrectnessScore=cfg.getMinCorrectnessScore();
		numberRepeats=cfg.getNumberRepeats();
	}

	@Override
	void save() {
		SociohubPluginConfig cfg=(SociohubPluginConfig) editCtl.plugin;
		cfg.setUseWork(useWork);
	    cfg.setCacheDays(cacheDays);
	    cfg.setMinCorrectnessScore(minCorrectnessScore);
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

	public int getMinCorrectnessScore() {
		return minCorrectnessScore;
	}

	public void setMinCorrectnessScore(int minCorrectnessScore) {
		this.minCorrectnessScore = minCorrectnessScore;
	}

	public int getNumberRepeats() {
		return numberRepeats;
	}

	public void setNumberRepeats(int numberRepeats) {
		this.numberRepeats = numberRepeats;
	}

	
	
}
