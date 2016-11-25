package ru.simplgroupp.webapp.analysis.model.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.interfaces.ModelBeanLocal;
import ru.simplgroupp.persistence.AIModelParamEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.AIModel;
import ru.simplgroupp.util.ModelUtils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class EditParamsController extends AbstractSessionController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 762263650138076769L;

	@EJB
	public ModelBeanLocal modelBean;	
	
	protected AIModel model;
	protected String customizationKey;
	protected AIModelParamEntity currentVar;
	protected List<AIModelParamEntity> paramsInfo = new ArrayList<AIModelParamEntity>(15);
	
	public void reloadModel(boolean bFirst) {
		if (bFirst) {
			paramsInfo = modelBean.openModelSession(model.getId(), customizationKey);
		} else {
			paramsInfo = modelBean.getModelParams(model.getId(), customizationKey);
		}
	}

	public AIModel getModel() {
		return model;
	}

	public void setModel(AIModel model) {
		this.model = model;
	}

	public AIModelParamEntity getCurrentVar() {
		if (currentVar == null) {
			return new AIModelParamEntity();
		} else {
			return currentVar;
		}
	}
	
	public void addCurrentVarLsn(ActionEvent event) {
		currentVar = new AIModelParamEntity();
		currentVar.setName("newVariable");
		currentVar.setCustomKey(customizationKey);
	}	
	
	public boolean getHasParams() {
		return (paramsInfo.size() > 0);
	}
	
	public void loadCurrentVarLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		String vname = (String) event.getComponent().getAttributes().get("varname");
		currentVar = ModelUtils.findParam(paramsInfo, vname);		 
	}
	
	public void deleteCurrentVarLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		String vname = (String) event.getComponent().getAttributes().get("varname");
		try {
			modelBean.deleteModelParam(model.getId(), vname);
		} catch (ActionException ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
		}
		reloadModel(false);
	}
	
	public boolean canDelete(AIModelParamEntity param) {
		return (! modelBean.isVariableStandart(param.getName()));
	}
	
	public boolean canEdit(AIModelParamEntity param) {
		return modelBean.isVariableEditable(param.getName());
	}	
	
	public boolean getIsNewCurrentVar() {
		return (currentVar != null && currentVar.getAiModel() == null);
	}
	
	public boolean getIsStandartCurrentVar() {
		return (currentVar != null && modelBean.isVariableStandart(currentVar.getName()));
	}	
	
	public String getCurrentVarDataTypeName() {
		if (currentVar == null) {
			return null;
		} else {
			if (Convertor.TYPE_DATE.equals(currentVar.getDataType())) {
				return "Дата";
			}
			if (Convertor.TYPE_DOUBLE.equals(currentVar.getDataType())) {
				return "Десятичное число";
			}
			if (Convertor.TYPE_INTEGER.equals(currentVar.getDataType())) {
				return "Целое число";
			}	
			if (Convertor.TYPE_LONG_STRING.equals(currentVar.getDataType())) {
				return "Длинный текст";
			}
			if (Convertor.TYPE_SHORT_STRING.equals(currentVar.getDataType())) {
				return "Строка";
			}			
			return null;
		}
	}
	
	public void saveCurrentVarLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		if (currentVar == null) {
			return;
		}
		
		if (currentVar.getAiModel() == null) {
			// новая переменная
			try {
				currentVar.setCustomKey(this.customizationKey);
				model = modelBean.addModelParam(model.getId(), currentVar);
/*				
				if (customizationKey != null) {
					currentVar.setCustomKey(null);
					modelBean.addModelParam(model.getId(), currentVar);
					currentVar.setCustomKey(this.customizationKey);
				}
*/				
			} catch (ActionException ex) {
				JSFUtils.handleAsError(facesCtx, null, ex);
			}
		} else {
			// существующая переменная
			try {
				currentVar.setCustomKey(this.customizationKey);
				model = modelBean.saveModelParam(currentVar);
/*				
				if (customizationKey != null) {
					currentVar.setCustomKey(null);
					modelBean.saveModelParam(currentVar);
					currentVar.setCustomKey(this.customizationKey);
				}
*/				
			} catch (ActionException ex) {
				JSFUtils.handleAsError(facesCtx, null, ex);
			}

		}
		reloadModel(false);
		currentVar = null;
	}
	
	public void applyVarsLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		modelBean.applyModelParams(model.getId(), customizationKey);
	}

	public void revertVarsLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		paramsInfo = modelBean.revertModelParams(model.getId(), customizationKey);
	}
	
	public String getCustomizationKey() {
		return customizationKey;
	}

	public void setCustomizationKey(String customizationKey) {
		this.customizationKey = customizationKey;
	}

	public List<AIModelParamEntity> getParamsInfo() {
		return paramsInfo;
	}	
	
	public void clearParams() {
		modelBean.closeModelSession(model, customizationKey);
	}

	@Override
	public void init() {
		super.init();
	}
}
