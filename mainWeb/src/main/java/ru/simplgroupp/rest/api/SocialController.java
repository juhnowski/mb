package ru.simplgroupp.rest.api;

import ru.simplgroupp.rest.api.service.SocialService;
import ru.simplgroupp.rest.api.service.UserService;
import ru.simplgroupp.transfer.PeopleContact;
import ru.simplgroupp.transfer.Users;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.Map;

/**
 * Для асинхронной авторизации в сервисах
 */
@Singleton
@Path("/social")
public class SocialController {
    @Inject
    private SocialService socialService;

    @Inject
    private UserService userService;

    @Inject
    private HttpServletRequest httpServletRequest;

    @Inject
    private HttpServletResponse httpServletResponse;


    /**
     * Каллбэк для авторизации в однокласниках
     */
    @GET
    @Path("/ok")
    public String ok(@QueryParam("error") String error,
                     @QueryParam("code") String code,
                     @QueryParam("state") String state) {
        String socialId = socialService.ok(error, code);
        if (socialId != null) {
            if (state != null && state.equals("form")) {
                HttpSession session = httpServletRequest.getSession();
                session.setAttribute("ok-account", socialId);
            } else if (httpServletRequest.getRemoteUser() != null) {
                Users user = userService.getUser();
                socialService.saveSocialId(user, socialId, PeopleContact.NETWORK_OK);
            }

            return "<script>\n" +
                    "    if (window.opener.reInitSocialButtons) {window.opener.reInitSocialButtons()};\n" +
                    "    if (window.opener.getPeopleContact) {window.opener.getPeopleContact()};\n" +
                    "    window.close();" +
                    "</script>\n";
        }

        return "<script>window.close();</script>";
    }

    /**
     * Каллбэк для авторизации во вконтакте. Перекидывает на второй каллбэк
     */
    @GET
    @Path("/vk")
    public String vk() {
        return "<script>self.location='vk/token?'+location.hash.substr(1);</script>";
    }

    /**
     * Второй каллбэк для авторизации вконтакте. Переход из первого
     */
    @GET
    @Path("/vk/token")
    public String vk_token(@QueryParam("access_token") String accessToken,
                           @QueryParam("user_id") String userId,
                           @QueryParam("state") String state) {
        String socialId = socialService.vk(accessToken, userId);
        if (socialId != null) {
            if (state != null && state.equals("form")) {
                HttpSession session = httpServletRequest.getSession();
                session.setAttribute("vk-account", socialId);
            } else if (httpServletRequest.getRemoteUser() != null) {
                Users user = userService.getUser();
                socialService.saveSocialId(user, socialId, PeopleContact.NETWORK_VK);
            }


            return "<script>\n" +
                    "    if (window.opener.reInitSocialButtons) {window.opener.reInitSocialButtons()};\n" +
                    "    if (window.opener.getPeopleContact) {window.opener.getPeopleContact()};\n" +
                    "    window.close();\n" +
                    "</script>";
        }


        return "<script>window.close();</script>";
    }

    /**
     * Калбэк для авторизации фэйсбук
     */
    @GET
    @Path("/fb")
    public String fb_token(@QueryParam("code") String code,
                           @QueryParam("state") String state) {
        String socialId = socialService.fb(code);
        if (socialId != null) {
            if (state != null && state.equals("form")) {
                HttpSession session = httpServletRequest.getSession();
                session.setAttribute("fb-account", socialId);
            } else if (httpServletRequest.getRemoteUser() != null) {
                Users user = userService.getUser();
                socialService.saveSocialId(user, socialId, PeopleContact.NETWORK_FB);
            }

            return "<script>\n" +
                    "    if (window.opener.reInitSocialButtons) {window.opener.reInitSocialButtons()};\n" +
                    "    if (window.opener.getPeopleContact) {window.opener.getPeopleContact()};\n" +
                    "    window.close();\n" +
                    "</script>";
        }


        return "<script>window.close();</script>";
    }

    /**
     * Каллбэк для авторизации мэйл.ру - перекидывает на второй
     */
    @GET
    @Path("/ml")
    public String ml() {
        return "<script>self.location='ml/token?'+location.hash.substr(1);</script>";
    }

    /**
     * Второй каллбэк для авторизации мэйл.ру. Переход из первого
     */
    @GET
    @Path("/ml/token")
    public String ml_token(@QueryParam("access_token") String accessToken,
                           @QueryParam("x_mailru_vid") String x_mailru_vid,
                           @QueryParam("state") String state) {
        String socialId = socialService.ml(accessToken, x_mailru_vid);
        if (socialId != null) {
            if (state != null && state.equals("form")) {
                HttpSession session = httpServletRequest.getSession();
                session.setAttribute("ml-account", socialId);
            } else if (httpServletRequest.getRemoteUser() != null) {
                Users user = userService.getUser();
                socialService.saveSocialId(user, socialId, PeopleContact.NETWORK_MM);
            }

            return "<script>" +
                    "    if (window.opener.reInitSocialButtons) {window.opener.reInitSocialButtons()};\n" +
                    "    if (window.opener.getPeopleContact) {window.opener.getPeopleContact()};\n" +
                    "    window.close();" +
                    "</script>";
        }


        return "<script>window.close();</script>";
    }

    /**
     * Возвращает список соц сетей id которых сохранено в сессии
     */
    @GET
    @Path("/getAll")
    public JsonResult<Map<String, Boolean>> getSocialInSession() {
        Map<String, Boolean> responseMap = new HashMap<>();

        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            if (session.getAttribute("ok-account") != null) {
                responseMap.put("ok", true);
            }

            if (session.getAttribute("vk-account") != null) {
                responseMap.put("vk", true);
            }

            if (session.getAttribute("fb-account") != null) {
                responseMap.put("fb", true);
            }

            if (session.getAttribute("ml-account") != null) {
                responseMap.put("ml", true);
            }
        }


        return new JsonResult<>(responseMap);
    }

    /**
     * Удаляем соц сеть из сессии
     *
     * @param network название соц сети (ok, vk, fb, ml)
     */
    @GET
    @Path("/remove/{network}")
    public JsonResult<String> removeSocialId(@PathParam("network") String network) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {

            switch (network) {
                case "ok":
                    session.removeAttribute("ok-account");
                    break;
                case "vk":
                    session.removeAttribute("vk-account");
                    break;
                case "fb":
                    session.removeAttribute("fb-account");
                    break;
                case "ml":
                    session.removeAttribute("ml-account");
                    break;
            }
        }

        return new JsonResult<>("");
    }
}
