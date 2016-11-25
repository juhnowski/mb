package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.ContactAcquiringPluginConfig;

public class EditPluginContactAcqController extends EditPluginExtController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4363708664571731668L;
	protected boolean useWork;
    private Long shopId;

    private Long scid;

    private String password;   //боевой FB073958-F9B7-4D7C-AE7F-96F3C45D9B60


    private String testUrl;

    private String workUrl;	

	@Override
	void reload() {
		ContactAcquiringPluginConfig rcfg = (ContactAcquiringPluginConfig) editCtl.plugin;
		useWork = rcfg.isUseWork();
		shopId = rcfg.getShopId();
		scid = rcfg.getScid();
		password = rcfg.getPassword();
		testUrl = rcfg.getTestUrl();
		workUrl = rcfg.getWorkUrl();
		// TODO Auto-generated method stub

	}

	@Override
	void save() {
		ContactAcquiringPluginConfig rcfg = (ContactAcquiringPluginConfig) editCtl.plugin;
		rcfg.setUseWork(useWork);
		rcfg.setShopId(shopId);
		rcfg.setScid(scid);
		rcfg.setPassword(password);
		rcfg.setTestUrl(testUrl);
		rcfg.setWorkUrl(workUrl);
		// TODO Auto-generated method stub

	}

	public boolean isUseWork() {
		return useWork;
	}

	public void setUseWork(boolean useWork) {
		this.useWork = useWork;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getScid() {
		return scid;
	}

	public void setScid(Long scid) {
		this.scid = scid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
