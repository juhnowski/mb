package ru.simplgroupp.webapp.analysis.process.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import org.activiti.engine.repository.ProcessDefinition;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.WorkflowEngineBeanLocal;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ViewIndexController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	WorkflowBeanLocal wfBean;
	
	@EJB
	WorkflowEngineBeanLocal wfEngineBean;
	
	protected List<ProcessDefinition> procDefs;
	
	@PostConstruct
	public void init() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		procDefs = wfEngineBean.getLatestProcessDefinitions();
	}
	
	public List<ProcessDefinition> getProcDefs() {
		return procDefs;
	}

	public void uploadBarListener(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getUploadedFile();

		wfEngineBean.deployProcess(item.getData());
	}	
}
