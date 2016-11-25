package ru.simplgroupp.rest.api;

import ru.simplgroupp.rest.api.data.antifraud.AntifraudField;
import ru.simplgroupp.rest.api.data.antifraud.AntifraudPage;
import ru.simplgroupp.rest.api.service.AntifraudService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;


@RequestScoped
@Path("/af")
public class AntifraudController {

    private AntifraudService service;
    private static final String REQUEST_COOKIE_NAME = "request_id";

    @PUT
    @Path("/field")
    public void addField(AntifraudField field, @Context HttpServletRequest request) {

        field.setSessionId(request.getSession().getId());
        service.addField(field);
    }

    @PUT
    @Path("/page")
    public void addPage(AntifraudPage page, @Context HttpServletRequest request) {

        page.setSessionId(request.getSession().getId());
        for (Cookie cookie : request.getCookies()) {
            if (REQUEST_COOKIE_NAME.equals(cookie.getName())) {
                page.setRequestId(cookie.getValue());
            }
        }
        service.addPage(page);
    }

    public AntifraudController() {}

    @Inject
    public AntifraudController(AntifraudService service) {
        this.service = service;
    }
}
