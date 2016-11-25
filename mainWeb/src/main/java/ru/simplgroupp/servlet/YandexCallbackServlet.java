package ru.simplgroupp.servlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.plugins.payment.YandexAcquiringPluginConfig;
import ru.simplgroupp.interfaces.ActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.YandexAcquiringBeanLocal;
import ru.simplgroupp.services.PaymentService;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Yandex callback
 */
public class YandexCallbackServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7989876682811620550L;

	private static final Logger logger = LoggerFactory.getLogger(YandexCallbackServlet.class);

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private static final int SUCCESS = 0;
    private static final int AUTH_ERROR = 1;
    private static final int INVALID_ORDER = 100;
    private static final int INVALID_REQUEST = 200;

    @EJB
    private PaymentService paymentService;

    @EJB
    private transient ActionProcessorBeanLocal actionProcessor;

    @EJB
    private WorkflowBeanLocal workflowBean;
    
    @EJB
    private PaymentDAO payDAO;    

    private long yandexShopId;

    private String yandexShopPassword;


    @Override
    public void init(ServletConfig config) throws ServletException {
        ActionContext actionContext = actionProcessor.createActionContext(null, true);
        PluginExecutionContext yandexExecutionContext = PluginExecutionContext.createExecutionContext(actionContext,
                YandexAcquiringBeanLocal.SYSTEM_NAME);

        YandexAcquiringPluginConfig yandexPluginConfig = (YandexAcquiringPluginConfig) yandexExecutionContext
                .getPluginConfig();
        yandexShopId = yandexPluginConfig.getShopId();
        yandexShopPassword = yandexPluginConfig.getPassword();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("Parameters: " + request.getParameterMap());

        response.setHeader("Content-Type", "text/xml");

        String requestDatetime = request.getParameter("requestDatetime");
        String action = request.getParameter("action");
        String md5 = request.getParameter("md5");
        String shopId = request.getParameter("shopId");
        String shopArticleId = request.getParameter("shopArticleId");
        String invoiceId = request.getParameter("invoiceId");
        String customerNumber = request.getParameter("customerNumber");
        String orderNumber = request.getParameter("orderNumber");
        String orderCreatedDatetime = request.getParameter("orderCreatedDatetime");
        String orderSumAmount = request.getParameter("orderSumAmount");
        String orderSumCurrencyPaycash = request.getParameter("orderSumCurrencyPaycash");
        String orderSumBankPaycash = request.getParameter("orderSumBankPaycash");
        String shopSumAmount = request.getParameter("shopSumAmount");
        String shopSumCurrencyPaycash = request.getParameter("shopSumCurrencyPaycash");
        String shopSumBankPaycash = request.getParameter("shopSumBankPaycash");
        String paymentPayerCode = request.getParameter("paymentPayerCode");
        String paymentType = request.getParameter("paymentType");
        String paymentDatetime = request.getParameter("paymentDatetime");

        if (!checkMd5(action, orderSumAmount, orderSumCurrencyPaycash, orderSumBankPaycash, shopId, invoiceId,
                customerNumber, md5)) {

            errorResponse(action, invoiceId, AUTH_ERROR, "Ошибка авторизации", "Несовпадение значения параметра md5 с" +
                    " результатом расчета хэш-функции", response);
            return;
        }

        if (!action.equals("checkOrder") && !action.equals("paymentAviso")) {
            errorResponse(action, invoiceId, INVALID_REQUEST, "Ошибка разбора запроса", "Неверный параметр action",
                    response);
            return;
        }

        Integer paymentId;
        try {
            paymentId = Integer.valueOf(orderNumber);
        } catch (NumberFormatException e) {
            errorResponse(action, invoiceId, INVALID_ORDER, "Заказ не найден", "Неверный параметр invoiceId", response);
            return;
        }

        try {
            Payment payment = payDAO.getPayment(paymentId, Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST));
            if (payment == null) {
                errorResponse(action, invoiceId, INVALID_ORDER, "Заказ не найден", "Неверный параметр orderNumber",
                        response);
                return;
            }

            if ("paymentAviso".equals(action)) {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("paymentId", paymentId);
                data.put("date", df.parse(paymentDatetime));
                workflowBean.repaymentReceivedYandex(payment.getCredit().getCreditRequest().getId(), data);
            }
            successResponse(action, invoiceId, response);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean checkMd5(String action, String orderSumAmount, String orderSumCurrencyPaycash,
                             String orderSumBankPaycash, String shopId, String invoiceId, String customerNumber,
                             String md5) {


        String calculatedMd5 = DigestUtils.md5Hex(action + ";" + orderSumAmount + ";" + orderSumCurrencyPaycash + ";"
                + orderSumBankPaycash + ";" + shopId + ";" + invoiceId + ";" + customerNumber + ";" +
                yandexShopPassword).toUpperCase();
        return calculatedMd5.equals(md5);
    }

    private void successResponse(String action, String invoiceId, HttpServletResponse response) throws
            IOException {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<").append("paymentAviso".equals(action) ? "paymentAvisoResponse" : "checkOrderResponse")
                .append(" performedDatetime=\"").append(df.format(new Date()))
                .append("\" code=\"").append(SUCCESS).append("\" invoiceId=\"").append(invoiceId).append("\"")
                .append(" shopId=\"").append(yandexShopId).append("\"/>");

        logger.info(xml.toString());
        response.getWriter().print(xml.toString());
    }

    private void errorResponse(String action, String invoiceId, int code, String message, String techMessage,
                               HttpServletResponse response) throws IOException {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<").append("paymentAviso".equals(action) ? "paymentAvisoResponse" : "checkOrderResponse")
                .append(" performedDatetime=\"").append(df.format(new Date()))
                .append("\" code=\"").append(code)
                .append("\" invoiceId=\"").append(invoiceId == null ? "" : invoiceId)
                .append("\" shopId=\"").append(yandexShopId)
                .append("\" message=\"").append(message)
                .append("\" techMessage=\"").append(techMessage).append("\"/>");

        logger.info(xml.toString());
        response.getWriter().print(xml.toString());
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setWorkflowBean(WorkflowBeanLocal workflowBean) {
        this.workflowBean = workflowBean;
    }

    public void setActionProcessor(ActionProcessorBeanLocal actionProcessor) {
        this.actionProcessor = actionProcessor;
    }
}
