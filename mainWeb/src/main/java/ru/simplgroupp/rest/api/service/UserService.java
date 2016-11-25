package ru.simplgroupp.rest.api.service;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.Users;

@RequestScoped
public class UserService {
    private HttpServletRequest request;

	@EJB
	private UsersBeanLocal userBean;

	public Users getUser() {
        String userName = this.request.getRemoteUser();
        return userBean.findUserByLogin(userName.toLowerCase(), Utils.setOf(Users.Options.INIT_ROLES, PeopleMain.Options.INIT_PEOPLE_PERSONAL));

    }

    public UserService() {}

    @Inject
    public UserService(HttpServletRequest request) {
        this.request = request;
    }
}