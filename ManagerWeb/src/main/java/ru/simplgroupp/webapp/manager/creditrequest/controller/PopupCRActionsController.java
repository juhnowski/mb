package ru.simplgroupp.webapp.manager.creditrequest.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;



import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.webapp.manager.controller.AbstractPopupActionController;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

public class PopupCRActionsController extends AbstractPopupActionController<CreditRequest> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7171901095353840682L;
	
	@EJB
	protected KassaBeanLocal kassa;	
	
	@EJB
	CreditRequestDAO crDAO;
	
	protected void reloadData() {
		businessObject = crDAO.getCreditRequest(prmBusinessObjectId, Utils.setOf());
	}
	
	protected boolean handleStdAction(FacesContext facesCtx, int prmBusinessObjectId, WorkflowObjectActionDef actionDef) throws KassaException {
		
		if (ProcessKeys.MSG_REMOVE.equals(actionDef.getSignalRef())) {
			kassa.deleteCreditRequest(prmBusinessObjectId);
			facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, "#{actCR.executeLsn}", "actionexecuted");
			return true;
		}

		
		return false;
	}
	
}
