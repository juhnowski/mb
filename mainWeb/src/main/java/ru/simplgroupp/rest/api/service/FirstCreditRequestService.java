package ru.simplgroupp.rest.api.service;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.*;
import ru.simplgroupp.ejb.ApplicationAction;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.fias.ejb.IFIASService;
import ru.simplgroupp.fias.persistence.AddrObj;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.interfaces.service.OrganizationService;
import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.persistence.*;
import ru.simplgroupp.rest.api.data.EmploymentData;
import ru.simplgroupp.rest.api.data.SocialData;
import ru.simplgroupp.rest.api.data.firstcreditrequest.*;
import ru.simplgroupp.servlet.RequestCache;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.GenUtils;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.util.*;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

import static ru.simplgroupp.rest.api.FiasController.AddressRest;

@RequestScoped
public class FirstCreditRequestService {

    public final static long FILE_SIZE_LIMIT_MB = 5;
    public final static long FILE_SIZE_LIMIT_B = FILE_SIZE_LIMIT_MB * 1024 * 1024;
    public final static Set<String> ALLOWED_MIME = new HashSet<String>();
    public final static Set<String> ALLOWED_EXTENSIONS = new HashSet<String>();
    public static final int USER_WORK_COOKIE_MAX_AGE = 86400 * 7; // 1 неделя
    public final static String USER_WORK_COOKIE_NAME = "anketa-userWork";
    public final static String USER_WORK_DATA_STORAGE_TYPE = "anketa:userWork:data";
    public final static String USER_WORK_FILE_STORAGE_TYPE = "anketa:userWork:file";
    private static final Logger logger = LoggerFactory.getLogger(FirstCreditRequestService.class);

    static {
        Collections.addAll(ALLOWED_MIME,
                "image/photoshop", "image/x-photoshop", "image/psd", "application/photoshop", "application/psd", "zz-application/zz-winassoc-psd", // PSD
                "image/tiff", // TIFF
                "image/bmp", "image/x-bmp", "image/x-bitmap", "image/x-xbitmap", "image/x-win-bitmap", "image/x-windows-bmp", "image/ms-bmp", "image/x-ms-bmp", "application/bmp", "application/x-bmp", "application/x-win-bitmap", // BMP
                "image/jpeg", "image/jpg", "image/jpe_", "image/pjpeg", "image/vnd.swiftview-jpeg", // JPEG
                "image/gif", "image/x-xbitmap", "image/gi_", // GIF
                "application/postscript", "application/eps", "application/x-eps", "image/eps", "image/x-eps", // EPS
                "image/png", "application/png", "application/x-png", // PNG
                "application/pdf", "application/x-pdf", "application/acrobat", "applications/vnd.pdf", "text/pdf", "text/x-pdf", // PDF
                "application/pcx", "application/x-pcx", "image/pcx", "image/x-pc-paintbrush", "image/x-pcx", "zz-application/zz-winassoc-pcx", // PCX
                "image/ico", "image/x-icon", "application/ico", "application/x-ico", "application/x-win-bitmap", "image/x-win-bitmap", "application/octet-stream", // ICO
                "application/cdr", "application/coreldraw", "application/x-cdr", "application/x-coreldraw", "image/cdr", "image/x-cdr", "zz-application/zz-winassoc-cdr", // CDR
                "application/msword", "application/doc", "appl/text", "application/vnd.msword", "application/vnd.ms-word", "application/winword", "application/word", "application/x-msw6", "application/x-msword", // DOC
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"); // DOCX
        Collections.addAll(ALLOWED_EXTENSIONS, "psd", "tiff", "bmp", "jpeg", "gif", "eps", "png", "pict", "pdf", "pcx", "ico", "cdr", "ai", "raw", "doc", "docx", "pages");
    }

    @PersistenceContext(unitName = "MicroPU")
    private EntityManager emMicro;

    @EJB
    protected MailBeanLocal mailBean;
    @EJB
    protected ReferenceBooksLocal referenceBean;
    @EJB
    private KassaBeanLocal kassaBean;
    @EJB
    private RulesBeanLocal rulesBean;
    @EJB
    private CreditCalculatorBeanLocal creditCalc;
    @EJB
    private PeopleBeanLocal peopleBean;
    @EJB
    private CreditBeanLocal creditBean;
    @EJB
    private CreditRequestDAO crDAO;
    @EJB
    private ProductDAO prodDAO;
    @EJB
    private ProductBeanLocal productBean;
    @EJB
    private EventLogService eventLogService;
    @EJB
    private OrganizationService orgService;
    @EJB
    private PeopleDAO peopleDAO;
    @EJB
    private DocumentsDAO docsDAO;
    @EJB
    private UsersBeanLocal userBean;
    @EJB
    private AppServiceBeanLocal appServ;
    @EJB
    private AppBeanService appBean;
    @EJB
    private IFIASService fiasSrv;
    @EJB
    private TmpStorageDAO tmpStorageDAO;
    @EJB
    private AntifraudDAO antifraudDAO;

    @Inject
    private HttpServletRequest request;
    @Inject
    private HttpServletResponse response;

    @Inject
    private FirstCreditRequestCacheService cache;
    @Inject
    private RequestCache requestCache;


    public void saveRequest(FirstCreditRequestData data) throws Exception {
        // TODO: добавить всплывающее окно с повторным подтверждением
//        if (this.cache.getMsisdn() == null || !this.cache.getMsisdn().equals(data.getMainData().getPhone())) {
//            throw new KassaException("Не верный номер телефона");
//        }
//        if (this.cache.getMsisdnCode() == null || !this.cache.getMsisdnCode().equals(data.getMainData().getPhoneCode())) {
//            throw new KassaException("Не верный код подтверждения телефона");
//        }
//        if (this.cache.getEmail() == null || !this.cache.getEmail().equals(data.getMainData().getEmail())) {
//            throw new KassaException("Не верный адрес электронной почты");
//        }
//        if (this.cache.getEmailCode() == null || !this.cache.getEmailCode().equals(data.getMainData().getEmailCode())) {
//            throw new KassaException("Не верный код подтверждения электронной почты");
//        }

        if (data.getSumData().getProductId() == null) {
            data.getSumData().setProductId(prodDAO.getProductDefault().getId());
        }

        //заводим заявку и сохраняем условия кредита
        Integer idRequest = this.saveSumsData(data.getSumData(), data.getBehData(), data.getCreditPurpose());
        data.setRequestId(idRequest);


        saveSocialIds(idRequest);

        CreditRequestEntity creditRequestEntity = this.savePeopleMainData(idRequest, data.getMainData());
        antifraudDAO.updateFieldsRequestId(creditRequestEntity, request.getSession().getId());
        this.saveOtherCredit(data.getCrOtherData(), idRequest);
        // this.saveAccount(idRequest, data.getPayData());
        this.savePassport(data.getPassportData(), idRequest);
        this.saveGoogleAnalytics(data.getBehData(), idRequest);
        this.saveAddresses(data.getAddrData(), idRequest);
        this.saveFamilyData(data.getFamData(), idRequest);
        this.saveDopData(data.getDopDocsData(), idRequest);
        this.saveEmployment(data.getEmpData(), data.getJobAddrData(), idRequest);
        this.saveFriend(idRequest, data.getFriends());
        // UsersEntity user= this.saveUserData(idRequest,data.getBehData().getClientIp());
        PeopleContactEntity email = peopleBean.findPeopleByContactMan(peopleBean.findContactTypeForLogin(),
                creditRequestEntity.getPeopleMainId().getId());
        UsersEntity user = appBean.saveUser(creditRequestEntity, email.getValue(), data.getBehData().getClientIp());
        if (user != null) {
            ApplicationAction action = appServ.startApplicationAction(
                    SignalRef.toString(null, null, ProcessKeys.MSG_ADD_CREDIT_REQUEST), true, "Выдача нового займа",
                    new BusinessObjectRef(CreditRequest.class.getName(), idRequest)
            );
            appBean.newCredit(idRequest, user.getId(), action);
            this.cache.setAuthUsername(user.getUsername());
        } else {
            logger.error("User is null");
        }

        Cookie userWorkCookie = getRequestCookie(USER_WORK_COOKIE_NAME);
        this.tmpStorageDAO.delete(userWorkCookie.getValue());

        userWorkCookie.setValue("");
        userWorkCookie.setPath("/");
        userWorkCookie.setMaxAge(0);
        this.response.addCookie(userWorkCookie);

    }

    public void auth() throws ServletException {
        long authMaxDelay = 60;

        if (this.cache.getAuthTimeStart() != null && (new Date().getTime() - this.cache.getAuthTimeStart()) / 1000 < authMaxDelay) {
            UsersEntity user = this.userBean.findUserByLogin(this.cache.getAuthUsername());

            this.requestCache.logout();
            this.requestCache.login(user.getUsername(), user.getPassword(), false);
        }

        this.cache.setAuthUsername(null);
    }

    public String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public String getStakeProcedure() {
        ProductsEntity product = prodDAO.getProductDefault();
        return productBean.getFunctionStakeNewRequestUnknown(product.getId());
    }

    public SumData getRequestData(Double initialSum, Integer initialDays) {
        ProductsEntity product = prodDAO.getProductDefault();
        Map<String, Object> limits = productBean.getNewRequestProductConfig(product.getId());
        Double stakeMin = (Double) limits.get(ProductKeys.CREDIT_STAKE_MIN);
        Double stakeMax = (Double) limits.get(ProductKeys.CREDIT_STAKE_MAX);

        SumData sumData = new SumData();
        sumData.setProductId(product.getId());
        sumData.setStakeMin(stakeMin);
        sumData.setStakeMax(stakeMax);
        sumData.setCreditDaysMin(Convertor.toInteger(limits.get(ProductKeys.CREDIT_DAYS_MIN)));
        sumData.setCreditDaysMax(Convertor.toInteger(limits.get(ProductKeys.CREDIT_DAYS_MAX)));
        sumData.setCreditSumMin((Double) limits.get(ProductKeys.CREDIT_SUM_MIN));
        sumData.setCreditSumMax((Double) limits.get(ProductKeys.CREDIT_SUM_MAX_UNKNOWN));

        Double creditSum;
        if (initialSum != null && initialSum >= sumData.getCreditSumMin() && initialSum <= sumData.getCreditSumMax()) {
            creditSum = initialSum;
        } else {
            creditSum = sumData.getCreditSumMin();
        }
        Integer creditDays;
        if (initialDays != null && initialDays >= sumData.getCreditDaysMin() && initialDays <= sumData.getCreditDaysMax()) {
            creditDays = initialDays;
        } else {
            creditDays = sumData.getCreditDaysMin();
        }

        double stake = creditCalc.calcInitialStake(creditSum, creditDays, limits);

        sumData.setStake(stake);
        sumData.setCreditSum(creditSum);
        sumData.setCreditDays(creditDays);

        Integer addDays = Convertor.toInteger(limits.get(ProductKeys.ADDITIONAL_DAYS_FOR_CALCULATE));
        if (addDays == null) {
            addDays = 0;
        }
        sumData.setAdditionalDayPayment(addDays);

        return sumData;
    }

    public void saveFriend(Integer creditRequestId, List<PeopleFriendData> friends) throws KassaException {
        if (friends != null) {
            CreditRequestEntity creditRequest = crDAO.getCreditRequestEntity(creditRequestId);

            PeopleMainEntity people = creditRequest.getPeopleMainId();

            for (PeopleFriendData friend : friends) {
                boolean b = this.peopleBean.saveFriend(people.getId(), friend.getFirstName(),
                        friend.getLastName(), "", friend.getPhone(), friend.isCanCall(), PeopleFriend.NOT_FOR_BONUS);
                if (!b) {
                    throw new KassaException("Вы уже отправляли приглашение этому человеку");
                }
            }
        }
    }

    public Integer saveSumsData(SumData data, PeopleBehaviorData beh, String purpose) throws KassaException {

        CreditRequestEntity creditRequest = null;

        //константы для расчета ставки
        Map<String, Object> limits = productBean.getNewRequestProductConfig(data.getProductId());
        //считаем ставку
        double stake = creditCalc.calcInitialStake(data.getCreditSum(), data.getCreditDays(), limits);

        //пишем данные заявки
        creditRequest = kassaBean.newCreditRequest(creditRequest, creditRequest != null ? creditRequest.getPeopleMainId().getId() : null,
                CreditStatus.IN_PROCESS, null, null, null, null, null, Partner.SYSTEM,
                data.getProductId(), new Date(), null, new Date(), null, data.getCreditSum(), data.getCreditDays(),
                stake, null, null, null, null, purpose, RefCreditRequestWay.ONLINE,null);

        //ищем, был ли записан лог по данной заявке      
        EventLogEntity eventLog = eventLogService.findLog(creditRequest.getId(), creditRequest.getPeopleMainId().getId(), EventCode.NEW_CREDIT_REQUEST, null);

        //определим местоположение
        String geoPlace = kassaBean.saveGeoLocation(creditRequest, beh.getClientIp());
        //если это не Россия, дальше не пойдем
        if (StringUtils.isNotEmpty(geoPlace) && !geoPlace.toUpperCase().contains("RU") && !geoPlace.toUpperCase().contains("LOCALHOST")) {
            logger.error("Заход из другой страны " + geoPlace);
            throw new KassaException(ErrorKeys.ANOTHER_COUNTRY, "Взять заем можно только находясь в РФ");
        }

        //если не писали в лог, то запишем
        if (eventLog == null) {
            eventLogService.saveLog(beh.getClientIp(), eventLogService.getEventType(EventType.INFO).getEntity(),
                    eventLogService.getEventCode(EventCode.NEW_CREDIT_REQUEST).getEntity(),
                    "новая кредитная заявка на сумму " + data.getCreditSum(), creditRequest,
                    peopleDAO.getPeopleMainEntity(creditRequest.getPeopleMainId().getId()), null, null,
                    HTTPUtils.getShortBrowserName(beh.getUserAgent()), Convertor.toLimitString(beh.getUserAgent(), 200),
                    HTTPUtils.getUserOS(Convertor.toLimitString(beh.getUserAgent(), 50)), geoPlace);
        }

        //ищем, писали ли данные об устройстве
        DeviceInfoEntity deviceInfo = kassaBean.findDeviceInfo(creditRequest.getId());
        if (deviceInfo == null) {
            // сохраняем данные об устройстве, с которого подается заявка
            kassaBean.newDeviceInfo(creditRequest.getId(), beh.getResW(), beh.getResH(), beh.getUserAgent());
        }
        return creditRequest.getId();
    }

    public CreditRequestEntity savePeopleMainData(Integer creditRequestId, PeopleMainData data) throws KassaException {

        CreditRequestEntity creditRequest = crDAO.getCreditRequestEntity(creditRequestId);

        PeopleMainEntity people = creditRequest.getPeopleMainId();

        if (validateEmail(data.getEmail())) {
            try {
                peopleBean.setPeopleContactClient(people, PeopleContact.CONTACT_EMAIL, data.getEmail().toLowerCase(), false);
            } catch (Exception ex) {
                logger.error("Не удалось сохранить контакты " + ex);
                throw new KassaException(ex);
            }
        }
        ;

        if (validatePhone(data.getPhone())) {
            try {
                peopleBean.setPeopleContactClient(people, PeopleContact.CONTACT_CELL_PHONE,
                        Convertor.fromMask(data.getPhone()), false);

            } catch (Exception ex) {
                logger.error("Не удалось сохранить контакты " + ex);
                throw new KassaException(ex);
            }
        }

        if (validateContacts(data)) {
            //пишем персональные данные
            PeoplePersonalEntity peoplePersonal = peopleBean.findPeoplePersonalActive(people);
            try {
                peopleBean.newPeoplePersonal(peoplePersonal, people.getId(), null, Partner.CLIENT,
                        data.getLastName(), data.getFirstName(), data.getMiddleName(), null,
                        data.getBirthday(), data.getPlaceOfBith(), Convertor.toInteger(data.getGender()), new Date(), ActiveStatus.ACTIVE);
            } catch (Exception e) {
                logger.error("Не удалось сохранить персональные данные по человеку, ошибка " + e.getMessage());
            }
        }

        //запишем снилс и инн, если они есть
        try {
            peopleBean.savePeopleMain(people, data.getInn(), data.getSnils());
        } catch (PeopleException e2) {
            logger.error("Не удалось записать снилс и инн по человеку " + people.getId() + ", ошибка " + e2);
        }
        return creditRequest;
    }

    public boolean validateLocation(String ipAddress) throws KassaException {
        String location = this.kassaBean.getGeoLocation(ipAddress);
        return StringUtils.isNotEmpty(location) && (location.toUpperCase().contains("RU") || location.toUpperCase().contains("LOCALHOST"));
    }

    public boolean validateEmail(String email) throws KassaException {
        if (email == null) {
            return false;
        }
        PeopleContactEntity contactEmail = peopleBean.findPeopleByContactClient(PeopleContact.CONTACT_EMAIL, email);
        if (contactEmail != null) {
            PeopleMainEntity peopleWithEmail = contactEmail.getPeopleMainId();
            int cnt = kassaBean.findManWithPrivateCabinet(peopleWithEmail);
            //уже есть человек с такой почтой, дальше не пойдем
            if (cnt > 0) {
                logger.error("Email " + email + " зарегистрирован на другого пользователя");
                throw new KassaException(ErrorKeys.EMAIL_EXISTS, "Email " + email + " зарегистрирован на другого пользователя");
            }
        }
        return true;
    }

    public boolean validatePhone(String phone) throws KassaException {
        if (phone == null) {
            return false;
        }
        int cnt;
        PeopleContactEntity contactPhone = peopleBean.findPeopleByContactClient(PeopleContact.CONTACT_CELL_PHONE, Convertor.fromMask(phone));
        if (contactPhone != null) {
            PeopleMainEntity peopleWithPhone = contactPhone.getPeopleMainId();
            cnt = kassaBean.findManWithPrivateCabinet(peopleWithPhone);
            //уже есть человек с таким телефоном, дальше не пойдем
            if (cnt > 0) {
                logger.error("Телефон " + phone + " зарегистрирован на другого пользователя");
                throw new KassaException(ErrorKeys.PHONE_EXISTS, "Телефон " + phone + " зарегистрирован на другого пользователя");
            }
        }
        return true;
    }

    public boolean validateContacts(PeopleMainData mainData) throws KassaException {
        if (mainData.getFirstName() == null || mainData.getLastName() == null || mainData.getMiddleName() == null || mainData.getBirthday() == null) {
            return false;
        }

        /*int cnt;
        PeoplePersonalEntity existingPerson = peopleBean.findPeopleByPersonalData(mainData.getLastName(), mainData.getFirstName(), mainData.getMiddleName(), mainData.getBirthday());
        if (existingPerson != null) {
            PeopleMainEntity peopleMain = existingPerson.getPeopleMainId();
            cnt = kassaBean.findManWithPrivateCabinet(peopleMain);
            //уже есть человек с такими ФИО и ДР, дальше не пойдем
            if (cnt > 0) {
                logger.error("Человек " + mainData.getLastName() + " " + mainData.getFirstName() + " " + mainData.getMiddleName() + " уже существует в системе");
                throw new KassaException(ErrorKeys.PERSON_EXISTS, "Человек " + mainData.getLastName() + " " + mainData.getFirstName() + " " + mainData.getMiddleName() + " уже существует в системе");
            }
        }*/

        return true;
    }

    public void saveOtherCredit(PeopleCreditOtherBank data, Integer creditRequestId) throws KassaException {
        PartnersEntity parClient = referenceBean.getPartnerEntity(Partner.CLIENT);

        CreditRequestEntity ccRequest = crDAO.getCreditRequestEntity(Convertor.toInteger(creditRequestId));
        PeopleMainEntity peopleMain = ccRequest.getPeopleMainId();

        //сохраняем предыдущий кредит
        if (Convertor.toInteger(data.getPrevcredits()) > 0) {

            List<CreditEntity> lstcred = creditBean.findCredits(peopleMain, ccRequest, parClient, false, null);
            CreditEntity cred = null;
            if (lstcred.size() > 0) {
                cred = lstcred.get(0);
            }
            try {
                creditBean.newCredit(cred, peopleMain.getId(), ccRequest.getId(), Partner.CLIENT, false,
                        data.getOverdue(), data.getCreditOrganization(),
                        Convertor.toDouble(data.getCreditsumprev(), CalcUtils.dformat),
                        Convertor.toDate(data.getCreditdate(), DatesUtils.FORMAT_ddMMYYYY),
                        Utils.defaultBooleanFromIntegerNull(Convertor.toInteger(data.getCreditisover())),
                        Convertor.toInteger(data.getCredittype()), Convertor.toInteger(data.getCurrencytype()),
                        BaseCredit.CREDIT_OWNER, null,
                        Convertor.toDate(data.getCreditclosedate(), DatesUtils.FORMAT_ddMMYYYY),
                        Convertor.toDate(data.getFactcreditclosedate(), DatesUtils.FORMAT_ddMMYYYY),
                        Convertor.toDouble(data.getCreditSumMonth(), CalcUtils.dformat));
            } catch (Exception e) {
                logger.error("Не удалось сохранить кредит по заявке " + ccRequest.getId() + ", ошибка " + e);
                throw new KassaException("Не удалось сохранить кредит по заявке " + ccRequest.getId() + ", ошибка " + e);
            }

        } else {
            List<CreditEntity> lstcred = creditBean.findCredits(peopleMain, ccRequest, parClient, false, null);
            if (lstcred != null && lstcred.size() > 0) {
                CreditEntity cred = lstcred.get(0);
                creditBean.removeCredit(cred.getId());

            }
        }
    }

    public void saveAccount(Integer creditRequestId, PeoplePaymentData data) throws KassaException {

        CreditRequestEntity creditRequest = crDAO.getCreditRequestEntity(creditRequestId);
        PeopleMainEntity peopleMain = creditRequest.getPeopleMainId();

        //сохраняем счета
        AccountEntity account = peopleBean.findAccountActive(peopleMain, data.getOption());

        switch (data.getOption()) {
            case Account.BANK_TYPE: {

                try {
                    account = peopleBean.newAccount(account, peopleMain.getId(), new Date(),
                            null, null, data.getBankaccount(), Account.BANK_TYPE,
                            data.getBik(), data.getCorrespondentAccount(), null,
                            null, null, null, null, null, ActiveStatus.ACTIVE);
                } catch (Exception e) {
                    logger.error("Не удалось сохранить счет, ошибка " + e);
                }

           /*   AccountEntity payonlineAccount = peopleBean.findAccountActive(peopleMain, Account.PAYONLINE_CARD_TYPE);
                if (payonlineAccount != null) {
                    payonlineAccount.setIsactive(ActiveStatus.ACTIVE);
                }*/
            }

            break;
            case Account.CARD_TYPE: {

                try {
                    account = peopleBean.newAccount(account, peopleMain.getId(), new Date(),
                            Convertor.toInteger(data.getOption2()), data.getContest(), null, Account.CARD_TYPE,
                            null, null, data.getCardnomer(), null, null, null, null, null, ActiveStatus.ACTIVE);
                } catch (Exception e) {
                    logger.error("Не удалось сохранить счет, ошибка " + e);
                }
            }

            break;
            case Account.CONTACT_TYPE: {
                try {
                    account = peopleBean.newAccount(account, peopleMain.getId(), new Date(),
                            null, null, null, Account.CONTACT_TYPE,
                            null, null, null, null, null, null, null, null, ActiveStatus.ACTIVE);
                } catch (Exception e) {
                    logger.error("Не удалось сохранить счет, ошибка " + e);
                }
            }

            break;
            case Account.YANDEX_TYPE: {
                try {
                    account = peopleBean.newAccount(account, peopleMain.getId(), new Date(),
                            null, (1 == data.getAccept()), data.getYandexcardnomer(), Account.YANDEX_TYPE,
                            null, null, null, null, null, null, null, null, ActiveStatus.ACTIVE);
                } catch (Exception e) {
                    logger.error("Не удалось сохранить счет, ошибка " + e);
                }
            }
            break;

            case Account.QIWI_TYPE: {

                try {
                    account = peopleBean.newAccount(account, peopleMain.getId(), new Date(),
                            null, (1 == data.getAccept()), data.getQiwiPhone(), Account.QIWI_TYPE,
                            null, null, null, null, null, null, null, null, ActiveStatus.ACTIVE);
                } catch (Exception e) {
                    logger.error("Не удалось сохранить счет, ошибка " + e);
                }
            }

            break;
        }

        creditRequest.setAccountId(account);
        emMicro.merge(creditRequest);

    }

    public void savePassport(PeoplePassportData data, Integer reqId) throws KassaException {
        CreditRequestEntity ccRequest = crDAO.getCreditRequestEntity(reqId);

        PeopleMainEntity pMain = ccRequest.getPeopleMainId();

        //ищем, нет ли такого документа в БД
        validatePassport(data);

        //сохраняем документ
        DocumentEntity pasp = null;
        pasp = peopleBean.findPassportActive(pMain);
        try {
            peopleBean.newDocument(pasp, pMain.getId(), null, Partner.CLIENT, data.getSeria(),
                    data.getNomer(), Convertor.toDate(data.getDateGive(), DatesUtils.FORMAT_ddMMYYYY),
                    data.getKodByGive(), data.getByGive(), ActiveStatus.ACTIVE);
        } catch (Exception e2) {
            logger.error("Не удалось сохранить паспорт, ошибка " + e2.getMessage());
        }
    }

    public boolean validatePassport(PeoplePassportData passportData) throws KassaException {
        if (passportData == null || passportData.getNomer() == null || passportData.getSeria() == null) {
            return false;
        }

        DocumentEntity existingPasport = peopleBean.findDocument(Documents.PASSPORT_RF, passportData.getSeria(), passportData.getNomer());

        if (existingPasport != null) {
            PeopleMainEntity peopleMain = existingPasport.getPeopleMainId();
            int cnt = kassaBean.findManWithPrivateCabinet(peopleMain);
            if (cnt > 0) {
                logger.error("Человек с паспортом " + passportData.getSeria() + " " + passportData.getNomer() + " уже существует в системе");
                throw new KassaException(ErrorKeys.DOCUMENT_EXISTS, "Человек с паспортом " + passportData.getSeria() + " " + passportData.getNomer() + " уже существует в системе");
            }
        }

        return true;
    }

    public void saveGoogleAnalytics(PeopleBehaviorData data, Integer reqId) throws KassaException {

        //сохраняем веб аналитику
        CreditRequestEntity ccRequest = crDAO.getCreditRequestEntity(reqId);

        PeopleMainEntity pMain = ccRequest.getPeopleMainId();
        Date todaydate = new Date();
        if (StringUtils.isNotEmpty(data.getSource_from())) {
            peopleBean.savePeopleBehavior(reqId, pMain.getId(), "REFER.FROM", data.getSource_from(),
                    null, null, Partner.GOOGLEANALYTICS, todaydate);
        }

        if (Convertor.toLong(data.getVisit_count()) != null) {
            peopleBean.savePeopleBehavior(reqId, pMain.getId(), "HITS", null,
                    data.getVisit_count(), null, Partner.GOOGLEANALYTICS, todaydate);
        }

        if (Convertor.toLong(data.getGa_visitor_id()) != null) {
            peopleBean.savePeopleBehavior(reqId, pMain.getId(), "GA.ID", null,
                    data.getGa_visitor_id(), null, Partner.GOOGLEANALYTICS, todaydate);
        }


        peopleBean.savePeopleBehavior(reqId, pMain.getId(), "DATE.LAST", null, null,
                todaydate, Partner.GOOGLEANALYTICS, todaydate);


        if ((StringUtils.isNotEmpty(data.getFirst_vizit_date()))) {
            peopleBean.savePeopleBehavior(reqId, pMain.getId(), "DATE.FIRST", null, null,
                    Convertor.fromUnixTimestampToDate(data.getFirst_vizit_date()), Partner.GOOGLEANALYTICS, todaydate);
        }
    }

    public void saveAddresses(PeopleAddressData data, Integer reqId) throws KassaException {
        //save register address data
        CreditRequestEntity ccRequest = crDAO.getCreditRequestEntity(reqId);

        PeopleMainEntity pMain = ccRequest.getPeopleMainId();

        AddressEntity registerationAddress = peopleBean.findAddressActive(pMain, FiasAddress.REGISTER_ADDRESS);

        try {
            peopleBean.newAddressFias(registerationAddress, pMain.getId(), null, Partner.CLIENT, FiasAddress.REGISTER_ADDRESS,
                    null, new Date(), null, null, ActiveStatus.ACTIVE, data.getRegAddress().getFiasId(),
                    data.getRegAddress().getHome(), data.getRegAddress().getKorpus(),
                    data.getRegAddress().getBuilder(), data.getRegAddress().getApartment());
        } catch (Exception e1) {
            logger.error("Не удалось сохранить адрес, ошибка " + e1);
        }

        AddressEntity register_addr = null;

        //сохраняем информацию о совпадении адреса регистрации с адресом проживания
        register_addr = peopleBean.findAddressActive(pMain, FiasAddress.REGISTER_ADDRESS);
        Integer matuche = data.isRegIsLive() ? 1 : 0;
        register_addr.setIsSame(matuche);

        emMicro.persist(register_addr);

        //если адреса не совпадают, запишем новый адрес
        if (matuche != BaseAddress.IS_SAME) {
            AddressEntity residentAddress = peopleBean.findAddressActive(pMain, FiasAddress.RESIDENT_ADDRESS);

            try {
                peopleBean.newAddressFias(residentAddress, pMain.getId(), null, Partner.CLIENT, FiasAddress.RESIDENT_ADDRESS,
                        null, new Date(), null, null, ActiveStatus.ACTIVE, data.getLiveAddress().getFiasId(),
                        data.getLiveAddress().getHome(), data.getLiveAddress().getKorpus(),
                        data.getLiveAddress().getBuilder(), data.getLiveAddress().getApartment());
            } catch (Exception e1) {
                logger.error("Не удалось сохранить адрес проживания, ошибка " + e1);
            }


        } else {
            //если вводили адрес проживания, закроем его
            AddressEntity resident_addr = peopleBean.findAddressActive(pMain, FiasAddress.RESIDENT_ADDRESS);
            if (resident_addr != null) {
                peopleBean.closeAddress(resident_addr, new Date());
            }
        }

        //save people home phone
        if (StringUtils.isNotEmpty(data.getRegAddress().getPhone())) {
            try {
                peopleBean.setPeopleContactClient(pMain, PeopleContact.CONTACT_HOME_PHONE, Convertor.fromMask(data.getRegAddress().getPhone()), data.getRegAddress().getPhone() != null);
            } catch (Exception e) {
                logger.error("Не удалось сохранить домашний телефон " + e);
                throw new KassaException(e);
            }
        } else {
            PeopleContactEntity pc = peopleBean.findPeopleByContactMan(PeopleContact.CONTACT_HOME_PHONE, pMain.getId());
            if (pc != null) {
                try {
                    peopleBean.changeContactActive(pc.getId());
                } catch (PeopleException e) {
                    logger.error("Не удалось изменить домашний телефон " + e);
                    throw new KassaException(e);
                }
            }
        }

        // сохраняем телефон по месту проживания, если он есть конечно
        if (data.getLiveAddress() != null && StringUtils.isNotEmpty(data.getLiveAddress().getResidentPhone())) {
            try {
                peopleBean.setPeopleContactClient(pMain, PeopleContact.CONTACT_HOME_PHONE,
                        Convertor.fromMask(data.getLiveAddress().getResidentPhone()), true);
            } catch (Exception e) {
                logger.error("Не удалось сохранить дополнительный телефон " + e);
                throw new KassaException(e);
            }
        }

        // сохраняем дополнительный телефон
        if (data.getLiveAddress() != null && StringUtils.isNotEmpty(data.getLiveAddress().getPhone())) {
            try {
                peopleBean.setPeopleContactClient(pMain, PeopleContact.CONTACT_DOPPHONE1,
                        Convertor.fromMask(data.getLiveAddress().getPhone()), true);
            } catch (Exception e) {
                logger.error("Не удалось сохранить дополнительный телефон " + e);
                throw new KassaException(e);
            }
        } else {
            PeopleContactEntity pc = peopleBean.findPeopleByContactMan(PeopleContact.CONTACT_HOME_REGISTER_PHONE, pMain.getId());
            if (pc != null) {
                try {

                    peopleBean.changeContactActive(pc.getId());
                } catch (PeopleException e) {
                    logger.error("Не удалось изменить домашний телефон по месту регистрации " + e);
                    throw new KassaException(e);
                }
            }
        }

        //save misc data
        PeopleMiscEntity pMisc = peopleBean.findPeopleMiscActive(pMain);

        try {
            peopleBean.newPeopleMisc(pMisc, pMain.getId(), null, Partner.CLIENT,
                    null, pMisc != null ? pMisc.getChildren() : null, null, null,
                    Convertor.toDate(data.getLiveAddress().getRealtyDate(), DatesUtils.FORMAT_ddMMYYYY),
                    null, null,
                    data.getLiveRealty(), false, new Date(), ActiveStatus.ACTIVE, false);
        } catch (Exception e) {
            logger.error("Произошла ошибка при сохранении доп.данных, ошибка " + e);
        }
    }


    public void saveFamilyData(PeopleFamilyData data, Integer reqId) throws KassaException {

        CreditRequestEntity ccRequest = crDAO.getCreditRequestEntity(reqId);

        PeopleMainEntity pMain = ccRequest.getPeopleMainId();
        //сохраняем дополнительные данные
        PeopleMiscEntity pMisc = peopleBean.findPeopleMiscActive(pMain);

        try {
            peopleBean.newPeopleMisc(pMisc, pMain.getId(), null, Partner.CLIENT,
                    data.getFamStatus(), data.getChildCount(), null, null,
                    pMisc == null ? null : pMisc.getRealtyDate(), null,
                    null, null, false, new Date(), ActiveStatus.ACTIVE, false);
        } catch (Exception e) {
            logger.error("Произошла ошибка при сохранении доп.данных, ошибка " + e);
        }
    }

    public void saveDopData(PeopleDopDocsData data, Integer reqId) throws KassaException, IOException {

        if (data != null) {
            CreditRequestEntity ccRequest = crDAO.getCreditRequestEntity(reqId);

            PeopleMainEntity pMain = ccRequest.getPeopleMainId();
            //сохраняем дополнительные данные
            PeopleMiscEntity pMisc = peopleBean.findPeopleMiscActive(pMain);
            boolean car = data.isHasAuto();
            try {
                peopleBean.newPeopleMisc(pMisc, pMain.getId(), null, Partner.CLIENT,
                        null, pMisc.getChildren(), null, null, pMisc.getRealtyDate(), null, pMisc.getRegDate(),
                        null, car, new Date(), ActiveStatus.ACTIVE, data.isHasDriverDoc());
            } catch (Exception e) {
                logger.error("Произошла ошибка при сохранении доп.данных, ошибка " + e);
            }

            Cookie userWorkCookie = getRequestCookie(USER_WORK_COOKIE_NAME);
            Map<String, FileBean> filesCache = new HashMap<String, FileBean>();
            for (TmpStorageEntity fileEntity : this.tmpStorageDAO.readList(userWorkCookie.getValue(), USER_WORK_FILE_STORAGE_TYPE)) {
                if (fileEntity.getData() != null) {
                    ByteArrayInputStream byteIn = new ByteArrayInputStream(fileEntity.getData());
                    ObjectInputStream in = new ObjectInputStream(byteIn);
                    try {
                        FileBean fileBean = (FileBean) in.readObject();
                        if (!StringUtils.isEmpty(fileBean.getExternalKey())) {
                            filesCache.put(fileBean.getExternalKey(), fileBean);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new KassaException(e);
                    }
                }
            }

            if (data.getPassportDocFile() != null && filesCache.containsKey(data.getPassportDocFile())) {
                FileBean passportFile = filesCache.get(data.getPassportDocFile());

                this.docsDAO.saveDocumentPage(null, pMain.getId(), DocumentMedia.SCAN_TYPE_PASP,
                        null, 1, passportFile.getContentType(), passportFile.getName(), passportFile.getData());

            }

            if (data.getInnDocFile() != null && filesCache.containsKey(data.getInnDocFile())) {
                FileBean innFile = filesCache.get(data.getInnDocFile());

                this.docsDAO.saveDocumentPage(null, pMain.getId(), DocumentMedia.SCAN_TYPE_INN,
                        null, 1, innFile.getContentType(), innFile.getName(), innFile.getData());

            }

            if (data.getSnilsDocFile() != null && filesCache.containsKey(data.getSnilsDocFile())) {
                FileBean snilsFile = filesCache.get(data.getSnilsDocFile());

                this.docsDAO.saveDocumentPage(null, pMain.getId(), DocumentMedia.SCAN_TYPE_SNILS,
                        null, 1, snilsFile.getContentType(), snilsFile.getName(), snilsFile.getData());

            }

            if (data.getDriverDocFile() != null && filesCache.containsKey(data.getDriverDocFile())) {
                FileBean driverFile = filesCache.get(data.getDriverDocFile());

                this.docsDAO.saveDocumentPage(null, pMain.getId(), DocumentMedia.SCAN_TYPE_DRIVE,
                        null, 1, driverFile.getContentType(), driverFile.getName(), driverFile.getData());

            }
        }
    }

    public void saveEmployment(EmploymentData data, FiasAddressData jobAddrData, Integer creditRequestId) throws KassaException {

        CreditRequestEntity ccRequest = crDAO.getCreditRequestEntity(Convertor.toInteger(creditRequestId));
        PeopleMainEntity peopleMain = ccRequest.getPeopleMainId();

        //сохраняем информацию о занятости

        EmploymentEntity employ = peopleBean.findEmployment(peopleMain, null, referenceBean.getPartnerEntity(Partner.CLIENT), Employment.CURRENT);


        try {
            peopleBean.newEmployment(employ, peopleMain.getId(), null, Partner.CLIENT, data.getPlaceWork(),
                    data.getOccupation(), data.getEducationId(),
                    data.getTypeWorkId(), 3,
                    data.getProfessionId(), data.getExtSalaryId(),
                    Convertor.toDouble(data.getSalary()), Convertor.toDouble(data.getExtSalary()),
                    Convertor.toDate(data.getExperience(), DatesUtils.FORMAT_ddMMYYYY), null,
                    Convertor.toDate(data.getDateStartWork(), DatesUtils.FORMAT_ddMMYYYY),
                    Employment.CURRENT, new Date(), data.getAdditOutcomeSum(),
                    Convertor.toDate(data.getSalaryDate(), DatesUtils.FORMAT_ddMMYYYY), data.getProfessionTypeId());
        } catch (Exception e) {
            logger.error("Не удалось сохранить данные о занятости");
        }


        //save work phone
        if (StringUtils.isNotEmpty(data.getWorkPhone())) {
            try {
                peopleBean.setPeopleContactClient(peopleMain, PeopleContact.CONTACT_WORK_PHONE,
                        Convertor.fromMask(data.getWorkPhone()),
                        !StringUtils.isEmpty(data.getWorkPhone()));
            } catch (Exception e) {
                logger.error("Не удалось сохранить рабочий телефон " + e);
                throw new KassaException(e);
            }
        } else {
            PeopleContactEntity pc = peopleBean.findPeopleByContactMan(PeopleContact.CONTACT_WORK_PHONE, peopleMain.getId());
            if (pc != null) {
                try {

                    peopleBean.changeContactActive(pc.getId());
                } catch (PeopleException e) {
                    logger.error("Не удалось изменить рабочий телефон " + e);
                    throw new KassaException(e);
                }
            }
        }


        //save working address data if it's not empty
        if (jobAddrData != null && StringUtils.isNotEmpty(jobAddrData.getFiasId())) {
            AddressEntity jobAddress = peopleBean.findAddressActive(peopleMain, FiasAddress.WORKING_ADDRESS);

            try {
                peopleBean.newAddressFias(jobAddress, peopleMain.getId(), null, Partner.CLIENT, FiasAddress.WORKING_ADDRESS,
                        null, new Date(), null, null, ActiveStatus.ACTIVE, jobAddrData.getFiasId(),
                        jobAddrData.getHome(), jobAddrData.getKorpus(),
                        jobAddrData.getBuilder(), null);
            } catch (Exception e1) {
                logger.error("Не удалось сохранить адрес проживания, ошибка " + e1);
            }

        } else {
            AddressEntity register_addr = peopleBean.findAddressActive(peopleMain, FiasAddress.WORKING_ADDRESS);
            if (register_addr != null) {
                peopleBean.closeAddress(register_addr, new Date());
            }


        }
    }

    public UsersEntity saveUserData(Integer creditRequestId, String ipAddress) {

        CreditRequestEntity ccRequest = crDAO.getCreditRequestEntity(Convertor.toInteger(creditRequestId));
        PeopleMainEntity peopleMain = ccRequest.getPeopleMainId();
        Integer i = kassaBean.findMaxCreditRequestNumber(new Date());
        Integer userId = null;
        UsersEntity user = null;
        try {

            CreditRequest crt = crDAO.getCreditRequest(ccRequest.getId(), Utils.setOf());
            String agreement = "";
            if (crt != null) {
                agreement = kassaBean.generateAgreement(crt, new Date(), 0);
            }

            ccRequest = kassaBean.newCreditRequest(ccRequest, peopleMain.getId(), CreditStatus.FILLED,
                    true, true, true, true, true, null, null, new Date(), new Date(), new Date(),
                    i, ccRequest.getCreditsum(), ccRequest.getCreditdays(),
                    ccRequest.getStake(), agreement, null, GenUtils.genUniqueNumber(new Date(), i, ccRequest.getStake() * 100),
                    null, null, RefCreditRequestWay.ONLINE,null);

            PeopleContactEntity Email = peopleBean.findPeopleByContactMan(peopleBean.findContactTypeForLogin(), peopleMain.getId());
            if (Email != null) {
                user = userBean.addUserClient(peopleMain, Email.getValue());
            }

            eventLogService.saveLog(ipAddress, eventLogService.getEventType(EventType.INFO).getEntity(), eventLogService.getEventCode(EventCode.SAVE_CREDIT_REQUEST).getEntity(),
                    "", ccRequest, peopleMain, null, null, "", "", "", "");
        } catch (Exception e) {
            logger.error("Can't save user");

        }

        return user;
    }

    public void sendPhoneConfirmation(String msisdn) {
        msisdn = msisdn.replaceAll("\\D", "");
        // String code = StaticFuncs.generateCode(6);
        String code = mailBean.generateCodeForSending();
        this.cache.setMsisdnCode(code);
        this.cache.setMsisdn(msisdn);

        StaticFuncs.log("Phone: " + msisdn + " code=" + code);
        mailBean.sendSMSV2(msisdn, "Код: " + code + " Это код подтверждения номера телефона в системе Ontime.");
    }

    public boolean verifyPhoneConfirmation(String verifyCode) {
        return verifyCode != null && verifyCode.equals(this.cache.getMsisdnCode());
    }

    public void sendEmailConfirmation(String email) {
        // String code = StaticFuncs.generateCode(6);
        String code = mailBean.generateCodeForSending();
        this.cache.setEmailCode(code);
        this.cache.setEmail(email);

        StaticFuncs.log("Email: " + email + " code=" + code);
        try {
            //генерируем письмо с версткой
            Map<String, Object> mp = new HashMap<String, Object>();
            mp.put("description", "Для подтверждения регистрации заявки, введите в поле Код подтверждения Email код " + code);
            String smsgBody = kassaBean.generateEmail(null, Report.EMAIL_ID, mp);
            mailBean.send("Код подтверждения email-адреса", smsgBody, email);
        } catch (Exception e) {
            logger.error("Не удалось послать email, ошибка " + e);
        }
    }

    public boolean verifyEmailConfirmation(String verifyCode) {
        return verifyCode != null && verifyCode.equals(this.cache.getEmailCode());
    }

    public InitialInfoData buildInfo() throws KassaException {
        InitialInfoData info = new InitialInfoData();

        List<ReferenceData> marriageTypes = new ArrayList<>();
        for (Reference ref : referenceBean.getMarriageTypes()) {
            marriageTypes.add(new ReferenceData(ref));
        }
        info.setMarriageTypes(marriageTypes);

        List<ReferenceData> realtyTypes = new ArrayList<>();
        for (Reference ref : referenceBean.getRealtyTypes()) {
            realtyTypes.add(new ReferenceData(ref));
        }
        info.setRealtyTypes(realtyTypes);

        List<ReferenceData> educationTypes = new ArrayList<>();
        for (Reference ref : referenceBean.getEducationTypes()) {
            educationTypes.add(new ReferenceData(ref));
        }
        info.setEducationTypes(educationTypes);

        List<ReferenceData> employTypes = new ArrayList<>();
        for (Reference ref : referenceBean.getEmployTypes()) {
            employTypes.add(new ReferenceData(ref));
        }
        info.setEmployTypes(employTypes);

        List<ReferenceData> extSalaryTypes = new ArrayList<>();
        for (Reference ref : referenceBean.getExtSalaryTypes()) {
            extSalaryTypes.add(new ReferenceData(ref));
        }
        info.setExtSalaryTypes(extSalaryTypes);

        /*
        List<ReferenceData> banksList = new ArrayList<>();
        for(Bank ref : referenceBean.getBanksList()) {
            ReferenceData data = new ReferenceData();
            data.setName(ref.getName());
            data.setCode(ref.getBik());
            banksList.add(data);
        }
        info.setBanksList(banksList);*/

        List<ReferenceData> banksList = new ArrayList<>();
        List<Organization> list = orgService.getCreditOrganizations();

        for (Organization organization : list) {

            ReferenceData data = new ReferenceData();
            data.setName(organization.getName());
            data.setCode(Integer.toString(organization.getId()));
            banksList.add(data);
        }
        info.setBanksList(banksList);

        List<ReferenceData> creditTypes = new ArrayList<>();
        for (Reference ref : referenceBean.getCreditTypes()) {
            creditTypes.add(new ReferenceData(ref));
        }
        info.setCreditTypes(creditTypes);

        List<ReferenceData> currencyTypes = new ArrayList<>();
        for (Reference ref : referenceBean.getCurrencyTypes()) {
            currencyTypes.add(new ReferenceData(ref));
        }
        info.setCurrencyTypes(currencyTypes);

        List<ReferenceData> сreditOverdueTypes = new ArrayList<>();
        for (Reference ref : referenceBean.getCreditOverdueTypes()) {
            сreditOverdueTypes.add(new ReferenceData(ref));
        }
        info.setСreditOverdueTypes(сreditOverdueTypes);

        List<ReferenceData> professions = new ArrayList<>();
        for (Reference ref : referenceBean.getOrganizationTypes()) {
            professions.add(new ReferenceData(ref));
        }
        info.setProfessions(professions);

        List<ReferenceData> professionTypes = new ArrayList<>();
        for (Reference ref : referenceBean.getProfessionTypes()) {
            professionTypes.add(new ReferenceData(ref));
        }
        info.setProfessionTypes(professionTypes);

        List<ReferenceData> creditPurposes = new ArrayList<>();
        for (Reference ref : referenceBean.getCreditPurposeTypes()) {
            creditPurposes.add(new ReferenceData(ref));
        }
        info.setCreditPurposes(creditPurposes);


        Map<String, String> socialData = kassaBean.getSocNetworkApp();
        SocialData sd = new SocialData();

        sd.setFb(socialData.get("fb"));
        sd.setVk(socialData.get("vk"));
        sd.setMm(socialData.get("mm"));
        sd.setOk(socialData.get("odk"));
        sd.setCallBackUrl(socialData.get("callBackUrl"));
        info.setSocialData(sd);


        Map<String, Integer> accountTypesMap = new HashMap<>();
        accountTypesMap.put("CARD_TYPE", Account.CARD_TYPE);
        accountTypesMap.put("CONTACT_TYPE", Account.CONTACT_TYPE);
        info.setAccountTypesMap(accountTypesMap);

        ValidationDicts validationDicts = new ValidationDicts();
        validationDicts.setFileSizeLimitB(FILE_SIZE_LIMIT_B);
        validationDicts.setFileSizeLimitMB(FILE_SIZE_LIMIT_MB);
        validationDicts.setAllowedMime(ALLOWED_MIME);
        validationDicts.setAllowedExtensions(ALLOWED_EXTENSIONS);
        info.setValidationDicts(validationDicts);

        Cookie userWorkCookie = getRequestCookie(USER_WORK_COOKIE_NAME);
        if (userWorkCookie != null) {
            TmpStorageEntity entity = this.tmpStorageDAO.read(userWorkCookie.getValue(), USER_WORK_DATA_STORAGE_TYPE);
            if (entity != null && entity.getData() != null) {
                try {
                    ByteArrayInputStream byteIn = new ByteArrayInputStream(entity.getData());
                    ObjectInputStream in = new ObjectInputStream(byteIn);
                    DataHolder prevData = (DataHolder) in.readObject();
                    info.setPrevData(prevData);
                } catch (IOException ignored) {
                } catch (ClassNotFoundException e) {
                    throw new KassaException(e);
                }
            }
        }

        return info;
    }

    public Cookie getRequestCookie(String name) {
        if (name != null) {
            for (Cookie cookie : this.request.getCookies()) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public String saveUploadedFile(FileItem item) throws KassaException {
        if (item.getSize() > FILE_SIZE_LIMIT_B) {
            throw new KassaException(String.format("Превышен максимально допустимый размер файла (%s МБ)", FILE_SIZE_LIMIT_MB));
        }

        String type = item.getContentType(), extension = getExtension(item.getName());
        if (!(type != null && ALLOWED_MIME.contains(type.toLowerCase()) || extension != null && ALLOWED_EXTENSIONS.contains(extension.toLowerCase()))) {
            throw new KassaException("Недопустимый формат файла");
        }

        Cookie userWorkCookie = getRequestCookie(USER_WORK_COOKIE_NAME);
        if (userWorkCookie != null) {
            FileBean fileBean = new FileBean();
            fileBean.setExternalKey(UUID.randomUUID().toString());
            fileBean.setName(item.getName());
            fileBean.setContentType(item.getContentType());
            try {
                fileBean.setData(IOUtils.toByteArray(item.getInputStream()));
            } catch (IOException e) {
                throw new KassaException(e);
            }

            TmpStorageEntity entity = new TmpStorageEntity();
            entity.setCdate(new Date());
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(byteOut);
                out.writeObject(fileBean);
                entity.setData(byteOut.toByteArray());
            } catch (IOException e) {
                throw new KassaException(e);
            }
            entity.setExternalKey(userWorkCookie.getValue());
            entity.setMaxAge(USER_WORK_COOKIE_MAX_AGE);
            entity.setType(USER_WORK_FILE_STORAGE_TYPE);
            this.tmpStorageDAO.persist(entity);

            return fileBean.getExternalKey();
        } else {
            throw new AssertionError("User work cookie on files upload must be already created");
        }
    }

    public String getExtension(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        String[] parts = fileName.split("\\.");
        if (parts.length == 1) {
            return null;
        }
        return parts[parts.length - 1];
    }

    public void saveUserWork(DataHolder holder) throws KassaException {
        String fiasId;
        if (holder != null && holder.getData() != null && holder.getData().getAddrData() != null && holder.getData().getAddrData().getRegAddress() != null) {
            fiasId = holder.getData().getAddrData().getRegAddress().getFiasId();
            if (fiasId != null) {
                holder.getFiasMap().put(fiasId, getAddressRestList(fiasId));
            }
        }
        if (holder != null && holder.getData() != null && holder.getData().getAddrData() != null && holder.getData().getAddrData().getLiveAddress() != null) {
            fiasId = holder.getData().getAddrData().getLiveAddress().getFiasId();
            if (fiasId != null) {
                holder.getFiasMap().put(fiasId, getAddressRestList(fiasId));
            }
        }
        if (holder != null && holder.getData() != null && holder.getData().getJobAddrData() != null) {
            fiasId = holder.getData().getJobAddrData().getFiasId();
            if (fiasId != null) {
                holder.getFiasMap().put(fiasId, getAddressRestList(fiasId));
            }
        }

//        if (holder != null && holder.getData() != null && holder.getData().getCrOtherData() != null && holder.getData().getCrOtherData().getCreditOrganization() != null) {
//            BankEntity bank = referenceBean.getBank(String.format("%09d", Integer.parseInt(holder.getData().getCrOtherData().getCreditOrganization().toString())));
//            holder.setCreditOrganisationReference(new ReferenceData());
//            holder.getCreditOrganisationReference().setName(bank.getName());
//            holder.getCreditOrganisationReference().setCode(bank.getBik());
//        }

        try {

            TmpStorageEntity entity = new TmpStorageEntity();

            String cookieName = USER_WORK_COOKIE_NAME;
            String cookieValue = Long.toString(new Date().getTime()) + ":" + UUID.randomUUID().toString();
            Cookie userWorkCookie = new Cookie(cookieName, cookieValue);
            userWorkCookie.setMaxAge(USER_WORK_COOKIE_MAX_AGE);
            response.addCookie(userWorkCookie);

            entity.setCdate(new Date());
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(holder);
            entity.setData(byteOut.toByteArray());
            entity.setExternalKey(cookieValue);
            entity.setMaxAge(USER_WORK_COOKIE_MAX_AGE);
            entity.setType(USER_WORK_DATA_STORAGE_TYPE);
            this.tmpStorageDAO.persist(entity);

            Cookie oldUserWorkCookie = getRequestCookie(USER_WORK_COOKIE_NAME);
            if (oldUserWorkCookie != null) {
                this.tmpStorageDAO.delete(oldUserWorkCookie.getValue(), USER_WORK_DATA_STORAGE_TYPE);
                for (TmpStorageEntity fileEntity : this.tmpStorageDAO.readList(oldUserWorkCookie.getValue(), USER_WORK_FILE_STORAGE_TYPE)) {
                    fileEntity.setExternalKey(cookieValue);
                    fileEntity.setCdate(entity.getCdate());
                }
            }
        } catch (JsonMappingException | JsonGenerationException e) {
            throw new KassaException(e);
        } catch (IOException e) {
            throw new KassaException(e);
        }
    }

    public List<ReferenceData> getBanksList(String term, Integer page, Integer pageSize) {
        List<ReferenceData> resultList = new ArrayList<>();
        for (Bank bank : referenceBean.getBanksList(term, page, pageSize)) {
            ReferenceData ref = new ReferenceData();
            ref.setCode(bank.getBik());
            ref.setName(bank.getName());
            resultList.add(ref);
        }
        return resultList;
    }

    public List<AddressRest> getAddressRestList(String fiasId) {
        List<AddrObj> addrObjList = new LinkedList<>();
        AddrObj addrObj = null;

        while (!StringUtils.isEmpty(fiasId)) {
            addrObjList.add(0, addrObj = this.fiasSrv.findAddressObjectByAoguid(fiasId));
            fiasId = addrObj.getParentAOGUID();
        }

        return AddressRest.toAddressRestList(addrObjList);
    }

    public boolean validateLuhn(String data) throws KassaException {

        int dataLength = data.length();

        if (dataLength < 16 || dataLength > 18) {
            throw new KassaException("Номер карты должен иметь длину от 16 до 18 символов");
        }

        int sum = 0;
        boolean alternate = false;
        for (int i = dataLength - 1; i >= 0; i--) {
            int n = 0;
            try {
                n = Integer.parseInt(data.substring(i, i + 1));
            } catch (NumberFormatException e) {
                throw new KassaException("Номер карты должен состоять из цифр");
            }
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        if (sum % 10 != 0) {
            throw new KassaException("Ошибочный номер карты");
        }
        return true;
    }

    public boolean hasPrevData() {
        Cookie userWorkCookie = getRequestCookie(USER_WORK_COOKIE_NAME);
        return userWorkCookie != null && this.tmpStorageDAO.exists(userWorkCookie.getValue(), USER_WORK_DATA_STORAGE_TYPE);
    }

    public void saveSocialIds(Integer creditRequestId) {
        CreditRequestEntity creditRequest = crDAO.getCreditRequestEntity(creditRequestId);
        if (creditRequest == null) {
            return;
        }

        PeopleMainEntity people = creditRequest.getPeopleMainId();


        HttpSession session = request.getSession(false);
        if (session != null) {
            String ok = (String) session.getAttribute("ok-account");
            String vk = (String) session.getAttribute("vk-account");
            String fb = (String) session.getAttribute("fb-account");
            String ml = (String) session.getAttribute("ml-account");

            // TODO use peopleBean.changeContact()
            try {
                if (ok != null) {
                    peopleBean.setPeopleContactClient(people, PeopleContact.NETWORK_OK, ok, false);
                }

                if (vk != null) {
                    peopleBean.setPeopleContactClient(people, PeopleContact.NETWORK_VK, vk, false);
                }

                if (fb != null) {
                    peopleBean.setPeopleContactClient(people, PeopleContact.NETWORK_FB, fb, false);
                }

                if (ml != null) {
                    peopleBean.setPeopleContactClient(people, PeopleContact.NETWORK_MM, ml, false);
                }
            } catch (Exception e) {
                logger.warn("save social id exception", e);
            }
        }
    }
}
