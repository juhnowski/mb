package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.EquifaxPluginConfig;

public class EditPluginEFController extends EditPluginExtController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3191238036023870121L;
	protected boolean useWork;
	protected int cacheDays;
    protected int numberRepeats;
    
	@Override
	void reload() {
		EquifaxPluginConfig rcfg = (EquifaxPluginConfig) editCtl.plugin;
		useWork = rcfg.isUseWork();
		cacheDays=rcfg.getCacheDays();
		numberRepeats=rcfg.getNumberRepeats();

	}

	@Override
	void save() {
		EquifaxPluginConfig rcfg = (EquifaxPluginConfig) editCtl.plugin;
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
