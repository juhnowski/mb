package ru.simplgroupp.webapp.analysis.partners.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class EditPartnersController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7609670120226821883L;

	@EJB
	protected ReferenceBooksLocal refBook;
	protected Integer reqId;
	protected Partner partner;
	
	 @PostConstruct
	 public void init()	{
			FacesContext facesCtx = FacesContext.getCurrentInstance();
			if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
				if (reqId == null) {
					Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
					if (prms.containsKey("reqid")) {
						reqId = Convertor.toInteger(prms.get("reqid"));
					}
				}
				if (reqId != null) {
					reloadItem(facesCtx, reqId);
				}
			}
		}
	 
	 protected void reloadItem(FacesContext facesCtx, Integer refId){
		 partner=refBook.getPartner(refId);
	 }

	public Integer getReqId() {
		return reqId;
	}

	public void setReqId(Integer reqId) {
		this.reqId = reqId;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}
	 
	public String save(){
		refBook.savePartner(partner);
		return "save";
	}
	
	public String close(){
		return "close";
	}
}
