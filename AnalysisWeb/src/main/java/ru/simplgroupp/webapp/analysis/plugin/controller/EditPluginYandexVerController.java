package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.YandexIdentificationPayPluginConfig;

public class EditPluginYandexVerController extends EditPluginExtController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4031915921522330955L;

	private boolean useWork ;

    private String agentId;

    private String testUrl;

    private String workUrl;
    
	@Override
	void reload() {
		YandexIdentificationPayPluginConfig config=(YandexIdentificationPayPluginConfig) editCtl.plugin;
		useWork=config.isUseWork();
		agentId=config.getAgentId();
		testUrl=config.getTestUrl();
		workUrl=config.getWorkUrl();
		
	}

	@Override
	void save() {
		YandexIdentificationPayPluginConfig config=(YandexIdentificationPayPluginConfig) editCtl.plugin;
		config.setAgentId(agentId);
		config.setUseWork(useWork);
		config.setTestUrl(testUrl);
		config.setWorkUrl(workUrl);
	}

	public boolean isUseWork() {
		return useWork;
	}

	public void setUseWork(boolean useWork) {
		this.useWork = useWork;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getTestUrl() {
		return testUrl;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	public String getWorkUrl() {
		return workUrl;
	}

	public void setWorkUrl(String workUrl) {
		this.workUrl = workUrl;
	}

	
}
