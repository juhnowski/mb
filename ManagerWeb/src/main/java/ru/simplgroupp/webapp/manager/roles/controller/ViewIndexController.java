package ru.simplgroupp.webapp.manager.roles.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Roles;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ViewIndexController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4026277042074464185L;
	
	@EJB
	UsersBeanLocal userServ;
	
	private List<Roles> roles;
	
	@PostConstruct
	public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
        	reloadRoles();
        }
	}
	
	private void reloadRoles() {
		roles = userServ.getRoles();
	}

	public List<Roles> getRoles() {
		return roles;
	}
	
	public String createRole() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			Roles aobj = userServ.createRole(null, null);
			return "/views/roles/edit.xhtml?faces-redirect=true&roleid=" + aobj.getId().toString();
		} catch (ActionException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		}

	}
	
	public void deleteItem(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Integer roleid = Convertor.toInteger(event.getComponent().getAttributes().get("roleid"));
		
		if (roleid!=null){
		  try {
			  userServ.deleteRole(roleid);
			  reloadRoles();
		  } catch (Exception ex) {
			  JSFUtils.handleAsError(facesCtx, null, ex);
		  }
		}
	}	
}
