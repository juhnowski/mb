package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.OkbHunterPluginConfig;

public class EditPluginOkbHunterController extends EditPluginExtController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4679530227362199543L;
	protected boolean useWork;
    protected int numberRepeats;
    protected String changePasswordUrl;

	@Override
	void reload() {
		OkbHunterPluginConfig rcfg = (OkbHunterPluginConfig) editCtl.plugin;
		useWork = rcfg.isUseWork();
		changePasswordUrl=rcfg.getChangePasswordUrl();
		numberRepeats=rcfg.getNumberRepeats();
		
	}

	@Override
	void save() {
		OkbHunterPluginConfig rcfg = (OkbHunterPluginConfig) editCtl.plugin;
		rcfg.setUseWork(useWork);
		rcfg.setNumberRepeats(numberRepeats);
		rcfg.setChangePasswordUrl(changePasswordUrl);
		
	}

	public boolean isUseWork() {
		return useWork;
	}

	public void setUseWork(boolean useWork) {
		this.useWork = useWork;
	}

	public int getNumberRepeats() {
		return numberRepeats;
	}

	public void setNumberRepeats(int numberRepeats) {
		this.numberRepeats = numberRepeats;
	}

	public String getChangePasswordUrl() {
		return changePasswordUrl;
	}

	public void setChangePasswordUrl(String changePasswordUrl) {
		this.changePasswordUrl = changePasswordUrl;
	}

	
}
