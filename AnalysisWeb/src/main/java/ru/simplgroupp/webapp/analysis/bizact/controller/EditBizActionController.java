package ru.simplgroupp.webapp.analysis.bizact.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.BizActionDAO;
import ru.simplgroupp.dao.interfaces.RulesDAO;
import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.exception.ExceptionInfo;
import ru.simplgroupp.exception.ResultType;
import ru.simplgroupp.interfaces.BizActionBeanLocal;
import ru.simplgroupp.interfaces.BizActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.RuleSetBeanLocal;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.persistence.AIConstantEntity;
import ru.simplgroupp.persistence.AIRuleEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.AIRule;
import ru.simplgroupp.transfer.BizAction;
import ru.simplgroupp.transfer.EventCode;
import ru.simplgroupp.util.EventLogUtils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class EditBizActionController extends AbstractSessionController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -501908062882908107L;
	
	@EJB
	BizActionBeanLocal bizBean;
	
	@EJB
	BizActionDAO bizDAO;
	
	@EJB
	BizActionProcessorBeanLocal bizProc;
	
	@EJB
	RuleSetBeanLocal ruleSet;
	
	@EJB
	RulesDAO rulesDAO;
	
	@EJB
	EventLogService eventLog;
	
	protected Integer prmBizActionId;
	protected BizAction bizAction;
	protected List<SelectItem> selectedEvents;
	protected AIRule ruleOptions;
	protected AIRule ruleMessages;

	public Integer getPrmBizActionId() {
		return prmBizActionId;
	}

	public void setPrmBizActionId(Integer prmBizActionId) {
		this.prmBizActionId = prmBizActionId;
	}
	
	public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed())
        {
            if (prmBizActionId == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("bizActionId")) {
                	prmBizActionId = Convertor.toInteger( prms.get("bizActionId") );
                }
            }
            if (prmBizActionId != null) {
                reloadBizAction(facesCtx, prmBizActionId);
            }
        }	
	}

	private void reloadBizAction(FacesContext facesCtx, Integer prmBizActionId2) {
		bizAction = bizDAO.getBizAction(prmBizActionId2, Utils.setOf(BizAction.Options.INIT_EVENTS));
		
		selectedEvents = new ArrayList<SelectItem>(10);
		List<EventCode> lstAll = eventLog.getEventCodes();
		for (Integer evt: bizAction.getEvents() ) {
			int n = EventLogUtils.find(lstAll, evt);
			if (n >= 0 ) {
				SelectItem si = new SelectItem();
				si.setValue(evt);
				si.setLabel(lstAll.get(n).getName());
				selectedEvents.add(si);
			}
		}
		
		ruleOptions = rulesDAO.findRuleByName(bizAction.getRuleOptionsName(), Utils.setOf(AIRuleEntity.Options.INIT_CONTSTANT));
		if (ruleOptions == null) {
			ruleOptions = new AIRule( new AIRuleEntity() );
			ruleOptions.getEntity().setRuleType(2);
			ruleOptions.getEntity().setPackageName(bizAction.getRuleOptionsName());
			AIConstantEntity con = new AIConstantEntity();
			con.setAiRule(ruleOptions.getEntity());
			ruleOptions.getEntity().getConstants().add(con);
		}
		
		ruleMessages = rulesDAO.findRuleByName(bizAction.getRuleMessagesName(), Utils.setOf(AIRuleEntity.Options.INIT_CONTSTANT));
		if (ruleMessages == null) {
			ruleMessages = new AIRule( new AIRuleEntity() );
			ruleMessages.getEntity().setRuleType(2);
			ruleMessages.getEntity().setPackageName(bizAction.getRuleMessagesName());
			AIConstantEntity con = new AIConstantEntity();
			con.setAiRule(ruleMessages.getEntity());
			ruleMessages.getEntity().getConstants().add(con);
		}		
	}
	
	public void addConstantItem(ActionEvent event) {
		String whatRule = (String) event.getComponent().getAttributes().get("whatRule");
		AIRule rule = null;
		if ("O".equalsIgnoreCase(whatRule)) {
			rule = ruleOptions;
		} else if ("M".equalsIgnoreCase(whatRule)) {
			rule = ruleMessages;
		} else {
			return;
		}
		AIConstantEntity con = new AIConstantEntity();
		con.setAiRule(rule.getEntity());
		rule.getEntity().getConstants().add(con);		
	}
	
	public void deleteConstantItem(ActionEvent event) {
		Number nidx = (Number) event.getComponent().getAttributes().get("constidx");
		String whatRule = (String) event.getComponent().getAttributes().get("whatRule");
		AIRule rule = null;
		if ("O".equalsIgnoreCase(whatRule)) {
			rule = ruleOptions;
		} else if ("M".equalsIgnoreCase(whatRule)) {
			rule = ruleMessages;
		} else {
			return;
		}		
		
		rule.getEntity().getConstants().remove(nidx.intValue());
	}

	public BizAction getBizAction() {
		return bizAction;
	}
	
	public String runNow() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			bizProc.runNow(bizAction.getId());
		} catch (ActionException ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
		}
		return "success";
	}
	
	public String save() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		// проверяем правильность правил
		boolean bOk = true;
		if (StringUtils.isNotBlank(bizAction.getRuleEnabled()) || StringUtils.isNotBlank(bizAction.getRuleAction())) {
			List<ExceptionInfo> lstEx = ruleSet.preCompileBizActionRules(bizAction);
			for (ExceptionInfo ex: lstEx) {
				if (ex.getResultType().equals(ResultType.FATAL)) {
					JSFUtils.addAsError(facesCtx, false, ex.getMessage());
					bOk = false;
				} else {
					JSFUtils.addAsWarning(facesCtx, false, ex.getMessage());
				}
			}
		}
		
		if (! bOk) {
			return null;
		}
		
		// сохраняем события	
		bizAction = bizBean.saveBizAction(bizAction);
		
		// сохраняем настройки правила
		if (ruleOptions.getEntity().isEmptyConstants()) {
			if (ruleOptions.getEntity().getId() != null) {
				// удаляем пустое правило
				rulesDAO.deleteRule(ruleOptions.getEntity().getId());
			}
		} else {
			rulesDAO.saveRule(ruleOptions.getEntity());
		}
		
		// сохраняем сообщения 
		if (ruleMessages.getEntity().isEmptyConstants()) {
			if (ruleMessages.getEntity().getId() != null) {
				// удаляем пустое правило
				rulesDAO.deleteRule(ruleMessages.getEntity().getId());
			}
		} else {
			rulesDAO.saveRule(ruleMessages.getEntity());
		}		
		return "success";
	}
	
	public String cancel() {
		return "success";
	}
	
	public String close() {
		return "success";
	}

	public List<SelectItem> getSelectedEvents() {
		return selectedEvents;
	}

	public void setSelectedEvents(List<SelectItem> selectedEvents) {
		this.selectedEvents = selectedEvents;
	}

	public AIRuleEntity getRuleOptions() {
		return ruleOptions.getEntity();
	}

	public AIRuleEntity getRuleMessages() {
		return ruleMessages.getEntity();
	}
}
