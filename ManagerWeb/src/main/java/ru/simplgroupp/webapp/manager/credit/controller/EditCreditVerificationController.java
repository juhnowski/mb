package ru.simplgroupp.webapp.manager.credit.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.ejb.EnvironmentSnapshot;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.CreditBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.rules.AbstractContext;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.BaseCredit;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.EventCode;
import ru.simplgroupp.transfer.EventLog;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.util.AppUtil;
import ru.simplgroupp.util.WorkflowUtil;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;
import ru.simplgroupp.workflow.StateRef;
import ru.simplgroupp.workflow.WorkflowObjectState;
import ru.simplgroupp.workflow.WorkflowObjectStateDefExt;

public class EditCreditVerificationController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2189105267003064281L;

	@EJB
	CreditBeanLocal creditBean;
	
	@EJB
	CreditDAO creditDAO;
	
	@EJB
	AppBean appBean;
	
    @EJB 
    EventLogService eventLog;	
    
	@EJB
	AppServiceBeanLocal appServ;   
	
	@EJB
	WorkflowBeanLocal wfBean;
	
	protected Integer prmCreditId;
	
	protected Credit credit;
	
    protected EnvironmentSnapshot snapshot;
	protected List<WorkflowObjectState> actionStates;	
	protected WorkflowObjectStateDefExt currentState;
	protected String returnAction;
	
	@PostConstruct
	public void init() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			try {
				if (prmCreditId == null) {
					Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
					if (prms.containsKey("creditid")) {
						prmCreditId = Convertor.toInteger(prms.get("creditid"));
					}		
					if (prms.containsKey("prmBusinessObjectId")) {
						prmCreditId = Convertor.toInteger(prms.get("prmBusinessObjectId"));
					}	
				}
				if (prmCreditId != null) {
					reloadCredit(facesCtx, prmCreditId);
				}
			} catch (KassaException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			}
		}				
	}
	
	private void reloadCurrentState() {
		WorkflowObjectState st = WorkflowUtil.findWFState1(actionStates, new StateRef("procCR", "*", "*"));
		if (st == null) {
			return;
		}
		currentState = creditBean.getCreditStates().get(st.getDefinition().getStateRef().toString());
	}
	
	private void reloadCredit(FacesContext facesCtx, Integer prmCreditId2) throws KassaException {
		credit = creditDAO.getCredit(prmCreditId2, Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST,
                    PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_DOCUMENT,
                    BaseCredit.Options.INIT_PROLONGS, BaseCredit.Options.INIT_PAYMENTS));
		
		EventLog accLog = eventLog.findLatestByCreditId(eventLog.getEventCode(EventCode.STOP_CREDIT).getId(), credit.getId() );
		credit.setAccidentLog(accLog);
		reloadActionStates();
		reloadCurrentState();
	}
	
	private void reloadActionStates() {
		AbstractContext context = AppUtil.getDefaultContext(credit, Credit.class.getName());
		context.setCurrentUser(userCtl.getUser());
		
		snapshot = appServ.getSnapshot(context, CreditRequest.class.getName(), true, true);
		actionStates = snapshot.getStates();
	} 	

	public String save() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			creditBean.saveCredit(credit);
			appBean.saveCredit(credit, returnAction);
			return "success";
		} catch (WorkflowException ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
			return null;
		}
	}
	
	public void prolongCancelLsn(ActionEvent event) {
		Number nid = (Number) event.getComponent().getAttributes().get("prolongid");
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			wfBean.goProcCR(credit.getCreditRequest().getId(), SignalRef.toString(ProcessKeys.DEF_CREDIT_PROLONG, null, ProcessKeys.MSG_PROLONG_CANCEL), null);
		} catch (WorkflowException ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
		}
	}
	
	public Integer getPrmCreditId() {
		return prmCreditId;
	}
	public void setPrmCreditId(Integer prmCreditId) {
		this.prmCreditId = prmCreditId;
	}
	public Credit getCredit() {
		return credit;
	}	
	
	public WorkflowObjectStateDefExt getCurrentState() {
		return currentState;
	}

	public String getReturnAction() {
		return returnAction;
	}

	public void setReturnAction(String returnAction) {
		this.returnAction = returnAction;
	}
}
