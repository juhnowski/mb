package ru.simplgroupp.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.simplgroupp.rest.AppBean;
import ru.simplgroupp.rest.AppInitializer;

public class AppearanceFilter implements Filter {
	
	@EJB
	AppInitializer appInit;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String requestURI = request.getRequestURI();
        
        if (requestURI.startsWith("/main/index.html")) {
        	String newURI = "/main/appearance/" + appInit.getAppearance() + "/index.shtml";
        	HttpServletResponse hres = (HttpServletResponse) res;
        	hres.sendRedirect(newURI);
        	return;
        }
 
        if (requestURI.startsWith("/main/appearance/" + appInit.getAppearance() + "/")) {
        	chain.doFilter(req, res);    	
        } else if (requestURI.startsWith("/main/")) {
        	String newURI = requestURI.replace("/main/", "/main/appearance/" + appInit.getAppearance() + "/");
        	HttpServletResponse hres = (HttpServletResponse) res;
        	hres.sendRedirect(newURI); 
        } else {        
        	String newURI = "/main/appearance/" + appInit.getAppearance() + "/index.shtml";
        	HttpServletResponse hres = (HttpServletResponse) res;
        	hres.sendRedirect(newURI);        	
        }
       
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
