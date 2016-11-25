package ru.simplgroupp.rest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.data.Data;
import ru.simplgroupp.data.ErrorData;
import ru.simplgroupp.data.Step2Data;
import ru.simplgroupp.data.Step3Data;
import ru.simplgroupp.data.Step4Data;
import ru.simplgroupp.data.Step5Data;
import ru.simplgroupp.data.Step7Data;
import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.plugins.payment.PayonlineAcquiringPluginConfig;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.fias.ejb.IFIASService;
import ru.simplgroupp.interfaces.ActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.MailBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.PayonlineAcquiringBeanLocal;
import ru.simplgroupp.interfaces.service.CreditRequestWizardService;
import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.servlet.RequestCache;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.Report;
import ru.simplgroupp.transfer.creditrequestwizard.Step0Data;
import ru.simplgroupp.transfer.creditrequestwizard.Step1Data;
import ru.simplgroupp.transfer.creditrequestwizard.Step6Data;
import ru.simplgroupp.workflow.PluginExecutionContext;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Бэкэнд для первой заявки
 */
@Path("/")
@Stateless
public class CreditRequestWizardPostController {

    private static final Logger logger = LoggerFactory.getLogger(CreditRequestWizardPostController.class);

    @EJB
    protected KassaBeanLocal kassaBean;

    @EJB
    protected PeopleBeanLocal peopleBean;

    @EJB
    protected MailBeanLocal mailBean;

    @EJB
    protected IFIASService fiasBean;
    
    @EJB
    protected CreditRequestDAO crDAO;
    
    @EJB
    protected AppBean appBean;

    @EJB
    private transient ActionProcessorBeanLocal actionProcessor;

    @EJB
    protected CreditRequestWizardService wizardService;

    @EJB
    protected Step1DataValidator step1Validator;

    @EJB
    private Step6DataValidator step6Validator;

    @Inject
    private RequestCache requestCache;

    /**
     * Отправляет на телефон код подтверждения
     *
     * @param phone телефон на который шлем
     * @return хэш код в котором зашифрован код подтверждения и телефон
     */
    @POST
    @Path("/sms/{phone}")
    public String sendSMS(@PathParam("phone") String phone) {
        String code = StaticFuncs.generateCode(6);
        StaticFuncs.log("Phone: " + phone + " code=" + code);
        mailBean.sendSMSV2(phone.replaceAll("[^\\d.]", ""), "Код: " + code+" Это код подтверждения номера телефона в системе Растороп.");
        return StaticFuncs.md5("salt" + phone + "supersalt" + code + "megasalt"); // с этим хэшем будем сравнивать ответ
    }

    /**
     * Отправляет на email код подтверждения
     *
     * @param email телефон на который шлем
     * @return хэш код в котором зашифрован код подтверждения и email
     */
    @POST
    @Path("/email/{email}")
    public String sendEmail(@PathParam("email") String email) {
        String code = StaticFuncs.generateCode(6);
        StaticFuncs.log("Email: " + email + " code=" + code);
        try {
            //генерируем письмо с версткой
            Map<String,Object> mp=new HashMap<String,Object>();
            mp.put("description", "Для подтверждения регистрации заявки, введите в поле Код подтверждения Email код " + code);
            String smsgBody = kassaBean.generateEmail(null, Report.EMAIL_ID, mp);
            mailBean.send("Код подтверждения email-адреса", smsgBody, email);
        } catch (Exception e) {
        	logger.error("Не удалось послать email, ошибка "+e);
        }
        return StaticFuncs.md5("salt" + email + "supersalt" + code + "megasalt"); // с этим хэшем будем сравнивать ответ
    }

    /**
     * Отправляет повторно код подтверждения на 7м шаге
     *
     * @return хэш код в котором зашифрован код подтверждения и телефон
     */
    @POST
    @Path("/sms")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data sendSMS(Data reqdata,
                        @Context HttpServletRequest req) {
    	// TODO Убрать совсем, ибо не надо
        Data ans = new Data();
        Integer reqId = 0; // первый раз тут если не поменяется
        Integer stepcount = 0; // первый раз тут если не поменяется
        if (reqdata.getSession_id() != null && reqdata.getRequest_id() != null) {
            reqId = StaticFuncs.decodeIdFromHash(reqdata.getRequest_id());
            if (reqId != 0) {
                stepcount = StaticFuncs.getSessionStepCount(reqdata.getSession_id(), reqId);
            }
        }
        // теперь если в reqId и stepcount 6 - то мы первый раз на этой странице
        if (reqId == 0 || stepcount < 6) {
            // а этого быть не может никак!
            return Data.setErrorRedirect();
        }

        Step7Data info = new Step7Data();
        Map<String, Object> map = kassaBean.paramsStep7(reqId);
        StaticFuncs.log(map);

        String code = StaticFuncs.generateCode(6);
        String phone = (String) map.get("cellphone");
        StaticFuncs.log("Phone: " + phone + " code=" + code);
        mailBean.sendSMSV2(phone.replaceAll("[^\\d.]", ""), "Код: " + code+" Это код для подтверждения согласия с условиями работы в системе Растороп.");
        info.setPhonehash(StaticFuncs.md5("saltsupersalt" + code + "megasalt"));

        ans.setStatus(true);
        ans.setStep7(info);
        ans.setRequest_id(StaticFuncs.generateHashId(reqId));
        ans.setSession_id(StaticFuncs.generateSessionId(reqId, stepcount));
        return ans;
    }

    /**
     * Попытка залогинить юзера
     */
    @POST
    @Path("/login")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String login(ArrayList<String> data, @Context HttpServletRequest request){
        if(data.size()==2){
            String res;
            String login = data.get(0);
            String password = data.get(1);

            try {
                res = kassaBean.getLink(login, password);
            } catch (KassaException e) {
                return "{\"status\":false}";
            }
            if(res!=null){
                // Пытаемся авторизовать на сессии, если пользователь не авторизован
                try {
                    this.requestCache.login(login, password, true);
                } catch (ServletException e) {
                    e.printStackTrace();
                }

                return "{\"status\":true,\"link\":\""+res+"\"}";
            }
        }
        return "{\"status\":false}";
    }

    /**
     * Выбор суммы и срока займа
     */
    @POST
    @Path("/step0")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step0(Data data, @Context HttpServletRequest request) {

        Data answer = new Data();
        Integer creditRequestId = 0;
        Integer stepCount = 0; // первый раз тут если не поменяется
        if (data.getRequest_id() != null) {
            creditRequestId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
            stepCount = StaticFuncs.getSessionStepCount(data.getSession_id(), creditRequestId);
            if (creditRequestId != 0) {
                try {
                    CreditRequest creditRequest = crDAO.getCreditRequest(creditRequestId, Collections.EMPTY_SET);
                    if (creditRequest != null) {
                        stepCount = StaticFuncs.getSessionStepCount(data.getSession_id(), creditRequestId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (data.getStep0() != null) {
            Step0Data step0 = data.getStep0();

            step0.setUserAgent(request.getHeader("User-Agent"));       
         
            step0.setClientIp(getRealIp(request));

            try {
                creditRequestId = appBean.doStep0(creditRequestId, step0);
                StaticFuncs.log("Step0 = " + creditRequestId);
            } catch (Exception e) {
            	if (e instanceof KassaException){
                	return Data.setErrorAlert(e.getMessage());
                } else {
                	e.printStackTrace();
                    return Data.setErrorAlert("Ошибка приложения");
                }
            }

            if (creditRequestId > 0) {
                String hash = StaticFuncs.generateHashId(creditRequestId);
                answer.setRequest_id(hash);
                if (stepCount == 0) {
                    stepCount++;
                }
                answer.setSession_id(StaticFuncs.generateSessionId(creditRequestId, stepCount)); // новая сессия
                answer.setStatus(true);
                return answer;
            }
        }

        return Data.setErrorAlert("Ошибка приложения");
    }

    private String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Сабмитим шаг 1
     */
    @POST
    @Path("/step1")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step1(Data data,
                      @Context HttpServletRequest req) {
        StaticFuncs.log("SEND STEP1");
        Data answer = new Data();
        Integer creditRequestId = 0;
        Integer stepCount = 0; // первый раз тут если не поменяется
        CreditRequest creditRequest = null;
        if (data.getSession_id() != null && data.getRequest_id() != null) {
            creditRequestId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
            if (creditRequestId != 0) {
                stepCount = StaticFuncs.getSessionStepCount(data.getSession_id(), creditRequestId);
                try {
                    creditRequest = crDAO.getCreditRequest(creditRequestId, Collections.EMPTY_SET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (creditRequestId == 0 || stepCount < 1) {
            // а этого быть не может никак!
            return Data.setErrorRedirect();
        }

        Step1Data step1 = data.getStep1();

        step1Validator.validate(step1, creditRequest, answer.getErrors());

        if (answer.getErrors().size() > 0) {
            answer.setStatus(false);
            return answer;
        }

        try {
            wizardService.saveStep1(creditRequestId, step1);
        } catch (Exception e) {
        	if (e instanceof KassaException){
        		return Data.setErrorAlert(e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Ошибка приложения");
            } else {
            	e.printStackTrace();
                return Data.setErrorAlert("Ошибка приложения");
            }
            
        }

        String hash = StaticFuncs.generateHashId(creditRequestId);
        answer.setRequest_id(hash);
        if (stepCount == 1) {
            stepCount++; // если мы были тут первый раз, то увеличим счетчик шагов
        }
        answer.setSession_id(StaticFuncs.generateSessionId(creditRequestId, stepCount)); // новая сессия
        answer.setStatus(true);
        answer.setStepcount(stepCount);
        return answer;
    }

    /**
     * Сабмитим шаг 2
     */
    @POST
    @Path("/step2")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step2(Data data, @Context HttpServletRequest request) {
        Data ans = new Data();
        Integer creditRequestId = 0; // первый раз тут если не поменяется
        Integer stepCount = 0; // первый раз тут если не поменяется
        if (data.getSession_id() != null && data.getRequest_id() != null) {
            creditRequestId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
            if (creditRequestId != 0) {
                stepCount = StaticFuncs.getSessionStepCount(data.getSession_id(), creditRequestId);
            }
        }
        // теперь если в reqId и stepcount 1 - то мы первый раз на этой странице
        if (creditRequestId == 0 || stepCount < 2) {
            // а этого быть не может никак!
            return Data.setErrorRedirect();
        }
        // во всех других случаях - это повторный заход или возврат
        Step2Data step2 = data.getStep2();
        if (StringUtils.isEmpty(step2.getGender())) {
            ans.getErrors().add(new ErrorData("gender", "Поле обязательно для заполнения"));
        }
        if (step2.getSeria().length() != 4) {
            ans.getErrors().add(new ErrorData("seria", "Поле обязательно для заполнения"));
        }
        if (step2.getNomer().length() != 6) {
            ans.getErrors().add(new ErrorData("nomer", "Поле обязательно для заполнения"));
        }
        if (StringUtils.isEmpty(step2.getWhenIssued())) {
            ans.getErrors().add(new ErrorData("whenIssued", "Поле обязательно для заполнения"));
        } else {
            Date d = Convertor.toDate(step2.getWhenIssued(), "dd.MM.yyyy");
            if (!StaticFuncs.checkDate(d)) {
                ans.getErrors().add(new ErrorData("whenIssued", "Паспорт не может быть выдан этой датой"));
            }
        }
        if (StringUtils.isEmpty(step2.getCode_dep())) {
            ans.getErrors().add(new ErrorData("code_dep", "Поле обязательно для заполнения"));
        }
        if (StringUtils.isEmpty(step2.getBirthplace())) {
            ans.getErrors().add(new ErrorData("birthplace", "Поле обязательно для заполнения"));
        }
        if (StringUtils.isEmpty(step2.getWhogive())) {
            ans.getErrors().add(new ErrorData("whogive", "Поле обязательно для заполнения"));
        }

        if (StringUtils.isEmpty(step2.getRegistrationFias())) {
            ans.getErrors().add(new ErrorData("region", "Поле обязательно для заполнения"));
        } else {
            if (fiasBean.hasChildren(step2.getRegistrationFias(), true)) {
                ans.getErrors().add(new ErrorData("region", "Поле обязательно для заполнения"));
            }
        }

        if (StringUtils.isEmpty(step2.getHome())) {
            ans.getErrors().add(new ErrorData("home", "Поле обязательно для заполнения"));
        }
        if (step2.getMarriage() == null || step2.getMarriage() < 0) {
            ans.getErrors().add(new ErrorData("marriage", "Поле обязательно для заполнения"));
        }

        if (StringUtils.isNotEmpty(step2.getSnils())) {
            step2.setSnils(step2.getSnils().replaceAll("[^\\d.]", ""));
            if (step2.getSnils().length() != 11) {
                ans.getErrors().add(new ErrorData("snils", "Поле обязательно для заполнения"));
            }
        }
        if (StringUtils.isNotEmpty(step2.getInn()) && step2.getInn().length() != 12) {
            ans.getErrors().add(new ErrorData("inn", "Поле обязательно для заполнения"));
        }
        if (StringUtils.isNotEmpty(step2.getSurname()) || StringUtils.isNotEmpty(step2.getName()) ||
                StringUtils.isNotEmpty(step2.getMidname()) || StringUtils.isNotEmpty(step2.getMobilephone())
                || StringUtils.isNotEmpty(step2.getBirthdate())) {
            // если хоть что-то заполняли у супруга то надо заполнить все
            if (StringUtils.isEmpty(step2.getSurname())) {
                ans.getErrors().add(new ErrorData("surname", "Поле обязательно для заполнения"));
            }
            if (StringUtils.isEmpty(step2.getName())) {
                ans.getErrors().add(new ErrorData("name", "Поле обязательно для заполнения"));
            }
            if (StringUtils.isEmpty(step2.getBirthdate())) {
                ans.getErrors().add(new ErrorData("birthdate", "Поле обязательно для заполнения"));
            } else {
                Date d = Convertor.toDate(step2.getBirthdate(), "dd.MM.yyyy");
                if (!StaticFuncs.checkDate(d)) {
                    ans.getErrors().add(new ErrorData("birthdate", "Дата не может быть в будущем"));
                }
            }
            if (!StringUtils.isEmpty(step2.getDatabeg())) {
                Date d = Convertor.toDate(step2.getDatabeg(), "dd.MM.yyyy");
                if (!StaticFuncs.checkDate(d)) {
                    ans.getErrors().add(new ErrorData("databeg", "Дата не может быть в будущем"));
                }
            }
        }

        if (StringUtils.isEmpty(step2.getRealtyDate())) {
            ans.getErrors().add(new ErrorData("realtyDate", "Поле обязательно для заполнения"));
        } else {
            Date d = Convertor.toDate(step2.getRealtyDate(), "dd.MM.yyyy");
            if (!StaticFuncs.checkDate(d)) {
                ans.getErrors().add(new ErrorData("realtyDate", "Дата не может быть в будущем"));
            }
        }

        if (ans.getErrors().size() > 0) {
            ans.setStatus(false);
            return ans;
        }
        
        Map<String, Object> step = new HashMap<String, Object>();
        step.put("id", creditRequestId);
        step.put("gender", step2.getGender());
        step.put("seria", step2.getSeria());
        step.put("nomer", step2.getNomer());
        step.put("whenIssued", step2.getWhenIssued());
        step.put("code_dep", step2.getCode_dep());
        step.put("place", step2.getBirthplace());
        step.put("whoIssued", step2.getWhogive());
        step.put("marriage", step2.getMarriage());
        step.put("children", step2.getChildren());
        step.put("snils", step2.getSnils());
        step.put("inn", step2.getInn());
        step.put("registrationFias", step2.getRegistrationFias());
        step.put("home", step2.getHome());
        step.put("builder", step2.getBuilder());
        step.put("korpus", step2.getKorpus());
        step.put("apartment", step2.getApartment());
        step.put("RealtyDate", step2.getRealtyDate());
        step.put("surname", step2.getSurname());
        step.put("name", step2.getName());
        step.put("midname", step2.getMidname());
        step.put("mobilephone", step2.getMobilephone());
        step.put("birthdate", step2.getBirthdate());
        step.put("typework", step2.getTypework());
        step.put("databeg", step2.getDatabeg());
        step.put("source_from", step2.getSource_from());
        step.put("first_vizit_date", step2.getFirst_vizit_date());
        step.put("ga_visitor_id", step2.getGa_visitor_id());
        step.put("visit_count", step2.getVisit_count());

        StaticFuncs.log(step);

        Integer req_id = 0;
        try {
            Map<String, Object> ansR = kassaBean.saveStep2(step);
            StaticFuncs.log(ans);
            if (ansR.get("peopleMainId") != null) {
                // TODO редирект на ЛК
                return Data.setErrorAlert("Уже есть такой пользователь!");
            } else if (ansR.get("creditRequestId") != null) {
                req_id = (Integer) ansR.get("creditRequestId");
            }
        } catch (Exception e) {
            
            if (e instanceof KassaException){
            	return Data.setErrorAlert(e.getMessage());
            } else {
            	e.printStackTrace();
                return Data.setErrorAlert("Ошибка приложения");
            }
        }

        StaticFuncs.log("Old id=" + creditRequestId + " New id=" + req_id);

        if (req_id > 0) {
            String hash = StaticFuncs.generateHashId(req_id);
            ans.setRequest_id(hash);
            if (stepCount == 2)
                stepCount++; // если мы были тут первый раз, то увеличим счетчик шагов
            ans.setSession_id(StaticFuncs.generateSessionId(req_id, stepCount)); // новая сессия
            ans.setStatus(true);
            ans.setStepcount(stepCount);
            return ans;
        }
        StaticFuncs.log("Error User id: 0 !!!");
        return Data.setErrorAlert("Ошибка приложения");
    }

    /**
     * Сабмитим шаг 3
     */
    @POST
    @Path("/step3")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step3(Data reqdata,
                      @Context HttpServletRequest req) {

        Data ans = new Data();
        Integer reqId = 0; // первый раз тут если не поменяется
        Integer stepcount = 0; // первый раз тут если не поменяется
        if (reqdata.getSession_id() != null && reqdata.getRequest_id() != null) {
            reqId = StaticFuncs.decodeIdFromHash(reqdata.getRequest_id());
            if (reqId != 0) {
                stepcount = StaticFuncs.getSessionStepCount(reqdata.getSession_id(), reqId);
            }
        }
        // теперь если в reqId и stepcount 2 - то мы первый раз на этой странице
        if (reqId == 0 || stepcount < 3) {
            // а этого быть не может никак!
            return Data.setErrorRedirect();
        }
        // во всех других случаях - это повторный заход или возврат
        Step3Data data = reqdata.getStep3();
        if (data.getMatuche() == 0) {
            if (StringUtils.isEmpty(data.getHomeFact())) {
                ans.getErrors().add(new ErrorData("homeFact", "Поле обязательно для заполнения"));
            }
            if (StringUtils.isNotEmpty(data.getPhone())) {
                if (!data.getPhone().matches("^\\+[0-9\\)\\(\\s,\\-]+(\\+[0-9]+)?$")) {
                    ans.getErrors().add(new ErrorData("phone", "Поле не соответствует формату"));
                }
            }
            if (StringUtils.isEmpty(data.getRegDate())) {
                ans.getErrors().add(new ErrorData("regDate", "Дата не может быть в будущем"));
            } else {
                Date d = Convertor.toDate(data.getRegDate(), "dd.MM.yyyy");
                if (!StaticFuncs.checkDate(d)) {
                    ans.getErrors().add(new ErrorData("regDate", "Поле обязательно для заполнения"));
                }
            }
        }
        if (data.getGround() == null) {
            ans.getErrors().add(new ErrorData("ground", "Поле обязательно для заполнения"));
        }
        if (StringUtils.isNotEmpty(data.getRegphone())) {
            if (!data.getRegphone().matches("^\\+[0-9\\)\\(\\s,\\-]+(\\+[0-9]+)?$")) {
                ans.getErrors().add(new ErrorData("regphone", "Поле не соответствует формату"));
            }
        }

        if (ans.getErrors().size() > 0) {
            ans.setStatus(false);
            return ans;
        }

        Map<String, Object> step = new HashMap<String, Object>();
        step.put("id", reqId);
        step.put("matuche", data.getMatuche().toString());
        step.put("livingFias", data.getLivingFias());
        step.put("home", data.getHomeFact());
        step.put("builder", data.getBuilderFact());
        step.put("korpus", data.getKorpusFact());
        step.put("apartment", data.getApartmentFact());
        step.put("ground", data.getGround().toString());
        step.put("phone", data.getPhone());
        step.put("available", (data.getAvailable() > 0));
        step.put("regphone", data.getRegphone());
        step.put("regavailable", (data.getRegavailable() > 0));
        step.put("RegDate", data.getRegDate());

        StaticFuncs.log(step);

        Integer req_id;
        try {
            req_id = kassaBean.saveStep3(step);
        } catch (Exception e) {
            e.printStackTrace();
            return Data.setErrorAlert("Ошибка приложения");
        }

        StaticFuncs.log("Old id=" + reqId + " New id=" + req_id);

        if (req_id > 0) {
            String hash = StaticFuncs.generateHashId(req_id);
            ans.setRequest_id(hash);
            if (stepcount == 3)
                stepcount++; // если мы были тут первый раз, то увеличим счетчик шагов
            ans.setSession_id(StaticFuncs.generateSessionId(req_id, stepcount)); // новая сессия
            ans.setStatus(true);
            ans.setStepcount(stepcount);
            return ans;
        }
        StaticFuncs.log("Error User id: 0 !!!");
        return Data.setErrorAlert("Ошибка приложения");
    }

    /**
     * Сабмитим шаг 4
     */
    @POST
    @Path("/step4")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step4(Data reqdata,
                      @Context HttpServletRequest req) {

        Data ans = new Data();
        Integer reqId = 0; // первый раз тут если не поменяется
        Integer stepcount = 0; // первый раз тут если не поменяется
        if (reqdata.getSession_id() != null && reqdata.getRequest_id() != null) {
            reqId = StaticFuncs.decodeIdFromHash(reqdata.getRequest_id());
            if (reqId != 0) {
                stepcount = StaticFuncs.getSessionStepCount(reqdata.getSession_id(), reqId);
            }
        }
        // теперь если в reqId и stepcount 3 - то мы первый раз на этой странице
        if (reqId == 0 || stepcount < 4) {
            // а этого быть не может никак!
            return Data.setErrorRedirect();
        }
        // во всех других случаях - это повторный заход или возврат
        Step4Data data = reqdata.getStep4();
        if (data.getEducation() == null) {
            ans.getErrors().add(new ErrorData("education", "Поле обязательно для заполнения"));
        }
        if (data.getEmployment() == null) {
            ans.getErrors().add(new ErrorData("employment", "Поле обязательно для заполнения"));
        }

        if (StringUtils.isEmpty(data.getExperience())) {
            ans.getErrors().add(new ErrorData("experience", "Поле обязательно для заполнения"));
        } else {
            Date d = Convertor.toDate(data.getExperience(), "dd.MM.yyyy");
            if (!StaticFuncs.checkDate(d)) {
                ans.getErrors().add(new ErrorData("experience", "Поле обязательно для заполнения"));
            }
        }
        if (StringUtils.isNotEmpty(data.getDatestartwork())) {
            Date d = Convertor.toDate(data.getDatestartwork(), "dd.MM.yyyy");
            if (!StaticFuncs.checkDate(d)) {
                ans.getErrors().add(new ErrorData("datestartwork", "Дата не может быть в будущем"));
            }
        }
        if (StringUtils.isEmpty(data.getMonthlyincome())) {
            ans.getErrors().add(new ErrorData("monthlyincome", "Поле обязательно для заполнения"));
        } else {
            if (!StringUtils.isNumeric(data.getMonthlyincome())) {
                ans.getErrors().add(new ErrorData("monthlyincome", "Поле должно содержать цифры"));
            }
        }
        if (StringUtils.isNotEmpty(data.getExtincome()) && !StringUtils.isNumeric(data.getExtincome())) {
            ans.getErrors().add(new ErrorData("extincome", "Поле должно содержать цифры"));
        }

        if (StringUtils.isNotEmpty(data.getExtincome()) && data.getExtsalarysource() == null) {
            ans.getErrors().add(new ErrorData("extsalarysource", "Поле обязательно для заполнения"));
        }

        if (ans.getErrors().size() > 0) {
            ans.setStatus(false);
            return ans;
        }

        Map<String, Object> step = new HashMap<String, Object>();
        step.put("id", reqId);
        step.put("experience", data.getExperience());
        step.put("workplace", data.getWorkplace());
        step.put("occupation", data.getOccupation());
        step.put("datestartwork", data.getDatestartwork());
        step.put("monthlyincome", data.getMonthlyincome());
        step.put("workphone", data.getWorkphone());
        step.put("extincome", data.getExtincome());
        step.put("extsalarysource", data.getExtsalarysource());
        step.put("jobFias", data.getJobFias());
        step.put("home", data.getHome());
        step.put("builder", data.getBuilder());
        step.put("korpus", data.getKorpus());
        step.put("salarydate", data.getSalarydate());
        step.put("education", data.getEducation().toString());
        step.put("employment", data.getEmployment().toString());
        step.put("profession", data.getProfession() == null ? "" : data.getProfession().toString());
        step.put("freqType", "3");
        step.put("available", (data.getAvailable() > 0));
        step.put("car", (data.getCar() > 0));

        StaticFuncs.log(step);

        Integer req_id;
        try {
            req_id = kassaBean.saveStep4(step);
        } catch (Exception e) {
            e.printStackTrace();
            return Data.setErrorAlert("Ошибка приложения");
        }

        StaticFuncs.log("Old id=" + reqId + " New id=" + req_id);

        if (req_id > 0) {
            String hash = StaticFuncs.generateHashId(req_id);
            ans.setRequest_id(hash);
            if (stepcount == 4)
                stepcount++; // если мы были тут первый раз, то увеличим счетчик шагов
            ans.setSession_id(StaticFuncs.generateSessionId(req_id, stepcount)); // новая сессия
            ans.setStatus(true);
            ans.setStepcount(stepcount);
            return ans;
        }
        StaticFuncs.log("Error User id: 0 !!!");
        return Data.setErrorAlert("Ошибка приложения");
    }

    /**
     * Сабмитим шаг 5
     */
    @POST
    @Path("/step5")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step5(Data reqdata,
                      @Context HttpServletRequest req) {

        Data ans = new Data();
        Integer reqId = 0; // первый раз тут если не поменяется
        Integer stepcount = 0; // первый раз тут если не поменяется
        if (reqdata.getSession_id() != null && reqdata.getRequest_id() != null) {
            reqId = StaticFuncs.decodeIdFromHash(reqdata.getRequest_id());
            if (reqId != 0) {
                stepcount = StaticFuncs.getSessionStepCount(reqdata.getSession_id(), reqId);
            }
        }
        // теперь если в reqId и stepcount 4 - то мы первый раз на этой странице
        if (reqId == 0 || stepcount < 5) {
            // а этого быть не может никак!
            return Data.setErrorRedirect();
        }
        // во всех других случаях - это повторный заход или возврат
        Step5Data data = reqdata.getStep5();
        if (data.getPrevcredits() != null && data.getPrevcredits() == 1) {
            if (data.getCreditOrganization() == null) {
                ans.getErrors().add(new ErrorData("creditorganization", "Поле обязательно для заполнения"));
            }
            if (data.getCredittype() == null) {
                ans.getErrors().add(new ErrorData("credittype", "Поле обязательно для заполнения"));
            }
            if (data.getCurrencytype() == null) {
                ans.getErrors().add(new ErrorData("currencytype", "Поле обязательно для заполнения"));
            }
            if (StringUtils.isEmpty(data.getCreditdate())) {
                ans.getErrors().add(new ErrorData("creditdate", "Поле обязательно для заполнения"));
            } else {
                Date d = Convertor.toDate(data.getCreditdate(), "dd.MM.yyyy");
                if (!StaticFuncs.checkDate(d)) {
                    ans.getErrors().add(new ErrorData("creditdate", "Дата не может быть в будущем"));
                }
            }
            if (StringUtils.isEmpty(data.getCreditsumprev())) {
                ans.getErrors().add(new ErrorData("creditsumprev", "Поле обязательно для заполнения"));
            } else {
                if (!StringUtils.isNumeric(data.getCreditsumprev())) {
                    ans.getErrors().add(new ErrorData("creditsumprev", "Поле должно содержать цифры"));
                }
            }
            if (StringUtils.isEmpty(data.getOverdue())) {
                ans.getErrors().add(new ErrorData("overdue", "Поле обязательно для заполнения"));
            }
        }

        if (ans.getErrors().size() > 0) {
            ans.setStatus(false);
            return ans;
        }

        Map<String, Object> step = new HashMap<String, Object>();
        step.put("id", reqId);
        step.put("creditsumprev", data.getCreditsumprev());
        step.put("creditdate", data.getCreditdate());
        step.put("overdue", data.getOverdue());
        step.put("creditorganization", data.getCreditOrganization());
        step.put("prevcredits", data.getPrevcredits());
        step.put("credittype", data.getCredittype());
        step.put("currencytype", data.getCurrencytype());
        step.put("creditisover", data.getCreditisover());

        StaticFuncs.log(step);

        Integer req_id;
        try {
            req_id = wizardService.saveStep5(step);
        } catch (Exception e) {
            e.printStackTrace();
            return Data.setErrorAlert("Ошибка приложения");
        }

        StaticFuncs.log("Old id=" + reqId + " New id=" + req_id);

        if (req_id > 0) {
            String hash = StaticFuncs.generateHashId(req_id);
            ans.setRequest_id(hash);
            if (stepcount == 5)
                stepcount++; // если мы были тут первый раз, то увеличим счетчик шагов
            ans.setSession_id(StaticFuncs.generateSessionId(req_id, stepcount)); // новая сессия
            ans.setStatus(true);
            ans.setStepcount(stepcount);
            return ans;
        }
        StaticFuncs.log("Error User id: 0 !!!");
        return Data.setErrorAlert("Ошибка приложения");
    }

    /**
     * Сабмитим шаг 6
     */
    @POST
    @Path("/step6")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step6(Data data,
                      @Context HttpServletRequest req) {

        Data answer = new Data();
        Integer creditRequestId = 0; // первый раз тут если не поменяется
        Integer stepcount = 0; // первый раз тут если не поменяется
        if (data.getSession_id() != null && data.getRequest_id() != null) {
            creditRequestId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
            if (creditRequestId != 0) {
                stepcount = StaticFuncs.getSessionStepCount(data.getSession_id(), creditRequestId);
            }
        }
        // теперь если в reqId и stepcount 5 - то мы первый раз на этой странице
        if (creditRequestId == 0 || stepcount < 6) {
            // а этого быть не может никак!
            return Data.setErrorRedirect();
        }
        // во всех других случаях - это повторный заход или возврат
        Step6Data step6 = data.getStep6();
        step6Validator.validate(step6, answer.getErrors());


        if (step6.getOption() == Account.CARD_TYPE) {
            if (step6.getPayonlineAmount() == null) {
                answer.getErrors().add(new ErrorData("verify_card_button", "Пройдите верификацию карты"));
            } else if (StringUtils.isEmpty(step6.getPayonlineAmount())) {
                answer.getErrors().add(new ErrorData("payonlineAmount", "Поле обязательно для заполнения"));
            } else {
                try {
                    double payonlineSum = Double.valueOf(step6.getPayonlineSum());

                    ActionContext actionContext = actionProcessor.createActionContext(null, true);
                    PluginExecutionContext payonlineExecutionContext = PluginExecutionContext.createExecutionContext(actionContext,
                            PayonlineAcquiringBeanLocal.SYSTEM_NAME);

                    PayonlineAcquiringPluginConfig payonlinePluginConfig = (PayonlineAcquiringPluginConfig)
                            payonlineExecutionContext
                                    .getPluginConfig();

                    byte[] f = DigestUtils.sha("Amount=" + data.getStep6().getPayonlineAmount().replace(",", ".")
                            + "&PrivateSecurityKey=" + payonlinePluginConfig.getVerificationCardPrivateSecurityKey());

                    long s = 100;
                    for (int i = 0; i < 20; i++) {
                        s += ((int) f[i] & 0xFF) % 31;
                    }
                    double amount = s / 100.0;
                    logger.info("Calculated amount: " + amount);
                    if (amount != payonlineSum) {
                        answer.getErrors().add(new ErrorData("payonlineSum", "Неверная сумма"));
                    }

                } catch (NumberFormatException e) {
                    answer.getErrors().add(new ErrorData("payonlineSum", "Поле должно содержать цифры"));
                }
            }
        }

        if (answer.getErrors().size() > 0) {
            answer.setStatus(false);
            return answer;
        }

        try {
            wizardService.saveStep6(creditRequestId, step6);
        } catch (Exception e) {
            e.printStackTrace();
            return Data.setErrorAlert("Ошибка приложения");
        }

        if (creditRequestId > 0) {
            String hash = StaticFuncs.generateHashId(creditRequestId);
            answer.setRequest_id(hash);
            if (stepcount == 6) {
                stepcount++; // если мы были тут первый раз, то увеличим счетчик шагов
            }
            answer.setSession_id(StaticFuncs.generateSessionId(creditRequestId, stepcount)); // новая сессия
            answer.setStatus(true);
            answer.setStepcount(stepcount);
            return answer;
        }
        StaticFuncs.log("Error User id: 0 !!!");
        return Data.setErrorAlert("Ошибка приложения");
    }

    /**
     * Сабмитим шаг 7
     */
    @POST
    @Path("/step7")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step7(Data reqdata,
                      @Context HttpServletRequest req) {

        Data ans = new Data();
        Integer reqId = 0; // первый раз тут если не поменяется
        Integer stepcount = 0; // первый раз тут если не поменяется
        if (reqdata.getSession_id() != null && reqdata.getRequest_id() != null) {
            reqId = StaticFuncs.decodeIdFromHash(reqdata.getRequest_id());
            if (reqId != 0) {
                stepcount = StaticFuncs.getSessionStepCount(reqdata.getSession_id(), reqId);
            }
        }
        // теперь если в reqId и stepcount 6 - то мы первый раз на этой странице
        if (reqId == 0 || stepcount < 7) {
            // а этого быть не может никак!
            return Data.setErrorRedirect();
        }
        // во всех других случаях - это повторный заход или возврат
        Step7Data data = reqdata.getStep7();

       /* if (StringUtils.isEmpty(data.getSmsCode()) || StringUtils.isEmpty(data.getPhonehash())) {
            ans.getErrors().add(new ErrorData("smsCode", "Поле обязательно для заполнения"));
        } else {
            if (!data.getPhonehash().equals(StaticFuncs.md5("saltsupersalt" + data.getSmsCode() + "megasalt"))) {
                ans.getErrors().add(new ErrorData("smsCode", "Не верный код подтверждения"));
            }
        }*/

        if (ans.getErrors().size() > 0) {
            ans.setStatus(false);
            return ans;
        }

        Map<String, Object> step = new HashMap<String, Object>();
        step.put("id", reqId);
        //step.put("smsCode", data.getSmsCode());
        step.put("creditsum", data.getCreditsum());
        step.put("creditdays", data.getCreditdays());

        StaticFuncs.log(step);

        Integer req_id;
        try {
        	int creditRequestId = appBean.saveStep7(step);
            req_id = appBean.doStep7(creditRequestId, step);
        } catch (Exception e) {
        	if (e instanceof KassaException){
        		return Data.setErrorAlert(e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Ошибка приложения");
            } else {
            	e.printStackTrace();
                return Data.setErrorAlert("Ошибка приложения");
            }
        }

        StaticFuncs.log("Old id=" + reqId + " New id=" + req_id);

        if (req_id > 0) {
            String hash = StaticFuncs.generateHashId(req_id);
            ans.setRequest_id(hash);
            if (stepcount == 7)
                stepcount++; // если мы были тут первый раз, то увеличим счетчик шагов
            ans.setSession_id(StaticFuncs.generateSessionId(req_id, stepcount)); // новая сессия
            ans.setStatus(true);
            ans.setStepcount(stepcount);
            return ans;
        }
        StaticFuncs.log("Error User id: 0 !!!");
        return Data.setErrorAlert("Ошибка приложения");
    }
}
