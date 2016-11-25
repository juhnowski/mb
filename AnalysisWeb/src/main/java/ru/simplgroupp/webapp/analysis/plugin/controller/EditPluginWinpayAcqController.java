package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.WinpayAcquiringPluginConfig;

public class EditPluginWinpayAcqController extends EditPluginExtController {

	private String apiKey;

	private String secretKey;

        private String payUrl;

        private String testUrl;

	private boolean useWork ;

	@Override
	void reload() {
		WinpayAcquiringPluginConfig pluginConfig = (WinpayAcquiringPluginConfig) editCtl.plugin;
		apiKey = pluginConfig.getApiKey();
		secretKey = pluginConfig.getSecretKey();
		payUrl = pluginConfig.getPayUrl();
		testUrl = pluginConfig.getTestUrl();
		useWork = pluginConfig.isUseWork();
	}

	@Override
	void save() {
		WinpayAcquiringPluginConfig pluginConfig = (WinpayAcquiringPluginConfig) editCtl.plugin;
		pluginConfig.setApiKey(apiKey);
		pluginConfig.setSecretKey(secretKey);
		pluginConfig.setPayUrl(payUrl);
		pluginConfig.setTestUrl(testUrl);
		pluginConfig.setUseWork(useWork);
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getTestUrl() {
		return testUrl;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	public boolean isUseWork() {
		return useWork;
	}

	public void setUseWork(boolean useWork) {
		this.useWork = useWork;
	}
}