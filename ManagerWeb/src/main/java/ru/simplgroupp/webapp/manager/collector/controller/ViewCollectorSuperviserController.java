package ru.simplgroupp.webapp.manager.collector.controller;

import ru.simplgroupp.dao.interfaces.CollectorDAO;
import ru.simplgroupp.dao.interfaces.UsersDAO;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 14.09.2015
 * 18:55
 */

public class ViewCollectorSuperviserController extends AbstractSessionController implements Serializable {
    @EJB
    private CollectorDAO collectorDAO;
    @EJB
    private UsersDAO usersDAO;

    private Integer collectorID;
    private Integer collectorTaskID;
    private Collector collector;
    private Credit credit;
    private List<Users> userList;
    private Integer selectedUserID;
    private Integer creditRequestID;
    private List<PeopleContact> contactList;

    @PostConstruct
    public void init() {
        super.init();
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            if (collectorID == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("collectorID")) {
                    collectorID = Convertor.toInteger(prms.get("collectorID"));
                }
            }
            if (collectorID != null) {
                reloadCollector(collectorID);
            }
        }
        userList = usersDAO.findUsersByRoleID(Roles.ROLE_COLLECTOR, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
        if (collector.getUser() != null) {
            selectedUserID = collector.getUser().getId();
        }
        creditRequestID = credit.getCreditRequest().getId();
    }

    public String assignCollector() {
        if ((collector.getUser() == null) || (!selectedUserID.equals(collector.getUser().getId()))) {
            if (collectorID != null) {
                Collector newCol = collectorDAO.assignRequestToCollector(selectedUserID, collectorID);

                return "view?faces-redirect=true&collectorID=" + newCol.getId();
            }
        }
        return "";
    }

    private void reloadCollector(Integer collectorID) {
        collector = collectorDAO.getCollector(collectorID, Utils.setOf(
                PeopleMain.Options.INIT_CREDIT
                , PeopleMain.Options.INIT_CREDIT_REQUEST
                , PeopleMain.Options.INIT_PEOPLE_PERSONAL
                , PeopleMain.Options.INIT_DOCUMENT
                , PeopleMain.Options.INIT_ADDRESS
                , PeopleMain.Options.INIT_EMPLOYMENT
                , PeopleMain.Options.INIT_PEOPLE_CONTACT
                , PeopleMain.Options.INIT_PEOPLE_CONTACT_ALL
        ));
        credit = collector.getCredit();
        credit.init(Utils.setOf(Credit.Options.INIT_CREDIT_REQUEST));

        contactList = new ArrayList<PeopleContact>();
        for (PeopleContact contact : collector.getPeopleMain().getPeopleContactsAll()) {
            if (contact.getPartner().getId().equals(Partner.SYSTEM)) {
                contactList.add(contact);
            }
        }
    }

    public String close() {
        return "close";
    }

    public Integer getCollectorTaskID() {
        return collectorTaskID;
    }

    public void setCollectorTaskID(Integer collectorTaskID) {
        this.collectorTaskID = collectorTaskID;
    }

    public Integer getCollectorID() {
        return collectorID;
    }

    public void setCollectorID(Integer collectorID) {
        this.collectorID = collectorID;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public List<Users> getUserList() {
        return userList;
    }

    public void setUserList(List<Users> userList) {
        this.userList = userList;
    }

    public Integer getSelectedUserID() {
        return selectedUserID;
    }

    public void setSelectedUserID(Integer selectedUserID) {
        this.selectedUserID = selectedUserID;
    }

    public Integer getCreditRequestID() {
        return creditRequestID;
    }

    public void setCreditRequestID(Integer creditRequestID) {
        this.creditRequestID = creditRequestID;
    }

    public List<PeopleContact> getContactList() {
        return contactList;
    }

    public void setContactList(List<PeopleContact> contactList) {
        this.contactList = contactList;
    }
}
