package ru.simplgroupp.rest.api.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.dao.interfaces.ProductDAO;
import ru.simplgroupp.ejb.ApplicationAction;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.*;
//import ru.simplgroupp.interfaces.service.AppBeanLocal;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.persistence.ProductsEntity;
import ru.simplgroupp.rest.api.data.addcredit.AccountInfoData;
import ru.simplgroupp.rest.api.data.addcredit.NewCreditInfoData;
import ru.simplgroupp.rest.api.data.addcredit.SaveRequestData;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.GenUtils;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.util.ProductKeys;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Сервис отвечающий за работу страницы выдачи нового кредита
 */
@RequestScoped
public class NewCreditService {
    private static final Logger logger = LoggerFactory.getLogger(NewCreditService.class);

    private static final Set PEOPLE_OPTIONS = Utils.setOf(PeopleMain.Options.INIT_ACCOUNTACTIVE, PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_DOCUMENT, PeopleMain.Options.INIT_ADDRESS, PeopleMain.Options.INIT_PEOPLE_MISC);

    private HttpServletRequest request;
    private OverviewService os;
    private AiService aiSrv;
    private UserService userSrv;
    private KassaBeanLocal kassaBean;
    private ProductDAO productDAO;
    private ProductBeanLocal productBean;
    private PeopleDAO peopleDAO;
    private CreditCalculatorBeanLocal creditCalc;
    private ReferenceBooksLocal refBooks;
    private AppServiceBeanLocal appServ;
    private AppBeanService appBean;
   

    /**
     * Метод инициализации. Должен быть вызван перед началом использования сервиса. В CDI контексте вызывается
     * автоматически при инстанциации объекта контейнером.
     * @throws KassaException
     */
    @PostConstruct
    public void init() throws KassaException {
        this.os.init();
    }

    /**
     * Формирует объект для заполнения страницы выдачи нового кредита.
     * @return объект с данными для страницы.
     */
    public NewCreditInfoData buildInfo(Double summ, Integer days) throws KassaException {
        NewCreditInfoData info = new NewCreditInfoData();

        PeopleMain peopleMain = getPeopleMain();
        Map<String, Object> limits = getLimits();

        Map<String, Object> cparams = creditCalc.calcCreditParams(peopleMain.getId(), limits);
        Double sumMin= Convertor.toDouble(cparams.get(ProductKeys.CREDIT_SUM_MIN));
        Double sumMax=Convertor.toDouble(cparams.get(ProductKeys.CREDIT_SUM_MAX));

        info.setCreditDaysMin(Convertor.toInteger(cparams.get(ProductKeys.CREDIT_DAYS_MIN)));
        info.setCreditDaysMax(Convertor.toInteger(cparams.get(ProductKeys.CREDIT_DAYS_MAX)));
        info.setCreditSumMin(sumMin.intValue());
        info.setCreditSumMax(sumMax.intValue());
        Double stakeMin = (Double) limits.get(ProductKeys.CREDIT_STAKE_MIN);
        Double stakeMax = (Double) limits.get(ProductKeys.CREDIT_STAKE_MAX);

        info.setStakeMin(stakeMin);
        info.setStakeMax(stakeMax);

        CreditRequest creditRequest = getCreditRequest(peopleMain, limits, summ, days);

        info.setCreditSum((creditRequest.getCreditSum() < sumMin ? sumMin : creditRequest.getCreditSum()).intValue());
        info.setCreditDays(creditRequest.getCreditDays());

        Double stake = creditCalc.calcInitialStake(creditRequest.getCreditSum(), creditRequest.getCreditDays(), limits);
        info.setStake(stake);

        List<AccountInfoData> accounts = new ArrayList<>();
        for(Account transfer : peopleMain.getAccountsActive()) {
            AccountInfoData accountInfo = new AccountInfoData();
            accountInfo.setId(transfer.getId());
            accountInfo.setDescription(transfer.getDescription());
            accounts.add(accountInfo);
        }
        info.setAccounts(accounts);
     
        info.setAllowed(this.aiSrv.getCanAdd());
        info.setDiscardMessage(this.aiSrv.getMsgCanAdd());
        Integer addDays=Convertor.toInteger(limits.get(ProductKeys.ADDITIONAL_DAYS_FOR_CALCULATE));
        if (addDays==null){
        	addDays=0;
        }
        info.setAdditionalDayPayment(addDays);

        return info;
    }

    /**
     * Данные по текущему пользователю.
     * @return объект с данными по текущему пользователю.
     */
    public PeopleMain getPeopleMain() {
        int peopleId = this.userSrv.getUser().getPeopleMain().getId();
        return peopleDAO.getPeopleMain(peopleId, PEOPLE_OPTIONS);
    }

    /**
     * Получить последний запрос на выдачу кредита.
     * @param peopleMain объект с данными по текущему пользователю.
     * @param limits ограничения на параметры выдаваемых кредитов.
     * @param summ сумма новго кредта.
     * @param days количество дней на которые будет сформирован новый кредит.
     * @return запрос на выдачу кредита.
     * @throws KassaException
     */
    public CreditRequest getCreditRequest(PeopleMain peopleMain, Map<String, Object> limits, Double summ, Integer days) throws KassaException {
        CreditRequest cr = kassaBean.findCreditRequestInProcess(peopleMain.getId(), PEOPLE_OPTIONS);

        if (cr == null) {
            //если не было редиректа
            if (summ == null){
                cr = kassaBean.initCreditRequestForPeople(peopleMain, new Date(), limits);
            } else {
                cr = kassaBean.initCreditRequestForPeople(peopleMain, new Date(), summ, days, limits);
            }
        }

        return cr;
    }

    /**
     * Получить параметры кредита для продукта по умолчанию.
     * @return параметры продукта по умолчанию.
     * @throws KassaException
     */
    public Map<String, Object> getLimits() throws KassaException {
        Map<String, Object> limits;
        if (productDAO.getProductDefault()!=null){
            limits = productBean.getNewRequestProductConfig(productDAO.getProductDefault().getId());
        } else {
            logger.error("В системе нет продукта по умолчанию");
            throw new KassaException("В системе нет продукта по умолчанию");
        }

        return limits;
    }

    /**
     * Создает запрос на выдачу кредита.
     * @param saveRequest объект с параметрами запрашиваемого кредита.
     * @throws Exception
     */
    public void save(SaveRequestData saveRequest) throws Exception {
        double summ = 0;
        int days = 0;

        CreditRequest creditRequest;
        Map<String, Object> limits;
        PeopleMain peopleMain;

        peopleMain = getPeopleMain();
        limits = getLimits();

        if (saveRequest.getCreditSum() != null) {
            summ = saveRequest.getCreditSum();
        }

        if (saveRequest.getCreditDays() != null) {
            days = saveRequest.getCreditDays();
        }

        creditRequest = getCreditRequest(peopleMain, limits, summ, days);
        Double stake = creditCalc.calcInitialStake(creditRequest.getCreditSum(), creditRequest.getCreditDays(), limits);
        creditRequest.setStake(stake);

        ProductsEntity product = productDAO.getProductDefault();
        creditRequest.setProduct(new Products(product));

        if (saveRequest.getAccountId() != null) {
            for (Account account : peopleMain.getAccountsActive()) {
                if (saveRequest.getAccountId().equals(account.getId())) {
                    creditRequest.setAccount(account);
                }
            }
        }

        //пишем данные в заявку
        creditRequest.setCreditDaysInitial(creditRequest.getCreditDays());
        creditRequest.setCreditSumInitial(creditRequest.getCreditSum());
        creditRequest.setDateStatus(new Date());
        creditRequest.setDateContest(new Date());
        creditRequest.setDateFill(new Date());
        creditRequest.setStatus(new CreditStatus(this.refBooks.getCreditRequestStatus(CreditStatus.FILLED)));
        creditRequest.setContestAsp(true);
        creditRequest.setContestPd(true);
        creditRequest.setContestCb(true);
        creditRequest.setContest(true);
        creditRequest.setConfirmed(true);
        creditRequest.setWay(new Reference(refBooks.findByCodeIntegerEntity(RefHeader.APPLICATION_WAY, RefCreditRequestWay.ONLINE)));
        Integer i=kassaBean.findMaxCreditRequestNumber(new Date());
        creditRequest.setNomer(i);
        creditRequest.setUniqueNomer(GenUtils.genUniqueNumber(new Date(), i, creditRequest.getStake() * 100));
        generateAgreement(creditRequest);



        Map<String, String> peopleBehaviorData = new HashMap<String, String>();
            String ga_visitor_id = saveRequest.getBehavior().get("ga_visitor_id");
            String first_vizit_date = saveRequest.getBehavior().get("first_vizit_date");
            String visit_count = saveRequest.getBehavior().get("visit_count");
            String source_from = saveRequest.getBehavior().get("source_from");

            logger.info("Новая кредитная заявка параметры поведения пользователя"+ga_visitor_id+ first_vizit_date+
                    visit_count+ source_from);

        logger.info("Новая кредитная заявка от человека "+peopleMain.getId());

        //сохранили данные
        ApplicationAction action = appServ.startApplicationAction(
                SignalRef.toString(null, null, ProcessKeys.MSG_ADD_CREDIT_REQUEST), true, "Выдача нового займа",
                new BusinessObjectRef(CreditRequest.class.getName(), creditRequest.getId())
        );
        if (action == null) {
            throw new Exception("Выдача нового займа уже происходит");
        }
        String ipAddr = getRealIp(request);
        String userAgent = Convertor.toLimitString(request.getHeader("user-agent"),200);
        appBean.newCredit(creditRequest,  ipAddr, userAgent, this.userSrv.getUser().getId(), action, peopleBehaviorData);
    }

    /**
     * Генерирует новую пользовательскую оферту.
     * @param creditRequest запрос на выдачу кредита для которого генерируется оферта.
     * @throws KassaException
     */
    private void generateAgreement(CreditRequest creditRequest) throws KassaException {
        String agreement = this.kassaBean.generateAgreement(creditRequest, creditRequest.getDateContest(),0);
        creditRequest.setAgreement(agreement);
     }

    /**
     * Возвращает IP адрес с которого пришел HTTP запрос.
     * @param request http запрос.
     * @return
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public NewCreditService() {}

    @Inject
    public NewCreditService(OverviewService os, AppBeanService appBean, HttpServletRequest request, AppServiceBeanLocal appServ, AiService aiSrv, UserService userSrv, KassaBeanLocal kassaBean, ProductDAO productDAO, ProductBeanLocal productBean, PeopleDAO peopleDAO, CreditCalculatorBeanLocal creditCalc, ReferenceBooksLocal refBooks) {
        
        this.os = os;
        this.appBean = appBean;
        this.request = request;
        this.appServ = appServ;
        this.aiSrv = aiSrv;
        this.userSrv = userSrv;
        this.kassaBean = kassaBean;
        this.productDAO = productDAO;
        this.productBean = productBean;
        this.peopleDAO = peopleDAO;
        this.creditCalc =creditCalc;
        this.refBooks = refBooks;
    }
}
