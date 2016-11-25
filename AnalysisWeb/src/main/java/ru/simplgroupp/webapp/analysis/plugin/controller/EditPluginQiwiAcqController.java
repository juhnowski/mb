package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.QiwiAcquiringPluginConfig;

public class EditPluginQiwiAcqController extends EditPluginExtController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5301295527766122566L;
	
	private String orderUrl;

    private String statusUrl;

    private String payUrl;

    private String login;

    private String password;

    private String providerId;

    private int connectTimeout;

    private int lifeTime; //храним в часах	

	@Override
	void reload() {
		QiwiAcquiringPluginConfig rcfg = (QiwiAcquiringPluginConfig) editCtl.plugin;
		orderUrl = rcfg.getOrderUrl();
		statusUrl = rcfg.getStatusUrl();
		payUrl = rcfg.getPayUrl();
		login = rcfg.getLogin();
		password = rcfg.getPassword();
		providerId = rcfg.getProviderId();
		connectTimeout = rcfg.getConnectTimeout();
		lifeTime = rcfg.getLifeTime();
		// TODO Auto-generated method stub

	}

	@Override
	void save() {
		QiwiAcquiringPluginConfig rcfg = (QiwiAcquiringPluginConfig) editCtl.plugin;
		rcfg.setOrderUrl(orderUrl);
		rcfg.setStatusUrl(statusUrl);
		rcfg.setPayUrl(payUrl);
		rcfg.setLogin(login);
		rcfg.setPassword(password);
		rcfg.setProviderId(providerId);
		rcfg.setConnectTimeout(connectTimeout);
		rcfg.setLifeTime(lifeTime);
		// TODO Auto-generated method stub

	}

	public String getOrderUrl() {
		return orderUrl;
	}

	public void setOrderUrl(String orderUrl) {
		this.orderUrl = orderUrl;
	}

	public String getStatusUrl() {
		return statusUrl;
	}

	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
	}

}
