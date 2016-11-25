package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.transfer.CreditStatus;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class CreditRequestStatusController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB
	protected ReferenceBooksLocal refBook;
	protected List<CreditStatus> listStatus;
	protected String name;
	
	public void init(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()){
			getListStatus();
		}
	}
	
	public List<CreditStatus> getListStatus(){
		listStatus=refBook.getCreditRequestStatuses();
		return listStatus;
	}
	
	public void saveItems(ActionEvent event){
		refBook.saveCreditRequestStatus(listStatus);
		getListStatus();
	}
	
	public void addItem(ActionEvent event){
		refBook.addCreditRequestStatus(name);
		getListStatus();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
