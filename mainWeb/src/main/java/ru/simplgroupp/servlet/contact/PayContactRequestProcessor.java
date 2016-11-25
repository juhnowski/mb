package ru.simplgroupp.servlet.contact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.CreditCalculatorBeanLocal;
import ru.simplgroupp.persistence.CreditEntity;
import ru.simplgroupp.persistence.PaymentEntity;
import ru.simplgroupp.servlet.ContactCallbackServlet;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.util.CalcUtils;
import ru.simplgroupp.util.DatesUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aro on 13.10.2014.
 */
public class PayContactRequestProcessor implements ContactRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PayContactRequestProcessor.class);

    @Override
    public ContactCallbackResponse process(ContactCallbackRequest callbackRequest,ContactCallbackServlet servlet) {
        ContactCallbackResponse response = new ContactCallbackResponse();
        response.setStamp(ContactCallbackServlet.CONTACT_STAMP_FORMATZ.format(new Date()));
        response.setPaymentInfo("");
        response.setErrorPayInfo("");
        response.setErrText("");

        String accountNumber = callbackRequest.gettAttr1();
        String externalId = callbackRequest.getID();
        Payment payment = servlet.getPaymentDAO().findPaymentByExternalId(externalId);
        if(payment != null){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_PAYMENT_ALREADY_DONE);
            response.setErrText("Payment already done.");
            response.setErrorPayInfo("Payment already done.");
            return response;
        }


        //Задача - найти CreditEntity
        CreditEntity creditEntity = servlet.findCredit(callbackRequest);
        if(creditEntity == null){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_PAYMENT_NOT_FOUND);
            response.setErrText("Не найден клиент с таким номером договора и документами.");
            response.setErrorPayInfo("Не найден клиент с таким номером договора и документами.");
            return response;
        }
        Double theSumma = callbackRequest.getTrnAmount();
        Map<String, Object> resCalc = servlet.getCreditCalc().calcCredit(creditEntity.getId(), new Date());
        Double sumBackMin = (Double) resCalc.get(CreditCalculatorBeanLocal.SUM_PERCENT);

        if(theSumma < sumBackMin){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_ANY_ERROR);
            response.setErrText("Сумма неправильная.Минимальная сумма платежа должна быть" + CalcUtils.sumToString(sumBackMin) + " рублей.");
            response.setErrorPayInfo("Сумма неправильная.Минимальная сумма платежа должна быть" + CalcUtils.sumToString(sumBackMin) + " рублей.");
            return response;
        }


        payment = servlet.getPaymentService().createPayment(creditEntity.getId(), Account.CONTACT_TYPE, Payment.SUM_FROM_CLIENT,theSumma,Payment.TO_SYSTEM,externalId, Partner.CONTACT, null, null);
        if(payment== null || payment.getEntity()==null){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_PAYMENT_NOT_FOUND);
            response.setErrText("Ошибка системы.Платеж не создается.");
            response.setErrorPayInfo("Ошибка системы.Платеж не создается.");
        }
        PaymentEntity paymentEntity = payment.getEntity();


        Map<String, Object> data = new HashMap<String, Object>();
        data.put("paymentId", paymentEntity.getId());

        try {
            data.put("date", ContactCallbackServlet.CONTACT_STAMP_FORMATZ.parse(callbackRequest.getTrnDate()));
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                data.put("date", DatesUtils.DATE_FORMAT_YYYYMMdd.parse(callbackRequest.getTrnDate()));
            } catch (ParseException e1) {
                e1.printStackTrace();
                data.put("date", new Date());
            }
        }

        try {
            logger.error("************DEBUG ONLY************creditEntity.getCreditRequestId().getId()="+creditEntity.getCreditRequestId().getId() +";  data=" + paymentEntity.getId());
            servlet.getWorkflowBean().repaymentReceivedContact(creditEntity.getCreditRequestId().getId(), data);
        } catch (WorkflowException e) {
            e.printStackTrace();
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_ANY_ERROR);
            response.setErrText("Ошибка сервера.Не проводится платеж.");
            response.setErrorPayInfo("Ошибка сервера.Не проводится платеж.");
            logger.error("servlet.getWorkflowBean().repaymentReceivedContact error!", e.getCause());
            return response;
        }
        response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_SUCCESS);
        response.setPaymentID(payment.getId());
        //response.setTrnAmount(CalcUtils.sumToString(theSumma));

        return response;

    }
}
