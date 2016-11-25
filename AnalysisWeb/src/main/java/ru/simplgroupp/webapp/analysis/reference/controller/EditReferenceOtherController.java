package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.RefHeader;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class EditReferenceOtherController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @EJB
    protected ReferenceBooksLocal refBook;
    
	protected Integer prmRefId;
	protected RefHeader header;
	protected String name;
	protected Integer codeInteger;
	protected String code;
	protected List<Reference> lstRef;
	
	@PostConstruct
	public void init() 
	{
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed())
		{
			if (prmRefId==null)
			{
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("refid")) {
					String ss = prms.get("refid");
					prmRefId = Convertor.toInteger(ss);
					getLstRef();
				}
			}
			
		}
	}
	
	
	
	public Integer getPrmRefId(){
		return prmRefId;
	}
	
	public void setPrmRefId(Integer prmRefId){
		this.prmRefId=prmRefId;
	}
	
	public RefHeader getHeader(){
		return header;
	}
	
	public List<Reference> getLstRef(){
		lstRef=refBook.listReferences(prmRefId, null, null, null, null, null, null,null);
		return lstRef;
	}
	
	public String save() {
		refBook.saveReference(lstRef);
		return "list?faces-redirect=true";
	}
	
	public String cancel() {
		return "list?faces-redirect=true";
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getCode(){
		return code;
	}
	
	public void setCode(String code){
		this.code=code;
	}
	
	public Integer getCodeInteger(){
		return codeInteger;
	}
	
	public void setCodeInteger(Integer codeInteger){
		this.codeInteger=codeInteger;
	}
	
	public void addItem(ActionEvent event) {
		refBook.addReferenceItem(prmRefId, name, code, codeInteger);
		getLstRef();
	}

	public void deleteItem(ActionEvent event) {
		Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("itemid"));
		if (itemid!=null){
			refBook.removeReferenceItem(itemid);
			getLstRef();
	
		}
	}
	

}

