package ru.simplgroupp.rest.api;

import ru.simplgroupp.rest.api.service.ListPeopleSumsService;
import ru.simplgroupp.rest.api.service.UserService;
import ru.simplgroupp.transfer.Users;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * User: Parfenov
 * Date: 04.06.2015
 * Time: 1:46
 */
@Path("/listsums")
@Stateless
public class ListSumsController {

    @Inject
    private ListPeopleSumsService sumsService;
    @Inject
    private UserService userServ;

    @GET
    @Path("/getData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getData(@Context HttpServletRequest request) {
        Users user = userServ.getUser();
        return new JsonResult<>(sumsService.getData(user));
    }

    @GET
    @Path("/mySum")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Double> mySum(@Context HttpServletRequest request) {
        Users user = userServ.getUser();
        return new JsonResult<>(sumsService.getMySum(user));
    }
}
