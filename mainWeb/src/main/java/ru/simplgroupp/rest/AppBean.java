package ru.simplgroupp.rest;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.KassaRuntimeException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.exception.WorkflowRuntimeException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.service.CreditRequestWizardService;
import ru.simplgroupp.transfer.creditrequestwizard.Step0Data;
import ru.simplgroupp.workflow.SignalRef;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import java.util.Map;

@Stateless
@LocalBean
public class AppBean {
	
	@EJB
	AppInitializer appInit;
	
    @EJB
    protected WorkflowBeanLocal workflowBean;

    @EJB
    protected KassaBeanLocal kassaBean;

    @EJB
    protected CreditRequestWizardService wizardService;
    
    @EJB
    protected UsersBeanLocal usersBean;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)  
    public int saveStep7(Map<String, Object> step) throws KassaException {
    	int creditRequestId = kassaBean.saveStep7(step);
    	return creditRequestId;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)    
    public Integer doStep7(int creditRequestId, Map<String, Object> step) throws KassaRuntimeException, KassaException {

        // форма заполнена, продолжить процесс
       try {
//        	List<WorkflowObjectStateDef> lstAct = workflowBean.getProcCRWfActions(creditRequestId, null, true);
        	String msgMore = SignalRef.toString("procNewCR", null, "msgMore");
			workflowBean.goProcCR(creditRequestId, msgMore, null);
		} catch (WorkflowRuntimeException e) {
			throw new KassaRuntimeException(e);
		} catch (WorkflowException e) {
			throw new KassaException(e);
		}        
        return creditRequestId;
    }    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)    
    public Integer doStep0(Integer inputCreditRequestId, Step0Data step0) throws KassaRuntimeException, KassaException {     
    	Integer creditRequestId = null;
        try {
        	creditRequestId = wizardService.saveStep0(inputCreditRequestId, step0);
			workflowBean.startOrFindProcCR(creditRequestId, null);
		} catch (WorkflowRuntimeException e) {
			throw new KassaRuntimeException(e);
		} catch (WorkflowException e) {
			throw new KassaException(e);
		} catch (KassaException e){
			throw new KassaException(e);
		}
    	return creditRequestId;
    }
    
}
