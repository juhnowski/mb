package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.service.OrganizationService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Organization;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class EditRefOrganizationsController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2933689519985609677L;

	@EJB
    OrganizationService orgService;
	
	protected Integer refId;
	protected Organization org;
	
	@PostConstruct
	public void init() 
	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (refId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("refid")) {
					String ss = prms.get("refid");
					refId = Convertor.toInteger(ss);
				}
			}
			if (refId != null) {
				reloadItem(facesCtx, refId);
				
			}
		}
	}
	
	protected void reloadItem(FacesContext facesCtx, Integer refId) {
		org=orgService.findOrganization(refId);
    }

	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}
	
	public String close(){
		return "close";
	}
	
	public String save(){
		orgService.saveOrganization(org, false);
		return "success";
	}
}
