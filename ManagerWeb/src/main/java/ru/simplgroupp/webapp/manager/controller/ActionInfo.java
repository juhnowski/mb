package ru.simplgroupp.webapp.manager.controller;

import ru.simplgroupp.workflow.WorkflowObjectActionDef;

public class ActionInfo {
	public WorkflowObjectActionDef action;
	public int mapIndex = -1;
	public AppDataController appData;
	
	public ActionInfo(AppDataController appData) {
		this.appData = appData;
	}
	
	public WorkflowObjectActionDef getAction() {
		return action;
	}
	
	
	
	public boolean isLink() {
		return appData.getIsLink(mapIndex);
	}
	public String getLinkOutcome() {
		return appData.getOutcome(mapIndex);
	}
	public boolean isRedirect() {
		return appData.getRedirect(mapIndex);
	}
	public String getConfirmMessage() {
		return appData.getConfirmMessage(mapIndex);
	}
	public String getConfirmScript() {
		String smsg = appData.getConfirmMessage(mapIndex);
		if (smsg == null) {
			return "return true; ";
		} else {
			return "if (! confirm('"+ smsg + "') ) { return false;}; return true;";
		}			 
	}
}
