package ru.simplgroupp.webapp.managernew.rest;

import org.codehaus.jackson.map.*;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;

@Provider
@Produces("application/json")
public class JacksonConfiguratorManager implements ContextResolver<ObjectMapper> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private ObjectMapper mapper = new ObjectMapper();

    public JacksonConfiguratorManager() {
        mapper.getSerializationConfig().setDateFormat(DATE_FORMAT);
        DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
        mapper.setDeserializationConfig(deserializationConfig.withDateFormat(DATE_FORMAT));
    }

    @Override
    public ObjectMapper getContext(Class<?> arg0) {
        return mapper;
    }

    @javax.enterprise.inject.Produces
    public ObjectMapper getMapper() {
        return this.mapper;
    }
}
