package ru.simplgroupp.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.CreditCalculatorBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.persistence.PeoplePersonalEntity;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.util.DatesUtils;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Сервлет для приёма платежей от Qiwi
 */
public class QiwiGateServlet extends HttpServlet {

	private static final long serialVersionUID = 4086502822053183258L;

	private enum Result {
        OK(0),
        TEMPORARY_ERROR(1),
        INVALID_ACCOUNT(4),
        ACCOUNT_NOT_FOUND(5),
        PAYMENT_DISALLOW(6),
        ACCOUNT_DISABLED(79),
        WAIT(90),
        AMOUNT_TOO_SMALL(241),
        AMOUNT_TOO_BIG(242),
        ACCOUNT_STATE_ERROR(243),
        UNKNOWN(300)
        ;

        private final int code;

        private Result(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(QiwiGateServlet.class);

    private static final int MIN_AMOUNT = 1;

    private static final int MAX_AMOUNT = 100000;

    @EJB
    private KassaBeanLocal kassaBean;

    @EJB
    private PeopleBeanLocal peopleBean;

    @EJB
    private CreditCalculatorBeanLocal creditCalculatorBean;

    @EJB
    private PaymentService paymentService;
    
    @EJB
    PaymentDAO payDAO;

    @EJB
    private WorkflowBeanLocal workflowBean;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean isCheck = "check".equals(request.getParameter("command"));
        boolean isPay = "pay".equals(request.getParameter("command"));

        String txnId = request.getParameter("txn_id");
        String uniqueNumber = request.getParameter("account");
        Double amount = ServletRequestUtils.getDoubleParameter(request, "sum");

        logger.info("request {txn_id: " + txnId + "; account: " + uniqueNumber + "; sum: " + amount + "}");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        if (txnId == null) {
            response.getWriter().print(checkAnswer(null, Result.UNKNOWN, "Incomplete request", null));
            return;
        }

        if (amount == null) {
            response.getWriter().print(checkAnswer(null, Result.UNKNOWN, "Incomplete request", null));
            return;
        }

        Payment payment = payDAO.findPaymentByExternalId(txnId);

        if (payment != null) {
            response.getWriter().print(payAnswer(txnId, payment));
            return;
        }

        if (uniqueNumber == null) {
            response.getWriter().print(checkAnswer(txnId, Result.INVALID_ACCOUNT, "Invalid account", null));
            return;
        }

        try {
            CreditRequest creditRequest = kassaBean.findCreditRequestByUniqueNumber(uniqueNumber);
            if (creditRequest == null) {
                response.getWriter().print(checkAnswer(txnId, Result.ACCOUNT_NOT_FOUND, "Account not found", null));
                return;
            }

            Credit credit = creditRequest.getAcceptedCredit();

            if (credit == null) {
                response.getWriter().print(checkAnswer(txnId, Result.ACCOUNT_STATE_ERROR, "Credit is not exist", null));
                return;
            }

            if (credit.getCreditDataEndFact() != null) {
                response.getWriter().print(checkAnswer(txnId, Result.ACCOUNT_DISABLED, "Credit is closed", null));
                return;
            }

            if (amount < MIN_AMOUNT) {
                response.getWriter().print(checkAnswer(txnId, Result.AMOUNT_TOO_SMALL, "Minimal amount is " + MIN_AMOUNT, null));
                return;
            }

            if (amount > MAX_AMOUNT) {
                response.getWriter().print(checkAnswer(txnId, Result.AMOUNT_TOO_BIG, "Maximal amount is " + MAX_AMOUNT, null));
                return;
            }

            if (isCheck) {
                response.getWriter().print(checkAnswer(txnId, Result.OK, "", credit));
                return;
            }

            if (isPay) {
                Date date = ServletRequestUtils.getDateParameter(request, "txn_date", DatesUtils.FORMAT_YYYYMMddHHmmss);

                if (date == null) {
                    response.getWriter().print(checkAnswer(null, Result.UNKNOWN, "Incomplete request", null));
                    return;
                }

                payment = paymentService.createSuccessClientPayment(credit.getId(), Partner.QIWI, amount, date, txnId);
                try {
                    workflowBean.repaymentReceivedQiwiGate(creditRequest.getId(), Utils.mapOfSO("paymentId", payment.getId()));
                } catch (WorkflowException e) {
                    throw new KassaException(e);
                }

                response.getWriter().print(payAnswer(txnId, payment));
                return;
            }
        } catch (KassaException e) {
            logger.error(e.getMessage(), e);
            response.getWriter().print(checkAnswer(txnId, Result.TEMPORARY_ERROR, "Server error", null));
            return;
        }
        response.getWriter().print(checkAnswer(txnId, Result.UNKNOWN, "Incomplete request", null));
    }

    private String checkAnswer(String txnId, Result error, String comment, Credit credit) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<response>");
        xml.append("<osmp_txn_id>").append(txnId == null ? "" : txnId).append("</osmp_txn_id>");
        xml.append("<result>").append(error.getCode()).append("</result>");
        if (Result.OK == error) {
            PeoplePersonalEntity peoplePersonal = peopleBean.findPeoplePersonalActive(credit.getPeopleMain().getEntity());

            xml.append("<fields>");
            xml.append("<field1 name=\"ФИО\" type=\"disp\">").append(peoplePersonal.getFullName()).append("</field1>");
            xml.append("<field2 name=\"Сумма возврата\" type=\"disp\">").append(credit.getCreditSumBack())
                    .append("</field2>");
            xml.append("</fields>");
        }
        xml.append("<comment>").append(comment).append("</comment>");
        xml.append("</response>");

        return xml.toString();
    }

    private String payAnswer(String txnId, Payment payment) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<response>");
        xml.append("<osmp_txn_id>").append(txnId == null ? "" : txnId).append("</osmp_txn_id>");
        xml.append("<prv_txn>").append(payment.getId()).append("</prv_txn>");
        xml.append("<sum>").append(payment.getAmount()).append("</sum>");
        xml.append("<result>0</result>");
        xml.append("<comment>OK</comment>");
        xml.append("</response>");

        return xml.toString();
    }

    public void setKassaBean(KassaBeanLocal kassaBean) {
        this.kassaBean = kassaBean;
    }

    public void setCreditCalculatorBean(CreditCalculatorBeanLocal creditCalculatorBean) {
        this.creditCalculatorBean = creditCalculatorBean;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setWorkflowBean(WorkflowBeanLocal workflowBean) {
        this.workflowBean = workflowBean;
    }
}
