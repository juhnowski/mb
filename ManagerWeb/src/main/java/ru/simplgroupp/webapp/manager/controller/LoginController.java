package ru.simplgroupp.webapp.manager.controller;

import java.io.Serializable;
import java.security.Principal;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.webapp.controller.AbstractController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class LoginController extends AbstractController implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9121113018063613405L;
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	@EJB
    UsersBeanLocal userBean;

	protected String userName="Email";
	protected String userPassword="Пароль";
	
	public String login() {
		
		 FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		 Principal pp = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		 if (pp!=null){
			 return "success";
		 }
		 
		 HttpServletRequest request = (HttpServletRequest) 
		    		facesCtx.getExternalContext().getRequest();

		    try {
		    	logger.info("user name "+getUserName().toLowerCase());  
				request.login(getUserName().toLowerCase(), DigestUtils.md5Hex(getUserPassword()));
				
			} catch (ServletException e) {
				logger.error("ошибка user name "+getUserName().toLowerCase()+", password "+DigestUtils.md5Hex(getUserPassword()));
				JSFUtils.handleAsError(facesCtx, null, new Exception("Неверное имя входа или пароль"));
				setUserPassword("Пароль");
				return null;
			}
		    
		logger.info("login success user name "+getUserName().toLowerCase()); 
		return "success";
	}

		
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
}
