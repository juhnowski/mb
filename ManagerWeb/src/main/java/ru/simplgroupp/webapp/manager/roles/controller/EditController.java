package ru.simplgroupp.webapp.manager.roles.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.dao.interfaces.BizActionDAO;
import ru.simplgroupp.dao.interfaces.UsersDAO;
import ru.simplgroupp.ejb.BusinessEvent;
import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.persistence.BizActionEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.EventCode;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.RefHeader;
import ru.simplgroupp.transfer.RefSystemFeature;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.transfer.Roles;
import ru.simplgroupp.util.BizActionUtils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.controller.UserDataController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class EditController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6692745897151773734L;

	@EJB
	UsersBeanLocal userServ;
	
	@EJB
	UsersDAO usersDAO;	
	
    @EJB
    BizActionDAO bizDAO;	
	
	@EJB
	ReferenceBooksLocal refServ;
	
	protected Integer roleId;
	protected Roles role;
	protected Map<Integer, List<BizActionEntity>> bizActions;
	protected List<Integer> bizActOn;
	protected Map<Integer, List<Reference>> features;
	protected List<Integer> featureOn;
	protected List<Reference> featuresTop;
	
	@PostConstruct
	public void init()	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (facesCtx!=null && !facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
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
	
	public boolean featureEnabled(Integer id) {
		return (featureOn.indexOf(id) >= 0);
	}
	public boolean bizactEnabled(Integer id) {
		return (bizActOn.indexOf(id) >= 0);
	}	
	
	private void reloadRole(FacesContext facesCtx, Integer roleId){
		role = userServ.getRole(roleId);
		
		bizActions = new HashMap<Integer, List<BizActionEntity>>(10);
		bizActOn = new ArrayList();
		
		featuresTop = refServ.listReferenceTop(RefHeader.SYSTEM_FEATURE);
//		refCRMain = ReferenceUtil.find(featuresTop, RefSystemFeature.CREDIT_REQUEST_MAIN, null);
//		refCMain = ReferenceUtil.find(featuresTop, RefSystemFeature.CREDIT_MAIN, null);
		features = new HashMap<Integer, List<Reference>>(10);
		
		for (Reference reft: featuresTop) {
			features.put(reft.getId(), refServ.listReferenceSub(RefHeader.SYSTEM_FEATURE, reft.getId()));
			String bizObjectClass = null;
			switch (reft.getCodeInteger().intValue()) {
				case RefSystemFeature.CREDIT_REQUEST_MAIN: bizObjectClass = CreditRequest.class.getName();break;
				case RefSystemFeature.CREDIT_MAIN: bizObjectClass = Credit.class.getName();break;
				case RefSystemFeature.PEOPLE_MAIN: bizObjectClass = PeopleMain.class.getName();break;
				case RefSystemFeature.PAYMENT_MAIN: bizObjectClass = Payment.class.getName();break;
			}
			if (bizObjectClass != null) {
				bizActions.put(reft.getId(), bizDAO.listBOActions(bizObjectClass, null, null, null, null));
			} 
		}
		
		for (List<BizActionEntity> lstBiz: bizActions.values()) {
			for (BizActionEntity biz: lstBiz) {
				if (BizActionUtils.hasRole(biz, role.getName())) {
					bizActOn.add(biz.getId());
				}
			}
		}
		
		featureOn = new ArrayList();
		
		List<Object[]> lst = usersDAO.listRolePerm(role.getId());
		for (Object[] row:lst ) {
			if (row[0] != null) {
				featureOn.add(((Number) row[0]).intValue()); 
			}
			if (row[1] != null) {
				bizActOn.add(((Number) row[1]).intValue()); 
			}
			
		}
	}

	public void save(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			role = userServ.saveRoles(role);
			userServ.setRPermissions(role.getId(), featureOn, bizActOn);
			
		} catch (ActionException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			
		}
		
	}
	
	public void actionChangedEvent(@Observes BusinessEvent event) {
		if (event.getEventCode() != EventCode.ROLE_PERM_CHANGED) {
			return;
		}
		
		Roles role = (Roles) event.getBusinessObject();
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		UserDataController userCtl = (UserDataController) facesCtx.getExternalContext().getSessionMap().get("userData");
		userCtl.reloadPerms(role);
		
	}
	
	public String cancel(){
		return "/views/roles/index.xhtml?faces-redirect=true";
	}
	
	public void featureSwitchLsn(ActionEvent event) {
		Integer fid = Convertor.toInteger(event.getComponent().getAttributes().get("featureid"));
		Boolean onoff = (Boolean) event.getComponent().getAttributes().get("onoff");
		if (onoff == null) {
			onoff = false;
		}
		featureOn.remove(fid);
		if (!onoff) {
			featureOn.add(fid);
		} 
	}
	
	public void bizactSwitchLsn(ActionEvent event) {
		Integer fid = Convertor.toInteger(event.getComponent().getAttributes().get("bizactionid"));
		Boolean onoff = (Boolean) event.getComponent().getAttributes().get("onoff");
		if (onoff == null) {
			onoff = false;
		}		
		bizActOn.remove(fid);
		if (!onoff) {
			bizActOn.add(fid);
		}
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

	public List<BizActionEntity> bizActionsFor(Integer topId) {
		return bizActions.get(topId);
	}

	public List<Reference> featuresFor(Integer topId) {
		return features.get(topId);
	}

	public List<Reference> getFeaturesTop() {
		return featuresTop;
	}
	
}
