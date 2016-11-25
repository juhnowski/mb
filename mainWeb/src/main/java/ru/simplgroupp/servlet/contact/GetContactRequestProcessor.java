package ru.simplgroupp.servlet.contact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.persistence.PaymentEntity;
import ru.simplgroupp.persistence.PaymentStatus;
import ru.simplgroupp.servlet.ContactCallbackServlet;

import java.util.Date;

/**
 * Created by aro on 13.10.2014.
 */
public class GetContactRequestProcessor implements ContactRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GetContactRequestProcessor.class);

    @Override
    public ContactCallbackResponse process(ContactCallbackRequest callbackRequest,ContactCallbackServlet servlet) {
        ContactCallbackResponse response = new ContactCallbackResponse();
        response.setStamp(ContactCallbackServlet.CONTACT_STAMP_FORMATZ.format(new Date()));
        //Задача - найти Payment
        Integer paymentId = null;
        try {
            paymentId = Integer.valueOf(callbackRequest.gettAttr2());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if(paymentId == null){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_PAYMENT_NOT_FOUND);
            response.setErrText("Не найден ID платежа.Элемент tAttr2");
            return response;
        }
        PaymentEntity payment = servlet.getPaymentDAO().getPayment(paymentId);
        if(payment == null){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_PAYMENT_NOT_FOUND);
            response.setErrText("Не найден платеж.Проверьте элемент tAttr2");
            return response;
        }

        response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_SUCCESS);
        response.setPaymentID(paymentId);
        PaymentStatus paymentStatus = payment.getStatus();
        if(PaymentStatus.ERROR.equals(paymentStatus)){
            response.setState(ContactCallbackServlet.CONTACT_CALLBACK_STATE_CANCELLED);
        }else if(PaymentStatus.SUCCESS.equals(paymentStatus)){
            response.setState(ContactCallbackServlet.CONTACT_CALLBACK_STATE_PAYED);
            response.setPaymentInfo("Платеж успешно проведен!");
        }else{
            response.setState(ContactCallbackServlet.CONTACT_CALLBACK_STATE_NOT_PAYED);
        }


        return response;
    }
}
