package ru.simplgroupp.webapp.manager.collector.controller;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.CallsDAO;
import ru.simplgroupp.dao.interfaces.CollectorDAO;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.persistence.PartnersEntity;
import ru.simplgroupp.persistence.PeopleMainEntity;
import ru.simplgroupp.services.PeopleContactService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.util.DatesUtils;
import ru.simplgroupp.util.ProductKeys;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 21.08.2015
 * 12:54
 */

public class ViewCollectorController extends AbstractSessionController implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ViewCollectorController.class);

    @EJB
    private CollectorDAO collectorDAO;
    @EJB
    private CreditCalculatorBeanLocal creditCalc;
    @EJB
    private ProductBeanLocal productBean;
    @EJB
    private ReferenceBooksLocal referenceBean;
    @EJB
    private PeopleContactService peopleContactService;
    @EJB
    private MailBeanLocal mailBeanLocal;
    @EJB
    private CallsDAO callsDAO;
    @EJB
    private PeopleBeanLocal peopleBeanLocal;
    @EJB
    private PeopleDAO peopleDAO;
    @EJB
    private CreditBeanLocal creditBeanLocal;

    protected Integer collectorID;
    private Integer newContactTypeID;
    private Collector collector;
    private Credit credit;

    /**
     * что считаем
     * 1 - сумма обычная
     * 2 - сумма для продления
     */
    protected Integer calc = 1;
    /**
     * общая сумма
     */
    protected Double sumAll;
    /**
     * сумма основного долга
     */
    protected Double sumMain;
    /**
     * сумма процентов
     */
    protected Double sumPercent;
    /**
     * сумма процентов к выплате
     */
    protected Double sumPercentPay;
    /**
     * сумма процентов без штрафа
     */
    protected Double sumPercentWithoutPenalty;
    /**
     * сумма штрафа
     */
    protected Double sumPenalty = new Double(0);
    /**
     * сумма комиссии
     */
    protected Double sumComission = new Double(0);
    /**
     * дата расчета
     */
    protected Date dateCalc;
    /**
     * дата продления максимальная
     */
    protected Date dateLong;
    /**
     * дата, до которой продляем
     */
    protected Date dateNew;
    /**
     * дней просрочки
     */
    protected Integer daysOverdue = 0;
    /**
     * константы для просрочки
     */
    protected Map<String, Object> overdueLimits;

    /**
     * ID заявки
     */
    private Integer creditRequestID;

    /**
     * Значение для контакта
     */
    private String newContactValue;

    /**
     * Контакты
     */
    private List<PeopleContact> contactList;

    /**
     * Смс
     */
    private List<Messages> smsList = new ArrayList<Messages>();

    /**
     * Сообщения
     */
    private List<Messages> mailList = new ArrayList<Messages>();

    /**
     * Звонки
     */
    private List<Call> callsList;

    /**
     * Результаты обзвона
     */
    private List<CallResult> callResultList;

    /**
     * Статусы кредита
     */
    private List<Reference> creditStatusList;

    /**
     * Статус кредита
     */
    private Integer creditStatusID;

    /**
     * Активная задача по данному клиенту
     */
    private Task activeTask;

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
            if (collectorID != null) {
                reloadItem(collectorID);
            }
            if (creditStatusList == null) {
                creditStatusList = new ArrayList<Reference>();
                List<Reference> refs = referenceBean.getCreditStatusTypes();
                for (Reference ref : refs) {
                    // берем только нужные нам статусы
                    if (ref.getCodeInteger().equals(Credit.CREDIT_COURT)) {
                        creditStatusList.add(ref);
                    }
                }
            }
        }
    }

    protected void reloadItem(Integer collectorID) {
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
        creditRequestID = credit.getCreditRequest().getId();

        contactList = new ArrayList<PeopleContact>();
        for (PeopleContact contact : collector.getPeopleMain().getPeopleContactsAll()) {
            if (contact.getPartner().getId().equals(Partner.SYSTEM)) {
                contactList.add(contact);
            }
        }

        if (credit.getProduct() != null) {
            overdueLimits = productBean.getOverdueProductConfig(credit.getProduct().getId());
        }
        List<Messages> messagesList = mailBeanLocal.getMessagesByPeopleAndUserID(collector.getPeopleMain().getId(), collector.getUser().getId());
        for (Messages message : messagesList) {
            if (message.getMessageType().getCodeInteger().equals(Messages.SMS)) {
                smsList.add(message);
            } else if (message.getMessageType().getCodeInteger().equals(Messages.EMAIL)) {
                mailList.add(message);
            }
        }
        callResultList = callsDAO.getCallResultsByPeopleAndUserID(collector.getPeopleMain().getId(), collector.getUser().getId());
        reloadCalls();

        // берем все задачи коллектора
        List<Task> tasks = collectorDAO.getListCollectorTasks(new Date(), collector.getUser().getId(), ActiveStatus.ACTIVE, null);
        for (Task task : tasks) {
            if (task.getPeopleMainId().getId().equals(collector.getPeopleMain().getId())) {
                // берем задачу по текущему клиенту
                activeTask = task;
                break;
            }
        }
    }

    public void reloadCalls() {
        callsList = callsDAO.getCallsByPeople(collector.getPeopleMain().getId(),
                Utils.setOf(Call.Options.INIT_USER, PeopleMain.Options.INIT_PEOPLE_PERSONAL));
    }

    public void updateCalls() {
        for (Call call : callsList) {
            callsDAO.saveCallsEntity(call.getEntity());
        }

        reloadCalls();
    }

    public void changeCalcType(ValueChangeEvent event){
        calc = Convertor.toInteger(event.getNewValue());
        sumAll = new Double(0);
        sumMain = new Double(0);
        sumPercent = new Double(0);
        sumPenalty = new Double(0);
        // если это продление
        if (calc == 2) {
            // рассчитываем параметры продления
            Map<String, Object> cpparams = creditCalc.calcProlongDays(credit, credit.getCreditDataEnd());
            Integer longdays = Convertor.toInteger(cpparams.get(CreditCalculatorBeanLocal.PROLONG_DAYS));
            dateLong= DateUtils.addDays(credit.getCreditDataEnd(), longdays);
        }
    }

    public void recalc(ActionEvent event) {
        switch (calc) {
            //сумма к возврату
            case 1: {
                Map<String, Object> resCalc = creditCalc.calcCredit(credit, dateCalc);
                sumAll = Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_BACK));
                sumMain = Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_MAIN));
                log.info("Дата для расчета " + dateCalc);
                log.info("Сумма общая " + sumAll);
                sumPenalty = Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_PENALTY));
                sumPercent = Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_PERCENT));
                sumPercentWithoutPenalty = sumPercent - sumPenalty;

                log.info("Сумма процентов " + sumPercent);
                daysOverdue = Convertor.toInteger(resCalc.get(CreditCalculatorBeanLocal.DAYS_OVERDUE));
                log.info("Сумма обычных процентов при просрочке " + sumPercentWithoutPenalty);

                break;
            }
            //сумма к возврату - продление
            case 2: {
                sumPenalty = new Double(0);
                //продление возможно только из льготного периода просрочки
                if (daysOverdue <= Convertor.toInteger(overdueLimits.get(ProductKeys.CREDIT_DAYS_MAX_LGOT))) {
                    // есть возможность продления - не все дни продления выбраны
                    if (dateLong != credit.getCreditDataEnd()) {
                        //дата расчета не может быть больше максимальной даты продления
                        if (dateCalc.after(dateLong)) {
                            dateCalc = dateLong;
                        }

                        Map<String, Object> resCalc = creditCalc.calcCredit(credit, dateCalc);
                        sumPercentPay = Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_PERCENT));
                        daysOverdue = Convertor.toInteger(resCalc.get(CreditCalculatorBeanLocal.DAYS_OVERDUE));

                        // рассчитываем параметры кредита
                        Map<String, Object> crparams = creditCalc.calcCreditInitial(credit.getSumMainRemain(), credit.getCreditPercent(), DatesUtils.daysDiff(dateNew, dateCalc), null);
                        sumPercent = Convertor.toDouble(crparams.get(CreditCalculatorBeanLocal.SUM_PERCENT));
                        sumMain = credit.getSumMainRemain();
                        sumAll = ((sumPercent == null) ? 0 : sumPercent) + ((sumMain == null) ? 0 : sumMain);
                    }
                }
                break;
            }
        }
    }

    public void saveNewContact() {
        PeopleMainEntity peopleMainEntity = peopleDAO.getPeopleMainEntity(credit.getPeopleMain().getId());
        PartnersEntity partnersEntity = referenceBean.getPartnerEntity(Partner.SYSTEM);

        peopleBeanLocal.setPeopleContact(peopleMainEntity, partnersEntity, newContactTypeID, newContactValue, true, null, new Date());
        reloadItem(collectorID);

        newContactValue = null;
        newContactTypeID = 1;
    }

    public String changeStatus() {
        Integer eventStatus = null;
        if (creditStatusID.equals(Credit.CREDIT_COURT)) {
            eventStatus = EventCode.CREDIT_TO_COURT;
        }

        // меняем статус кредита
        creditBeanLocal.changeCreditStatus(credit.getId(), creditStatusID, null, new Date(), eventStatus);
        // ставим запись у коллектора в статус Архивный
        collectorDAO.changeCollectorStatus(credit.getId(), credit.getPeopleMain().getId(), ActiveStatus.ARCHIVE);

        // отправляем клиента в блэклист
        PeopleMainEntity peopleMainEntity = peopleDAO.getPeopleMainEntity(credit.getPeopleMain().getId());
        try {
            peopleBeanLocal.saveBlackList(peopleMainEntity, BlackList.REASON_CREDIT_OVERDUE, new Date(), null);
        } catch (PeopleException e) {
            logger.error("Ошибка при сохранении в blacklist", e);

            FacesContext facesCtx = FacesContext.getCurrentInstance();
            JSFUtils.handleAsError(facesCtx, null, e);
        }

        return "index?faces-redirect=true";
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

    public String close() {
        return "index?faces-redirect=true";
    }

    public String toCallResult() {
        return "call_result?faces-redirect=true";
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public Integer getCollectorID() {
        return collectorID;
    }

    public void setCollectorID(Integer collectorID) {
        this.collectorID = collectorID;
    }

    public Integer getCalc() {
        return calc;
    }

    public void setCalc(Integer calc) {
        this.calc = calc;
    }

    public Double getSumAll() {
        return sumAll;
    }

    public void setSumAll(Double sumAll) {
        this.sumAll = sumAll;
    }

    public Double getSumMain() {
        return sumMain;
    }

    public void setSumMain(Double sumMain) {
        this.sumMain = sumMain;
    }

    public Double getSumPercent() {
        return sumPercent;
    }

    public void setSumPercent(Double sumPercent) {
        this.sumPercent = sumPercent;
    }

    public Double getSumPercentPay() {
        return sumPercentPay;
    }

    public void setSumPercentPay(Double sumPercentPay) {
        this.sumPercentPay = sumPercentPay;
    }

    public Double getSumPercentWithoutPenalty() {
        return sumPercentWithoutPenalty;
    }

    public void setSumPercentWithoutPenalty(Double sumPercentWithoutPenalty) {
        this.sumPercentWithoutPenalty = sumPercentWithoutPenalty;
    }

    public Double getSumPenalty() {
        return sumPenalty;
    }

    public void setSumPenalty(Double sumPenalty) {
        this.sumPenalty = sumPenalty;
    }

    public Double getSumComission() {
        return sumComission;
    }

    public void setSumComission(Double sumComission) {
        this.sumComission = sumComission;
    }

    public Date getDateCalc() {
        return dateCalc;
    }

    public void setDateCalc(Date dateCalc) {
        this.dateCalc = dateCalc;
    }

    public Date getDateLong() {
        return dateLong;
    }

    public void setDateLong(Date dateLong) {
        this.dateLong = dateLong;
    }

    public Date getDateNew() {
        return dateNew;
    }

    public void setDateNew(Date dateNew) {
        this.dateNew = dateNew;
    }

    public Integer getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(Integer daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public Map<String, Object> getOverdueLimits() {
        return overdueLimits;
    }

    public void setOverdueLimits(Map<String, Object> overdueLimits) {
        this.overdueLimits = overdueLimits;
    }

    public void changeDate(ValueChangeEvent event) {
        dateCalc = (Date) event.getNewValue();
    }

    public void changeDateNew(ValueChangeEvent event) {
        dateNew = (Date) event.getNewValue();
    }

    public Integer getCreditRequestID() {
        return creditRequestID;
    }

    public void setCreditRequestID(Integer creditRequestID) {
        this.creditRequestID = creditRequestID;
    }

    public String getNewContactValue() {
        return newContactValue;
    }

    public void setNewContactValue(String newContactValue) {
        this.newContactValue = newContactValue;
    }

    public Integer getNewContactTypeID() {
        return newContactTypeID;
    }

    public void setNewContactTypeID(Integer newContactTypeID) {
        this.newContactTypeID = newContactTypeID;
    }

    public List<PeopleContact> getContactList() {
        return contactList;
    }

    public void setContactList(List<PeopleContact> contactList) {
        this.contactList = contactList;
    }

    public List<Messages> getMailList() {
        return mailList;
    }

    public void setMailList(List<Messages> mailList) {
        this.mailList = mailList;
    }

    public List<Messages> getSmsList() {
        return smsList;
    }

    public void setSmsList(List<Messages> smsList) {
        this.smsList = smsList;
    }

    public List<CallResult> getCallResultList() {
        return callResultList;
    }

    public void setCallResultList(List<CallResult> callResultList) {
        this.callResultList = callResultList;
    }

    public List<Call> getCallsList() {
        return callsList;
    }

    public void setCallsList(List<Call> callsList) {
        this.callsList = callsList;
    }

    public void changeContactType(ValueChangeEvent event) {
        newContactTypeID = (Integer) event.getNewValue();
    }

    public List<Reference> getCreditStatusList() {
        return creditStatusList;
    }

    public void setCreditStatusList(List<Reference> creditStatusList) {
        this.creditStatusList = creditStatusList;
    }

    public Integer getCreditStatusID() {
        return creditStatusID;
    }

    public void setCreditStatusID(Integer creditStatusID) {
        this.creditStatusID = creditStatusID;
    }

    public Task getActiveTask() {
        return activeTask;
    }

    public void setActiveTask(Task activeTask) {
        this.activeTask = activeTask;
    }
}
