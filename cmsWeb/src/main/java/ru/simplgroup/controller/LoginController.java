package ru.simplgroup.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.IOException;


/**
 * User: Parfenov
 * Date: 24.07.2015
 * Time: 0:18
 */
@Path("/login")
public class LoginController {

    @GET
    @Path("logout")
    public void getData(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        try {
            response.setHeader("Cache-Control", "no-cache, no-store");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            HttpSession ses = request.getSession(false);
            if (ses != null) {
                ses.invalidate();// remove session.
            }

            request.logout();
            response.sendRedirect(request.getContextPath());
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
