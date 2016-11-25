package ru.simplgroupp.webapp.managernew.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Фильтр для редиректа не авторизованных пользователей на страницу авторизации.
 *
 * Для конфигурации используются следующие параметры авторизации:
 * LoginRedirectFilterManager.LOGIN_URL_PARAM - url страницы на которую будет перенаправлен не авторизованный запрос. Путь
 * указывается относительно contextRoot в котором выполняется запрос. Например: contextRoot=/main, параметр выставлен в
 * /index.html, значит редирект будет на /main/index.html. При редиректе к адресу добавляются 2 query параметра:
 * forceLogin=true и redirectTo=srcUrl, где srcUrl - адрес на который запрошен доступ.
 *
 * LoginRedirectFilterManager.EXCLUDED_URLS_PARAM - список регулярных выражений разделенных переводом строки. Запросы на адреса
 * соответствующие хотя бы одному выражению будут пропускаться через фильтр, даже если они не авторизованны. При компиляции
 * выражений к ним добавляется текущий contextRoot(аналогично  LoginRedirectFilterManager.LOGIN_URL_PARAM).
 */
public class LoginRedirectFilterManager implements Filter {
    public static final String LOGIN_URL_PARAM = "ru.simplgroupp.webapp.managernew.servlet.LoginRedirectFilterManager.loginUrl";
    public static final String EXCLUDED_URLS_PARAM = "ru.simplgroupp.webapp.managernew.servlet.LoginRedirectFilterManager.excludedURIs";

    private String loginUrl;
    private List<Pattern> excludedUrls;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();

        this.loginUrl = context.getContextPath() + filterConfig.getInitParameter(LOGIN_URL_PARAM);

        this.excludedUrls = new ArrayList<>();
        String excludedUrlsStr = filterConfig.getInitParameter(EXCLUDED_URLS_PARAM);
        if (excludedUrlsStr != null) {
            StringTokenizer tokenizer = new StringTokenizer(filterConfig.getInitParameter(EXCLUDED_URLS_PARAM));
            while (tokenizer.hasMoreTokens()) {
                String url = context.getContextPath() + tokenizer.nextToken();
                this.excludedUrls.add(Pattern.compile(url));
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getRemoteUser() != null && !"".equals(request.getRemoteUser())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        boolean excluded = false;
        for(Pattern p : this.excludedUrls) {
            if (p.matcher(request.getRequestURI()).matches()) {
                excluded = true;
            }
        }
        if(request.getRequestURI().equals(this.loginUrl)){
            excluded = true;
        }

        if (excluded) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String url = this.loginUrl;
            url += (url.contains("?") ? "&" : "?") + "forceLogin=true&redirectTo=" + request.getRequestURI();
            response.sendRedirect(url);
        }
    }

    @Override
    public void destroy() {

    }
}
