package ru.simplgroupp.webapp.managernew.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Denis Grabok [digrabok@gmail.com]
 */
public class ContentTypeFilterManager implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String url = ((HttpServletRequest)servletRequest).getRequestURL().toString();
        if (url.endsWith(".ico")) {
            servletResponse.setContentType("image/x-icon");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
