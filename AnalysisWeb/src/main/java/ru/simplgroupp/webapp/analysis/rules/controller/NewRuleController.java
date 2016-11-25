package ru.simplgroupp.webapp.analysis.rules.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;

import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.persistence.AIRuleEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class NewRuleController extends AbstractSessionController implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3269509432682841593L;

	@EJB
	protected RulesBeanLocal rulesBean;
	
	protected String rulePack;
	protected String ruleDesc;
	protected Integer ruleConst=1;
	protected String ruleBody;
	protected String ruleKb;
	
	public String add() {
				
	    AIRuleEntity rule=rulesBean.createRule(ruleConst, rulePack, ruleDesc,ruleBody,ruleKb);
		String surl = "edit?faces-redirect=true&ruleid=" + rule.getId().toString();
		return surl;
	}
	
	public String getRulePack(){
		return rulePack;
	}
	
	public void setRulePack(String rulePack){
		this.rulePack=rulePack;
	}
	
	public String getRuleDesc(){
		return ruleDesc;
	}
	
	public void setRuleDesc(String ruleDesc){
		this.ruleDesc=ruleDesc;
	}
	
	public String getRuleKb(){
		return ruleKb;
	}
	
	public void setRuleKb(String ruleKb){
		this.ruleKb=ruleKb;
	}
	
	public String getRuleBody(){
		return ruleBody;
	}
	
	public void setRuleBody(String ruleBody){
		this.ruleBody=ruleBody;
	}
	
	public Integer getRuleConst(){
		return ruleConst;
	}
	
	public void setRuleConst(Integer ruleConst){
		this.ruleConst=ruleConst;
	}
	
	public void changeType(ValueChangeEvent event){
	   ruleConst=Convertor.toInteger(event.getNewValue());
	}
}
