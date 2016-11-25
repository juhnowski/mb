package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.QiwiPayPluginConfig;

public class EditPluginQiwiPayController extends EditPluginExtController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7905850286920308052L;
	private Long terminalId = 56212L;

	@Override
	void reload() {
		QiwiPayPluginConfig rcfg = (QiwiPayPluginConfig) editCtl.plugin;
		terminalId = rcfg.getTerminalId();
		// TODO Auto-generated method stub

	}

	@Override
	void save() {
		QiwiPayPluginConfig rcfg = (QiwiPayPluginConfig) editCtl.plugin;
		rcfg.setTerminalId(terminalId);
		// TODO Auto-generated method stub

	}

	public Long getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(Long terminalId) {
		this.terminalId = terminalId;
	}


}
