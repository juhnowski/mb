package ru.simplgroupp.rest.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.method.MethodConstraintViolationException;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.JsonResult;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Provider
public class AppExceptionsMapper implements ExceptionMapper<Exception> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Response toResponse(Exception e) {
        Throwable rootException = getRootException(e);

        if (rootException instanceof KassaException) {
            return Response.status(Response.Status.OK).entity(new JsonResult<>(e)).build();
        } else if (rootException instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } else if (rootException instanceof RuntimeException&&rootException.getClass().getName().contains("MethodNotAllowedException")) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } else if (e instanceof MethodConstraintViolationException) {
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(getJssonValidationErrors(
                        (MethodConstraintViolationException)e)).build();
            } catch (JsonProcessingException e1) {
                throw new RuntimeException(e1);
            }
        }

        throw new RuntimeException(e);
    }

    private Object getJssonValidationErrors(MethodConstraintViolationException e) throws JsonProcessingException {
        return "{ \"errors\" : " + objectMapper.writeValueAsString(getViolations(e)) + "}";
    }

    private Map<String, List<String>> getViolations(MethodConstraintViolationException e) {
        Map<String, List<String>> res = new HashMap<>();
        for (ConstraintViolation cv : e.getConstraintViolations()) {
            String path = "";
            if (null != cv.getPropertyPath()) {
                path = cv.getPropertyPath().toString();
            }
            List<String> msgs = res.get(path);
            if (null == msgs) {
                msgs = new ArrayList<>();
                res.put(path.substring(path.indexOf(".") + 1), msgs);
            }
            msgs.add(cv.getMessage());
        }
        return res;
    }

    public Throwable getRootException(Throwable e) {
        if (e.getCause() != null) {
            return getRootException(e.getCause());
        } else {
            return e;
        }
    }
}
