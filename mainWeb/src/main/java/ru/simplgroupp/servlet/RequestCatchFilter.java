package ru.simplgroupp.servlet;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestCatchFilter implements Filter {
    private RequestCache cache;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.cache.setRequest((HttpServletRequest) servletRequest);
        this.cache.setResponse((HttpServletResponse) servletResponse);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    public RequestCatchFilter() {}

    @Inject
    public RequestCatchFilter(RequestCache cache) {
        this.cache = cache;
    }
}
