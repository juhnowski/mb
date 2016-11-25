package ru.simplgroup.controller;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by PARFENOV on 22.07.2015.
 * <p/>
 * REST приложение
 */
@ApplicationPath("/rest")
public class MainApplication extends Application {

    private HashSet<Class<?>> classes = new HashSet<Class<?>>();


    public MainApplication() {
        classes.add(CmsController.class);
        classes.add(LoginController.class);
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }

}
