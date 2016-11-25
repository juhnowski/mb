package ru.simplgroupp.rest.api.service;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.faces.context.FacesContext;

import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.ejb.ApplicationAction;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.dao.interfaces.*;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.toolkit.common.GenUtils;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.persistence.*;
import ru.simplgroupp.util.*;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AppBeanService {

	@EJB
	KassaBeanLocal kassa;	
	
	@EJB
	CreditBeanLocal creditBean;
	
	@EJB
	WorkflowBeanLocal workflow;
	
	@EJB 
	AppServiceBeanLocal appServ;
	
	@EJB
	BizActionProcessorBeanLocal bizProc;
	
	@EJB
	OfficialDocumentsDAO officialDocumentsDAO;
	
	@EJB
	private UsersBeanLocal userBean;
	  
	@EJB
	private CreditRequestDAO crDAO;
	
	@EJB
    private EventLogService eventLogService;
	
	@EJB
    private PeopleBeanLocal peopleBean;

	@EJB
	private GeneratorService generatorService;
	
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveProlongRequest(int creditId, 
			int creditRequestId, Prolong prolong, 
			String ipAddr, String userAgent, 
			ApplicationAction action) throws WorkflowException, 
			KassaException {
		
		try {
			ProlongEntity plong=kassa.saveLongRequestNew(creditId, prolong, prolong.getLongDate(), ipAddr,userAgent);
			//workflow.goProcCR(creditRequestId, SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST, null, ProcessKeys.MSG_PROLONG), Collections.<String, Object>emptyMap());
			WorkflowObjectActionDef actionDef = new WorkflowObjectActionDef(false);
			actionDef.setBusinessObjectClass(Prolong.class.getName());
			actionDef.setSignalRef(ProcessKeys.MSG_PROLONG);
			actionDef.setRunProcessDefKey(ProcessKeys.DEF_CREDIT_PROLONG);
			workflow.goProc(Prolong.class.getName(), plong.getId(), actionDef, SignalRef.toString("*", null, "msgNone"), Utils.mapOfSO("creditId", creditId ));
		} catch (WorkflowException e) {
			throw e;
		} catch (KassaException e) {
			throw e;
		} finally {
			appServ.endApplicationAction(action);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public RefinanceEntity saveRefinanceRequest(int creditId, 
			Refinance refinance,  ApplicationAction action) throws WorkflowException, 
			KassaException{
		
		WorkflowObjectActionDef actionDef = new WorkflowObjectActionDef(false);
		actionDef.setBusinessObjectClass(Refinance.class.getName());
		actionDef.setSignalRef(ProcessKeys.MSG_REFINANCE_RUN);
		actionDef.setRunProcessDefKey(ProcessKeys.DEF_REFINANCE);

		try {
			RefinanceEntity refin = creditBean.saveRefinanceRequest(creditId, refinance, refinance.getRefinanceDate());
			workflow.goProc(Refinance.class.getName(), refin.getId(), actionDef, SignalRef.toString("*", null, "msgNone"), Utils.mapOfSO("creditId", creditId ));
			return refin;
		}catch (KassaException e) {
			throw e;
		} catch (WorkflowException e) {
			throw e;
		} 
		finally {
			appServ.endApplicationAction(action);
		}
	}
	
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void cancelProlong(Prolong prolong, 
			int creditRequestId, ApplicationAction action) throws WorkflowException{
		try {
			workflow.goProcCR(creditRequestId, SignalRef.toString(ProcessKeys.DEF_CREDIT_PROLONG, null, ProcessKeys.MSG_PROLONG), Collections.<String, Object>emptyMap());
		} catch (WorkflowException e) {
			throw e;
		} finally {
			appServ.endApplicationAction(action);
		}		
	}
	
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveClientRefuse(int creditRequestId, int userId,ApplicationAction action) throws KassaException, WorkflowException{
		try {
			kassa.saveClientRefuse(creditRequestId,userId);
			workflow.goProcCR(creditRequestId, SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST, null, ProcessKeys.MSG_CLIENT_REJECT), Collections.<String, Object>emptyMap());
		} finally {
			appServ.endApplicationAction(action);
		}		
	}
	
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)	
	public void newCredit(CreditRequest cre, 
			String ipAddr, String userAgent, 
			int clientUserId, 
			ApplicationAction action, 
			Map<String, String> behdata) throws WorkflowException, 
			KassaException{
		try {
			int id = kassa.saveCreditRequest(cre, ipAddr, userAgent, behdata);			
			ProcessInstance pinst = workflow.startOrFindProcCR(id, clientUserId);
		} catch (WorkflowException e) {
			throw e;
		} catch (KassaException e) {
			throw e;
		} finally {
			appServ.endApplicationAction(action);
		}			
	}
	
    @Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)	
	public void newCredit(Integer creditRequestId,Integer clientUserId, 
			ApplicationAction action) throws WorkflowException{
		try {
					
			ProcessInstance pinst = workflow.startOrFindProcCR(creditRequestId, clientUserId);
		} catch (WorkflowException e) {
			throw e;
		
		} finally {
			appServ.endApplicationAction(action);
		}			
	}

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)	
	public void executeBusinessAction(String bisunessClassName, String signalRef, Object businessObjectId) {
//		bizProc.e
	}
	
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void startCredit(int creditRequestId, ApplicationAction action) throws WorkflowException{

		try {
			workflow.goProcCR(creditRequestId, SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST, null, ProcessKeys.MSG_ACCEPT), Collections.<String, Object>emptyMap());
		} catch (WorkflowException e) {
			throw e;			
		} finally {
			appServ.endApplicationAction(action);
		}		
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)	
	public OfficialDocumentsEntity saveDocuments(OfficialDocuments document){
		document=officialDocumentsDAO.saveOfficialDocument(document);
		OfficialDocumentsEntity doc=officialDocumentsDAO.getOfficialDocumentEntity(document.getId());
		return doc;
		
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)	
	public UsersEntity saveUser(CreditRequestEntity ccRequest,String login,String ipAddress){

         
         PeopleMainEntity peopleMain = ccRequest.getPeopleMainId();
         Integer i = generatorService.findMaxCreditRequestNumber(new Date());
         UsersEntity user=null;

         try{

          CreditRequest crt=crDAO.getCreditRequest(ccRequest.getId(), Utils.setOf());
          String agreement ="";
          if (crt!=null) {
            agreement = kassa.generateAgreement(crt, new Date(),0);
          }

          ccRequest=kassa.newCreditRequest(ccRequest,peopleMain.getId(),CreditStatus.FILLED,
        	true,true,true,true,true,null,null,new Date(),new Date(),new Date(),
        	i,ccRequest.getCreditsum(), ccRequest.getCreditdays(),
    		ccRequest.getStake(),agreement,null,generatorService.genUniqueNumber(new Date(), i, ccRequest.getStake() * 100),
                null,null,RefCreditRequestWay.ONLINE,null);

         
          if (StringUtils.isNotEmpty(login)) {
               
            	   user=userBean.addUserClient(peopleMain,login);

          }
        
          eventLogService.saveLog(ipAddress, eventLogService.getEventType(EventType.INFO).getEntity(), eventLogService.getEventCode(EventCode.SAVE_CREDIT_REQUEST).getEntity(), 
           "", ccRequest, peopleMain, null,null,"","","","");
       } catch (Exception e) {
          
          
       }
   
       return user;
    }
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CreditRequest saveCreditRequest(CreditRequest creditRequest){
		CreditRequest crequest=kassa.saveCreditRequest(creditRequest);
		return crequest;
	}
	
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void startCreditNew(int creditRequestId) throws KassaException,WorkflowException{
		ApplicationAction action = appServ.startApplicationAction(SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST,
                null, ProcessKeys.MSG_ACCEPT), true, "Подписание оферты", new BusinessObjectRef(
                CreditRequest.class.getName(), creditRequestId));
        if (action == null) {
            throw new KassaException("Подписание оферты уже обрабатывается");
        }
		try {
			
			workflow.goProcCR(creditRequestId, SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST, null, ProcessKeys.MSG_ACCEPT), Collections.<String, Object>emptyMap());
		} catch (WorkflowException e) {
			throw e;			
		} finally {
			appServ.endApplicationAction(action);
		}		
	}
}
