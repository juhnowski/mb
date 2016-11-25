package ru.simplgroupp.webapp.manager.admin.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.persistence.UsersEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.PeoplePersonal;
import ru.simplgroupp.transfer.Roles;
import ru.simplgroupp.transfer.Users;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class EditUsersController extends AbstractSessionController implements Serializable {

   
    /**
	 * 
	 */
	private static final long serialVersionUID = -8265443265495648429L;

	@EJB
    UsersBeanLocal userBean;

    protected Integer prmUserId;

    protected Users users;

    protected String surname;

    protected String name;

    protected String midname;

    protected List<Roles> roles;

    protected Integer workplaceId;

    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            if (prmUserId == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("userid")) {
                    String ss = prms.get("userid");
                    prmUserId = Convertor.toInteger(ss);
                }
            }
            if (prmUserId != null) {
                reloadUser(facesCtx, prmUserId);
                roles = userBean.getRoles();
                PeoplePersonal ppl = users.getPeopleMain().getPeoplePersonalActive();
                if (ppl != null) {
                    surname = ppl.getSurname();
                    name = ppl.getName();
                    midname = ppl.getMidname();
                    workplaceId = users.getWorkplace() == null ? null : users.getWorkplace().getId();
                }
            }
        }
    }

    protected void reloadUser(FacesContext facesCtx, Integer prmId) {
        users = userBean.getUser(prmId, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, Users.Options.INIT_ROLES));
    }

    public Users getUsers() {
        return users;
    }

    public Integer getPrmUserId() {
        return prmUserId;
    }

    public void setPrmUserId(Integer prmUserId) {
        this.prmUserId = prmUserId;
    }

    public String save() {
    	FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
      
			userBean.saveUser(users, surname, name, midname, workplaceId);
		} catch (Exception e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		}
        return "success";
    }

    public String cancel() {
        return "canceled";
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMidname() {
        return midname;
    }

    public void setMidname(String midname) {
        this.midname = midname;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    public Integer getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(Integer workplaceId) {
        this.workplaceId = workplaceId;
    }
    
    public void changeLogin(ValueChangeEvent event){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		String login=(String) event.getNewValue();
		if (StringUtils.isNotEmpty(login)){
			UsersEntity newUser = userBean.findUserByLogin(login);
			if (newUser!=null&&newUser.getPeopleMainId().getId()!=users.getPeopleMain().getId()){
				JSFUtils.handleAsError(facesCtx, "login", new Exception("Пользователь с именем входа "+login+" уже есть в системе"));
			}
		}
		
	}
}
