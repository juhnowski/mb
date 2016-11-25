package ru.simplgroupp.webapp.analysis.model.controller;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;


public class EditModelController extends ViewModelController {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5817819861340878455L;
	protected EditParamsController editParamsCtl;
	
	@PostConstruct
	public void init() {
		editParamsCtl = new EditParamsController();
		editParamsCtl.modelBean = modelBean;
		editParamsCtl.setUserCtl(getUserCtl());
		editParamsCtl.init();		
		super.init();
		
	}
	
	public String save() {
		model.setDateChange(new Date());
		modelBean.saveModel(model);
		return "success";
	}
	
	public String cancel() {
		return "canceled";
	}
	
	public EditParamsController getEditParamsCtl() {
		return editParamsCtl;
	}

	@Override
	protected void reloadModel(FacesContext facesCtx, Integer prmId2) {
		super.reloadModel(facesCtx, prmId2);
		
		editParamsCtl.model = model;
	}	
}
