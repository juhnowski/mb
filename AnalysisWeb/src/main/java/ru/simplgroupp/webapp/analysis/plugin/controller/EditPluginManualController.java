package ru.simplgroupp.webapp.analysis.plugin.controller;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.ejb.plugins.payment.ManualPluginConfig;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;
import ru.simplgroupp.workflow.WorkflowObjectStateDef;

public class EditPluginManualController extends EditPluginExtController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8766315640483092911L;
	protected WorkflowObjectStateDef taskSingleExecute;
	protected WorkflowObjectActionDef actSingleExecute;
	protected WorkflowObjectActionDef editSingleExecute;

	@Override
	void reload() {
		// TODO Auto-generated method stub
		ManualPluginConfig mplc = (ManualPluginConfig) editCtl.plugin;
		
		taskSingleExecute = mplc.getTaskSyncSingleExecute().copy();		
		actSingleExecute = new WorkflowObjectActionDef();
		editSingleExecute = null;
	}

	@Override
	void save() {
		ManualPluginConfig mplc = (ManualPluginConfig) editCtl.plugin;
		
		mplc.getTaskSyncSingleExecute().setDescription(taskSingleExecute.getDescription());
		mplc.getTaskSyncSingleExecute().getActions().clear();
		mplc.getTaskSyncSingleExecute().getActions().addAll(taskSingleExecute.getActions());
		// TODO Auto-generated method stub
	}

	public WorkflowObjectStateDef getTaskSingleExecute() {
		return taskSingleExecute;
	}

	public WorkflowObjectActionDef getActSingleExecute() {
		return actSingleExecute;
	}
	
	public String addTaskAction() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Map<String,String> mp = facesCtx.getExternalContext().getRequestParameterMap();	
		String taskName = mp.get("taskName");
		
		WorkflowObjectStateDef state = null;
		WorkflowObjectActionDef act = null;
		if (taskName.contains(ProcessKeys.TASK_EXECUTE_SINGLE)) {
			state = taskSingleExecute;
			act = actSingleExecute;
		}
		
		// TODO проверить обяз. поля
		state.getActions().add(act);
		act = new WorkflowObjectActionDef(); 
		
		return null;
	}
	
	public void editTaskActionLsn(ActionEvent event) {
		String taskName = (String) event.getComponent().getAttributes().get("taskName");
		String sigRef = (String) event.getComponent().getAttributes().get("actName");
		
		WorkflowObjectStateDef state = null;
		WorkflowObjectActionDef act = null;
		if (taskName.contains(ProcessKeys.TASK_EXECUTE_SINGLE)) {
			state = taskSingleExecute;
			editSingleExecute = state.findBySignalRef(sigRef);
			act = editSingleExecute;
		}		
	}	
	
	public void deleteTaskActionLsn(ActionEvent event) {
		String taskName = (String) event.getComponent().getAttributes().get("taskName");
	}

	public WorkflowObjectActionDef getEditSingleExecute() {
		return editSingleExecute;
	}
}
