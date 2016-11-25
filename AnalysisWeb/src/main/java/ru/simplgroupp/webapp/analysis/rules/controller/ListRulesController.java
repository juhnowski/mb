package ru.simplgroupp.webapp.analysis.rules.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.RulesDAO;
import ru.simplgroupp.persistence.AIRuleEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListRulesController extends AbstractListController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	protected RulesDAO rulesDAO;
	
	protected String prmFor;
	protected String prmDescr;
	
	
	public String getPrmFor() {
		return prmFor;
	}

	public void setPrmFor(String prmFor) {
		this.prmFor = prmFor;
	}

	public String getPrmDescr() {
		return prmDescr;
	}

	public void setPrmDescr(String prmDescr) {
		this.prmDescr = prmDescr;
	}
	
	@PostConstruct
	public void init() {
		super.init();
	}	

	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel<AIRuleEntity> createModel() {
		return new AIRuleDataModel();
	}
	
	protected void nullIfEmpty() {
		if (StringUtils.isBlank(prmFor)) {
			prmFor = null;
		}
		if (StringUtils.isBlank(prmDescr)) {
			prmDescr = null;
		}
	}	
	
	public void clearLsn(ActionEvent event) {
		prmFor = null;
		prmDescr=null;
		refreshSearch();
		
	}	
	
	public class AIRuleDataModel extends StdRequestDataModel<AIRuleEntity> {
		
		public AIRuleDataModel() {
			super();
		}

		@Override
		protected List<AIRuleEntity> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			
			nullIfEmpty();

			List<AIRuleEntity> lstRules = rulesDAO.listRules(prmFor,prmDescr, null, null);
			return lstRules;
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return rulesDAO.countRules(prmFor,prmDescr, null, null);
		}

		@Override
		public AIRuleEntity getRowData() {
			if (rowKey == null) {
				return null;
			} else {
				return rulesDAO.getAIRule(((Number) rowKey).intValue(), null);
			}
		}
		
	}

	public void deleteItem(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Integer ruleid = Convertor.toInteger(event.getComponent().getAttributes().get("ruleid"));
		
		if (ruleid!=null){
		  try {
			  rulesDAO.deleteRule(ruleid);
		  } catch (Exception ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
		  }
		}
	}	
}
