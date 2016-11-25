package ru.simplgroupp.servlet.contact;

import ru.simplgroupp.exception.ExceptionInfo;
import ru.simplgroupp.exception.ResultType;
import ru.simplgroupp.exception.Type;
import ru.simplgroupp.persistence.PaymentEntity;
import ru.simplgroupp.servlet.ContactCallbackServlet;
import ru.simplgroupp.util.ErrorKeys;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by aro on 13.10.2014.
 */
public class CancelPayContactRequestProcessor implements ContactRequestProcessor {

    @Override
    public ContactCallbackResponse process(ContactCallbackRequest callbackRequest,ContactCallbackServlet servlet) {
        ContactCallbackResponse response = new ContactCallbackResponse();
        response.setStamp(ContactCallbackServlet.CONTACT_STAMP_FORMATZ.format(new Date()));
        //Задача - найти Payment
        Integer paymentId = null;
        try {
            paymentId = Integer.valueOf(callbackRequest.getPaymentID());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            if(paymentId == null){
                paymentId = Integer.valueOf(callbackRequest.gettAttr2());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            if(paymentId == null){
                paymentId = Integer.valueOf(callbackRequest.getID());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(paymentId == null){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_PAYMENT_NOT_FOUND);
            response.setErrText("Не найден платеж.Проверьте элемент PaymentID или tAttr2");
            return response;
        }

        PaymentEntity payment = servlet.getPaymentDAO().getPayment(paymentId);
        if(payment == null){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_PAYMENT_NOT_FOUND);
            response.setErrText("Не найден платеж.Проверьте элемент PaymentID или tAttr2");
            return response;
        }

        Date processDate = null;
        try {
            processDate = callbackRequest.getStamp()!=null ? ContactCallbackServlet.CONTACT_STAMP_FORMATZ.parse(callbackRequest.getStamp()) : new Date();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(processDate==null){
            processDate = new Date();
        }

        //Отменяю платеж
        servlet.getPaymentService().processFailPayment(paymentId,processDate,new ExceptionInfo(ErrorKeys.WAIT,"Cancelled by Contact PPP",Type.BUSINESS, ResultType.FATAL));

        response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_SUCCESS);

        return response;

    }
}
