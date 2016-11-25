package ru.simplgroupp.webapp.manager.collector.controller;

import org.ajax4jsf.model.SequenceRange;
import ru.simplgroupp.dao.impl.ListCollectorContainer;
import ru.simplgroupp.dao.interfaces.CollectorDAO;
import ru.simplgroupp.persistence.CollectorEntity;
import ru.simplgroupp.toolkit.common.*;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.util.ListContainer;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 19.08.2015
 * 12:18
 */

public class ListCollectorController extends AbstractListController implements Serializable {
    @EJB
    CollectorDAO collectorDAO;

    List<Collector> newRequestList;
    /**
     * Фамилия
     */
    private String prmPeopleSurname;
    /**
     * Имя
     */
    private String prmPeopleName;
    /**
     * Номер кредита
     */
    private String prmCreditAccount;
    /**
     * Статус кредита в расширенном поиске
     */
    private Integer prmIsActiveID = ActiveStatus.ACTIVE;
    /**
     * Сумма кредита в расширенном поиске
     */
    private MoneyRange prmCreditSum = new MoneyRange(null, null);
    /**
     * Сумма возврата в расширенном поиске
     */
    private MoneyRange prmCreditSumBack = new MoneyRange(null, null);

    /**
     * Дней просрочки кредита
     */
    private IntegerRange prmMaxDelay = new IntegerRange(null, null);

    /**
     * Дата начала кредита
     */
    private DateRange prmCreditStartDate = new DateRange(null, null);

    /**
     * Дата окончания кредита
     */
    private DateRange prmCreditEndDate = new DateRange(null, null);

    /**
     * Дата следующего контакта
     */
    private DateRange prmDateNextContact = new DateRange(null, null);

    /**
     * Список заданий для коллектора на текущую дату
     */
    private List<Task> collectorTasks;

    @Override
    public void initData() throws Exception {
        reloadNewRequestList();
        reloadCollectorTasks();
    }

    public void reloadCollectorTasks() {
        collectorTasks = collectorDAO.getListCollectorTasks(new Date(), userCtl.getUser().getId(), ActiveStatus.ACTIVE, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
    }

    public void reloadNewRequestList() {
        ListCollectorContainer container = collectorDAO.genData(ListCollectorContainer.class);
        container.setPrmUserId(userCtl.getUser().getId());
        container.setPrmIsActive(ActiveStatus.DRAFT);
        container.setObjectOptions(Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
        newRequestList = collectorDAO.listData(-1, -1, container);
    }

    @Override
    public StdRequestDataModel createModel() {
        return new CollectorDataModel();
    }

    public List<Collector> getNewRequestList() {
        return newRequestList;
    }

    public void setNewRequestList(List<Collector> newRequestList) {
        this.newRequestList = newRequestList;
    }

    public class CollectorDataModel extends StdRequestDataModel<Collector> {
        @Override
        protected List<Collector> internalList(SequenceRange seqRange, SortCriteria[] sorting) throws WebAppException {
            nullIfEmpty();
            return collectorDAO.listData(seqRange.getFirstRow(), seqRange.getRows(), getListContainer());
        }

        private ListCollectorContainer getListContainer() {
            ListCollectorContainer container = collectorDAO.genData(ListCollectorContainer.class);
            container.setPrmSurname(prmPeopleSurname);
            container.setPrmName(prmPeopleName);
            container.setPrmCreditAccount(prmCreditAccount);
            container.setPrmUserId(userCtl.getUser().getId());
            container.setObjectOptions(Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
            container.setPrmCreditsum(prmCreditSum);
            container.setPrmCreditsumback(prmCreditSumBack);
            container.setPrmIsActive(prmIsActiveID);
            container.setPrmMaxDelay(prmMaxDelay);
            container.setPrmCreditDataBeg(prmCreditStartDate);
            container.setPrmCreditDataEnd(prmCreditEndDate);
            container.setPrmDateNextContact(prmDateNextContact);
            return container;
        }

        @Override
        protected int internalRowCount() throws WebAppException {
            return collectorDAO.countData(getListContainer());
        }

        @Override
        public Collector getRowData() {
            if (rowKey == null) {
                return null;
            } else {
                return collectorDAO.getCollector(((Number) rowKey).intValue(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
            }
        }
    }

    private void nullIfEmpty() {
        if (prmCreditSum != null && prmCreditSum.getFrom() != null && prmCreditSum.getFrom().intValue() == 0) {
            prmCreditSum.setFrom(null);
        }
        if (prmCreditSum != null && prmCreditSum.getTo() != null && prmCreditSum.getTo().intValue() == 0) {
            prmCreditSum.setTo(null);
        }

        if (prmCreditSumBack != null && prmCreditSumBack.getFrom() != null && prmCreditSumBack.getFrom().intValue() == 0) {
            prmCreditSumBack.setFrom(null);
        }
        if (prmCreditSumBack != null && prmCreditSumBack.getTo() != null && prmCreditSumBack.getTo().intValue() == 0) {
            prmCreditSumBack.setTo(null);
        }

        if (prmMaxDelay != null && prmMaxDelay.getFrom() != null && prmMaxDelay.getFrom().intValue() == 0) {
            prmMaxDelay.setFrom(null);
        }
        if (prmMaxDelay != null && prmMaxDelay.getTo() != null && prmMaxDelay.getTo().intValue() == 0) {
            prmMaxDelay.setTo(null);
        }
    }

    public String dummy() {
        return null;
    }

    public void searchListener(ActionEvent event) {
        refreshSearch();
    }

    public void clearListener(ActionEvent event) {
        prmPeopleSurname = null;
        prmCreditAccount = null;
        prmPeopleName = null;
        prmIsActiveID = ActiveStatus.ACTIVE;
        prmCreditSum = new MoneyRange(null, null);
        prmCreditSumBack = new MoneyRange(null, null);
        prmMaxDelay = new IntegerRange(null, null);
        prmCreditStartDate = new DateRange(null, null);
        prmCreditEndDate = new DateRange(null, null);
        prmDateNextContact = new DateRange(null, null);
        refreshSearch();
    }

    public String takeRequest() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
        if (prms.containsKey("collectorID")) {
            Integer id= Convertor.toInteger(prms.get("collectorID"));
            Collector collector = collectorDAO.getCollector(id, null);
            if (collector!=null) {
                collectorDAO.saveCollectorRecordWithUser(collector, userCtl.getUser().getId(), ActiveStatus.ACTIVE, EventCode.COLLECTOR_CLIENT_GET);
                return "view?faces-redirect=true&collectorID=" + collector.getId();
            }
        }
        return null;
    }

    public String getPrmCreditAccount() {
        return prmCreditAccount;
    }

    public void setPrmCreditAccount(String prmCreditAccount) {
        this.prmCreditAccount = prmCreditAccount;
    }

    public String getPrmPeopleName() {
        return prmPeopleName;
    }

    public void setPrmPeopleName(String prmPeopleName) {
        this.prmPeopleName = prmPeopleName;
    }

    public String getPrmPeopleSurname() {
        return prmPeopleSurname;
    }

    public void setPrmPeopleSurname(String prmPeopleSurname) {
        this.prmPeopleSurname = prmPeopleSurname;
    }

    public IntegerRange getPrmMaxDelay() {
        return prmMaxDelay;
    }

    public void setPrmMaxDelay(IntegerRange prmMaxDelay) {
        this.prmMaxDelay = prmMaxDelay;
    }

    public MoneyRange getPrmCreditSumBack() {
        return prmCreditSumBack;
    }

    public void setPrmCreditSumBack(MoneyRange prmCreditSumBack) {
        this.prmCreditSumBack = prmCreditSumBack;
    }

    public MoneyRange getPrmCreditSum() {
        return prmCreditSum;
    }

    public void setPrmCreditSum(MoneyRange prmCreditSum) {
        this.prmCreditSum = prmCreditSum;
    }

    public Integer getPrmIsActiveID() {
        return prmIsActiveID;
    }

    public void setPrmIsActiveID(Integer prmIsActiveID) {
        this.prmIsActiveID = prmIsActiveID;
    }

    public DateRange getPrmDateNextContact() {
        return prmDateNextContact;
    }

    public void setPrmDateNextContact(DateRange prmDateNextContact) {
        this.prmDateNextContact = prmDateNextContact;
    }

    public DateRange getPrmCreditStartDate() {
        return prmCreditStartDate;
    }

    public void setPrmCreditStartDate(DateRange prmCreditStartDate) {
        this.prmCreditStartDate = prmCreditStartDate;
    }

    public DateRange getPrmCreditEndDate() {
        return prmCreditEndDate;
    }

    public void setPrmCreditEndDate(DateRange prmCreditEndDate) {
        this.prmCreditEndDate = prmCreditEndDate;
    }

    public List<Task> getCollectorTasks() {
        return collectorTasks;
    }

    public void setCollectorTasks(List<Task> collectorTasks) {
        this.collectorTasks = collectorTasks;
    }
}
