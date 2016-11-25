package ru.simplgroupp.rest.api.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.dao.interfaces.OfficialDocumentsDAO;
import ru.simplgroupp.ejb.ApplicationAction;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.persistence.RefinanceEntity;
import ru.simplgroupp.rest.api.data.refinance.RefinanceInfoData;
import ru.simplgroupp.rest.api.data.refinance.SmsRequestData;
import ru.simplgroupp.rest.api.data.refinance.SmsResponseData;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.util.CalcUtils;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RequestScoped
public class RefinanceCreditService {
    private static final Logger logger = LoggerFactory.getLogger(RefinanceCreditService.class);

    @Inject private AiService aiSrv;
    @Inject private OverviewService ovSrv;
    @Inject private UserService userSrv;
    @Inject private CreditBeanLocal creditBean;
    @Inject private PeopleBeanLocal peopleBean;
    @Inject private PeopleDAO peopleDAO;
    @Inject private OfficialDocumentsDAO documentsDAO;
    @Inject private ProductBeanLocal productBean;
    @Inject private KassaBeanLocal kassa;
    @Inject private MailBeanLocal mailBean;
    @Inject private RefinanceCreditCacheService cache;
    @Inject private AppServiceBeanLocal appServ;
    @Inject private WorkflowBeanLocal workflow;

    private Credit credit;
    private PeopleMain people;
    private Map<String, Object> limits;
    private Refinance refinance;
    private Double sumMin;
    private Double sumMax;
    private Double sumAll;
    private Double stake;
    private Date dataend;
    protected Double sumMain;
    protected Double sumPercent;
    protected Double sumBack;
    /**
     * оферта
     */
    protected OfficialDocuments document;
    
    /**
     * Метод инициализации. Должен быть вызван перед началом использования сервиса. В CDI контексте вызывается
     * автоматически при инстанциации объекта контейнером.
     * @throws KassaException
     */
    @PostConstruct
    public void init() throws KassaException {
        ovSrv.init();
        if (this.userSrv.getUser() != null) {
            reloadCredit();
        }
    }

    /**
     * Обновляет данные по текущему кредиту пользователя.
     * @throws KassaException
     */
    private void reloadCredit() throws KassaException {
        int peopleId = this.userSrv.getUser().getPeopleMain().getId();

        credit = creditBean.findCreditActive(peopleId,
                Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST, BaseCredit.Options.INIT_REFINANCES));
        people = peopleDAO.getPeopleMain(peopleId, Utils.setOf(PeopleMain.Options.INIT_ACCOUNTACTIVE,
                PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_PEOPLE_PERSONAL,
                PeopleMain.Options.INIT_DOCUMENT, PeopleMain.Options.INIT_ADDRESS,PeopleMain.Options.INIT_PEOPLE_MISC));

        limits=productBean.getRefinanceProductConfig(credit.getProduct().getId());

        try{
            refinance=creditBean.initRefinance(credit, limits);
        } catch (Exception e){
            refinance=null;
        }
        sumMin=refinance.getRefinanceAmount();
        sumMax=credit.getCreditSumBack();

        sumAll=credit.getCreditSumBack();

        stake=refinance.getRefinanceStake();
        dataend= DateUtils.addDays(refinance.getRefinanceDate(), refinance.getRefinanceDays());
        recalc();
        document=peopleBean.initOfficialDocument(people, credit.getCreditRequest().getId(),
	    		credit.getId(),OfficialDocuments.OFERTA_REFINANCE);
        generateAgreement(refinance.getRefinanceDate());
    }

    /**
     * Обновляет параметры текущего кредита пользователя.
     */
    private void recalc(){
        sumMain=sumAll-refinance.getRefinanceAmount();
        sumPercent= CalcUtils.roundFloor(CalcUtils.calcSumPercentSimple(sumMain, refinance.getRefinanceDays(),
                false, refinance.getRefinanceStake()), 0);
        sumBack=sumMain+sumPercent;
        logger.info("Сумма процентов "+sumPercent+", сумма возврата "+sumBack);
    }

    /**
     * Генерирует пользовательскую оферту для рефинансирования кредита.
     * @param dateOferta Дата оферты.
     * @throws KassaException
     */
    private void generateAgreement(Date dateOferta) throws KassaException {
        String agreement = null;
        try {
            agreement = kassa.generateAgreement(people,refinance, sumMain,
                    credit.getCreditRequest().getDateSign(),new Date());
            refinance.setAgreement(agreement);
            document.setDocText(agreement);
        } catch (Exception ex) {
            agreement = ex.getMessage();
        }

    }

    /**
     * Формирует объект для заполнения страницы рефинансирования кредита.
     * @return объект с данными для страницы.
     */
    public RefinanceInfoData buildInfo() {
        RefinanceInfoData info = new RefinanceInfoData();

        info.setCanRefinance(aiSrv.getCanRefinance());
        info.setCanRefinanceRun(aiSrv.getCanRefinanceRun());
        info.setMsgCanRefinance(aiSrv.getMsgCanRefinance());
        info.setMsgCanRefinanceRun(aiSrv.getMsgCanRefinanceRun());
        info.setRefinanceAmount(refinance.getRefinanceAmount());
        info.setSumAll(sumAll);
        info.setSumPercent(sumPercent);
        info.setSumBack(sumBack);
        info.setDataend(new SimpleDateFormat("dd.MM.yyyy").format(dataend));
        info.setSumMin(sumMin);
        info.setSumMax(sumMax);
        info.setStake(stake == null ? 0 : stake);
        info.setRefinanceDays(refinance.getRefinanceDays());
        info.setAgreement(refinance.getAgreement());

        return info;
    }

    public SmsResponseData recalcAgreement(SmsRequestData smsRequest) {
        String msg = null;
        try {
            try {
                refinance.setRefinanceAmount(smsRequest.getRefinanceAmount());
                recalc();
            } catch (NumberFormatException e) {
                msg = "Ошибка преобразования ";
            }

            if (msg == null) {
                generateAgreement(refinance.getRefinanceDate());
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
            smsResponse.setAgreement(this.refinance.getAgreement());
        }

        return smsResponse;
    }

    /**
     * Отправить SMS код пользователю.
     * @param smsRequest объект с данными для формирования новых параметров кредита и отправки SMS кода.
     * @return объект с результатами отправки SMS кода.
     */
    public SmsResponseData sendCodeLsn(SmsRequestData smsRequest) {
        String msg = null;
        String msisdn = null;
        try {

            try {
                refinance.setRefinanceAmount(smsRequest.getRefinanceAmount());
                recalc();
            } catch (NumberFormatException e) {
                msg = "Ошибка преобразования ";
            }

            if (msg == null) {
                generateAgreement(refinance.getRefinanceDate());
                String smsCode = mailBean.generateCodeForSending();
                //String smsCode = GenUtils.genCode(6);
                this.cache.setSmsCode(smsCode);
                refinance.setSmsCode(smsCode);
                document.setSmsCode(smsCode);
                logger.info("Sms code " + smsCode);
                mailBean.sendSMSV2(people.getCellPhone().getValue(), "Код " + smsCode
                        + ". Это код для подписания оферты на рефинансирование займа в системе Онтайм.");
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
            smsResponse.setAgreement(this.refinance.getAgreement());
            smsResponse.setMsisdn(msisdn);
        }

        return smsResponse;
    }

    /**
     * Создание рефинансировани кредита.
     * @param smsRequest объект с новыми параметрами по кредиту.
     * @return объект с результатами процесса рефинансирования кредита.
     */
    public SmsResponseData save(SmsRequestData smsRequest) {
        String msg = null;
        if (StringUtils.isEmpty(smsRequest.getSmsCode())) {
            msg = "Необходимо ввести код СМС";
        }

        if (msg == null) {
            try {
                if (this.cache.getSmsCode().equals(smsRequest.getSmsCode())) {

                    try {
                        refinance.setRefinanceAmount(smsRequest.getRefinanceAmount());
                        recalc();
                    } catch (NumberFormatException e) {
                        msg = "Ошибка преобразования ";
                    }

                    if (msg == null) {
                    	refinance.setSmsCode(smsRequest.getSmsCode());
                        generateAgreement(refinance.getRefinanceDate());
                        document.setSmsCode(smsRequest.getSmsCode());
                        document.setDocNumber(refinance.getUniqueNomer());
                        msg = saveRefinanceRequest();
                    }
                } else {
                    msg = "Неверный код СМС";
                }

            } catch (KassaException e) {
                msg = e.getMessage();
            }
        }

        SmsResponseData smsResponse = new SmsResponseData();
        if (msg != null) {
            smsResponse.setSuccess(false);
            smsResponse.setMsg(msg);
        } else {
            smsResponse.setSuccess(true);
            smsResponse.setAgreement(this.refinance.getAgreement());
        }

        return smsResponse;
    }

    /**
     * Создание рефинансирования кредита.
     * @return сообщение об ошибке. null - успешно.
     */
    protected String saveRefinanceRequest() {
        ApplicationAction action = appServ.startApplicationAction(
                ProcessKeys.MSG_REFINANCE, true, "Рефинансирование",
                new BusinessObjectRef(Credit.class.getName(), credit.getId()),
                new BusinessObjectRef(CreditRequest.class.getName(), credit.getCreditRequest().getId())
        );
        if (action == null) {
            return "Рефинансирование уже запущено";

        }

        WorkflowObjectActionDef actionDef = new WorkflowObjectActionDef(false);
        actionDef.setBusinessObjectClass(Refinance.class.getName());
        actionDef.setSignalRef(ProcessKeys.MSG_REFINANCE_RUN);
        actionDef.setRunProcessDefKey(ProcessKeys.DEF_REFINANCE);

        try {
        	documentsDAO.saveOfficialDocument(document);
            RefinanceEntity refin = creditBean.saveRefinanceRequest(credit.getId(), refinance, refinance.getRefinanceDate());
            workflow.goProc(Refinance.class.getName(), refin.getId(), actionDef, SignalRef.toString("*", null, "msgNone"), Utils.mapOfSO("creditId", credit.getId() ));
        }catch (KassaException | WorkflowException ignored) {
        } finally {
            appServ.endApplicationAction(action);
        }

        return null;
    }
}
