package ru.simplgroupp.webapp.managernew.rest.providers;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.webapp.managernew.rest.api.JsonResult;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AppExceptionsMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        Throwable rootException = getRootException(e);

        if (rootException instanceof KassaException) {
            return Response.status(Response.Status.OK).entity(new JsonResult<>(e)).build();
        } else if (rootException instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } else if (rootException instanceof RuntimeException&&rootException.getClass().getName().contains("MethodNotAllowedException")) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }

        throw new RuntimeException(e);
    }

    public Throwable getRootException(Throwable e) {
        if (e.getCause() != null) {
            return getRootException(e.getCause());
        } else {
            return e;
        }
    }
}
