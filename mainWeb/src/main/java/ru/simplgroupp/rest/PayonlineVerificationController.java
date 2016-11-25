package ru.simplgroupp.rest;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.data.Data;
import ru.simplgroupp.data.PayonlineCallback;
import ru.simplgroupp.data.PayonlineVerificationRedirectRest;
import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.plugins.payment.PayonlineAcquiringPluginConfig;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.ActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.PayonlineAcquiringBeanLocal;
import ru.simplgroupp.interfaces.service.CreditRequestWizardService;
import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.persistence.AccountEntity;
import ru.simplgroupp.workflow.PluginExecutionContext;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.UUID;

/**
 * Верификация карты через payonline
 */
@Path("/payonline")
@Stateless
public class PayonlineVerificationController {

    public static final Logger logger = LoggerFactory.getLogger(PayonlineVerificationController.class);

    public static final String CHARSET_UTF_8 = "UTF-8";

    @EJB
    private transient ActionProcessorBeanLocal actionProcessor;

    @EJB
    private WorkflowBeanLocal workflowBean;

    @Context
    private UriInfo uriInfo;

    @EJB
    private Step6DataValidator step6Validator;

    @EJB
    protected CreditRequestWizardService wizardService;

    @POST
    @Path("/redirect")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response verificationRedirect(Data data, @Context HttpServletRequest request) throws KassaException {
        Integer creditRequestId = 0;
        Integer stepcount = 0; // первый раз тут если не поменяется
        if (data.getSession_id() != null && data.getRequest_id() != null) {
            creditRequestId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
            if (creditRequestId != 0) {
                stepcount = StaticFuncs.getSessionStepCount(data.getSession_id(), creditRequestId);
            }
        }

        // теперь если в reqId и stepcount 5 - то мы первый раз на этой странице
        if (creditRequestId == 0 || stepcount < 5) {
            // а этого быть не может никак!
            throw new IllegalStateException();
        }

        Data answer = new Data();

        step6Validator.validate(data.getStep6(), answer.getErrors());

        if (answer.getErrors().size() > 0) {
            answer.setStatus(false);
            return Response.ok(answer).build();
        }

        AccountEntity account = wizardService.saveStep6(creditRequestId, data.getStep6());

        ActionContext actionContext = actionProcessor.createActionContext(null, true);
        PluginExecutionContext payonlineExecutionContext = PluginExecutionContext.createExecutionContext(actionContext,
                PayonlineAcquiringBeanLocal.SYSTEM_NAME);

        PayonlineAcquiringPluginConfig payonlinePluginConfig = (PayonlineAcquiringPluginConfig)
                payonlineExecutionContext
                        .getPluginConfig();

        Integer merchantId = payonlinePluginConfig.getVerificationCardMerchantId();
        String orderId = UUID.randomUUID().toString();
        String amount = UUID.randomUUID().toString();
        String currency = "RUB";

        String securityKey = DigestUtils.md5Hex(
                "MerchantId=" + merchantId
                        + "&OrderId=" + orderId
                        + "&Amount=" + amount
                        + "&Currency=" + currency
                        + "&PrivateSecurityKey=" + payonlinePluginConfig.getVerificationCardPrivateSecurityKey()
        );

        String returnUrl = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + "/main/index6.shtml?verifysum=1&account";

        PayonlineVerificationRedirectRest redirectRest = new PayonlineVerificationRedirectRest();
        redirectRest.setMerchantId(merchantId);
        redirectRest.setAmount(amount);
        redirectRest.setCurrency(currency);
        redirectRest.setOrderId(orderId);
        redirectRest.setSecurityKey(securityKey);
        redirectRest.setAccountId(account.getId());
        redirectRest.setReturnUrl(returnUrl);
        redirectRest.setRedirectUrl(payonlinePluginConfig.getVerificationCardUrl());

        return Response.ok(redirectRest).build();
    }

    @POST
    @Path("/verifysumm")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response verifySumm(Data data, @Context HttpServletRequest request) {
        Integer creditRequestId = 0;
        Integer stepcount = 0; // первый раз тут если не поменяется
        if (data.getSession_id() != null && data.getRequest_id() != null) {
            creditRequestId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
            if (creditRequestId != 0) {
                stepcount = StaticFuncs.getSessionStepCount(data.getSession_id(), creditRequestId);
            }
        }

        // теперь если в reqId и stepcount 5 - то мы первый раз на этой странице
        if (creditRequestId == 0 || stepcount < 5) {
            // а этого быть не может никак!
            throw new IllegalStateException();
        }

        ActionContext actionContext = actionProcessor.createActionContext(null, true);
        PluginExecutionContext payonlineExecutionContext = PluginExecutionContext.createExecutionContext(actionContext,
                PayonlineAcquiringBeanLocal.SYSTEM_NAME);

        PayonlineAcquiringPluginConfig payonlinePluginConfig = (PayonlineAcquiringPluginConfig)
                payonlineExecutionContext
                        .getPluginConfig();

        byte[] f = DigestUtils.sha("Amount=" + data.getStep6().getPayonlineAmount()
                + "&PrivateSecurityKey=" + payonlinePluginConfig.getPrivateSecurityKey());

        long s = 100;
        for (int i = 0; i < 20; i++) {
            s += f[i] % 31;
        }
        String amount = String.valueOf(s/100.0);
        logger.info("Случайная сумма по рассчетам "+amount);
        logger.info("Случайная сумма от payonline "+data.getStep6().getPayonlineAmount());
        return amount.equals(data.getStep6().getPayonlineAmount()) ? Response.ok().build() : Response.noContent().build();
    }

    @POST
    @Path("/callback")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Object verificationRedirect(PayonlineCallback callback) {
        return null;
    }
}
