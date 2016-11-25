package ru.simplgroupp.servlet.contact;

import ru.simplgroupp.interfaces.CreditCalculatorBeanLocal;
import ru.simplgroupp.persistence.CreditEntity;
import ru.simplgroupp.servlet.ContactCallbackServlet;
import ru.simplgroupp.util.CalcUtils;

import java.util.Date;
import java.util.Map;

/**
 * Created by aro on 13.10.2014.
 */
public class CheckContactRequestProcessor implements ContactRequestProcessor {

    @Override
    public ContactCallbackResponse process(ContactCallbackRequest callbackRequest,ContactCallbackServlet servlet) {
        ContactCallbackResponse response = new ContactCallbackResponse();
        response.setStamp(ContactCallbackServlet.CONTACT_STAMP_FORMATZ.format(new Date()));
        response.setPaymentInfo("");
        response.setErrorPayInfo("");
        response.setErrText("");

        String accountNumber = callbackRequest.gettAttr1();

        //Задача - найти CreditEntity
        CreditEntity creditEntity = servlet.findCredit(callbackRequest);
        if(creditEntity == null){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_PAYMENT_NOT_FOUND);
            response.setErrText("Не найден клиент с таким номером договора и документами.");
            response.setErrorPayInfo("Не найден клиент с таким номером договора и документами.");
            return response;
        }
        Map<String, Object> resCalc = servlet.getCreditCalc().calcCredit(creditEntity.getId(), new Date());
        Double sumBackMin = (Double) resCalc.get(CreditCalculatorBeanLocal.SUM_PERCENT);
        Double theSumma = callbackRequest.getTrnAmount();
        if(sumBackMin>theSumma){
            theSumma = sumBackMin;
        }


        /* Платеж создаваться не будет
        Payment payment = servlet.getPaymentService().createPayment(creditEntity.getId(), Account.CONTACT_TYPE, Payment.SUM_FROM_CLIENT,theSumma,Payment.TO_SYSTEM);
        if(payment!=null){
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_SUCCESS);
            response.setPaymentID(payment.getId());
            response.setTrnAmount(theSumma);
        }else{
            response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_PAYMENT_NOT_FOUND);
            response.setErrText("Ошибка создания платежа");
        }
        */
        response.setRe(ContactCallbackServlet.CONTACT_CALLBACK_RETURN_CODE_SUCCESS);
        response.setPaymentID(1);
        response.setTrnAmount(CalcUtils.sumToString(theSumma));

        return response;

    }
}
