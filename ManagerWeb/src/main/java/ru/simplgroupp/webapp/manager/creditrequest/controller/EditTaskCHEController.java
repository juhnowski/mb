package ru.simplgroupp.webapp.manager.creditrequest.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.service.CreditInfoService;
import ru.simplgroupp.transfer.ChangeRequests;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

public class EditTaskCHEController extends EditCRActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	AppBean appBean;
	
	@EJB
	WorkflowBeanLocal workflow;	
	
	@EJB
	protected CreditInfoService creditInfo;
	
	private ChangeRequests changeRequest; 
	
	@PostConstruct
	public void init(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
	
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			changeRequest = creditInfo.findChangeRequestWaitingEdit(getCreditRequest().getId());
		}
	}
	
	public ChangeRequests getChangeRequest() {
		return changeRequest;
	}

	@Override
	protected boolean handleStdAction(FacesContext facesCtx, ActionEvent event,
			SignalRef signalRef, WorkflowObjectActionDef actionDef) throws Exception {
		
		boolean bHandled = super.handleStdAction(facesCtx, event, signalRef, actionDef);
		if (bHandled) {
			return true;
		}
		
		if (ProcessKeys.MSG_ACCEPT.equalsIgnoreCase(signalRef.getName())) {
			appBean.saveChangeRequest(actionDef, signalRef, changeRequest.getId(), editCtl.getCreditRequest().getId(), ProcessKeys.MSG_ACCEPT.equals(signalRef.getName()));
			facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, null, "actionexecuted");
			return true;
		}
		
		return false;
	}

}
