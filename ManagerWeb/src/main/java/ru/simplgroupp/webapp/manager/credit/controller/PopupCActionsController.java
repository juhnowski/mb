package ru.simplgroupp.webapp.manager.credit.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.CreditBeanLocal;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.webapp.manager.controller.AbstractPopupActionController;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

public class PopupCActionsController extends AbstractPopupActionController<Credit> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7657543337825543692L;
	
	@EJB
	CreditBeanLocal creditBean;
	
    @EJB
    CreditDAO creditDAO;	
	
	@EJB
	AppBean appBean;
	
	@EJB
	AppServiceBeanLocal appServ;

	@Override
	protected void reloadData() throws KassaException {
		businessObject = creditDAO.getCredit(prmBusinessObjectId, Utils.setOf());
	}

	@Override
	protected boolean handleStdAction(FacesContext facesCtx,
			int prmBusinessObjectId, WorkflowObjectActionDef actionDef)
			throws KassaException {
/*		
		if (ProcessKeys.MSG_SUSPEND.equals(actionDef.getSignalRef())) {
	    	ApplicationAction action = appServ.startApplicationAction(
	    			SignalRef.toString(null, null, ProcessKeys.MSG_SUSPEND), true, "Приостановка кредита", 
	    			new BusinessObjectRef(Credit.class.getName(), prmBusinessObjectId)
	    			);
	    	if (action == null) {
	    		JSFUtils.handleAsError(facesCtx, null, new Exception("Приостановка кредита уже происходит"));
	    		return true;
	    	} 
	    	
			appBean.signalCredit(prmBusinessObjectId, actionDef.getSignalRef(), action);
			facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, "#{actC.executeLsn}", "actionexecuted");
			return true;
		}
		
		if (ProcessKeys.MSG_ACTIVATE.equals(actionDef.getSignalRef())) {
	    	ApplicationAction action = appServ.startApplicationAction(
	    			SignalRef.toString(null, null, ProcessKeys.MSG_ACTIVATE), true, "Перезапуск кредита", 
	    			new BusinessObjectRef(Credit.class.getName(), prmBusinessObjectId)
	    			);
	    	if (action == null) {
	    		JSFUtils.handleAsError(facesCtx, null, new Exception("Перезапуск кредита уже происходит"));
	    		return true;
	    	} 
	    	
	    	appBean.signalCredit(prmBusinessObjectId, actionDef.getSignalRef(), action);
	    	
			facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, "#{actC.executeLsn}", "actionexecuted");
			return true;
		}	
*/		
		return false;
	}

}
