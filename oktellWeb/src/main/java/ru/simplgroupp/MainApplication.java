package ru.simplgroupp;

import ru.simplgroupp.controller.MainController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("rest")
public class MainApplication extends Application {
    private Set<Class<?>> classes = new HashSet<>();


    public MainApplication() {
        classes.add(MainController.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
