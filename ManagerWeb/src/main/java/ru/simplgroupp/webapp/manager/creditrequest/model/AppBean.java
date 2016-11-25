package ru.simplgroupp.webapp.manager.creditrequest.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.ejb.ApplicationAction;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.KassaRuntimeException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.exception.WorkflowRuntimeException;
import ru.simplgroupp.interfaces.BusinessObjectResult;
import ru.simplgroupp.interfaces.CreditBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.interfaces.service.CreditInfoService;
import ru.simplgroupp.persistence.CreditEntity;
import ru.simplgroupp.persistence.CreditRequestEntity;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.ChangeRequests;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.RefHeader;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.util.DecisionKeys;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.DecisionState;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

/**
 * Обёртка, чтобы выполнить в одной транзакции операции из разных ejb
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AppBean {

	@EJB
	KassaBeanLocal kassa;	
	
	@EJB
	WorkflowBeanLocal workflow;
	
	@EJB
	CreditBeanLocal creditBean;
	
    @EJB
    CreditDAO creditDAO;	
	
	@EJB
	AppServiceBeanLocal appServ;
		
	@EJB
	protected CreditInfoService creditInfo;
		
	@EJB
	protected ReferenceBooksLocal refBook;
	
	@EJB
	CreditRequestDAO creditRequestDAO;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveCreditApprove(int creditRequestId, String comment, String signalRef,String processDefKey) 
		throws KassaRuntimeException, KassaException, WorkflowRuntimeException, WorkflowException {
		Map<String, Object> mp = workflow.getProcessVariables(processDefKey, CreditRequest.class.getName(), new Integer(creditRequestId));
		DecisionState ds = (DecisionState) mp.get(ProcessKeys.VAR_DECISION_STATE);
		ds.finish(true, DecisionKeys.RESULT_ACCEPT);
		if (processDefKey.equals(ProcessKeys.DEF_CREDIT_CONSIDER)){
		    workflow.goProcCR(creditRequestId, signalRef, Utils.<String, Object>mapOf(ProcessKeys.VAR_DECISION_STATE, ds));
		} else {
			workflow.goProcCROffline(creditRequestId, signalRef, Utils.<String, Object>mapOf(ProcessKeys.VAR_DECISION_STATE, ds));
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveCreditRefuse(int creditRequestId, String comment, Integer rejectCode, String signalRef,String processDefKey) 
			throws KassaRuntimeException, KassaException, WorkflowRuntimeException, WorkflowException {
		Map<String, Object> mp = workflow.getProcessVariables(processDefKey, CreditRequest.class.getName(), new Integer(creditRequestId));
		DecisionState ds = (DecisionState) mp.get(ProcessKeys.VAR_DECISION_STATE);
		ds.finish(true, rejectCode);
		if (processDefKey.equals(ProcessKeys.DEF_CREDIT_CONSIDER)){
		    workflow.goProcCR(creditRequestId, signalRef, Utils.<String, Object>mapOf(ProcessKeys.VAR_DECISION_STATE, ds));
		} else {
			workflow.goProcCROffline(creditRequestId, signalRef, Utils.<String, Object>mapOf(ProcessKeys.VAR_DECISION_STATE, ds));
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public CreditRequestEntity saveCreditDecision(CreditRequestEntity ccRequest, String comment){
		ccRequest.setComment(comment);
		ccRequest.setDateDecision(new Date());
		ccRequest.setDecisionWayId(refBook.findByCodeIntegerEntity(RefHeader.EXECUTION_WAY, Reference.MANUAL_EXEC));
		ccRequest=creditRequestDAO.saveCreditRequest(ccRequest);
		return ccRequest;
	}
	
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void signalCredit(int creditId, String signal, ApplicationAction action) {
		try {
			CreditEntity ent = creditDAO.getCreditEntity(creditId);
			BusinessObjectResult bres = new BusinessObjectResult(Credit.class.getName(), new Integer(creditId), true, null);
			workflow.goProcCR(ent.getCreditRequestId().getId(), signal, Utils.<String, Object>mapOf(ProcessKeys.VAR_TASK_RESULT, bres));
		} catch (WorkflowException e) {
			FacesContext facesCtx = FacesContext.getCurrentInstance();
            JSFUtils.handleAsError(facesCtx, null, e);
		} finally {
			appServ.endApplicationAction(action);
		}
	}	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveClientRefuse(int creditRequestId, int userId, SignalRef cmdRefuse) throws KassaException, WorkflowException{
		BusinessObjectResult bres = new BusinessObjectResult(CreditRequest.class.getName(), new Integer(creditRequestId), true, null);
		kassa.saveClientRefuse(creditRequestId,userId);
		workflow.goProcCR(creditRequestId, cmdRefuse.toString(), Utils.<String, Object>mapOf(ProcessKeys.VAR_TASK_RESULT, bres));
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void questionsAnswered(int creditRequestId, String readyAction,String processDefKey) throws WorkflowRuntimeException, WorkflowException {
		BusinessObjectResult bres = new BusinessObjectResult(CreditRequest.class.getName(), new Integer(creditRequestId), true, null);
		if (processDefKey.equals(ProcessKeys.DEF_CREDIT_REQUEST)){
		    workflow.goProcCR(creditRequestId, readyAction, Utils.<String, Object>mapOf(ProcessKeys.VAR_TASK_RESULT, bres));
		} else {
			workflow.goProcCROffline(creditRequestId, readyAction, Utils.<String, Object>mapOf(ProcessKeys.VAR_TASK_RESULT, bres));
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void crEdited(int creditRequestId, String readyAction) throws WorkflowRuntimeException, WorkflowException {
		BusinessObjectResult bres = new BusinessObjectResult(CreditRequest.class.getName(), new Integer(creditRequestId), true, null);
		workflow.goProcCR(creditRequestId, readyAction, Utils.<String, Object>mapOf(ProcessKeys.VAR_TASK_RESULT, bres));
	}	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void ofertaSigned(int creditRequestId, String readyAction) throws WorkflowRuntimeException, WorkflowException {
		BusinessObjectResult bres = new BusinessObjectResult(CreditRequest.class.getName(), new Integer(creditRequestId), true, null);
		workflow.goProcCROffline(creditRequestId, readyAction, Utils.<String, Object>mapOf(ProcessKeys.VAR_TASK_RESULT, bres));
	}	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ChangeRequests saveChangeRequest(WorkflowObjectActionDef actionDef, Integer chRequestId, int creditRequestId,
			int userId, Date reqDate, String description) throws WorkflowException {
		ChangeRequests ch = creditInfo.saveChangeRequest(chRequestId, creditRequestId, userId, reqDate, description);

		BusinessObjectResult bres = new BusinessObjectResult(CreditRequest.class.getName(), new Integer(creditRequestId), true, null);
		workflow.goProc(CreditRequest.class.getName(),creditRequestId, actionDef, SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST, null, "msgChangeRequest"),
				Utils.<String, Object>mapOf(ProcessKeys.VAR_TASK_RESULT, bres, ProcessKeys.VAR_DATA, Utils.<String, Object>mapOf("createdNew", new Boolean(chRequestId == null)))
		);
		return ch;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ChangeRequests saveChangeRequest(WorkflowObjectActionDef actionDef, SignalRef signalRef, Integer chRequestId, int creditRequestId, boolean bConfirm) throws WorkflowException {
		ChangeRequests ch = creditInfo.saveChangeRequest(chRequestId, bConfirm);
		
		BusinessObjectResult bres = new BusinessObjectResult(CreditRequest.class.getName(), new Integer(creditRequestId), true, null);
		workflow.goProc(CreditRequest.class.getName(), creditRequestId, actionDef, signalRef.toString(), Utils.<String, Object>mapOf(ProcessKeys.VAR_TASK_RESULT, bres));
		return ch;
	}	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveCredit(Credit credit, String returnAction) throws WorkflowException {
		
		if (StringUtils.isNotEmpty(returnAction)) {
			Map<String, Object> mpParams = new HashMap<String,Object>(1);
			mpParams.put("lastStopPoint", returnAction);
			workflow.goProcCR(credit.getCreditRequest().getId(), "procCR::msgActivate", mpParams);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteCreditRequest(Integer creditRequestId) throws KassaException, WorkflowException {
					
		    kassa.deleteCreditRequest(creditRequestId);
			
	}
}
