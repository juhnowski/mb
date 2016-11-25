package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.SkbPluginConfig;

public class EditPluginSkbController extends EditPluginExtController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3918934058579593837L;
	protected boolean useWork;
	protected int cacheDays;
	protected int numberRepeats;
	
	@Override
	void reload() {
		SkbPluginConfig cfg=(SkbPluginConfig) editCtl.plugin;
		useWork = cfg.isUseWork();
		cacheDays=cfg.getCacheDays();
		numberRepeats=cfg.getNumberRepeats();
	}

	@Override
	void save() {
		SkbPluginConfig cfg=(SkbPluginConfig) editCtl.plugin;
		cfg.setUseWork(useWork);
	    cfg.setCacheDays(cacheDays);
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

	public int getNumberRepeats() {
		return numberRepeats;
	}

	public void setNumberRepeats(int numberRepeats) {
		this.numberRepeats = numberRepeats;
	}

	
	
}
