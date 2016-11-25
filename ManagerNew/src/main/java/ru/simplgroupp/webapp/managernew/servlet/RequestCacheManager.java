package ru.simplgroupp.webapp.managernew.servlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

@RequestScoped
public class RequestCacheManager implements Serializable {


	private static final Logger logger = LoggerFactory.getLogger(RequestCacheManager.class);
    private static final long serialVersionUID = -3348032448017948446L;

    private HttpServletRequest request;
    private HttpServletResponse response;

    public void login(String login, String password, boolean hashingRequired) throws ServletException {
        if (this.request.getRemoteUser() == null) {
            password = password.trim();
            password = hashingRequired ? DigestUtils.md5Hex(password) : password;
            this.request.login(login, password);
        }
    }

    public void logout() throws ServletException {
        if (this.request.getRemoteUser() != null) {
            this.request.logout();
        }
    }

    public boolean isAuthenticated() {
        return this.request.getRemoteUser() != null;
    }

    public RequestCacheManager() {}

//    @Produces @RequestScoped
//    public HttpServletRequest getRequest() {
//        return this.request;
//    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

//    @Produces @RequestScoped
//    public HttpServletResponse getResponse() {
//        return response;
//    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
