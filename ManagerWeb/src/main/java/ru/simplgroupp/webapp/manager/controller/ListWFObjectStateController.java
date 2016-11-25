package ru.simplgroupp.webapp.manager.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.WorkflowEngineBeanLocal;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.util.WorkflowUtil;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.workflow.StateRef;
import ru.simplgroupp.workflow.WorkflowObjectState;

public class ListWFObjectStateController extends AbstractListController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 528106943834719141L;
	protected StateRef prmStateRef;
	protected String prmTaskDefKey;
	protected String prmBusinessObjectClass;
	protected String prmAssignee;
	protected List<String> prmCandidateRoles = new ArrayList<String>(1); 
	
	@EJB
	WorkflowEngineBeanLocal wfEngine;	
	
	@EJB
	WorkflowBeanLocal wfBean;
	
	@EJB
	KassaBeanLocal kassaBean;

	@EJB
	CreditRequestDAO crDAO;
	
	@PostConstruct
	public void init() {
		super.init();
	}
	
	@Override
	public void initData() throws Exception {
		if (StringUtils.isBlank(prmTaskDefKey)) {
			prmStateRef = null;
		} else {
			prmStateRef = StateRef.valueOf(prmTaskDefKey);
		}
	}
	
	protected void nullIfEmpty() {
		// TODO
	}
	
	public void clearLsn(ActionEvent event) 
	{
		prmBusinessObjectClass = null;
		prmTaskDefKey = null;
		prmStateRef = null;
		prmAssignee = null;
		
		refreshSearch();
	}	

	@Override
	public StdRequestDataModel createModel() {
		return new WFObjectStateDataModel();
	}

	public class WFObjectStateDataModel extends StdRequestDataModel<WorkflowObjectState> {

		@Override
		protected List<WorkflowObjectState> internalList(
				SequenceRange seqRange, SortCriteria[] sorting)
				throws WebAppException {
			nullIfEmpty();
			return wfEngine.listTasks(seqRange.getFirstRow(), seqRange.getRows(), sorting, (prmStateRef==null)?null:prmStateRef.getName(), prmAssignee, prmCandidateRoles, prmBusinessObjectClass, (prmStateRef==null)?null:prmStateRef.getProcessDefKey(), (prmStateRef==null)?null:prmStateRef.getPluginName());
		}

		@Override
		protected int internalRowCount() throws WebAppException {	
			nullIfEmpty();
			return (int) wfEngine.countTasks((prmStateRef==null)?null:prmStateRef.getName(), prmAssignee, prmCandidateRoles, prmBusinessObjectClass, (prmStateRef==null)?null:prmStateRef.getProcessDefKey(), (prmStateRef==null)?null:prmStateRef.getPluginName() );
		}

		@Override
		public WorkflowObjectState getRowData() {
			if (rowKey == null)
				return null;
			else {
				try {
					WorkflowObjectState state = wfBean.getWFOByPrimaryKey(rowKey.toString());
					WorkflowUtil.fillWFO(state, Utils.setOf(), crDAO);
					return state;
				} catch (Exception e) {
					return null;
				}
			}
		}
		
	}

	public String getPrmTaskDefKey() {
		return prmTaskDefKey;
	}

	public void setPrmTaskDefKey(String prmTaskDefKey) {
		this.prmTaskDefKey = prmTaskDefKey;
	}

	public String getPrmBusinessObjectClass() {
		return prmBusinessObjectClass;
	}

	public void setPrmBusinessObjectClass(String prmBusinessObjectClass) {
		this.prmBusinessObjectClass = prmBusinessObjectClass;
	}

	public String getPrmAssignee() {
		return prmAssignee;
	}

	public void setPrmAssignee(String prmAssignee) {
		this.prmAssignee = prmAssignee;
	}

	public StateRef getPrmStateRef() {
		return prmStateRef;
	}

	public List<String> getPrmCandidateRoles() {
		return prmCandidateRoles;
	}
}
