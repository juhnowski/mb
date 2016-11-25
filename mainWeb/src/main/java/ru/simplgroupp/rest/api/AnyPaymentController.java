package ru.simplgroupp.rest.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.data.payment.PaymentSubmitData;
import ru.simplgroupp.rest.api.data.payment.RepayData;
import ru.simplgroupp.rest.api.service.AnyPaymentService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * контроллер для страницы в ЛК
 * платежи по займу
 */
@Path("anypayment")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class AnyPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(AnyPaymentController.class);

    @Inject private AnyPaymentService serv;

    @GET
    @Path("getRepayData")
    public RepayData getRepayData(  ) throws KassaException {
        return serv.initCtl();

    }

    @POST
    @Path("submit")
    public Map<String, String> submitPayment(PaymentSubmitData paymentData) throws KassaException {
        return this.serv.submitPayment(paymentData);
    }
}
