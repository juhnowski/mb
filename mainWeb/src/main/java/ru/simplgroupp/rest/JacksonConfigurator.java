package ru.simplgroupp.rest;

import org.codehaus.jackson.map.*;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;
import java.util.Date;

@Provider
@Produces("application/json")
public class JacksonConfigurator implements ContextResolver<ObjectMapper> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private ObjectMapper mapper = new ObjectMapper();

    public JacksonConfigurator() {
        mapper.getSerializationConfig().setDateFormat(DATE_FORMAT);
        DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
        mapper.setDeserializationConfig(deserializationConfig.withDateFormat(DATE_FORMAT));
    }

    @Override
    public ObjectMapper getContext(Class<?> arg0) {
        return mapper;
    }
}
