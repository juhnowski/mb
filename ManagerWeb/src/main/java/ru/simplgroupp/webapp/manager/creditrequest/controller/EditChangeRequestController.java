package ru.simplgroupp.webapp.manager.creditrequest.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.service.CreditInfoService;
import ru.simplgroupp.persistence.ChangeRequestsEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.ChangeRequests;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

public class EditChangeRequestController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB
	protected KassaBeanLocal kassaBean;
	
	@EJB
	protected CreditInfoService creditInfo;
	
	@EJB
	protected ReferenceBooksLocal refBook;
	
	@EJB
	AppBean appBean;
	
	@EJB
	CreditRequestDAO crDAO;
	
	protected Integer prmCreditRequestId;
	protected CreditRequest creditRequest;
	protected ChangeRequests changeRequest;
	protected WorkflowObjectActionDef actionDef;
	
	@PostConstruct
	public void init() {
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed())
		{
			String prmActionDef = null;
			if (prmCreditRequestId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("ccRequestId")) {
					prmCreditRequestId = Convertor.toInteger(prms.get("ccRequestId"));
				}	
				if (prms.containsKey("prmBusinessObjectId")) {
					prmCreditRequestId = Convertor.toInteger(prms.get("prmBusinessObjectId"));
				}	
				prmActionDef =  prms.get("actionDef");
			}
			if (prmCreditRequestId != null) {
				reloadCreditRequest(facesCtx, prmCreditRequestId);
				changeRequest=initChangeRequest();
				actionDef = new WorkflowObjectActionDef();
				actionDef.loadFromString(prmActionDef);
			}			
		}
	}

	public String save(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try { 
			if (actionDef!=null)
			  appBean.saveChangeRequest(actionDef, changeRequest.getId(), prmCreditRequestId, userCtl.getUser().getId(), new Date(), changeRequest.getDescription());
			return "success";
		} catch (WorkflowException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		}
		
	}
	
	public String close(){
		return "canceled";
	}
	
	protected void reloadCreditRequest(FacesContext facesCtx, Integer prmId) {
		try {
			creditRequest = crDAO.getCreditRequest(prmId, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL,PeopleMain.Options.INIT_PEOPLE_CONTACT,PeopleMain.Options.INIT_DOCUMENT,PeopleMain.Options.INIT_ADDRESS));
		} catch (Exception e) {
			JSFUtils.handleAsError(facesCtx, null, e);
		}

	}
	
	private ChangeRequests initChangeRequest() {
		ChangeRequests ch= creditInfo.findChangeRequestWaitingEdit(prmCreditRequestId);
		if (ch == null) {
			ch = new ChangeRequests(new ChangeRequestsEntity());
			ch.setCreditRequest(creditRequest);
			ch.setUser(userCtl.getUser());
			ch.setRequestDate(new Date());			
		}		
		return ch;
	}
	
	public Integer getPrmCreditRequestId() {
		return prmCreditRequestId;
	}

	public void setPrmCreditRequestId(Integer prmCreditRequestId) {
		this.prmCreditRequestId = prmCreditRequestId;
	}
	
	public CreditRequest getCreditRequest() {
		return creditRequest;
	}
	
	public ChangeRequests getChangeRequest(){
		return changeRequest;
	}
	
	public void setChangeRequest(ChangeRequests changeRequest){
		this.changeRequest=changeRequest;
	}
}
