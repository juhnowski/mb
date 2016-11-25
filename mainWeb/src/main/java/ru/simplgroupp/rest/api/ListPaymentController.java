package ru.simplgroupp.rest.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.data.CreditData;
import ru.simplgroupp.rest.api.data.RepaymentScheduleData;
import ru.simplgroupp.rest.api.data.payment.ListPaymentData;
import ru.simplgroupp.rest.api.service.ListPaymentService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * контроллер для страницы в ЛК
 * платежи по займу
 */
@Path("/listpayment")
@Stateless
public class ListPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(ListPaymentController.class);

    @Inject
    ListPaymentService serv;


    @POST
    @Path("/getListPayment")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<ListPaymentData> getListPayment(Map<String, String> maps, @Context HttpServletRequest request) {
        try {
            return new JsonResult<>(serv.getLstPay());
        } catch (KassaException ex) {
            return new JsonResult<>(ex);
        }

    }


    /**
     * График платежей (Личный кабинет)
     * client/schedule.shtml
     *
     * @param request
     * @return
     */
    @POST
    @Path("/getLstSched")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<RepaymentScheduleData>> getLstSched(@Context HttpServletRequest request) {
        try {
            return new JsonResult<>(serv.getLstSched());
        } catch (KassaException e) {
            return new JsonResult<>(e);
        }
    }


    /**
     * История займов (Личный кабинет)
     * client/history.shtml
     *
     * @param request
     * @return
     */
    @POST
    @Path("/getHistory")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getHistory(@Context HttpServletRequest request) {
        try {
            return new JsonResult<>(serv.getHistoryCredits());
        } catch (KassaException e) {
            return new JsonResult<>(e);
        }
    }


    /**
     * Данные по кредиту
     *
     * @param map     - ключ creditId
     * @param request
     * @return
     */
    @POST
    @Path("/getCreditData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<CreditData> getCreditData(Map<String, String> map, @Context HttpServletRequest request) {
        try {
            return new JsonResult<>(serv.getCreditData(Integer.valueOf(map.get("creditId"))));
        } catch (KassaException e) {
            return new JsonResult<>(e);
        }
    }
}
