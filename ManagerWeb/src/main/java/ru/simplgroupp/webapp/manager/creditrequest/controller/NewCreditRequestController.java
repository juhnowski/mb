package ru.simplgroupp.webapp.manager.creditrequest.controller;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.interfaces.ProductBeanLocal;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.util.ProductKeys;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.manager.ejb.AppBeanMgr;
import ru.simplgroupp.webapp.manager.people.controller.ListPeopleQuickController;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.WorkflowObjectState;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCreditRequestController extends AbstractSessionController implements Serializable {

    private static final long serialVersionUID = 3539315583656759060L;

    private static final Map<String, String> stepForms = new HashMap<String, String>(3);

    static {
        stepForms.put("init", "newmanual.xthml");
        stepForms.put("taskFillCROffline", "edit_croff.xthml");
        stepForms.put("taskCreditDecision", "edit_decs.xthml");
    }

    @EJB
    private AppBeanMgr appBean;

    @EJB
    private PeopleBeanLocal peopleBean;

    @EJB
    private KassaBeanLocal kassaBean;

    @EJB
    private ProductBeanLocal productBean;

    @EJB
    private UsersBeanLocal userBean;

    @EJB
    private WorkflowBeanLocal wfBean;

    protected ListPeopleQuickController listCtl;

    protected EditCreditRequestOfflineController editCtl;

    protected Integer days;

    protected Integer minDays;

    protected Integer maxDays;

    protected Integer creditSum;

    protected Double minSum;

    protected Double maxSum;

    protected Integer creditRequestId;

    protected boolean chooseCustomer = false;

    private static final Logger logger = LoggerFactory.getLogger(NewCreditRequestController.class);
    
    @PostConstruct
    public void init() {
        Map<String, Object> limits =productBean.getNewRequestDefaultProductConfig();

        minDays = Convertor.toInteger( limits.get(ProductKeys.CREDIT_DAYS_MIN));
        maxDays = Convertor.toInteger( limits.get(ProductKeys.CREDIT_DAYS_MAX));
        minSum = (Double) limits.get(ProductKeys.CREDIT_SUM_MIN);
        maxSum = (Double) limits.get(ProductKeys.CREDIT_SUM_MAX_UNKNOWN);
    }

    public ListPeopleQuickController getListCtl() {
        return listCtl;
    }

    public void setListCtl(ListPeopleQuickController listCtl) {
        this.listCtl = listCtl;
    }

    public void searchLsn(ActionEvent event) {
        listCtl.searchLsn(event);
    }

    public void dummy() {
    }

    public String selectAndCreate() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        Integer pmainid = null;
        Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
        if (prms.containsKey("peopleid")) {
            pmainid = Convertor.toInteger(prms.get("peopleid"));
        }
        CreditRequest cr = appBean.createCR(creditSum.intValue(), days, pmainid);
        creditRequestId = cr.getId();
        return "edit_croff.xhtml?faces-redirect=true&prmBusinessObjectId=" + cr.getId().toString();
    }

    protected String getNextStep() {
        List<WorkflowObjectState> states = wfBean.getProcWfActions(CreditRequest.class.getName(), creditRequestId, Utils.setOf(), true);
        // TODO
        return null;
    }

    public String newCreate() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();

        try {
            CreditRequest cr = appBean.createCRNewPeople(creditSum.intValue(), days, listCtl.getData().getSurname(), listCtl.getData().getName(), listCtl.getData().getMidname(), listCtl.getData().getDateBirth(), listCtl.getData().getPaspSeries(), listCtl.getData().getPaspNumber(), listCtl.getData().getSnils(), listCtl.getData().getInn(), listCtl.getData().getEmail(), listCtl.getData().getPhone());
            creditRequestId = cr.getId();
            return "edit_croff.xhtml?faces-redirect=true&prmBusinessObjectId=" + cr.getId().toString();
        } catch (Exception e) {
            JSFUtils.handleAsError(facesCtx, null, new KassaException("Не удалось сохранить данные, ошибка " + e.getMessage()));
            return null;
        }
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getCreditSum() {
        return creditSum;
    }

    public void setCreditSum(Integer creditSum) {
        this.creditSum = creditSum;
    }

    public EditCreditRequestOfflineController getEditCtl() {
        return editCtl;
    }

    public void setEditCtl(EditCreditRequestOfflineController editCtl) {
        this.editCtl = editCtl;
    }

    public boolean isChooseCustomer() {
        return chooseCustomer;
    }

    public void setChooseCustomer(boolean chooseCustomer) {
        this.chooseCustomer = chooseCustomer;
    }

    public Integer getMinDays() {
        return minDays;
    }

    public Integer getMaxDays() {
        return maxDays;
    }

    public Double getMinSum() {
        return minSum;
    }

    public Double getMaxSum() {
        return maxSum;
    }

    public void toggleChooseCustomer(ActionEvent event) {
        this.chooseCustomer = !this.chooseCustomer;
        logger.info("chooseCustomer="+chooseCustomer);
    }
}
