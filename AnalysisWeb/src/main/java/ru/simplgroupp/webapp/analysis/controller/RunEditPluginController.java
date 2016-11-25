package ru.simplgroupp.webapp.analysis.controller;

import javax.ejb.EJB;

import ru.simplgroupp.ejb.PluginConfig;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.toolkit.ejb.EJBUtil;
import ru.simplgroupp.webapp.analysis.plugin.controller.AbstractEditPluginController;

public class RunEditPluginController extends AbstractEditPluginController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7794498426098032341L;
	@EJB
	ReferenceBooksLocal refBooks;	
	
	public void init(PluginConfig plc) {
		this.plugin = plc;
		refBooks = (ReferenceBooksLocal) EJBUtil.findEJB("java:app/engine-ejb/ReferenceBooks!ru.simplgroupp.interfaces.ReferenceBooksLocal");
	}

	@Override
	public ReferenceBooksLocal getRefBooks() {
		return refBooks;
	}
}
