package ru.simplgroupp.webapp.analysis.rules.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.dao.interfaces.RulesDAO;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.persistence.AIRuleEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ViewRuleController extends AbstractSessionController implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8607989741428827773L;

	@EJB
	protected RulesDAO rulesDAO;	
	
	@EJB
	protected RulesBeanLocal rulesBean;
	
	protected Integer prmRuleId;
	protected AIRuleEntity rule;

	public Integer getPrmRuleId() {
		return prmRuleId;
	}

	public void setPrmRuleId(Integer prmRuleId) {
		this.prmRuleId = prmRuleId;
	}
	
	@PostConstruct
	public void init() 
	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (prmRuleId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("ruleid")) {
					String ss = prms.get("ruleid");
					prmRuleId = Convertor.toInteger(ss);
				}
			}
			if (prmRuleId != null) {
				reloadRule(facesCtx, prmRuleId);
			}
			
		}
	}

	protected void reloadRule(FacesContext facesCtx, Integer ruleId) {
		rule = rulesDAO.getAIRule(ruleId, Utils.setOf(AIRuleEntity.Options.INIT_CONTSTANT));
	}

	public AIRuleEntity getRule() {
		return rule;
	}	
	
	
}
