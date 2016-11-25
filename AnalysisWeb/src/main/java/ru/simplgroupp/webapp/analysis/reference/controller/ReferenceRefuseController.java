package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.RefuseReason;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ReferenceRefuseController extends AbstractSessionController implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5022476371460639751L;

	@EJB
	protected ReferenceBooksLocal refBook;
	
	protected String name;
	protected String nameFull;
	protected Integer reasonId;
	protected String constantName;
	protected Integer forDecision=1;
	protected List<RefuseReason> listRefRefuse;
	
	@PostConstruct
	public void init(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()){
			listRefRefuse=refBook.getRefuseReasons();
		}
	}
	
	public List<RefuseReason> getListRefRefuse(){
		listRefRefuse=refBook.getRefuseReasons();
		return listRefRefuse;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getNameFull(){
		return nameFull;
	}
	
	public void setNameFull(String nameFull){
		this.nameFull=nameFull;
	}
	
	public Integer getReasonId(){
		return reasonId;
	}
	
	public void setReasonId(Integer reasonId){
		this.reasonId=reasonId;
	}
	
	public void addItem(ActionEvent event) {
		refBook.addRefuseReason(reasonId, name, nameFull, constantName,forDecision);
		getListRefRefuse();
		
	}
	
	public void deleteItem(ActionEvent event) {
		Integer refid = Convertor.toInteger(event.getComponent().getAttributes().get("refid"));
		if (refid!=null){
			refBook.removeRefuseReason(refid);
			getListRefRefuse();
		}
	}
	
		
	public void saveItems(ActionEvent event){
		refBook.saveRefuseReasons(listRefRefuse);
		getListRefRefuse();
	}

	public String getConstantName() {
		return constantName;
	}

	public void setConstantName(String constantName) {
		this.constantName = constantName;
	}

	public Integer getForDecision() {
		return forDecision;
	}

	public void setForDecision(Integer forDecision) {
		this.forDecision = forDecision;
	}
	
	
}
