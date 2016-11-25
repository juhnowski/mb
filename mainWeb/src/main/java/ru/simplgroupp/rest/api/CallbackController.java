package ru.simplgroupp.rest.api;

import ru.simplgroupp.data.ErrorData;
import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.rest.api.data.CallbackData;
import ru.simplgroupp.rest.api.service.CallbackService;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/callback")
public class CallbackController {
    @Inject
    private CallbackService callbackService;

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<ErrorData>> addCallback(CallbackData callback) {
        try {
            callbackService.addCallback(callback);
            return new JsonResult<>();
        } catch (PeopleException e) {
            return new JsonResult<>(e);
        }
    }
}
