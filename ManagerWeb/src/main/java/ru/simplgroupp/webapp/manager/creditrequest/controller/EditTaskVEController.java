package ru.simplgroupp.webapp.manager.creditrequest.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.interfaces.QuestionBeanLocal;
import ru.simplgroupp.transfer.QuestionAnswer;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;

public class EditTaskVEController extends EditCRActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	AppBean appBean;
	
	@EJB
	QuestionBeanLocal questBean;
	
	private List<QuestionAnswer> questList;
	
	@PostConstruct
	public void init(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
	
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			questList = questBean.listQuestionAnswer(0, 0, null, null, getCreditRequest().getId(), new int[] {QuestionAnswer.ANSWER_STATUS_ANSWERED, QuestionAnswer.ANSWER_STATUS_DENIAL});
	
		}
	}
	
	public void moreLsn(ActionEvent event) {
		String readyAction = SignalRef.toString(ProcessKeys.DEF_CREDIT_CONSIDER, null, "msgMore");
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			appBean.crEdited(getCreditRequest().getId(), readyAction);
			facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, null, "actionexecuted");
		} catch (Exception ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
		}	
	}

	public List<QuestionAnswer> getQuestList() {
		return questList;
	}


}
