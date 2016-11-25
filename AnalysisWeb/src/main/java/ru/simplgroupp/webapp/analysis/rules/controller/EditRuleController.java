package ru.simplgroupp.webapp.analysis.rules.controller;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.webapp.util.JSFUtils;

public class EditRuleController extends ViewRuleController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    protected String name;
    protected String dataType;
    protected String dataValue;
    protected String description;
    protected String dataValueText;
    
	public String save() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			rulesBean.saveRule(getRule());
			return "list?faces-redirect=true";
		} catch (Exception ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
			return null;
		}
	}
	
	public String cancel() {
		return "list?faces-redirect=true";
	}
	
	public void addItem(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		rulesBean.saveConstant(prmRuleId, name, dataType, dataValue,description,dataValueText);
		reloadRule(facesCtx, prmRuleId);
	}
	
	public void deleteItem(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Integer constid = Convertor.toInteger(event.getComponent().getAttributes().get("constid"));
		if (constid!=null) {
			rulesBean.deleteConstant(constid.intValue());
			reloadRule(facesCtx, prmRuleId);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getDataType(){
		return dataType;
	}
	
	public void setDataType(String dataType){
		this.dataType=dataType;
	}
	
	public String getDataValue(){
		return dataValue;
	}
	
	public void setDataValue(String dataValue){
		this.dataValue=dataValue;
	}
	
	public String getDataValueText(){
		return dataValueText;
	}
	
	public void setDataValueText(String dataValueText){
		this.dataValueText=dataValueText;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description){
		this.description=description;
	}
	
}
