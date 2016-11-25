package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.transfer.FMSRegion;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ReferenceRegionsController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1628088827270708412L;

	@EJB
	protected ReferenceBooksLocal refBook;
	
	protected String name;
	protected String code;
	protected String codeReg;
	protected String codeIso;
	protected List<FMSRegion> lstRegions;
	
	@PostConstruct
	public void init(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()){
			reloadRegions();
		}
	}

	private void reloadRegions(){
		lstRegions=refBook.getRegionsNewList();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeReg() {
		return codeReg;
	}

	public void setCodeReg(String codeReg) {
		this.codeReg = codeReg;
	}

	public String getCodeIso() {
		return codeIso;
	}

	public void setCodeIso(String codeIso) {
		this.codeIso = codeIso;
	}

	public List<FMSRegion> getLstRegions() {
		lstRegions=refBook.getRegionsNewList();
		return lstRegions;
	}
	
	public void addItem(ActionEvent event) {
		refBook.addRegionNew(code, name, codeReg,codeIso);
		getLstRegions();
	}
	
	public void saveItems(ActionEvent event){
		refBook.saveRegionsNew(lstRegions);
		getLstRegions();
	}
}
