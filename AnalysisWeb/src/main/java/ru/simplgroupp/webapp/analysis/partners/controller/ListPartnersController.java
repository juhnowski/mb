package ru.simplgroupp.webapp.analysis.partners.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.persistence.PartnersEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListPartnersController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4862011854461858875L;
	
	@EJB
	protected ReferenceBooksLocal refBook;
	
	protected List<Partner> listPartners;
	protected String name;
	protected String realName;
	
	@PostConstruct
	public void init()	{
			FacesContext facesCtx = FacesContext.getCurrentInstance();
			if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
				getListPartners();
			}
	}
	
	public List<Partner> getListPartners(){
		return refBook.getPartners();
	}
	
    public String addItem(){
    	PartnersEntity partners=refBook.addPartner(name, realName);
    	if (partners!=null){
    		Integer id=partners.getId();
    		return "edit?faces-redirect=true&reqid="+id.toString();
    	}
    	return null;
    }
	
    public void deleteItem(ActionEvent event) {
    	FacesContext facesCtx = FacesContext.getCurrentInstance();
		Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("reqid"));
		if (itemid!=null){
			try {
				refBook.removePartner(itemid);
			} catch (Exception e){
				JSFUtils.handleAsError(facesCtx, null, e);
			}
		}
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

    
}
