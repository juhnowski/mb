package ru.simplgroupp.webapp.manager.sms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.interfaces.MailBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.ReportBeanLocal;
import ru.simplgroupp.persistence.PartnersEntity;
import ru.simplgroupp.persistence.PeopleContactEntity;
import ru.simplgroupp.persistence.PeopleMainEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 17.09.2015
 * 19:01
 */

public class SmsController extends AbstractSessionController implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -6467174828264268916L;

	private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    @EJB
    private MailBeanLocal mailBeanLocal;
    @EJB
    private PeopleDAO peopleDAO;
    @EJB
    private ReferenceBooksLocal referenceBean;
    @EJB
    private ReportBeanLocal reportBeanLocal;
    @EJB
    private PeopleBeanLocal peopleBeanLocal;

    private Integer userID;
    private Integer peopleMainID;
    private Integer collectorID;

    private String phoneNumber;
    private String smsText;

    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            if (peopleMainID == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("peopleMainID")) {
                    peopleMainID = Convertor.toInteger(prms.get("peopleMainID"));
                }
            }
            if (userID == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("userID")) {
                    userID = Convertor.toInteger(prms.get("userID"));
                }
            }
            if (collectorID == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("collectorID")) {
                    collectorID = Convertor.toInteger(prms.get("collectorID"));
                }
            }
            if (phoneNumber == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("phoneNumber")) {
                    phoneNumber = Convertor.toString(prms.get("phoneNumber"));
                }
            }
        }
    }

    public String sendSms() {
        PeopleMain peopleMain = peopleDAO.getPeopleMain(peopleMainID, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_CONTACT_ALL));
        List<PeopleContact> contactList = peopleMain.getPeopleContactsAll();
        PeopleContact peopleContact = null;
        for (PeopleContact contact : contactList) {
            String num = phoneNumber.replaceAll("\\D", "");
            if (contact.getContact().getCodeInteger().equals(PeopleContact.CONTACT_CELL_PHONE) && contact.getValue().equals(num)) {
                peopleContact = contact;
            }
        }
        if (peopleContact == null) {
            PeopleMainEntity peopleMainEntity = peopleDAO.getPeopleMainEntity(peopleMain.getId());
            PartnersEntity partnersEntity = referenceBean.getPartnerEntity(Partner.SYSTEM);
            PeopleContactEntity contact = peopleBeanLocal.setPeopleContact(peopleMainEntity, partnersEntity, PeopleContact.CONTACT_CELL_PHONE, phoneNumber, true, null, new Date());
            peopleContact = new PeopleContact(contact);
        }
        mailBeanLocal.saveMessage(peopleMainID, userID, Messages.SMS, Reference.MANUAL_EXEC, peopleContact.getId(), new Date(), null, smsText);
        mailBeanLocal.sendSMSV2(phoneNumber, smsText);
        return "/views/collector/view?faces-redirect=true&collectorID=" + collectorID;
    }

    public String cancel() {
        return "/views/collector/view?faces-redirect=true&collectorID=" + collectorID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getPeopleMainID() {
        return peopleMainID;
    }

    public void setPeopleMainID(Integer peopleMainID) {
        this.peopleMainID = peopleMainID;
    }

    public Integer getCollectorID() {
        return collectorID;
    }

    public void setCollectorID(Integer collectorID) {
        this.collectorID = collectorID;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
