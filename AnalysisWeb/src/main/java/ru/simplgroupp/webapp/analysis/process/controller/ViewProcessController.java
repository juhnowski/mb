package ru.simplgroupp.webapp.analysis.process.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.activiti.engine.repository.ProcessDefinition;

import ru.simplgroupp.dao.interfaces.RulesDAO;
import ru.simplgroupp.interfaces.BizActionBeanLocal;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.WorkflowEngineBeanLocal;
import ru.simplgroupp.persistence.AIConstantEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.BizAction;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.workflow.ProcessKeys;

public class ViewProcessController extends AbstractSessionController implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4602629877257209306L;

	@EJB
	WorkflowBeanLocal wfBean;
	
	@EJB
	WorkflowEngineBeanLocal wfEngineBean;
	
	@EJB
	RulesBeanLocal rulesBean;
	
	@EJB
	RulesDAO rulesDAO;
	
	@EJB
	BizActionBeanLocal bizBean;
	
	protected String prmDefKey;
	protected ProcessDefinition process;
	protected List<AIConstantEntity> options=new ArrayList<AIConstantEntity>(0);
	protected List<AIConstantEntity> fmtMessages=new ArrayList<AIConstantEntity>(0);
	protected List<BizAction> bizActions;
	
	protected AIConstantEntity editedConstant;
	
	@PostConstruct
	public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed())
        {
            if (prmDefKey == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("procDefKey")) {
                	prmDefKey = prms.get("procDefKey").toString();
                }
            }
            if (prmDefKey != null) {
                reloadProcDef(facesCtx, prmDefKey);
            }
        }		
	}

	private void reloadProcDef(FacesContext facesCtx, String prmDefKey2) {
		process = wfEngineBean.getLatestProcessDefinition(prmDefKey2);		
		bizActions = bizBean.listBizActions(-1, -1, null, null, null, null, null, null, 0, null, null, null, null, true, process.getKey());
	}

	public String getPrmDefKey() {
		return prmDefKey;
	}

	public void setPrmDefKey(String prmDefKey) {
		this.prmDefKey = prmDefKey;
	}

	public ProcessDefinition getProcess() {
		return process;
	}
	
	public boolean isRequiredProcess() {
		return (ProcessKeys.DEF_CREDIT_REQUEST.equalsIgnoreCase(process.getKey()));
	}

	public List<AIConstantEntity> getOptions() {
		return options;
	}

	public List<AIConstantEntity> getFmtMessages() {
		return fmtMessages;
	}

	public List<BizAction> getBizActions() {
		return bizActions;
	}

	public AIConstantEntity getEditedConstant() {
		return editedConstant;
	}

	public void setEditedConstant(AIConstantEntity editedConstant) {
		this.editedConstant = editedConstant;
	}
	
	public void openEditedConstantLsn(ActionEvent event) {
		Integer conId = Convertor.toInteger(event.getComponent().getAttributes().get("aiConstantId"));
		
		editedConstant = rulesDAO.getConstant(conId);
	}
	
	public void saveEditedConstantLsn(ActionEvent event) {
		if (editedConstant == null) {
			return;
		}
		// TODO
	}
}
