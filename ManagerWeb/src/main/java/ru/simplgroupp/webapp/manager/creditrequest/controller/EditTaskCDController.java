package ru.simplgroupp.webapp.manager.creditrequest.controller;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.interfaces.CreditCalculatorBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.ProductBeanLocal;
import ru.simplgroupp.interfaces.service.CreditOfferService;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.persistence.CreditRequestEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.EventCode;
import ru.simplgroupp.transfer.EventType;
import ru.simplgroupp.transfer.RefCreditRequestWay;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.util.ProductKeys;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;

public class EditTaskCDController extends EditCRActionController {
	
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -6730503115964621255L;

	private static Logger logger = Logger.getLogger(EditTaskCDController.class.getName());

	@EJB
	AppBean appBean;
	
	@EJB 
	EventLogService eventLog;
	 
	@EJB
	KassaBeanLocal kassaBean;
	
	@EJB
	CreditRequestDAO creditRequestDAO;
	
	@EJB
	CreditOfferService creditOfferService;
	
	@EJB
	ProductBeanLocal productBean;
	
	@EJB
	private CreditCalculatorBeanLocal creditCalc;
	 
	private String commentApprove;
	private String commentRefuse;
	private Integer rejectReasonCode;
	private Integer hasOffer=0;
	private Integer creditDays;
	private Integer creditSum;
	private Double creditStake;
	private Map<String, Object> limits;
	
	public void approveLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			if (hasOffer==1){
				try {
				    creditOfferService.saveNewCreditOffer(getCreditRequest().getId(), 
						getCreditRequest().getPeopleMain().getId(), editCtl.getUserCtl().getUser().getId(), 
						Reference.MANUAL_EXEC, null, creditDays, new Double(creditSum), creditStake, null);
				} catch (Exception e){
					logger.severe("Не удалось сохранить другое предложение условий по заявке "+getCreditRequest().getId());
				}
			}
			CreditRequestEntity ccRequest=creditRequestDAO.getCreditRequestEntity(getCreditRequest().getId());
			ccRequest=appBean.saveCreditDecision(ccRequest, commentApprove);
			
			if (ccRequest.getWayId().getCodeinteger()==RefCreditRequestWay.DIRECT){
				appBean.saveCreditApprove(getCreditRequest().getId(), commentApprove, SignalRef.toString(ProcessKeys.DEF_CREDIT_CONSIDER_OFFLINE, null, ProcessKeys.MSG_ACCEPT),ProcessKeys.DEF_CREDIT_CONSIDER_OFFLINE);
			} else {
		        appBean.saveCreditApprove(getCreditRequest().getId(), commentApprove, SignalRef.toString(ProcessKeys.DEF_CREDIT_CONSIDER, null, ProcessKeys.MSG_ACCEPT),ProcessKeys.DEF_CREDIT_CONSIDER);
			}
		    //пишем в лог
	          eventLog.saveLog("localhost", eventLog.getEventType(EventType.INFO).getEntity(),
	        		  eventLog.getEventCode(EventCode.GET_CREDIT_REQUEST_RESULT).getEntity(), 
	        		  StringUtils.isNotEmpty(commentApprove)?commentApprove:"Было ручное одобрение заявки", ccRequest,
	                  null, editCtl.getUserCtl().getUser().getEntity(),null,"","","","");
	        facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, null, "actionexecuted");
		} catch (Exception ex) {
			logger.log(Level.INFO, "Ошибка ", ex);
		    JSFUtils.handleAsError(facesCtx, null, ex);
		}	
	}
	
	public void refuseLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			CreditRequestEntity ccRequest=creditRequestDAO.getCreditRequestEntity(getCreditRequest().getId());
			
			ccRequest=appBean.saveCreditDecision(ccRequest, commentRefuse);
			if (ccRequest.getWayId().getCodeinteger()==RefCreditRequestWay.DIRECT){
				appBean.saveCreditRefuse(getCreditRequest().getId(), commentRefuse, rejectReasonCode, SignalRef.toString(ProcessKeys.DEF_CREDIT_CONSIDER_OFFLINE, null, ProcessKeys.MSG_REJECT),ProcessKeys.DEF_CREDIT_CONSIDER_OFFLINE);
			} else {
			    appBean.saveCreditRefuse(getCreditRequest().getId(), commentRefuse, rejectReasonCode, SignalRef.toString(ProcessKeys.DEF_CREDIT_CONSIDER, null, ProcessKeys.MSG_REJECT),ProcessKeys.DEF_CREDIT_CONSIDER);
			}
			    //пишем в лог
		          eventLog.saveLog("localhost", eventLog.getEventType(EventType.INFO).getEntity(),
		        		  eventLog.getEventCode(EventCode.GET_CREDIT_REQUEST_RESULT).getEntity(), 
		        		  StringUtils.isNotEmpty(commentRefuse)?commentRefuse:"Был ручной отказ по заявке", ccRequest,
		                  null, editCtl.getUserCtl().getUser().getEntity(),null,"","","","");
			facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, null, "actionexecuted");
		} catch (Exception ex) {
			logger.log(Level.INFO, "Ошибка ", ex);
			JSFUtils.handleAsError(facesCtx, null, ex);
		}			
	}
	public String getCommentApprove() {
		return commentApprove;
	}
	public void setCommentApprove(String commentApprove) {
		this.commentApprove = commentApprove;
	}
	public String getCommentRefuse() {
		return commentRefuse;
	}
	public void setCommentRefuse(String commentRefuse) {
		this.commentRefuse = commentRefuse;
	}

	public Integer getRejectReasonCode() {
		return rejectReasonCode;
	}

	public void setRejectReasonCode(Integer rejectReasonCode) {
		this.rejectReasonCode = rejectReasonCode;
	}

	public Integer getHasOffer() {
		return hasOffer;
	}

	public void setHasOffer(Integer hasOffer) {
		this.hasOffer = hasOffer;
	}

	public Integer getCreditDays() {
		return creditDays;
	}

	public void setCreditDays(Integer creditDays) {
		this.creditDays = creditDays;
	}

	public Integer getCreditSum() {
		return creditSum;
	}

	public void setCreditSum(Integer creditSum) {
		this.creditSum = creditSum;
	}

	public Double getCreditStake() {
		return creditStake;
	}

	public void changeOffer(ValueChangeEvent event){
		
    	hasOffer=Convertor.toInteger(event.getNewValue());
		logger.info("hasOffer="+hasOffer);
		
	}
	
    public void changeSum(ValueChangeEvent event){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		creditSum=Convertor.toInteger(event.getNewValue());
		
		if (creditSum!=null){
			
		    if (creditSum>editCtl.getCreditSumMax()||creditSum<editCtl.getCreditSumMin()){
		    	creditSum=editCtl.getCreditSumMin().intValue();
		    	UIComponent input=JSFUtils.findComponent(facesCtx, "sumOffer");
				if (input!=null){
				   	((UIInput) input).setValue(editCtl.getCreditSumMin());
				}
		        JSFUtils.handleAsError(facesCtx, "sumOffer", new Exception("Можно ввести сумму от "+editCtl.getCreditSumMin()+" до "+editCtl.getCreditSumMax()+" рублей"));
		    }
		   
		    logger.info("Предложенная сумма "+creditSum);
		}
    }
}
