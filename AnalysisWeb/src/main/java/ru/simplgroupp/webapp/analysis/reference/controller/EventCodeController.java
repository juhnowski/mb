package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.transfer.EventCode;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class EventCodeController  extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB 
	EventLogService eventLog;
	
	protected List<EventCode> listEventCode;
	protected String name;
	
	public void init(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()){
			getListEventCode();
		}
	}
	
	public void saveItems(ActionEvent event){
		eventLog.saveEventCode(listEventCode);
		getListEventCode();
	}
	
	public void addItem(ActionEvent event){
		eventLog.addEventCode(name);
		getListEventCode();
	}
	
	public List<EventCode> getListEventCode(){
		listEventCode=eventLog.getEventCodes();
		return listEventCode;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
