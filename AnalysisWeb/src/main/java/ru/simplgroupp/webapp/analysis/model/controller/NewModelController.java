package ru.simplgroupp.webapp.analysis.model.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.exception.ModelException;
import ru.simplgroupp.interfaces.ModelBeanLocal;
import ru.simplgroupp.persistence.AIModelEntity;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class NewModelController extends AbstractSessionController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6319356143298317135L;

	@EJB
	ModelBeanLocal modelBean;	
	
	protected String modelName;
	protected Integer productId;
	protected Integer wayId;
	
	public String createModel() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		modelName = modelName.trim();
		
		int modelid = 0;
		try {
			AIModelEntity mdl = modelBean.createModel(modelName,productId, wayId);
			modelid = mdl.getId();
		} catch (ModelException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		}
		return "edit?faces-redirect=true&modelid=" + String.valueOf(modelid);
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getWayId() {
		return wayId;
	}

	public void setWayId(Integer wayId) {
		this.wayId = wayId;
	}
	
	
}
