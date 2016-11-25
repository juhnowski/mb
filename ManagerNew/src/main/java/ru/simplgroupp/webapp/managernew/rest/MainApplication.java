package ru.simplgroupp.webapp.managernew.rest;

import ru.simplgroupp.webapp.controller.ReferenceController;
import ru.simplgroupp.webapp.managernew.rest.api.FiasController;
import ru.simplgroupp.webapp.managernew.rest.api.auth.AuthorizationControllerManager;
import ru.simplgroupp.webapp.managernew.rest.providers.AppExceptionsMapper;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * REST приложение
 */
@ApplicationPath("/rest")
public class MainApplication extends Application {

    private HashSet<Class<?>> classes = new HashSet<Class<?>>();

    public MainApplication() {
        classes.add(AppExceptionsMapper.class);
        classes.add(JacksonConfiguratorManager.class);

        classes.add(FiasController.class);
        classes.add(ReferenceController.class);
        classes.add(LoginController.class);
        classes.add(AuthorizationControllerManager.class);
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }
}
