package ru.simplgroupp.rest.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.data.ErrorData;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.rest.CreditRequestWizardPostController;
import ru.simplgroupp.rest.api.data.BankData;
import ru.simplgroupp.rest.api.data.OverviewData;
import ru.simplgroupp.rest.api.service.OverviewService;
import ru.simplgroupp.rest.api.service.UserService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * контроллер для основной страницы в ЛК
 * текущий заем
 */
@Path("/overview")
@Stateless
public class OverviewController {

    private static final Logger logger = LoggerFactory.getLogger(CreditRequestWizardPostController.class);

    @EJB
    private UsersBeanLocal userBean;
    @Inject
    private OverviewService service;
    @Inject
    private UserService userServ;


    /**
     * Возвращает набор данных используемый для построения главной страницы личного кабинета.
     * @return набор данных для построения главной страницы личного кабинета.
     */
    @GET
    @Path("getLastCreditRequest")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<OverviewData> getLastCreditRequest() {
        try {
            return new JsonResult<>(service.reloadCredit());
        } catch (KassaException ex) {
            ex.printStackTrace();
            return new JsonResult<>(ex);
        }

    }

    /**
     * Позволяет скачать оферту по текущему кредиту.
     * @return ответ сервера с офертой по текущему кредиту.
     */
    @GET
    @Path("agreement")
    @Produces(MediaType.TEXT_HTML)
    public Response downloadAgreement() {
        Response.ResponseBuilder response = Response.ok();
        response.header("Content-Disposition", "attachment; filename=\"oferta.html\"");
        response.entity(this.service.getAgreement());
        return response.build();
    }

    /**
     * Отправить SMS код пользователю.
     * @return объект с результатами отправки SMS кода.
     * @throws KassaException
     */
    @PUT
    @Path("sms/send")
    public JsonResult<String> sendSms(Map<String, String> values) throws KassaException {
        try {
            return new JsonResult<>(service.sendCodeLsn(service.generateAccount(values)));
        } catch (KassaException e) {
            return new JsonResult<>(e);
        }
    }

    /**
     * Метод подтверждения условий кредита.
     * @param valueMap json который содержит sms код и способ получения кредита
     * @return текст ошибки. Если null - выполнено без ошибок.
     */
    @POST
    @Path("save")
    public JsonResult<List<ErrorData>> save(Map<String, String> valueMap) {
        String saving = service.save(valueMap.get("smsCode"), null);
        if (saving == null) {
            return new JsonResult<>();
        } else {
            return new JsonResult<>(new Exception(saving));
        }
    }

    /**
     * Отмена заявки по кредиту.
     * @throws WorkflowException
     * @throws KassaException
     */
    @POST
    @Path("refuse")
    public JsonResult refuse() throws WorkflowException, KassaException {
        service.refuse();

        return new JsonResult();
    }

    @GET
    @Path("banks")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<BankData>> banks(@QueryParam("term") String term, @QueryParam("page") Integer page) {
        int pageSize = 20;
//        List<ReferenceData> banksList = this.service.getBanksList(term, page, pageSize);
//        return new JsonPartialResult<>(banksList, banksList.size() >= pageSize);

        return new JsonResult<>(service.getBanksList(term, page, pageSize));
    }
    
    @GET
    @Path("activepaysys")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<Integer>> activePaymentSystems() {
        return new JsonResult<>(service.activePaymentSystems());
    }
    @GET
    @Path("isFirstRequest")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> isFirstRequest() {
        return new JsonResult<>(service.isFirstRequest());
    }

    @GET
    @Path("firstReqPaySystems")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<Integer>> getFirstPaymentSystemsCodes() {
        return new JsonResult<>(service.getFirstReqPaySystems());
    }

}
