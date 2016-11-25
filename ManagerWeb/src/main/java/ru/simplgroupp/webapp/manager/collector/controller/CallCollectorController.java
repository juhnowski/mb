package ru.simplgroupp.webapp.manager.collector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.CallsDAO;
import ru.simplgroupp.dao.interfaces.CollectorDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
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
import java.util.List;
import java.util.Map;

/**
 * 31.08.2015
 * 10:23
 */

public class CallCollectorController extends AbstractSessionController implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CallCollectorController.class);

    @EJB
    private CollectorDAO collectorDAO;
    @EJB
    private CallsDAO callsDAO;
    @EJB
    protected ReferenceBooksLocal referenceBean;

    protected Integer collectorID;
    private Collector collector;
    private Users user;
    private String comment;
    private Date callDateStart;
    private Date callDatePromise;
    private Date dateNextContact;
    private List<Reference> callResultTypes;
    private Integer callResultCodeIntegerID;
    private Integer taskID;

    @PostConstruct
    public void init()	{
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            if (collectorID == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("collectorID")) {
                    collectorID = Convertor.toInteger(prms.get("collectorID"));
                }
            }
            if (taskID == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("taskID")) {
                    taskID = Convertor.toInteger(prms.get("taskID"));
                }
            }
            if (collectorID != null) {
                reloadItem(collectorID);
            }
        }
        callResultTypes = referenceBean.getCallResultTypesList();
    }

    protected void reloadItem(Integer collectorID){
        collector = collectorDAO.getCollector(collectorID, Utils.setOf(
                PeopleMain.Options.INIT_CREDIT
                , PeopleMain.Options.INIT_CREDIT_REQUEST
                , PeopleMain.Options.INIT_PEOPLE_PERSONAL
                , PeopleMain.Options.INIT_DOCUMENT
                , PeopleMain.Options.INIT_ADDRESS
                , PeopleMain.Options.INIT_EMPLOYMENT
                , PeopleMain.Options.INIT_PEOPLE_CONTACT
        ));
        user = collector.getUser();
    }

    public String cancel() {
        return "canceled";
    }

    public String save() {
        Integer peopleID = collector.getPeopleMain().getId();
        Integer userID = user.getId();
        try {
            callsDAO.newCallResult(peopleID, userID, callResultCodeIntegerID, callDateStart, callDatePromise, dateNextContact, comment);

            // если есть активная задача меняем ей статус на архивный
            if (taskID != null) {
                collectorDAO.changeTaskStatus(userID, peopleID, ActiveStatus.ARCHIVE, new Date());
            }
            // и создаем новую задачу
            collectorDAO.createCollectorTask(dateNextContact, userID, peopleID, collectorID, Task.CLIENT_CALL_TASK, ActiveStatus.ACTIVE);
        } catch (KassaException e) {
            logger.error("Ошибка при сохранении результата звонка", e);

            FacesContext facesCtx = FacesContext.getCurrentInstance();
            JSFUtils.handleAsError(facesCtx, null, e);
        }
        return "view?faces-redirect=true&collectorID=" + collectorID;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public Integer getCollectorID() {
        return collectorID;
    }

    public void setCollectorID(Integer collectorID) {
        this.collectorID = collectorID;
    }

    public Date getCallDateStart() {
        return callDateStart;
    }

    public void setCallDateStart(Date callDateStart) {
        this.callDateStart = callDateStart;
    }

    public Date getCallDatePromise() {
        return callDatePromise;
    }

    public void setCallDatePromise(Date callDatePromise) {
        this.callDatePromise = callDatePromise;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Date getDateNextContact() {
        return dateNextContact;
    }

    public void setDateNextContact(Date dateNextContact) {
        this.dateNextContact = dateNextContact;
    }

    public List<Reference> getCallResultTypes() {
        return callResultTypes;
    }

    public void setCallResultTypes(List<Reference> callResultTypes) {
        this.callResultTypes = callResultTypes;
    }

    public Integer getCallResultCodeIntegerID() {
        return callResultCodeIntegerID;
    }

    public void setCallResultCodeIntegerID(Integer callResultCodeIntegerID) {
        this.callResultCodeIntegerID = callResultCodeIntegerID;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }
}
