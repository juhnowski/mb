package ru.simplgroupp.servlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.WorkflowBean;
import ru.simplgroupp.ejb.plugins.payment.PayonlineAcquiringPluginConfig;
import ru.simplgroupp.interfaces.ActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.PayonlineAcquiringBeanLocal;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.BaseCredit;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.workflow.PluginExecutionContext;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Payonline callback
 */
public class PayonlineCallbackServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6553807224494501860L;

	private static final Logger logger = LoggerFactory.getLogger(PayonlineCallbackServlet.class);

    @EJB
    private PaymentService paymentService;

    @EJB
    private transient ActionProcessorBeanLocal actionProcessor;

    @EJB
    private WorkflowBeanLocal workflowBean;
    
    @EJB
    private PaymentDAO payDAO;

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String privateSecurityKey;

    private String verificationCardPrivateSecurityKey;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ActionContext actionContext = actionProcessor.createActionContext(null, true);
        PluginExecutionContext payonlineExecutionContext = PluginExecutionContext.createExecutionContext(actionContext,
                PayonlineAcquiringBeanLocal.SYSTEM_NAME);

        PayonlineAcquiringPluginConfig payonlinePluginConfig = (PayonlineAcquiringPluginConfig)
                payonlineExecutionContext
                        .getPluginConfig();

        privateSecurityKey = payonlinePluginConfig.getPrivateSecurityKey();
        verificationCardPrivateSecurityKey = payonlinePluginConfig.getVerificationCardPrivateSecurityKey();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StringBuilder parametersInfo = new StringBuilder("{");
        for (String parameterName : request.getParameterMap().keySet()) {
            parametersInfo.append(parameterName).append(": ");
            String[] parameterValues = request.getParameterMap().get(parameterName);
            for (String parameterValue : parameterValues) {
                parametersInfo.append(parameterValue).append("; ");
            }
        }
        parametersInfo.append("}");
        logger.info("Parameters: " + parametersInfo);

        String dateTime = request.getParameter("DateTime");
        
        String transactionId = request.getParameter("TransactionID");
        String orderId = request.getParameter("OrderId");
        String amount = request.getParameter("Amount");
        String currency = request.getParameter("Currency");
        String securityKey = request.getParameter("SecurityKey");
        String provider = request.getParameter("Provider");
        String cardHolder = request.getParameter("CardHolder");
        String cardNumber = request.getParameter("CardNumber");
        String country = request.getParameter("Country");
        String city = request.getParameter("City");
        String address = request.getParameter("Address");
        String ipAddress = request.getParameter("IpAddress");
        String ipCountry = request.getParameter("IpCountry");
        String binCountry = request.getParameter("BinCountry");
        String phone = request.getParameter("Phone");
        String wmTranId = request.getParameter("WmTranId");
        String wmInvId = request.getParameter("WmInvId");
        String wmId = request.getParameter("WmId");
        String wmPurse = request.getParameter("WmPurse");
        String ymInvoiceId = request.getParameter("YmInvoiceId");
        String ymMode = request.getParameter("YmMode");
        String ymPayerCode = request.getParameter("YmPayerCode");
        String errorCode = request.getParameter("ErrorCode");

        if (!checkMd5(dateTime, transactionId, orderId, amount, currency, securityKey)) {
            logger.error("Несовпадение значения параметра securityKey с результатом расчета хэш-функции");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Integer paymentId;
        try {
        	paymentId = Integer.valueOf(orderId);
        } catch (NumberFormatException e) {
            try {
                UUID.fromString(orderId);
                String accountIdParameter = request.getParameter("accountId");
                Integer accountId = Convertor.toInteger(accountIdParameter);
                String cardNumberMasked = request.getParameter("CardNumber");
                String cardNumberHash = request.getParameter("CardNumberHash");

                if (errorCode != null) {
                    return;
                }

                if (accountId != null && cardNumberHash != null) {
                    paymentService.savePayonlineCardNumberHash(accountId, cardNumberMasked, 
                    		cardNumberHash,cardHolder);

                    ActionContext actionContext = actionProcessor.createActionContext(null, true);
                    PayonlineAcquiringBeanLocal payonlineAcquiring = actionContext.getPlugins().getPayonlineAcquiring();
                    payonlineAcquiring.paymentCancel(orderId, transactionId, PluginExecutionContext.createExecutionContext(actionContext,
                            PayonlineAcquiringBeanLocal.SYSTEM_NAME));
                }

                return;
            } catch (Exception pe) {
                logger.error(pe.getMessage(), pe);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        Date date;
        Date date1;
        try {
            date = df.parse(dateTime);
            logger.info("Date from Payonline "+date);
            date1=DateUtils.addHours(date, 3);
            logger.info("Date from Payonline with 3 hours "+date1);
        } catch (ParseException e) {
            logger.error("Неверный формат даты " + dateTime);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //Calendar gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
        gmtCalendar.setTime(date1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(gmtCalendar.getTimeInMillis());

        try {
            Payment payment = payDAO.getPayment(paymentId, Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST));
            if (payment == null) {
                logger.error("Платёж " + orderId + " не найден");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("paymentId", paymentId);
            data.put("date", calendar.getTime());
            data.put("errorCode", errorCode);
            workflowBean.repaymentReceivedPayonline(payment.getCredit().getCreditRequest().getId(), data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean checkMd5(String dateTime, String transactionID, String orderId, String amount, String currency,
                             String securityKey) {

        String choosenSecurityKey;
        try {
            UUID.fromString(orderId);
            choosenSecurityKey = verificationCardPrivateSecurityKey;
        } catch (IllegalArgumentException e) {
            choosenSecurityKey = privateSecurityKey;
        }

        String calculatedMd5 = DigestUtils.md5Hex("DateTime=" + dateTime +
                "&TransactionID=" + transactionID + "&OrderId=" + orderId + "&Amount=" + amount + "&Currency=" +
                currency +
                "&PrivateSecurityKey=" + choosenSecurityKey);
        return calculatedMd5.equals(securityKey);
    }

    public void setActionProcessor(ActionProcessorBeanLocal actionProcessor) {
        this.actionProcessor = actionProcessor;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setWorkflowBean(WorkflowBean workflowBean) {
        this.workflowBean = workflowBean;
    }
}
