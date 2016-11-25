package ru.simplgroupp.webapp.manager.people.controller;

import ru.simplgroupp.dao.interfaces.CallsDAO;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.MailBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EditPeopleController extends AbstractSessionController implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = -6021709594643611174L;

    @EJB
    protected PeopleBeanLocal ppl;
    @EJB
    protected KassaBeanLocal kassaBean;

    @EJB
    protected CallsDAO callsDAO;

    protected Integer prmPeopleId;
    protected PeopleMain people;
    protected List<CreditRequest> creditRequests;
    protected List<Call> callList;
    @EJB
    PeopleDAO peopleDAO;
    @EJB
    private MailBeanLocal mailBeanLocal;
    private Double savebonusamount;
    private Date savebonusdate;
    /**
     * Список писем
     */
    private List<Messages> mailList;

    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            if (prmPeopleId == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("peopleid")) {
                    String ss = prms.get("peopleid");
                    prmPeopleId = Convertor.toInteger(ss);
                }
            }
            if (prmPeopleId != null)
                reloadPeople(facesCtx, prmPeopleId);
                reloadCalls(prmPeopleId);
        }

        List<Messages> messagesList = mailBeanLocal.getMessagesByPeopleID(people.getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
        mailList = new ArrayList<Messages>();
        for (Messages message : messagesList) {
            if (message.getMessageType().getCodeInteger().equals(Messages.EMAIL)) {
                mailList.add(message);
            }
        }


    }

    protected void reloadPeople(FacesContext facesCtx, Integer prmId) {
        people = peopleDAO.getPeopleMain(prmId, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_DOCUMENT, PeopleMain.Options.INIT_PEOPLE_CONTACT,
                PeopleMain.Options.INIT_CREDIT, PeopleMain.Options.INIT_BONUS, PeopleMain.Options.INIT_LOGS));
        SortCriteria[] sorting = {new SortCriteria("DateContest", false)};
        creditRequests = kassaBean.listCreditRequests(-1, -1, sorting, Utils.setOf(CreditRequest.Options.INIT_CREDIT), prmId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    protected void reloadCalls(Integer peopleMainId) {
        List<Call> calls = callsDAO.getCallsByPeople(peopleMainId,
                Utils.setOf(Call.Options.INIT_USER, PeopleMain.Options.INIT_PEOPLE_PERSONAL));

        if (calls.size() > 0) {
            callList = calls;
        }
    }

    public void addBonus() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            ppl.addBonus(people.getId(), PeopleBonus.BONUS_CODE_MANUAL,
                    BaseCredit.OPERATION_IN, getSavebonusdate(),
                    getSavebonusamount(), null, null);
            reloadPeople(facesCtx, prmPeopleId);

        } catch (Exception ex) {
            JSFUtils.handleAsError(facesCtx, null, ex);

        }
    }

    public String cancel() {
        return "canceled";
    }

    public String saveBonus() {
        return "canceled";
    }

    public Integer getPrmPeopleId() {
        return prmPeopleId;
    }

    public void setPrmPeopleId(Integer prmPeopleId) {
        this.prmPeopleId = prmPeopleId;
    }

    public PeopleMain getPeople() {
        return people;
    }

    public List<CreditRequest> getCreditRequests() {
        return creditRequests;
    }

    public Double getSavebonusamount() {
        return savebonusamount;
    }

    public void setSavebonusamount(Double savebonusamount) {
        this.savebonusamount = savebonusamount;
    }

    public Date getSavebonusdate() {
        return savebonusdate;
    }

    public void setSavebonusdate(Date savebonusdate) {
        this.savebonusdate = savebonusdate;
    }

    public List<Messages> getMailList() {
        return mailList;
    }

    public void setMailList(List<Messages> mailList) {
        this.mailList = mailList;
    }

    public void updateCalls() {
        for (Call call : callList) {
            callsDAO.saveCallsEntity(call.getEntity());
        }

        reloadCalls(prmPeopleId);
    }

    public List<Call> getCallList() {
        return callList;
    }
}
