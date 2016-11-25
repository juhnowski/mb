package ru.simplgroupp.webapp.managernew.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.webapp.managernew.servlet.RequestCacheManager;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

/**
 * Created by Cobalt <unger1984@gmail.com> on 18.12.15.
 */
@Path("/login")
@Stateless
@LocalBean
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @EJB
    protected KassaBeanLocal kassaBean;

    @Inject
    private RequestCacheManager requestCacheManager;

    /**
     * Попытка залогинить юзера
     */
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String login(ArrayList<String> data, @Context HttpServletRequest request){
        if(data.size()==3){
            String res;
            String login = data.get(0).toLowerCase();
            String password = data.get(1);

            try {
                res = kassaBean.getLink(login, password);
            } catch (KassaException e) {
                return "{\"status\":false}";
            }
            if(res!=null){
                // Пытаемся авторизовать на сессии, если пользователь не авторизован
                try {
                    this.requestCacheManager.login(login, password, true);
                } catch (ServletException e) {
                    e.printStackTrace();
                }

                return "{\"status\":true,\"link\":\""+res+"\"}";
            }
        }
        return "{\"status\":false}";
    }
}
