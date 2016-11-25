package ru.simplgroupp.rest.api.service;

import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.persistence.ReferenceEntity;
import ru.simplgroupp.rest.api.data.CallbackData;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.CallBack;
import ru.simplgroupp.transfer.RefHeader;
import ru.simplgroupp.transfer.Reference;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class CallbackService {
    @EJB
    private PeopleBeanLocal peopleBean;

    @EJB
    private ReferenceBooksLocal refBean;


    public void addCallback(CallbackData callBack) throws PeopleException {
        CallBack callBackTransfer = new CallBack();
        callBackTransfer.setName(callBack.getFirstName());
        callBackTransfer.setSurname(callBack.getLastName());
        callBackTransfer.setPhone(callBack.getPhone());
        callBackTransfer.setEmail(callBack.getEmail());
        callBackTransfer.setMessage(callBack.getMessage());

        Reference callbackClientType = null;
        if (callBack.getClientType().equals("borrower")) {
            callbackClientType = refBean.findByCodeInteger(RefHeader.CALLBACK_CLIENT_TYPE, CallBack.BORROWER_TYPE);
        } else if (callBack.getClientType().equals("investor")) {
            callbackClientType = refBean.findByCodeInteger(RefHeader.CALLBACK_CLIENT_TYPE, CallBack.INVESTOR_TYPE);
        }
        callBackTransfer.setType(callbackClientType);

        peopleBean.addCallBack(callBackTransfer);
    }
}
