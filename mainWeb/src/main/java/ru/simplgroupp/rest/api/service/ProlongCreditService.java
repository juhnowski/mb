package ru.simplgroupp.rest.api.service;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.dao.interfaces.OfficialDocumentsDAO;
import ru.simplgroupp.ejb.ApplicationAction;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.persistence.ProlongEntity;
import ru.simplgroupp.rest.api.data.prolong.ProlongInfoData;
import ru.simplgroupp.rest.api.data.prolong.SmsRequestData;
import ru.simplgroupp.rest.api.data.prolong.SmsResponseData;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.util.ProductKeys;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Сервис обеспечивающий работу страницы продления кредита.
 */
@RequestScoped
public class ProlongCreditService {
    private static Logger logger = Logger.getLogger(ProlongCreditService.class.getName());

    private OverviewService os;
    private AiService aiSrv;
    private CreditBeanLocal creditBean;
    private KassaBeanLocal kassa;
    private CreditCalculatorBeanLocal creditCalc;
    private ProductBeanLocal productBean;
    private PeopleBeanLocal peopleBean;
    private PeopleDAO peopleDAO;
    private OfficialDocumentsDAO documentsDAO;
    private UserService userSrv;
    private HttpServletRequest request;
    private MailBeanLocal mailBean;
    private ProlongCreditCacheService cache;
    private AppServiceBeanLocal appServ;
    private WorkflowBeanLocal workflow;

    private String ipAddr;
    private String userAgent;

    private Credit credit;
    private PeopleMain people;
    private Prolong prolong;
    private Map<String, Object> limits;
    private Integer days;
    private Integer longDays;
    private Integer daysf;
    private Map<String, Object> cpparams;
    private Map<String, Object> crparams;
    private Double sumOldStake;
    private Double sumStake;
    private Double sumAll;
    private Long dateEnd;
    private String agreement;
    /**
     * оферта
     */
    private OfficialDocuments document;

    /**
     * Метод инициализации. Должен быть вызван перед началом использования сервиса. В CDI контексте вызывается
     * автоматически при инстанциации объекта контейнером.
     * @throws KassaException
     */
    @PostConstruct
    public void init () throws KassaException {
        os.init();
       
        this.ipAddr = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(this.ipAddr)) {
            this.ipAddr = request.getRemoteAddr();
        }
        this.userAgent = request.getHeader("user-agent");


        int peopleId = userSrv.getUser().getPeopleMain().getId();
        this.credit = creditBean.findCreditActive(peopleId, Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST));
        this.people = peopleDAO.getPeopleMain(peopleId, Utils.setOf(PeopleMain.Options.INIT_ACCOUNTACTIVE,
                PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_PEOPLE_PERSONAL,
                PeopleMain.Options.INIT_DOCUMENT, PeopleMain.Options.INIT_ADDRESS,PeopleMain.Options.INIT_PEOPLE_MISC));

        if (credit != null) {
            this.limits = productBean.getProlongProductConfig(credit.getProduct().getId());
            prolong = kassa.initProlong(people, credit, new Date(), limits);
            this.cpparams = creditCalc.calcProlongDays(credit, new Date());
            this.days = Convertor.toInteger(this.cpparams.get(CreditCalculatorBeanLocal.PROLONG_DAYS_FOR_PAYMENT));
            this.longDays = Convertor.toInteger(this.cpparams.get(CreditCalculatorBeanLocal.PROLONG_DAYS));
            this.daysf = Convertor.toInteger(this.cpparams.get(CreditCalculatorBeanLocal.PROLONG_DAYS_BEFORE_CREDIT_END));
            logger.info("Доступно дней продления " + this.longDays);
            logger.info("Дней для выплаты процентов " + this.days);
            logger.info("Дней до окончания кредита " + this.daysf);
            this.crparams = creditCalc.calcCredit(credit.getId(), new Date());
            this.sumOldStake = Convertor.toDouble(this.crparams.get(CreditCalculatorBeanLocal.SUM_PERCENT));
            logger.info("Проценты к выплате " + this.sumOldStake);
            document=peopleBean.initOfficialDocument(people, credit.getCreditRequest().getId(),
            		credit.getId(),OfficialDocuments.OFERTA_PROLONG);
            recalc();
            generateAgreement(prolong.getLongDate());
        }
    }

    /**
     * Формирует объект для заполнения страницы продления кредита.
     * @return объект с данными для страницы.
     */
    public ProlongInfoData buildInfo() throws KassaException {
        ProlongInfoData info = new ProlongInfoData();

        info.setCanProlong(this.aiSrv.getCanProlong());
        info.setCanProlongCancel(this.aiSrv.getCanProlongCancel());
        info.setMsgCanProlong(this.aiSrv.getMsgCanProlong());

        if (credit != null) {
            info.getCredit().setCreditPercent(credit.getCreditPercent());
            info.getCredit().setSumMainRemain(credit.getSumMainRemain());

            info.setCreditDaysMin(Convertor.toInteger(limits.get(ProductKeys.PROLONG_DAYS_MIN)));
            info.setCreditDaysMax(this.longDays);
            // рассчитываем параметры продления
            info.setDays(this.days);
            info.getProlong().setLongDays(this.prolong.getLongdays());
            info.setDaysf(this.daysf);
            // рассчитываем параметры кредита
            info.setSumOldStake(this.sumOldStake);
            info.setSumMain(credit.getSumMainRemain());
            info.setSumStake(this.sumStake);
            info.setSumAll(credit.getSumMainRemain() + info.getSumStake());
            info.setDateEnd(this.dateEnd);

            info.getProlong().setAgreement(this.agreement);
        }

        return info;
    }

    /**
     * Обновление параметров кредита в соответствии с заданными параметрами.
     */
    private void recalc() {
        // рассчитываем параметры кредита
    
        Map<String, Object> crparams = creditCalc.calcCreditInitial(credit.getSumMainRemain(), credit.getCreditPercent(), 
        		(prolong.getLongdays() == null ? 0 : prolong.getLongdays())+daysf,null);

        sumStake = Convertor.toDouble(crparams.get(CreditCalculatorBeanLocal.SUM_PERCENT));
        logger.info("Сумма процентов "+sumStake);
        sumAll = (credit.getSumMainRemain()) + sumStake;
        logger.info("Общая сумма "+sumAll);
        //если есть просрочка
        if (credit.isOverdue(new Date())){
            dateEnd=new Date().getTime();

        } else {
            dateEnd=credit.getCreditDataEnd().getTime();

        }
        prolong.setLongamount(sumAll);
    }

    /**
     * Генерация пользовательской оферты для продления кредита.
     * @param dateOferta Дата продления.
     * @throws KassaException
     */
    private void generateAgreement(Date dateOferta) throws KassaException {
        prolong.setLongdate(dateOferta);
        agreement = null;
        try {
        	agreement = kassa.generateAgreement(people, prolong,sumOldStake,
            		credit.isOverdue(new Date())?new Date():credit.getCreditDataEnd(),credit.getCreditRequest().getDateSign());
        
        } catch (Exception ex) {
            agreement = ex.getMessage();
        }
        prolong.setAgreement(agreement);
        document.setDocText(agreement);
    }

    public SmsResponseData recalcAgreement(SmsRequestData smsRequest) throws KassaException {
        String msg = null;
        try {
            try {
                Integer ldays = smsRequest.getLongDays() == null ? 0 : smsRequest.getLongDays();
                this.longDays = ldays;
                prolong.setLongdays(ldays);
                recalc();
            } catch (NumberFormatException e) {
                msg = "Ошибка преобразования ";
            }
            if (msg == null) {
                prolong.setLongamount(smsRequest.getSumAll());
                this.sumAll = smsRequest.getSumAll();
                generateAgreement(prolong.getLongDate());
            }
        } catch (Exception ex) {
            msg = "Не удалось отправить смс. ";
        }

        SmsResponseData smsResponse = new SmsResponseData();
        if (msg != null) {
            smsResponse.setSuccess(false);
            smsResponse.setMsg(msg);
        } else {
            smsResponse.setSuccess(true);
            smsResponse.setAgreement(this.agreement);
        }

        return smsResponse;
    }

    /**
     * Отправить SMS код пользователю.
     * @param smsRequest объект с данными для формирования новых параметров кредита и отправки SMS кода.
     * @return объект с результатами отправки SMS кода.
     * @throws KassaException
     */
    public SmsResponseData sendCodeLsn(SmsRequestData smsRequest) throws KassaException {
        String msg = null;
        String msisdn = null;
        try {
            try {
                Integer ldays = smsRequest.getLongDays() == null ? 0 : smsRequest.getLongDays();
                this.longDays = ldays;
                prolong.setLongdays(ldays);
                recalc();
            } catch (NumberFormatException e) {
                msg = "Ошибка преобразования ";
            }
            if (msg == null) {
                prolong.setLongamount(smsRequest.getSumAll());
                this.sumAll = smsRequest.getSumAll();
                generateAgreement(prolong.getLongDate());
                String smsCode = mailBean.generateCodeForSending();
                //String smsCode = GenUtils.genCode(6);
                this.cache.setSmsCode(smsCode);
                prolong.setSmsCode(smsCode);
                document.setSmsCode(smsCode);
                logger.info("Sms code " + smsCode);
                mailBean.sendSMSV2(people.getCellPhone().getValue(), "Код " + smsCode
                        + ". Это код для подписания оферты на пролонгацию займа в системе Онтайм.");
                msisdn = people.getCellPhone().getValue();
            }
        } catch (Exception ex) {
            msg = "Не удалось отправить смс. ";
        }

        SmsResponseData smsResponse = new SmsResponseData();
        if (msg != null) {
            smsResponse.setSuccess(false);
            smsResponse.setMsg(msg);
        } else {
            smsResponse.setSuccess(true);
            smsResponse.setAgreement(this.agreement);
            smsResponse.setMsisdn(msisdn);
        }

        return smsResponse;
    }

    /**
     * Создание продления кредита.
     * @param smsRequest объект с новыми параметрами по кредиту.
     * @return объект с результатами процесса создания продления кредита.
     */
    public SmsResponseData save(SmsRequestData smsRequest) {
        String msg = null;
        if (StringUtils.isEmpty(smsRequest.getSmsCode())) {
            msg = "Необходимо ввести код СМС";
        } else {
            try {
                if (this.cache.getSmsCode().equals(smsRequest.getSmsCode())) {

                    try {
                        Integer ldays = smsRequest.getLongDays() == null ? 0 : smsRequest.getLongDays();
                        this.longDays = ldays;
                        prolong.setLongdays(ldays);
                        recalc();
                    } catch(NumberFormatException e){
                        msg = "Ошибка преобразования ";
                    }

                    if (msg == null) {
                        prolong.setLongdate(new Date());
                        prolong.setLongamount(smsRequest.getSumAll());
                        prolong.setSmsCode(smsRequest.getSmsCode());
  
                        generateAgreement(prolong.getLongDate());
                        document.setSmsCode(smsRequest.getSmsCode());
                        document.setDocNumber(prolong.getUniqueNomer());
                        msg = saveProlongRequest();
                    }


                }else{
                    msg="Неверный код СМС";
                }

            }catch(KassaException e){
                msg = e.getMessage();
            }
        }

        SmsResponseData smsResponse = new SmsResponseData();

        if (msg != null) {
            smsResponse.setMsg(msg);
            smsResponse.setSuccess(false);
        } else {
            smsResponse.setSuccess(true);
        }
        return smsResponse;
    }

    /**
     * Создание продления кредита.
     * @return сообщение об ошибке. null - успешно.
     */
    protected String saveProlongRequest() {
    	String msg = null;
        ApplicationAction action = appServ.startApplicationAction(
                ProcessKeys.MSG_PROLONG, true, "Продление",
                new BusinessObjectRef(Prolong.class.getName(), prolong.getId()),
                new BusinessObjectRef(Credit.class.getName(), credit.getId()),
                new BusinessObjectRef(CreditRequest.class.getName(), credit.getCreditRequest().getId())
        );
        if (action == null) {
            msg = "Продление уже запущено";
            return msg;
        }

        try {
        	documentsDAO.saveOfficialDocument(document);
            ProlongEntity plong=kassa.saveLongRequestNew(credit.getId(), prolong, prolong.getLongDate(), ipAddr,userAgent);
            WorkflowObjectActionDef actionDef = new WorkflowObjectActionDef(false);
            actionDef.setBusinessObjectClass(Prolong.class.getName());
            actionDef.setSignalRef(ProcessKeys.MSG_PROLONG);
            actionDef.setRunProcessDefKey(ProcessKeys.DEF_CREDIT_PROLONG);
            workflow.goProc(Prolong.class.getName(), plong.getId(), actionDef, SignalRef.toString("*", null, "msgNone"), Utils.mapOfSO("creditId", credit.getId() ));
        } catch (WorkflowException | KassaException ignored) {
        } finally {
            appServ.endApplicationAction(action);
        }

        return msg;
    }

    public ProlongCreditService() {}

    @Inject
    public ProlongCreditService(OverviewService os, AiService aiSrv, CreditBeanLocal creditBean, KassaBeanLocal kassa, CreditCalculatorBeanLocal creditCalc, ProductBeanLocal productBean, PeopleDAO peopleDAO, UserService userSrv, HttpServletRequest request, MailBeanLocal mailBean, ProlongCreditCacheService cache, 
    		AppServiceBeanLocal appServ, WorkflowBeanLocal workflow,PeopleBeanLocal peopleBean,OfficialDocumentsDAO documentsDAO) {
        this.os = os;
        this.aiSrv = aiSrv;
        this.creditBean = creditBean;
        this.kassa = kassa;
        this.creditCalc = creditCalc;
        this.productBean = productBean;
        this.peopleDAO = peopleDAO;
        this.userSrv = userSrv;
        this.request = request;
        this.mailBean = mailBean;
        this.cache = cache;
        this.appServ = appServ;
        this.workflow = workflow;
        this.peopleBean=peopleBean;
        this.documentsDAO=documentsDAO;
    }
}
