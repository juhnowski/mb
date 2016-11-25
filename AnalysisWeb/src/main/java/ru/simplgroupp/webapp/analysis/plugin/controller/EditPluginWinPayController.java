package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.WinpayPayPluginConfig;

public class EditPluginWinPayController extends EditPluginExtController {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 7813178139728149918L;

	private String secretKey;

    private String payUrl;

    private String statusUrl;

    private String balanceUrl;

	@Override
	void reload() {
		WinpayPayPluginConfig rcfg = (WinpayPayPluginConfig) editCtl.plugin;
		secretKey = rcfg.getSecretKey();
		payUrl = rcfg.getPayUrl();		
		statusUrl = rcfg.getStatusUrl();
		balanceUrl = rcfg.getBalanceUrl();
		// TODO Auto-generated method stub

	}

	@Override
	void save() {
		WinpayPayPluginConfig rcfg = (WinpayPayPluginConfig) editCtl.plugin;
		rcfg.setSecretKey(secretKey);
		rcfg.setPayUrl(payUrl);
		rcfg.setStatusUrl(statusUrl);
		rcfg.setBalanceUrl(balanceUrl);
		// TODO Auto-generated method stub

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

	public String getStatusUrl() {
		return statusUrl;
	}

	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	public String getBalanceUrl() {
		return balanceUrl;
	}

	public void setBalanceUrl(String balanceUrl) {
		this.balanceUrl = balanceUrl;
	}

}
