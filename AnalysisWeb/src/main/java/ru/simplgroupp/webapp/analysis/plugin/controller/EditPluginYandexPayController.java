package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.YandexPayPluginConfig;

public class EditPluginYandexPayController extends EditPluginExtController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1528054351918240635L;
	private boolean useWork ;

    private String agentId;

    private String testUrl;

    private String workUrl;
    
	@Override
	void reload() {
		YandexPayPluginConfig config=(YandexPayPluginConfig) editCtl.plugin;
		useWork=config.isUseWork();
		agentId=config.getAgentId();
		testUrl=config.getTestUrl();
		workUrl=config.getWorkUrl();
		
	}

	@Override
	void save() {
		YandexPayPluginConfig config=(YandexPayPluginConfig) editCtl.plugin;
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
