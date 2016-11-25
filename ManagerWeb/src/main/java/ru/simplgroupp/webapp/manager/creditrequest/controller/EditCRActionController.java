package ru.simplgroupp.webapp.manager.creditrequest.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.persistence.BizActionEntity;
import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.rules.AbstractContext;
import ru.simplgroupp.rules.CreditRequestContext;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.controller.UserDataController;
import ru.simplgroupp.webapp.manager.controller.ActionInfo;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.SignalRef;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;
import ru.simplgroupp.workflow.WorkflowObjectState;
import ru.simplgroupp.workflow.WorkflowObjectStateDef;

abstract public class EditCRActionController extends AbstractSessionController implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4219878275523975593L;

	private static Logger logger = Logger.getLogger(EditCRActionController.class.getName());
	
	@EJB
	AppBean appBean;	
	
	@EJB 
	EventLogService eventLog;
	 
	@EJB
	KassaBeanLocal kassaBean;
	
	protected EditCreditRequestController editCtl;
	protected WorkflowObjectStateDef prmCurrentState;
	protected String excludedActions;
		
	public EditCreditRequestController getEditCtl() {
		return editCtl;
	}

	public void setEditCtl(EditCreditRequestController editCtl) {
		this.editCtl = editCtl;
	}
	
	public CreditRequest getCreditRequest() {
		return editCtl.creditRequest;
	}
	
	public UserDataController getUserCtl() {
		return editCtl.getUserCtl();
	}

	public WorkflowObjectStateDef getPrmCurrentState() {
		return prmCurrentState;
	}

	public void setPrmCurrentState(WorkflowObjectStateDef prmCurrentState) {
		this.prmCurrentState = prmCurrentState;
	}

	public String getExcludedActions() {
		return excludedActions;
	}

	public void setExcludedActions(String excludedActions) {
		this.excludedActions = excludedActions;
	}
	
	public List<WorkflowObjectActionDef> getStdActionsDefs() {
		String[] excl = null;
		if (excludedActions == null) {
			excl = new String[0];
		} else {
			excl = excludedActions.split(",");
		}
				
		List<WorkflowObjectActionDef> lstRes = new ArrayList<WorkflowObjectActionDef>(3);
		for (WorkflowObjectState state: editCtl.snapshot.getStates()) {
			for (WorkflowObjectActionDef actDef: state.getDefinition().getActions()) {
				if (actDef.isEnabled()) {
					if (Arrays.binarySearch(excl, actDef.getSignalRef() ) < 0) {
						lstRes.add(actDef);
					}
				}
			}
			
		}
		
/*		
		for (WorkflowObjectActionDef actDef: editCtl.snapshot.getObjectActions()) {
			if (actDef.isEnabled()) {
				if (Arrays.binarySearch(excl, actDef.getSignalRef() ) < 0) {
					lstRes.add(actDef);
				}
			}
		}		
*/		
		return lstRes;
	}
	
	protected boolean handleStdAction(FacesContext facesCtx, ActionEvent event, SignalRef signalRef, WorkflowObjectActionDef actionDef) throws Exception {
/*		
		if (ProcessKeys.MSG_CLIENT_REJECT.equals(signalRef.getName())) {
			appBean.saveClientRefuse(editCtl.getCreditRequest().getId(),editCtl.getUserCtl().getUser().getId(), signalRef);
		    facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, null, "actionexecuted");
			return true;
		}
*/		
		// TODO		
		return false;
	}
	
	public void stdActionLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {	
			
			String atrSignalRef = (String) event.getComponent().getAttributes().get("signalRef");
			SignalRef ref = SignalRef.valueOf(atrSignalRef);
			String atrActionDef = (String) event.getComponent().getAttributes().get("actionDef");
			WorkflowObjectActionDef actionDef = WorkflowObjectActionDef.valueOf(atrActionDef);
			handleStdAction(facesCtx, event, ref, actionDef);
		} catch (Exception ex) {
			logger.log(Level.INFO, "Ошибка ", ex);
			JSFUtils.handleAsError(facesCtx, null, ex);
		}	
	}
	
	public Integer getPrmBusinessObjectId() {
		return editCtl.getCreditRequest().getId();
	}
	

	public List<ActionInfo> getActionsBO() {
		List<ActionInfo> actionsBO = new ArrayList<ActionInfo>(3);
		//если найдены действия
		if (editCtl.snapshot.getObjectActions().size() > 0) {
			  for (WorkflowObjectActionDef def: editCtl.snapshot.getObjectActions()) {
				  if (! def.isEnabled() ) {
					continue;
				  }
				
				  ActionInfo info = new ActionInfo(editCtl.appDataCtl);
				  info.action = def;
				  info.mapIndex = editCtl.appDataCtl.findActionMap(def);
				  if (info.mapIndex >= 0) {
					  actionsBO.add(info);	
				  }				
			  }				
		}
		return actionsBO;
	}
	
	protected boolean handleStdAction(FacesContext facesCtx, int prmBusinessObjectId, WorkflowObjectActionDef actionDef) throws KassaException {
		return false; // TODO
	}	
	

	public void executeBOLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Integer prmBusinessObjectId = ((Number) event.getComponent().getAttributes().get("prmBusinessObjectId")).intValue();
		WorkflowObjectActionDef actionDef = WorkflowObjectActionDef.valueOf((String) event.getComponent().getAttributes().get("actionDef"));
		
		try {
			if (! handleStdAction(facesCtx, prmBusinessObjectId, actionDef) ) {
				if ( ! actionDef.isForProcess()) {
					BizActionEntity bizAct = editCtl.bizDAO.findBizObjectAction(actionDef.getBusinessObjectClass(), actionDef.getSignalRef());
					if (bizAct != null) {
						AbstractContext ctx =  new CreditRequestContext();
						ctx.setCurrentUser(editCtl.getUserCtl().getUser());
						
						editCtl.bizProc.executeBizAction(bizAct, prmBusinessObjectId, ctx);
						facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, null, "actionexecuted");
					}
				}
			}
		} catch (KassaException ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
		} catch (ActionException ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
		}		
	}	
}
