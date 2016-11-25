package ru.simplgroupp.webapp.manager.callback.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.CallBack;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ViewCallbackController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9195997662084204832L;

	@EJB
	PeopleBeanLocal peopleBean;
	
	@EJB
	PeopleDAO peopleDAO;
	
	protected CallBack callBack;
	protected Integer callId;
	
	@PostConstruct
	public void init()	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (callId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("callid")) {
					callId = Convertor.toInteger(prms.get("callid"));
				}
			
			}
			if (callId != null) {
				reloadItem(facesCtx,callId);
			}	
		}
	}
	
	protected void reloadItem(FacesContext facesCtx,Integer id){
		callBack=peopleDAO.getCallBack(id);
	}

	public Integer getCallId() {
		return callId;
	}

	public void setCallId(Integer callId) {
		this.callId = callId;
	}

	public CallBack getCallBack() {
		return callBack;
	}
	
	public String close(){
		return "index?faces-redirect=true";
	}
}
