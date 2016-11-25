package ru.simplgroupp.webapp.manager.roles.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Roles;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class DeleteController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6692745897151773734L;

	@EJB
	UsersBeanLocal userServ;
	
	@EJB
	ReferenceBooksLocal refServ;
	
	protected Integer roleId;
	protected Roles role;
	
	@PostConstruct
	public void init()	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (roleId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("roleid")) {
					roleId = Convertor.toInteger(prms.get("roleid"));
				}
			}
			if (roleId != null) {
				reloadRole(facesCtx, roleId);
			}	
		}
	}
	
	private void reloadRole(FacesContext facesCtx, Integer roleId){
		role = userServ.getRole(roleId);
	}

	public String delete(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
	
		try {
			userServ.deleteRole(role.getId());
			return "/views/roles/index.xhtml?faces-redirect=true";
		} catch (ActionException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		}
		
	}
	
	public String cancel(){
		return "/views/roles/index.xhtml?faces-redirect=true";
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Roles getRole() {
		return role;
	}
	
}
