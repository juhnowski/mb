package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.PayonlineAcquiringPluginConfig;

public class EditPluginPayAcqController extends EditPluginExtController {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 3016897130041019669L;
	private String bankCardUrl;
    private String verificationCardUrl;
    private String statusUrl;
    private String rebillUrl;
    private String cancelUrl;
    private Integer merchantId;
    private String privateSecurityKey;
    private Integer verificationCardMerchantId;
    private String verificationCardPrivateSecurityKey;
    
	@Override
	void reload() {
		PayonlineAcquiringPluginConfig rcfg = (PayonlineAcquiringPluginConfig) editCtl.plugin;
		bankCardUrl = rcfg.getBankCardUrl();
		verificationCardUrl = rcfg.getVerificationCardUrl();
		statusUrl = rcfg.getStatusUrl();
		rebillUrl = rcfg.getRebillUrl();
		cancelUrl = rcfg.getCancelUrl();
		merchantId = rcfg.getMerchantId();
		privateSecurityKey = rcfg.getPrivateSecurityKey();
		verificationCardMerchantId=rcfg.getVerificationCardMerchantId();
		verificationCardPrivateSecurityKey=rcfg.getVerificationCardPrivateSecurityKey();
		// TODO Auto-generated method stub

	}

	@Override
	void save() {
		PayonlineAcquiringPluginConfig rcfg = (PayonlineAcquiringPluginConfig) editCtl.plugin;
		rcfg.setBankCardUrl(bankCardUrl);
		rcfg.setVerificationCardUrl(verificationCardUrl);
		rcfg.setStatusUrl(statusUrl);
		rcfg.setRebillUrl(rebillUrl);
		rcfg.setCancelUrl(cancelUrl);
		rcfg.setMerchantId(merchantId);
		rcfg.setPrivateSecurityKey(privateSecurityKey);
		rcfg.setVerificationCardMerchantId(verificationCardMerchantId);
		rcfg.setVerificationCardPrivateSecurityKey(verificationCardPrivateSecurityKey);
		// TODO Auto-generated method stub

	}

	public String getBankCardUrl() {
		return bankCardUrl;
	}

	public void setBankCardUrl(String bankCardUrl) {
		this.bankCardUrl = bankCardUrl;
	}

	public String getVerificationCardUrl() {
		return verificationCardUrl;
	}

	public void setVerificationCardUrl(String verificationCardUrl) {
		this.verificationCardUrl = verificationCardUrl;
	}

	public String getStatusUrl() {
		return statusUrl;
	}

	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	public String getRebillUrl() {
		return rebillUrl;
	}

	public void setRebillUrl(String rebillUrl) {
		this.rebillUrl = rebillUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
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

	public Integer getVerificationCardMerchantId() {
		return verificationCardMerchantId;
	}

	public void setVerificationCardMerchantId(Integer verificationCardMerchantId) {
		this.verificationCardMerchantId = verificationCardMerchantId;
	}

	public String getVerificationCardPrivateSecurityKey() {
		return verificationCardPrivateSecurityKey;
	}

	public void setVerificationCardPrivateSecurityKey(
			String verificationCardPrivateSecurityKey) {
		this.verificationCardPrivateSecurityKey = verificationCardPrivateSecurityKey;
	}
	
	


}
