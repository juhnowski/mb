package ru.simplgroupp.rest.api.auth;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.persistence.PeopleContactEntity;
import ru.simplgroupp.servlet.RequestCache;
import ru.simplgroupp.rest.api.JsonResult;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.util.SettingsKeys;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Path("/")
@Stateless
public class AuthorizationController {
    public static final String ILLEGAL_TOKEN_MSG = "Illegal auth token";

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

    private static final String STATUS = "status";
    private static final String ERROR = "error";

    private static final int MIN_PASSWORD_LENGTH = 5;
    private static final int MAX_PASSWORD_LENGTH = 15;

    protected int peopleMainId;

    private AuthCache cache;
    private RequestCache requestCache;
    private UsersBeanLocal usersBean;

    @EJB
    protected KassaBeanLocal kassaBean;

    @EJB
    protected PeopleBeanLocal peopleBean;

    @EJB
    protected MailBeanLocal mailBean;

    @EJB
    protected RulesBeanLocal rulesBean;

    public AuthorizationController() {}

    @Inject
    public AuthorizationController(AuthCache cache, RequestCache requestCache, UsersBeanLocal usersBean) {
        this.cache = cache;
        this.requestCache = requestCache;
        this.usersBean = usersBean;
    }

    /**
     * Endpoint for access current pass phrase for token authentication
     * @return Pass phrase for token authentication.
     */
    @GET
    @Path("/auth/token/passphrase")
    public JsonResult<String> getTokenPasPhrase() {
        return new JsonResult<String>(this.cache.getPassPhrase());
    }

    @POST
    @Path("/auth/logout")
    public JsonResult<String> logout() {
        try {
            this.requestCache.logout();
        } catch (ServletException e) {
            // skip
        }
        return new JsonResult<>("ok");
    }

    @POST
    @Path("/auth/token/login")
    public JsonResult<LoginResult> loginByToken(String encryptedToken) {
        boolean authenticated = this.requestCache.isAuthenticated();

        if (!authenticated) {
            String passPhrase = this.cache.getPassPhrase();
            Users user = this.usersBean.findUserByToken(passPhrase, encryptedToken, Utils.setOf());
            if (user != null) {
                try {
                    this.requestCache.login(user.getUserName(), user.getPassword(), false);
                    authenticated = true;
                } catch (ServletException e) {
                    // ignore
                }
            }
        }

        if (authenticated) {
            return new JsonResult<LoginResult>(new LoginResult(true));
        } else {
            throw new IllegalArgumentException(ILLEGAL_TOKEN_MSG);
        }
    }

    /**
     * Восстановление пароля по номеру телефона
     */
    @POST
    @Path("/remind-by-phone")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String remindPassByPhone(String mobilePhone) {

        String checkCode = null;

        JSONObject response = new JSONObject();
        response.put( STATUS, true );

        if ( StringUtils.isBlank( mobilePhone ) ) {
            response.put( STATUS, false );
            response.put( ERROR, "Неверный формат электронного адреса" );
            return response.toJSONString();
        }
        PeopleContactEntity conPhone = null;
        List<PeopleContactEntity> phones=peopleBean.findPeopleByContact(PeopleContact.CONTACT_CELL_PHONE,Convertor.fromMask(mobilePhone), null, Partner.CLIENT, ActiveStatus.ACTIVE, null);
        for(PeopleContactEntity contact:phones){
			int i=kassaBean.findManWithPrivateCabinet(contact.getPeopleMainId());
			if (i!=0){
				conPhone=contact;
			}
		}
        String smsCodePhone = mailBean.generateCodeForSending();
   //     String smsCodePhone = GenUtils.genCode(6);
        if(conPhone==null){
            response.put( STATUS, false );
            response.put( ERROR, "Номер не зарегестрирован в системе" );
            return response.toJSONString();
        }
        peopleMainId=conPhone.getPeopleMainId().getId();
        try{
        	boolean bsend=mailBean.sendSMS(conPhone.getValue(), "Ваш код для смены пароля в системе Ontime: " + smsCodePhone);

		} catch (Exception ex) {
            response.put( STATUS, false );
            response.put(ERROR, "Внутренняя ошибка. Попытайтесь еще раз.");
            return response.toJSONString();
		}
        logger.info("sms code phone "+smsCodePhone);
        checkCode = smsCodePhone;
        cache.setCheckCode(checkCode);
        return response.toJSONString();
    }

    /**
     * Восстановление пароля по электронной почте
     */
    @POST
    @Path("/remind-by-email")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String remindPassByEmail(String email) {

        String checkCode = null;
        JSONObject response = new JSONObject();
        response.put( STATUS, true );

        if ( StringUtils.isBlank( email ) || !isValidEmailAddress( email) ) {
            response.put( STATUS, false );
            response.put( ERROR, "Неверный формат электронного адреса" );
            return response.toJSONString();
        }

        PeopleContactEntity conEmail = null;
        peopleMainId = -1;

        List<PeopleContactEntity> emails=peopleBean.findPeopleByContact(PeopleContact.CONTACT_EMAIL,email.toLowerCase(), null, Partner.CLIENT, ActiveStatus.ACTIVE,null);
        for(PeopleContactEntity contact:emails){
            int i = kassaBean.findManWithPrivateCabinet(contact.getPeopleMainId());
            if ( i != 0 ){
                conEmail=contact;
                break;
            }
        }

        if ( conEmail != null ) {
            logger.info("Человек с email "+conEmail.getPeopleMainId().getId());
            peopleMainId=conEmail.getPeopleMainId().getId();
        }

        if ( peopleMainId == -1 ) {
            response.put( STATUS, false );
            response.put( ERROR, "Эл. адрес не зарегестрирован в системе ");
            return response.toJSONString();
        }
        String codeEmail = mailBean.generateCodeForSending();
       // String codeEmail = GenUtils.genCode(6);
        logger.info( "code email "+codeEmail );
        try {

            Map<String,Object> desc = new HashMap<String,Object>();
            desc.put("description", "Ваш код № 1 для смены пароля в системе Ontime: " + codeEmail);
            String emailBody = kassaBean.generateEmail(null, Report.EMAIL_ID, desc);

            mailBean.send("Код для смены пароля", emailBody, conEmail.getValue());

        } catch (Exception ex) {
            response.put( STATUS, false );
            response.put( ERROR, "Внутренняя ошибка. Попытайтесь еще раз.");
            return response.toJSONString();
        }
        checkCode = codeEmail;
        cache.setCheckCode(checkCode);
        response.put( ERROR, "Код для смены пароля выслан на указанный e-mail" );
        return response.toJSONString();
    }

    @POST
    @Path("/remind-sms")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String remindPassSms(String code) {

        String checkCode = cache.getCheckCode();

        JSONObject response = new JSONObject();
        response.put( STATUS, true );

        if ( StringUtils.isBlank(code) || !checkCode.equals( code.trim() ) ) {
            response.put( STATUS, false );
            response.put( ERROR, "Неверный проверочный код");
            return response.toJSONString();
        }

        return response.toJSONString();
    }

    @POST
    @Path("/remind-confirm")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String remindPassConfirm(HashMap<String, Object> data) {

        String checkCode = cache.getCheckCode();

        String emailCode = (String) data.get("code");
        String newPassword1 = (String) data.get("newPassword1");
        String newPassword2 = (String) data.get("newPassword2");

        JSONObject response = new JSONObject();
        response.put(STATUS, true);

        if (checkCode == null || StringUtils.isBlank(emailCode) || !checkCode.equals(emailCode.trim())) {
            response.put(STATUS, false);
            response.put(ERROR, "Неверный проверочный код");
            return response.toJSONString();
        }

        if (StringUtils.isBlank(newPassword1) || StringUtils.isBlank(newPassword2)) {
            response.put(STATUS, false);
            response.put(ERROR, "Пароль не может быть пустым");
            return response.toJSONString();
        }

        if (!newPassword1.equals(newPassword2)) {
            response.put(STATUS, false);
            response.put(ERROR, "Пароли не совпадают");
            return response.toJSONString();
        }

        if(newPassword1.length() < MIN_PASSWORD_LENGTH || newPassword1.length() > MAX_PASSWORD_LENGTH) {
            response.put(STATUS, false);
            response.put(ERROR, "Длина пароля должна быть от " + MIN_PASSWORD_LENGTH +  " до " + MAX_PASSWORD_LENGTH + " символов");
            return response.toJSONString();
        }

        try {
            usersBean.changePassword(peopleMainId, newPassword1);
        } catch (Exception e) {
            response.put(STATUS, false);
            response.put(ERROR, "Ошибка при смене пароля ...");
        }

        return response.toJSONString();
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    @GET
    @Path("/auth/type")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String getAuthorizationType() {
        Map<String, Object> login_const = rulesBean.getLoginConstants();
        String result = (String) login_const.get(SettingsKeys.LOGIN_WAY);

        JSONObject response = new JSONObject();
        response.put(STATUS, true);
        response.put("type", result);

        return response.toJSONString();
    }
}
