package ru.simplgroupp.rest.api;

import ru.simplgroupp.data.ErrorData;
import ru.simplgroupp.rest.api.service.ReCaptchaService;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/recaptcha")
public class ReCaptchaController {
    @Inject
    private ReCaptchaService reCaptchaService;


    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<ErrorData>> addCallback(Map<String, String> params) {
        try {
            reCaptchaService.verify(params.get("response"));
            return new JsonResult<>();
        } catch (Exception e) {
            return new JsonResult<>(e);
        }
    }
}
