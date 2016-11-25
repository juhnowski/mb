package ru.simplgroupp.webapp.analysis.plugin.controller;

import java.io.Serializable;

abstract public class EditPluginExtController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8814184220726513035L;
	protected AbstractEditPluginController editCtl;

	public AbstractEditPluginController getEditCtl() {
		return editCtl;
	}

	public void setEditCtl(AbstractEditPluginController editCtl) {
		this.editCtl = editCtl;
		if (editCtl != null) {
			editCtl.extCtl = this;
			reload();
		}
	}
	
	abstract void reload();
	abstract void save();
}
