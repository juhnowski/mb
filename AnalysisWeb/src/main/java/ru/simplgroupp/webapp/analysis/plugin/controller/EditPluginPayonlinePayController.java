package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.PayonlinePayPluginConfig;

public class EditPluginPayonlinePayController extends EditPluginExtController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1942641641638038730L;

	private Integer merchantId;

    private String privateSecurityKey;

    private String refillUrl;

    private String personalInfoUrl;

	@Override
	void reload() {
		PayonlinePayPluginConfig cfg = (PayonlinePayPluginConfig) editCtl.plugin;
		merchantId=cfg.getMerchantId();
		privateSecurityKey=cfg.getPrivateSecurityKey();
		refillUrl=cfg.getRefillUrl();
		personalInfoUrl=cfg.getPersonalInfoUrl();
	}

	@Override
	void save() {
		PayonlinePayPluginConfig cfg = (PayonlinePayPluginConfig) editCtl.plugin;
		cfg.setMerchantId(merchantId);
		cfg.setPrivateSecurityKey(privateSecurityKey);
		cfg.setRefillUrl(refillUrl);
		cfg.setPersonalInfoUrl(personalInfoUrl);
		
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public String getPrivateSecurityKey() {
		return privateSecurityKey;
	}

	public void setPrivateSecurityKey(String privateSecurityKey) {
		this.privateSecurityKey = privateSecurityKey;
	}

	public String getRefillUrl() {
		return refillUrl;
	}

	public void setRefillUrl(String refillUrl) {
		this.refillUrl = refillUrl;
	}

	public String getPersonalInfoUrl() {
		return personalInfoUrl;
	}

	public void setPersonalInfoUrl(String personalInfoUrl) {
		this.personalInfoUrl = personalInfoUrl;
	}

	
}
