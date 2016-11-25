package ru.simplgroupp.webapp.manager.email.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.exception.ReportException;
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
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 31.08.2015
 * 14:14
 */

public class EmailController extends AbstractSessionController implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5070794003788373079L;

	private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @EJB
    private PeopleDAO peopleDAO;
    @EJB
    private MailBeanLocal mailBeanLocal;
    @EJB
    private ReferenceBooksLocal referenceBooksLocal;
    @EJB
    private ReferenceBooksLocal referenceBean;
    @EJB
    private PeopleBeanLocal peopleBeanLocal;
    @EJB
    private ReportBeanLocal reportBeanLocal;

    private Integer peopleMainID;
    private Integer userID;
    private Integer collectorID;
    private PeopleMain peopleMain;
    private String email;
    private String subject;
    private String messageBody;
    private String fromOutcome;
    private Integer messageID;

    @PostConstruct
    public void init()	{
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
            if (email == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("emailTo")) {
                    email = Convertor.toString(prms.get("emailTo"));
                }
            }
            if (messageID == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("messageID")) {
                    messageID = Convertor.toInteger(prms.get("messageID"));
                }
            }
            if (peopleMainID != null) {
                reloadItem(peopleMainID);
            }
        }
    }

    public String sendMailAction() {
        List<PeopleContact> contactList = peopleMain.getPeopleContactsAll();
        PeopleContact peopleContact = null;
        for (PeopleContact contact : contactList) {
            if (contact.getContact().getCodeInteger().equals(PeopleContact.CONTACT_EMAIL) && contact.getValue().equals(email)) {
                peopleContact = contact;
            }
        }
        if (peopleContact == null) {
            PeopleMainEntity peopleMainEntity = peopleDAO.getPeopleMainEntity(peopleMain.getId());
            PartnersEntity partnersEntity = referenceBean.getPartnerEntity(Partner.SYSTEM);
            PeopleContactEntity contact = peopleBeanLocal.setPeopleContact(peopleMainEntity, partnersEntity, PeopleContact.CONTACT_EMAIL, email, true, null, new Date());
            peopleContact = new PeopleContact(contact);
        }

        mailBeanLocal.saveMessage(peopleMainID, userID, Messages.EMAIL, Reference.MANUAL_EXEC, peopleContact.getId(), new Date(), subject, messageBody);
        mailBeanLocal.send(subject, messageBody, email);
        return collectorID != null ? "/views/collector/view?faces-redirect=true&collectorID=" + collectorID : "/views/customer/edit?faces-redirect=true&peopleid=" + peopleMainID;
    }

    public String cancel() {
        return collectorID != null ? "/views/collector/view?faces-redirect=true&collectorID=" + collectorID : "/views/customer/edit?faces-redirect=true&peopleid=" + peopleMainID;
    }

    protected void reloadItem(Integer collectorID) {
        peopleMain = peopleDAO.getPeopleMain(peopleMainID, Utils.setOf(
                        PeopleMain.Options.INIT_PEOPLE_CONTACT,
                        PeopleMain.Options.INIT_PEOPLE_CONTACT_ALL)
        );
        if (messageID != null) {
            List<Messages> messages = peopleMain.getMessages();
            Messages message = null;
            for (Messages mess : messages) {
                if (mess.getId().equals(messageID)) {
                    message = mess;
                    break;
                }
            }
            if (message != null) {
                subject = message.getMessageHeader();
                messageBody = message.getMessageBody();
            }
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("description", "");
            try {
                ReportGenerated rep = reportBeanLocal.generateReport(null, Report.EMAIL_ID, params);
                messageBody = rep.getAsString();
            } catch (ReportException e) {
                logger.error("Ошибка при загрузке шаблона", e);

                FacesContext facesCtx = FacesContext.getCurrentInstance();
                JSFUtils.handleAsError(facesCtx, null, e);
            }
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Integer getCollectorID() {
        return collectorID;
    }

    public void setCollectorID(Integer collectorID) {
        this.collectorID = collectorID;
    }

    public Integer getMessageID() {
        return messageID;
    }

    public void setMessageID(Integer messageID) {
        this.messageID = messageID;
    }
}