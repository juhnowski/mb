package ru.simplgroupp.webapp.manager.creditrequest.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.QuestionBeanLocal;
import ru.simplgroupp.interfaces.VerificationBeanLocal;
import ru.simplgroupp.interfaces.WorkflowEngineBeanLocal;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.persistence.CreditRequestEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.EventCode;
import ru.simplgroupp.transfer.EventType;
import ru.simplgroupp.transfer.Question;
import ru.simplgroupp.transfer.QuestionAnswer;
import ru.simplgroupp.transfer.QuestionVariant;
import ru.simplgroupp.transfer.RefCreditRequestWay;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;
import ru.simplgroupp.workflow.WorkflowObjectState;

public class EditTaskQAController extends EditCRActionController {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7917808657108702600L;

	private static Logger logger = Logger.getLogger(EditTaskQAController.class.getName());

	@EJB
	AppBean appBean;
	
	@EJB
	QuestionBeanLocal questBean;
	
	@EJB 
	EventLogService eventLog;
	 
	@EJB
	KassaBeanLocal kassaBean;
	
	@EJB
	WorkflowEngineBeanLocal wfEngine;
	
	@EJB
	CreditRequestDAO creditRequestDAO;
	
	
	private List<QuestionAnswer> questList;
	private int currentQuestion;
	private int currentQuestionType;
	private String currentString;
	private String thisString;
	private Long currentNumber;
	private Long thisNumber;
	private Double currentMoney;
	private Double thisMoney;
	private Date currentDate;
	private Date thisDate;
	private Integer currentSingle;
	private Integer thisSingle;
	
	@PostConstruct
	public void init(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()){
			reloadQuestList();
		  getQuestList();
		}
	}
	
	private void reloadQuestList() {
		for (WorkflowObjectState state: editCtl.actionTaskStates) {
			if (state.getTask().getTaskDefinitionKey().equals("taskExecuteSingle")) {
				String exId = state.getTask().getExecutionId();
				Map<String, Object> varsLocal = wfEngine.getExecutionVariables(exId);
				Map<String, Object> varData = (Map<String, Object>) varsLocal.get(ProcessKeys.VAR_DATA);
				List<String> verq = null;
				if (varData == null) {
					verq = null;
				} else {
					verq = (List<String>) varData.get("verify");
				}
				questList = questBean.listQuestionAnswer(0, 0, null, null, getCreditRequest().getId(), null);
				if (questList.size()==0) {
					questList = questBean.addQAList(getCreditRequest().getId(), verq);
				}		
				break;
			}
		}
	}
	
	public List<QuestionAnswer> getQuestList() {
		
		return questList;
	}
	
	public void readyLsn(ActionEvent event) {
		String readyAction = SignalRef.toString(ProcessKeys.DEF_SUB_STANDART, VerificationBeanLocal.SYSTEM_NAME, "msgMore");
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			
			CreditRequestEntity ccRequest=creditRequestDAO.getCreditRequestEntity(getCreditRequest().getId());
			if (ccRequest.getWayId().getCodeinteger()==RefCreditRequestWay.DIRECT){
			    appBean.questionsAnswered(getCreditRequest().getId(), readyAction,ProcessKeys.DEF_CREDIT_REQUEST_OFFLINE);
			} else {
				appBean.questionsAnswered(getCreditRequest().getId(), readyAction,ProcessKeys.DEF_CREDIT_REQUEST);
			}
			 
			    //пишем в лог
		          eventLog.saveLog("localhost", eventLog.getEventType(EventType.INFO).getEntity(),
		        		  eventLog.getEventCode(EventCode.VERIFICATOR_QUESTIONS).getEntity(), 
		        		  "Были заданы вопросы по заявке", ccRequest,
		                  null, editCtl.getUserCtl().getUser().getEntity(),null,"","","","");		
			facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, null, "actionexecuted");
		} catch (Exception ex) {
			logger.log(Level.INFO, "Ошибка ", ex);
			JSFUtils.handleAsError(facesCtx, null, ex);
		}	
	}
	
	public void answerDeny(ActionEvent event) {
		Integer qaid = Convertor.toInteger(event.getComponent().getAttributes().get("qaid"));
		if (qaid!=null){
		   questBean.saveQARefuse(getCreditRequest().getId(), questBean.getQuestion(qaid).getQuestionCode());
		}
		reloadQuestList();
	}
	
	public int getCurrentQuestion() {
		return currentQuestion;
	}

	public void setCurrentQuestion(int currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public String getCurrentString() {
		return currentString;
	}

	public void setCurrentString(String currentString) {
		this.currentString = currentString;
	}

	public Long getCurrentNumber() {
		return currentNumber;
	}

	public void setCurrentNumber(Long currentNumber) {
		this.currentNumber = currentNumber;
	}

	public Double getCurrentMoney() {
		return currentMoney;
	}

	public void setCurrentMoney(Double currentMoney) {
		this.currentMoney = currentMoney;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public Integer getCurrentSingle() {
		return currentSingle;
	}

	public void setCurrentSingle(Integer currentSingle) {
		this.currentSingle = currentSingle;
	}

	public int getCurrentQuestionType() {
		return currentQuestionType;
	}

	public void setCurrentQuestionType(int currentQuestionType) {
		this.currentQuestionType = currentQuestionType;
	}
	
	public String dummy(){
		return null;
	}
	
	private QuestionAnswer findById(int qaId) {
		for (QuestionAnswer qa: questList) {
			if (qa.getQuestion().getId().intValue() == qaId) {
				return qa;
			}
		}
		return null;
	}
	
	public void saveQA(ActionEvent event){
		Integer qaid = Convertor.toInteger(event.getComponent().getAttributes().get("qaid"));
		if (qaid == null) {
			return;
		}

		logger.info("вопрос "+qaid);
		QuestionAnswer currentQA = findById(qaid);
		
		Object ans=null;
		
		if (StringUtils.isNotEmpty(currentQA.getAnswerValueString())&& currentQA.getQuestion().getAnswerType() == Question.ANSWER_STRING) {
			ans=currentQA.getAnswerValueString();
		}
		if (currentQA.getAnswerValueNumber() !=null && currentQA.getQuestion().getAnswerType() == Question.ANSWER_NUMERIC) {
			ans=currentQA.getAnswerValueNumber();
		}
		if (currentQA.getAnswerValueDate()!=null && currentQA.getQuestion().getAnswerType() == Question.ANSWER_DATE) {
			ans= (Object) currentQA.getAnswerValueDate();
		}
		if (currentQA.getAnswerValueMoney()!=null && currentQA.getQuestion().getAnswerType() == Question.ANSWER_MONEY) {
			ans=(Object) currentQA.getAnswerValueMoney();
		}
		if (currentQA.getAnswerValueRef() !=null && currentQA.getAnswerValueRef() !=0 && currentQA.getQuestion().getAnswerType() == Question.ANSWER_SINGLE){
			ans=(Object) currentQA.getAnswerValueRef();
		}
		String comment=currentQA.getComment();
		logger.info("ответ "+ans);
		logger.info("вопрос "+ questBean.getQuestion(qaid).getQuestionCode());
		logger.info("комментарий "+comment);
		
		questBean.saveQA(getCreditRequest().getId(), questBean.getQuestion(qaid).getQuestionCode(),ans,comment);
		reloadQuestList();
	}
	
	public List<SelectItem> getVariants(Integer qv){
		List<QuestionVariant> lst=questBean.getQuestionVariants(qv);
		ArrayList<SelectItem> lstVar = new ArrayList<SelectItem>(lst.size());
		for (QuestionVariant var: lst) {
			SelectItem si = new SelectItem();
			si.setValue(var.getId());
			si.setLabel(var.getAnswerText());
			lstVar.add(si);
		}
		SelectItem si = new SelectItem();
		si.setValue(null);
		si.setLabel("---любой---");
		lstVar.add(si);
		return lstVar;
	}
	
	 public void changeNumber(ValueChangeEvent event){
	     thisNumber=Convertor.toLong(event.getNewValue());
	     logger.info("thisNumber "+thisNumber);
	 }
	
	 public void changeString(ValueChangeEvent event){
	     thisString=(String) event.getNewValue();
	     logger.info("thisString "+thisString);
	 }
	
	 public void changeMoney(ValueChangeEvent event){
	     thisMoney=Convertor.toDouble(event.getNewValue());
	     logger.info("thisMoney "+thisMoney);
	 }
	 
	 public void changeDate(ValueChangeEvent event){
	     thisDate=(Date) event.getNewValue();
	     logger.info("thisDate "+thisDate);
	 }
	 
	 public void changeSingle(ValueChangeEvent event){
	     thisSingle=Convertor.toInteger(event.getNewValue());
	     logger.info("thisSingle "+thisSingle);
	 }

}
