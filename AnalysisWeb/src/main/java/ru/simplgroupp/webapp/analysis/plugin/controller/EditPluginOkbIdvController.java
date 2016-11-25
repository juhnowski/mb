package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.OkbIdvPluginConfig;

public class EditPluginOkbIdvController extends EditPluginExtController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1658166537680114980L;
	protected boolean useWork;
	protected int cacheDays;
	protected int numberRepeats;
	
	@Override
	void reload() {
		OkbIdvPluginConfig rcfg = (OkbIdvPluginConfig) editCtl.plugin;
		useWork = rcfg.isUseWork();
		cacheDays=rcfg.getCacheDays();
		numberRepeats=rcfg.getNumberRepeats();

	}

	@Override
	void save() {
		OkbIdvPluginConfig rcfg = (OkbIdvPluginConfig) editCtl.plugin;
		rcfg.setUseWork(useWork);
		rcfg.setCacheDays(cacheDays);
		rcfg.setNumberRepeats(numberRepeats);

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
