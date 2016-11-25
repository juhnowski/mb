package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.OkbCaisPluginConfig;

public class EditPluginOkbCaisController extends EditPluginExtController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4176234494620275390L;
	protected boolean useWork;
	protected int cacheDays;
	protected int numberRepeats;
	protected int numberFunction;
	protected int numberFunctionUpload;
	
	@Override
	void reload() {
		OkbCaisPluginConfig rcfg = (OkbCaisPluginConfig) editCtl.plugin;
		useWork = rcfg.isUseWork();
		cacheDays=rcfg.getCacheDays();
		numberRepeats=rcfg.getNumberRepeats();
		numberFunction=rcfg.getNumberFunction();
		numberFunctionUpload=rcfg.getNumberFunctionUpload();
	}

	@Override
	void save() {
		OkbCaisPluginConfig rcfg = (OkbCaisPluginConfig) editCtl.plugin;
		rcfg.setUseWork(useWork);
		rcfg.setCacheDays(cacheDays);
		rcfg.setNumberRepeats(numberRepeats);
	    rcfg.setNumberFunction(numberFunction);
	    rcfg.setNumberFunctionUpload(numberFunctionUpload);
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

	public int getNumberFunction() {
		return numberFunction;
	}

	public void setNumberFunction(int numberFunction) {
		this.numberFunction = numberFunction;
	}

	public int getNumberFunctionUpload() {
		return numberFunctionUpload;
	}

	public void setNumberFunctionUpload(int numberFunctionUpload) {
		this.numberFunctionUpload = numberFunctionUpload;
	}

		
	
}
