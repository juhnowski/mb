package ru.simplgroupp.rest.api.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.AccountDAO;
import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.dao.interfaces.OfficialDocumentsDAO;
import ru.simplgroupp.ejb.ApplicationAction;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.persistence.AccountEntity;
import ru.simplgroupp.persistence.CreditRequestEntity;
import ru.simplgroupp.persistence.OfficialDocumentsEntity;
import ru.simplgroupp.persistence.ProlongEntity;
import ru.simplgroupp.persistence.RefinanceEntity;
import ru.simplgroupp.rest.api.data.BankData;
import ru.simplgroupp.rest.api.data.OverviewData;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;
import ru.simplgroupp.util.HtmlUtils;
import ru.simplgroupp.util.XmlUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Сервис обеспечивающий работу главной страницы личного кабинета.
 */
@RequestScoped
public class OverviewService implements Serializable {
    private static final long serialVersionUID = 8684624056159965148L;

    private static final Logger logger = LoggerFactory.getLogger(OverviewService.class);
    /**
     * последняя отказанная заявка
     */
    protected CreditRequest lastRefusedCreditRequest;
    @EJB
    CreditRequestDAO crDAO;
    @Inject
    private OverviewCacheService cache;
    @EJB
    private KassaBeanLocal kassaBean;
    @EJB
    private MailBeanLocal mailBean;
    @EJB
    private WorkflowBeanLocal workflow;
    @EJB
    private AppBeanService appBean;
    @EJB
    private CreditCalculatorBeanLocal creditCalc;
    @EJB
    private AppServiceBeanLocal appServ;
    @EJB
    private EventLogService eventLog;
    @EJB
    private CreditDAO creditDAO;
    @EJB
    private AccountDAO accountDAO;
    @EJB
    private ReferenceBooksLocal refBean;
    @EJB
    private PeopleBeanLocal peopleBean;
    @EJB
	OfficialDocumentsDAO officialDocumentsDAO;
    @EJB
    private CreditBeanLocal creditBean;
    @EJB
    private UsersBeanLocal userBean;
    @EJB
    private KassaBeanLocal kassa;
    @EJB
    private PaymentService payment;
    protected List<Integer> firstReqPaySystems= new ArrayList<Integer>(0);
    protected Integer firstRequestPaySysTimes = null;
    private Credit credit;
    /**
     * кредитная заявка
     */
    private CreditRequest creditRequest;
    /**
     * последняя кредитная заявка, нужна в основном для отказанных
     */
    private CreditRequest lastCreditRequest;
    /**
     * выплаченная сумма по кредиту
     */
    private Double paySum;
    /**
     * есть ли продление у кредита
     */
    private boolean hasProlongDraft;
    /**
     * есть ли рефинансирование у кредита
     */
    private boolean hasRefinanceDraft;
    /**
     * есть ли просрочка у кредита
     */
    private boolean isOverdue;
    /**
     * сумма процентов для продления
     */
    private Double percentSum;
    /**
     * сумма для рефинансирования
     */
    private Double refinanceSum;
    /**
	 * документ с офертой
	 */
	protected OfficialDocuments document;
	
    @Inject
    private UserService userServ;
    @Inject
    private HttpServletRequest request;
    @Inject
    private AiService ai;

    /**
     * Метод инициализации. Вызывается автоматически.
     *
     * @throws KassaException
     */
    @PostConstruct
    public void init() throws KassaException {
        if (this.userServ.getUser() != null) {
            reloadCredit();
        }
    }

    /**
     * формирует данные для заполнения главной страницы ЛК
     *
     * @return Данные для заполнения страницы.
     * @throws KassaException
     */
    public OverviewData reloadCredit() throws KassaException {

        SimpleDateFormat timeDf = new SimpleDateFormat("HH:mm");

        Users user = userServ.getUser();

        OverviewData data = new OverviewData();

        if (user != null) {

            percentSum = new Double(0);
            credit = creditBean.findCreditActive(userServ.getUser().getPeopleMain().getId(), Utils.setOf(BaseCredit.Options.INIT_PROLONGS, BaseCredit.Options.INIT_REFINANCES, BaseCredit.Options.INIT_PAYMENTS));
            lastRefusedCreditRequest = kassaBean.findCreditRequestRejected(userServ.getUser().getPeopleMain().getId(), null);
            if (credit == null) {
                creditRequest = kassa.findCreditRequestActive(userServ.getUser().getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_DOCUMENT, PeopleMain.Options.INIT_ADDRESS, PeopleMain.Options.INIT_PEOPLE_MISC));
                if (creditRequest == null) {
                    creditRequest = kassa.findCreditRequestWaitingSign(userServ.getUser().getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_DOCUMENT, PeopleMain.Options.INIT_ADDRESS, PeopleMain.Options.INIT_PEOPLE_MISC));
                    //если есть заявка для подписания
					if (creditRequest!=null){
					 
						      document=peopleBean.initOfficialDocument(userServ.getUser().getPeopleMain(),
						    		  creditRequest.getId(),null, OfficialDocuments.OFERTA_CREDIT);
						      creditRequest.setDateSign(new Date());
						      generateAgreement(new Date());
						   
					}
                }
                if (creditRequest == null) {
                    lastCreditRequest = kassa.findLastCreditRequestClosed(userServ.getUser().getPeopleMain().getId(), Utils.setOf());
                }
                paySum = new Double(0);

            } else {
                creditRequest = crDAO.getCreditRequest(credit.getEntity().getCreditRequestId().getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_DOCUMENT, PeopleMain.Options.INIT_ADDRESS, PeopleMain.Options.INIT_PEOPLE_MISC));
                paySum = payment.getCreditPaymentSum(credit.getId());
                ProlongEntity prolong = creditBean.findProlongDraft(credit.getId());
                hasProlongDraft = (prolong != null);
                if (prolong != null) {
                    Map<String, Object> crparams = creditCalc.calcCredit(credit.getId(), prolong.getLongdate());
                    percentSum = Convertor.toDouble(crparams.get(CreditCalculatorBeanLocal.SUM_PERCENT));
                }
                RefinanceEntity refinance = creditDAO.findRefinanceDraft(credit.getId());
                hasRefinanceDraft = (refinance != null);
                if (refinance != null) {
                    refinanceSum = refinance.getRefinanceAmount();
                }
                isOverdue = (credit.isOverdue(new Date()));
            }
            if (creditRequest!=null){
                firstReqPaySystems=payment.getFirstRequestPaymentSystems(creditRequest.getId());
                firstRequestPaySysTimes = payment.getFirstRequestPaySysTimes(creditRequest.getId());
            }
            else {
                firstReqPaySystems=payment.getFirstRequestPaymentSystems();
                firstRequestPaySysTimes = payment.getFirstRequestPaySysTimes();
            }
            ai.init();

            data.setCanRefinance(ai.getCanRefinance());
            data.setCanProlong(ai.getCanProlong());
            data.setSumIdPay(ai.getSumIdPay());
            data.setCanSign(ai.getCanSign());
            if (credit != null) {
                data.setHasCredit(true);
                data.setCredit_creditAccount(credit.getCreditAccount());
                data.setCredit_creditDataBeg(credit.getCreditDataBeg());
                data.setCredit_creditDataEnd(credit.getCreditDataEnd());
                data.setCredit_creditPercent(credit.getCreditPercent());
                data.setCredit_creditSum(credit.getCreditSum());
                data.setCredit_creditSumBack(credit.getCreditSumBack());
                data.setCredit_isActive(credit.getIsActive());
                data.setCredit_id(credit.getId());
            } else
                data.setHasCredit(false);

            if (creditRequest != null) {
                data.setHasCreditRequest(true);
                data.setCreditRequest_agreement(creditRequest.getAgreement());
                data.setCreditRequest_creditSum(creditRequest.getCreditSum());
                data.setCreditRequest_dateContest(creditRequest.getDateContest());
                data.setCreditRequest_dateContestTime(creditRequest.getDateContest() != null ? timeDf.format(creditRequest.getDateContest()) : "");
                data.setCreditRequest_status(creditRequest.getStatus().getName());
                data.setCreditRequest_uniqueNomer(creditRequest.getUniqueNomer());
                data.setCreditRequest_accepted(creditRequest.getAccepted());
                data.setCreditRequest_smsCode(creditRequest.getSmsCode());
            } else
                data.setHasCreditRequest(false);

            if (lastCreditRequest != null) {
                data.setHasLastCreditRequest(true);
                data.setLastCreditRequest_dateContest(lastCreditRequest.getDateContest());
                data.setLastCreditRequest_dateContestTime(lastCreditRequest.getDateContest() != null ? timeDf.format(lastCreditRequest.getDateContest()) : "");
                data.setLastCreditRequest_status(lastCreditRequest.getStatus().getName());
                data.setLastCreditRequest_uniqueNomer(lastCreditRequest.getUniqueNomer());
            } else
                data.setHasLastCreditRequest(false);

            data.setPaySum(paySum);
            data.setHasProlongDraft(hasProlongDraft);
            data.setHasRefinanceDraft(hasRefinanceDraft);
            data.setOverdue(isOverdue);
            data.setPercentSum(percentSum);
            data.setRefinanceSum(refinanceSum);
        }
        return data;
    }

    private void startCredit() throws KassaException, WorkflowException {
        ApplicationAction action = appServ.startApplicationAction(
                SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST, null, ProcessKeys.MSG_ACCEPT), true, "Подписание оферты",
                new BusinessObjectRef(CreditRequest.class.getName(), creditRequest.getId())
        );
        if (action == null) {
            throw new KassaException("Подписание оферты уже обрабатывается");
        }

        appBean.startCredit(creditRequest.getId(), action);
    }

    /**
     * Отмена кредита
     *
     * @throws KassaException
     * @throws WorkflowException
     */
    public void refuse() throws KassaException, WorkflowException {
        Users user = userServ.getUser();
        ApplicationAction action = appServ.startApplicationAction(
                SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST, null, ProcessKeys.MSG_REJECT), true, "Отказ от оферты",
                new BusinessObjectRef(CreditRequest.class.getName(), creditRequest.getId())
        );
        if (action == null) {
            throw new KassaException("Отказ от оферты уже обрабатывается");
        }

        appBean.saveClientRefuse(creditRequest.getId(), user.getId(), action);
    }

    /**
     * Отправка SMS пользователю
     *
     * @throws KassaException
     */
    public String sendCodeLsn(Account account) throws KassaException {
        try {
        	String smsCode = mailBean.generateCodeForSending();
         //   String smsCode = GenUtils.genCode(6);
            this.cache.setSmsCode(smsCode);
            creditRequest.setSmsCode(smsCode);
            creditRequest.setDateSign(new Date());
            document.setSmsCode(smsCode);
            logger.info("Sms code " + smsCode);
            String phoneNumber = creditRequest.getPeopleMain().getCellPhone().getValue();
            mailBean.sendSMSV2(phoneNumber, "Код " + smsCode + ". Это код для подписания оферты на предоставление займа в системе Ontime.");

            // сохраняем счет, так как иногда есть ошибки если счет сохраняется в save()
            if (account != null) {
                Account newAccount;
                if (account.getId() == null) {
                    newAccount = peopleBean.addAccount(account, creditRequest.getPeopleMain().getId());
                } else {
                    newAccount = account;
                }

                logger.info("Id account " + newAccount.getId());
                creditRequest = saveAccount(newAccount);
            }

            return phoneNumber;
        } catch (Exception ex) {
            throw new KassaException("Не удалось отправить смс. ");
        }
    }

    /**
     * Соглашение пользователя с условиями кредита
     *
     * @param confirmSmsCode sms код подтвержения от пользователя
     * @param account данные о способе оплаты
     * @return текст ошибки. Если null - выполнено без ошибок.
     */
    public String save(String confirmSmsCode, Account account) {
        if (StringUtils.isEmpty(confirmSmsCode)) {
            return "Необходимо ввести код СМС";
        }

        if (!confirmSmsCode.equals(this.cache.getSmsCode())) {
            return "Неверный код СМС";
        }


        try {
            if (account != null) {
                Account newAccount;
                if (account.getId() == null) {
                    newAccount = peopleBean.addAccount(account, creditRequest.getPeopleMain().getId());
                } else {
                    newAccount = account;
                }

                logger.info("Id account " + newAccount.getId());

                creditRequest = saveAccount(newAccount);
            }

            creditRequest.setSmsCode(confirmSmsCode);
            creditRequest.setDateSign(new Date());
            Map<String, Object> crparams = creditCalc.calcCreditInitial(creditRequest);
            creditRequest.setCreditDays(Convertor.toInteger(crparams.get(CreditCalculatorBeanLocal.DAYS_ACTUAL)));
           
            document.setSmsCode(confirmSmsCode);
            document.setDocNumber(creditRequest.getUniqueNomer());
            document.setDocDate(new Date());
            generateAgreement(new Date());
            OfficialDocumentsEntity doc=appBean.saveDocuments(document);
            	
            logger.info("Документ id "+doc.getId());
           // creditRequest=appBean.saveCreditRequest(creditRequest);
          
            kassaBean.saveCreditRequest(creditRequest);
            logger.info("Account id "+creditRequest.getAccount());
                  
           // startCredit();
            appBean.startCreditNew(creditRequest.getId());
            
        } catch (KassaException e) {
            return e.getMessage();
        } catch (WorkflowException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return e.getMessage();
        }

        return null;
    }

    /**
     * Генерация пользовательского соглашения
     *
     * @param dateOferta дата оферты
     * @throws KassaException
     */
    private void generateAgreement(Date dateOferta) throws KassaException {
        String agreement = kassaBean.generateAgreement(creditRequest, dateOferta, 1);
        document.setDocText(agreement);
        creditRequest.setAgreement(agreement);
      
    }

    /**
     * Формирует текстовое предстовление пользовательского соглашения для выгрузки клиенту
     *
     * @return пользовательское соглашение.
     */
    public String getAgreement() {
        String oferta="";
        if (document!=null){
           
            oferta=HtmlUtils.makeHtmlFromText(XmlUtils.ENCODING_UTF,document.getDocText());
            PeopleContact email = creditRequest.getPeopleMain().getEmail();
            if (email != null) {
                mailBean.send("Оферта в системе Онтайм по займу для ознакомления ", document.getDocText(), email.getValue());
            }
        }
        return oferta;
    }

    public Credit getCredit() {
        return this.credit;
    }

    public CreditRequest getCreditRequest() {
        return this.creditRequest;
    }


    public CreditRequest getLastRefusedCreditRequest() {
        return lastRefusedCreditRequest;
    }

	public OfficialDocuments getDocument() {
		return document;
	}


	public void setDocument(OfficialDocuments document) {
		this.document = document;
	}
	
    /**
     * Генерирует {@link Account} для последующего сохранения
     *
     * @param value json который пришел с сервера
     * @return если все ок, возвращает {@link Account}, иначе null
     */
    public Account generateAccount(Map<String, String> value) {
        Account account;


        if (value.containsKey("isNew") && !Boolean.valueOf(value.get("isNew"))) {
            Integer accountId = new Integer(value.get("id"));
            account = new Account(accountDAO.getAccountEntity(accountId));
            return account;

        } else if (value.containsKey("type")) {
            if (value.get("type").equals("contactSys")) {
                AccountEntity ent = new AccountEntity();
                Reference accType = refBean.getAccountType(Account.CONTACT_TYPE);
                ent.setAccountTypeId(accType.getEntity());
                account = new Account(ent);

                account.setAccountType(accType);
            } 
			else if (value.get("type").equals("unistream")) {
	                AccountEntity ent = new AccountEntity();
	                Reference accType = refBean.getAccountType(Account.UNISTREAM_TYPE);
	                ent.setAccountTypeId(accType.getEntity());
	                account = new Account(ent);
	
	                account.setAccountType(accType);
	        }
	        else if (value.get("type").equals("golden_crown")) {
                AccountEntity ent = new AccountEntity();
                Reference accType = refBean.getAccountType(Account.GOLDCORONA_TYPE);
                ent.setAccountTypeId(accType.getEntity());
                account = new Account(ent);

                account.setAccountType(accType);
	        }
			else if (value.get("type").equals("account")) {
                AccountEntity ent = new AccountEntity();
                Reference accType = refBean.getAccountType(Account.BANK_TYPE);
                ent.setAccountTypeId(accType.getEntity());
                account = new Account(ent);

                account.setAccountType(accType);
                account.setAccountNumber(value.get("accountnumber"));
                account.setBik(value.get("bik"));
                account.setCorrAccountNumber(value.get("corraccountnumber"));
                account.setBankName(value.get("bankname"));
            } else if (value.get("type").equals("qiwi")) {
                AccountEntity ent = new AccountEntity();
                Reference accType = refBean.getAccountType(Account.QIWI_TYPE);
                ent.setAccountTypeId(accType.getEntity());
                account = new Account(ent);

                account.setAccountType(accType);
                account.setAccountNumber(value.get("accountnumber"));
            } else if (value.get("type").equals("yandex")) {
                AccountEntity ent = new AccountEntity();
                Reference accType = refBean.getAccountType(Account.YANDEX_TYPE);
                ent.setAccountTypeId(accType.getEntity());
                account = new Account(ent);

                account.setAccountType(accType);
                account.setAccountNumber(value.get("accountnumber"));
            } else if (value.get("type").equals("card")) {
                AccountEntity ent = new AccountEntity();
                Reference accType = refBean.getAccountType(Account.CARD_TYPE);
                ent.setAccountTypeId(accType.getEntity());
                account = new Account(ent);

                account.setAccountType(accType);
                account.setAccountNumber(value.get("accountnumber"));
            } else {
                return null;
            }

            account.setIsActive(ActiveStatus.ACTIVE);
            account.setDateAdd(new Date());
            account.setPeopleMain(creditRequest.getPeopleMain());

            return account;
        }

        return null;
    }

    public List<BankData> getBanksList(String term, Integer page, Integer pageSize) {
        List<BankData> banks = new ArrayList<>();

        for (Bank bank : refBean.getBanksList(term, page, pageSize)) {
            BankData bankData = new BankData();
            bankData.setName(bank.getName());
            bankData.setBik(bank.getBik());
            bankData.setCorAccount(bank.getCorAccount());

            banks.add(bankData);
        }

        return banks;
    }

    private CreditRequest saveAccount(Account newAccount) {
        creditRequest.setAccount(newAccount);
        try {
        	CreditRequestEntity entity=kassaBean.saveCreditRequestWithAccount(creditRequest);
           	logger.info("Credit request entity account after save account "+entity.getAccountId());
        	creditRequest = crDAO.getCreditRequest(entity.getId(), Utils.setOf(
                    PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_PEOPLE_PERSONAL,
                    PeopleMain.Options.INIT_DOCUMENT, PeopleMain.Options.INIT_ADDRESS,
                    PeopleMain.Options.INIT_PEOPLE_MISC));
            logger.info("Credit request account after save account "+creditRequest.getAccount());
        } catch (Exception e) {
            logger.error("Не удалось сохранить счет " + e);
        }
        return creditRequest;
    }
    
   
    
    public List<Integer> activePaymentSystems() {
        List<Integer> codeIntegers = new ArrayList<Integer>();

        for (Reference ref : refBean.getAccountTypes()) {
        	if(ref.getIsActive() == ActiveStatus.ACTIVE)
        		codeIntegers.add(ref.getCodeInteger());
        }

        return codeIntegers;
    }
    public List<Integer> getFirstReqPaySystems() {
        return firstReqPaySystems;
    }
    public Boolean isFirstRequest() {
        return userServ.getUser().getPeopleMain().getClosedCreditsCount() < firstRequestPaySysTimes;
    }

}
