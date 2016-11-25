package ru.simplgroupp.webapp.manager.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.WorkflowEngineBeanLocal;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.util.UserUtil;
import ru.simplgroupp.util.WorkflowUtil;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.workflow.StateRef;
import ru.simplgroupp.workflow.WorkflowObjectState;
import ru.simplgroupp.workflow.WorkflowObjectStateDef;

public class ViewPSController extends AbstractSessionController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5312312298737550910L;

	@EJB
	WorkflowEngineBeanLocal wfEngine;
	
	@EJB
	WorkflowBeanLocal wfBean;
	
	@EJB
	KassaBeanLocal kassaBean;	
	
	@EJB
	CreditRequestDAO crDAO;
	
	protected List<WorkflowObjectStateDef> taskInfos = new ArrayList<WorkflowObjectStateDef>(3);
	private List<String> roleNames;
	private int maxRows = 5;
	protected String businessClassName;
	
	@PostConstruct
	public void init() {
	   reloadTaskInfos();
	}
	
	private void reloadTaskInfos() {
		taskInfos.clear();
		if (userCtl.getUser()!=null){
		    roleNames = UserUtil.rolesToStrings(userCtl.getUser().getRoles());
		    taskInfos = wfEngine.listTaskDefs(null, roleNames, businessClassName, null);
		}
		
	
	}
	
	public void refreshTaskInfos(ActionEvent event){
		reloadTaskInfos();
	}
	
	public List<WorkflowObjectStateDef> getTaskInfos() {
		return taskInfos;
	}
	
	public int getTaskInfoCount() {
		  return taskInfos.size();
	}	
	
	public Map<String, List<WorkflowObjectState>> getTasks() {
		return new Map<String, List<WorkflowObjectState>>() {

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsKey(Object key) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsValue(Object value) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public List<WorkflowObjectState> get(Object key) {
				StateRef ref = StateRef.valueOf(key.toString());
				List<WorkflowObjectState> lst = wfEngine.listTasks(0, maxRows, null, ref.getName(), null, roleNames, businessClassName, ref.getProcessDefKey(), ref.getPluginName());
				try {
					WorkflowUtil.fillWFO(lst, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL), crDAO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				return lst;
			}

			@Override
			public List<WorkflowObjectState> put(String key,
					List<WorkflowObjectState> value) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<WorkflowObjectState> remove(Object key) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void putAll(
					Map<? extends String, ? extends List<WorkflowObjectState>> m) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void clear() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Set<String> keySet() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Collection<List<WorkflowObjectState>> values() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<java.util.Map.Entry<String, List<WorkflowObjectState>>> entrySet() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public String getBusinessClassName() {
		return businessClassName;
	}

	public void setBusinessClassName(String businessClassName) {
		this.businessClassName = businessClassName;
	}
	
	public void assignMeLsn(ActionEvent event) {
		String ntaskid = (String) event.getComponent().getAttributes().get("wfotaskId");
		
		wfBean.assignTaskToUser(ntaskid, userCtl.getUser());
	}
	
	public void assignAllLsn(ActionEvent event) {
		String ntaskid = (String) event.getComponent().getAttributes().get("wfotaskId");
		
		wfBean.removeTaskFromUser(ntaskid);
	}
}
