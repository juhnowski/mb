package ru.simplgroupp.webapp.analysis.eventlog.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.EventLog;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ViewLogController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2357677588089148687L;

	@EJB
	protected EventLogService eventLog;
	
	protected Integer refId;
    protected EventLog event;
	
    @PostConstruct
	public void init() 
	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (refId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("refid")) {
					String ss = prms.get("refid");
					refId = Convertor.toInteger(ss);
				}
			}
			if (refId != null) {
				reloadItem(facesCtx, refId);
			}
		}
	}
	
	protected void reloadItem(FacesContext facesCtx, Integer refId) {
		event=eventLog.getEventLog(refId, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
	}
	
	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}
	
	public EventLog getEvent(){
		return event;
	}
	
	public String close(){
		return "close";
	}
	
}
