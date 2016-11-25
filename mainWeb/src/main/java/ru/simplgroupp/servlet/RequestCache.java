package ru.simplgroupp.servlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.transfer.Users;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.Serializable;

@RequestScoped
public class RequestCache implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(RequestCache.class);

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

    public RequestCache() {}

    @Produces @RequestScoped
    public HttpServletRequest getRequest() {
        return this.request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Produces @RequestScoped
    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}