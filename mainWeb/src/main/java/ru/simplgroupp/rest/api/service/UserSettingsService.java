package ru.simplgroupp.rest.api.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import ru.simplgroupp.dao.interfaces.AccountDAO;
import ru.simplgroupp.dao.interfaces.DocumentsDAO;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.data.ErrorData;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.fias.ejb.IFIASService;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.persistence.DocumentMediaEntity;
import ru.simplgroupp.rest.api.data.AccountData;
import ru.simplgroupp.rest.api.data.PeopleMainData;
import ru.simplgroupp.rest.api.data.ReferenceData;
import ru.simplgroupp.rest.api.data.user.*;
import ru.simplgroupp.toolkit.common.CheckUtils;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Сервис для обработки данные личного кабинета
 */
@RequestScoped
public class UserSettingsService {
    private static Logger logger = Logger.getLogger(UserSettingsService.class.getName());

    @Inject
    private OverviewService overviewService;

    @Inject
    private AiService aiService;

    @Inject
    private UserSettingsCacheService cacheService;

    @Inject
    private UserSettingsDataService userDataService;

    @Inject
    private FirstCreditRequestCacheService cacheFCR;

    @EJB
    private UsersBeanLocal userBean;

    @EJB
    private PeopleDAO peopleDAO;

    @EJB
    private PeopleBeanLocal peopleBean;

    @EJB
    private MailBeanLocal mailBean;

    @EJB
    private ReferenceBooksLocal refBook;

    @EJB
    private KassaBeanLocal kassaBean;

    @EJB
    private IFIASService fiasServ;

    @EJB
    private AddressBeanLocal addressBean;

    @EJB
    private AccountDAO accountDAO;

    @EJB
    private DocumentsDAO docsDAO;


    @PostConstruct
    private void init() throws KassaException {
        overviewService.init();
    }

    /**
     * ФИО пользователя
     *
     * @param user - пользователь
     * @return - фио
     */
    public String getUserName(Users user) {
        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
        return peopleMain.getPeoplePersonalActive().getFullName();
    }

    public PeopleMainData getPeopleMain(Users user) {
        return userDataService.getPeopleMainData(user);
    }

    private List<AccountData> initAccounts(List<Account> accounts) {
        List<AccountData> accountDatas = new ArrayList<>();
        for (Account account : accounts) {
            accountDatas.add(initAccount(account));
        }
        return accountDatas;
    }

    private AccountData initAccount(Account account) {
        AccountData accountData = new AccountData();
        accountData.setActive(account.getIsActive());
        accountData.setCodeType(account.getAccountType().getCodeInteger());
        accountData.setId(account.getId());
        accountData.setAccountnumber(account.getAccountNumber());
        accountData.setBankName(account.getBankName());
        accountData.setBik(account.getBik());
        accountData.setCardNumberMasked(account.getCardNumberMasked());
        accountData.setName(account.getAccountType().getName());
        return accountData;
    }

    public Map<String, Object> getSocialData(Users user) {
        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_CONTACT,
                PeopleMain.Options.INIT_PEOPLE_MISC, PeopleMain.Options.INIT_DOCUMENTMEDIA));

        Map<String, Object> map = new HashMap<>();
        if (peopleMain.getVk() != null) {
            map.put("vkId", peopleMain.getVk().getId());
            map.put("vk", peopleMain.getVk().getValue());
        }
        if (peopleMain.getFb() != null) {
            map.put("fbId", peopleMain.getFb().getId());
            map.put("fb", peopleMain.getFb().getValue());
        }
        if (peopleMain.getMm() != null) {
            map.put("mmId", peopleMain.getMm().getId());
            map.put("mm", peopleMain.getMm().getValue());
        }
        if (peopleMain.getOdk() != null) {
            map.put("odkId", peopleMain.getOdk().getId());
            map.put("odk", peopleMain.getOdk().getValue());
        }

        return map;
    }

    public void deleteDocumentMedia(Integer id) {
        docsDAO.deleteDocumentMedia(id);
    }

    public DocumentMediaEntity getDocumentMadia(Integer id) {
        return docsDAO.getDocumentMedia(id);
    }

    /**
     * конфигурационные данные для соц сетей
     *
     * @return - мар
     */
    public Map<String, String> getConfig() {
        Map<String, String> ans = new HashMap<>();
        Map<String, String> data = kassaBean.getSocNetworkApp();

        ans.put("fb", data.get("fb"));
        ans.put("vk", data.get("vk"));
        ans.put("mm", data.get("mm"));
        ans.put("odk", data.get("odk"));
        ans.put("callBackUrl", data.get("callBackUrl"));

        return ans;
    }

    /**
     * отправка смс кода подтверждения
     *
     * @param phoneStr номер телефона
     * @param user     пользователь
     * @return телефонный номер на который отправили смс
     * @throws KassaException
     */
    public String sendSmsCode(String phoneStr, Users user) throws KassaException {
        String code = mailBean.generateCodeForSending();

        String phoneNumber = Convertor.fromMask(phoneStr);
        logger.info("phone = " + phoneNumber + " sms code = " + code);

        try {
            if (phoneNumber == null) {
                phoneNumber = peopleBean.findContactClient(PeopleContact.CONTACT_CELL_PHONE, user.getPeopleMain().getId());
            }

            mailBean.sendSMSV2(phoneNumber, "Код " + code + ". Это код для подтверждения редактирования личных данных в системе");

            // с этим хэшем будем сравнивать ответ
            String hash = StaticFuncs.md5("salt" + phoneNumber + "supersalt" + code + "megasalt");
            cacheService.setSmsHash(hash);
            cacheService.setSmsCode(code);

            return hash;
        } catch (Exception ex) {
            throw new KassaException("Не удалось отправить смс.");
        }
    }

    /**
     * проверяем введенный смс код
     *
     * @param code смс код
     * @return возвращает хеш кода и телефона
     * @throws PeopleException
     */
    public String verifySmsCode(String code) throws PeopleException {
        String error = checkSmsCode(code);
        if (error != null) {
            throw new PeopleException(error);
        }

        return cacheService.getSmsHash();
    }

    /**
     * отправить письмо с кодом подтверждения
     *
     * @param email почта
     * @return возвра хеш кода и почты
     * @throws KassaException
     */
    public String sendEmailCode(String email) throws KassaException {
        String code = mailBean.generateCodeForSending();

        logger.info("email = " + email + " code = " + code);

        try {
            mailBean.send("Код подтверждения email-адреса", "Код: " + code + ". Это код для подтверждения редактирования личных данных в системе", email);

            // с этим хэшем будем сравнивать ответ
            String hash = StaticFuncs.md5("salt" + email + "supersalt" + code + "megasalt");
            cacheService.setEmailHash(hash);
            cacheService.setEmailCode(code);

            return hash;
        } catch (Exception ex) {
            throw new KassaException("Не удалось отправить смс. ");
        }
    }

    /**
     * проверяет валидность введенного email кода
     * если код введен не верно вылетает исключение, если верно возвращается хеш
     * возвращенный хеш пользователь должен прислать при сохранении данных, что бы избежать подмены
     *
     * @param code код введенный
     * @return хеш
     * @throws PeopleException
     */
    public String verifyEmailCode(String code) throws PeopleException {
        String error = checkEmailCode(code);
        if (error != null) {
            throw new PeopleException(error);
        }

        return cacheService.getEmailHash();
    }

    /**
     * Сохранение контактов пользователя
     *
     * @param maps - данные
     * @param user - пользователь
     * @return - лист ошибок
     * @throws KassaException
     * @throws PeopleException
     */
    public List<ErrorData> savePeopleContact(Map<String, Object> maps, Users user) throws KassaException, PeopleException, IOException {
        if (!aiService.getCanAdd()) {
            throw new PeopleException("Запрет на редактирование");
        }
        List<ErrorData> errors = new ArrayList<>();
        PeopleMain ppl = peopleDAO.getPeopleMain(user.getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_MISC, PeopleMain.Options.INIT_SPOUSE, PeopleMain.Options.INIT_PEOPLE_CONTACT));


        String oldPhone = peopleBean.findContactClient(PeopleContact.CONTACT_CELL_PHONE, user.getPeopleMain().getId());
        if (StringUtils.isEmpty((String) maps.get("phoneCode"))) {

            if (oldPhone == null) {
                errors.add(new ErrorData("phoneCode", "Поле обязательно для заполнения"));
            }
        } else {
            if (maps.get("phoneHash") != null &&
                    !maps.get("phoneHash").equals(StaticFuncs.md5("salt" + oldPhone + "supersalt" + maps.get("phoneCode") + "megasalt"))) {
                errors.add(new ErrorData("phoneCode", "Поле не соответствует формату"));
            }
        }

        if (errors.size() > 0) {
            return errors;
        }

        if (maps.get("inn") != null) {
            if (CheckUtils.CheckInn((String) maps.get("inn"))) {
                ppl.setInn((String) maps.get("inn"));
            }
        }
        if (maps.get("snils") != null) {
            if (CheckUtils.CheckSnils((String) maps.get("snils"))) {
                ppl.setSnils((String) maps.get("snils"));
            }
        }
        PeoplePersonal personal = ppl.getPeoplePersonalActive();
        PeopleMisc misc = ppl.getPeopleMiscActive();
        if (maps.get("car") != null) {
            misc.setCar((Boolean) maps.get("car"));
        }

        if (maps.get("hasDriverDoc") != null) {
            misc.setDriverLicense((Boolean) maps.get("hasDriverDoc"));
        }

        peopleBean.changePeopleData(personal, misc, null, ppl, Partner.CLIENT, new Date());


        if (maps.get("passportDocFile") != null && this.cacheFCR.hasUploadedFile((String) maps.get("passportDocFile"))) {
            FileItem passportFile = this.cacheFCR.getUploadedFile((String) maps.get("passportDocFile"));

            this.docsDAO.saveDocumentPage(null, user.getPeopleMain().getId(), DocumentMedia.SCAN_TYPE_PASP,
                    null, 1, passportFile.getContentType(), passportFile.getName(), IOUtils.toByteArray(passportFile.getInputStream()));

        }

        if (maps.get("innDocFile") != null && this.cacheFCR.hasUploadedFile((String) maps.get("innDocFile"))) {
            FileItem innFile = this.cacheFCR.getUploadedFile((String) maps.get("innDocFile"));

            this.docsDAO.saveDocumentPage(null, user.getPeopleMain().getId(), DocumentMedia.SCAN_TYPE_INN,
                    null, 1, innFile.getContentType(), innFile.getName(), IOUtils.toByteArray(innFile.getInputStream()));

        }

        if (maps.get("snilsFile") != null && this.cacheFCR.hasUploadedFile((String) maps.get("snilsFile"))) {
            FileItem snilsFile = this.cacheFCR.getUploadedFile((String) maps.get("snilsFile"));

            this.docsDAO.saveDocumentPage(null, user.getPeopleMain().getId(), DocumentMedia.SCAN_TYPE_SNILS,
                    null, 1, snilsFile.getContentType(), snilsFile.getName(), IOUtils.toByteArray(snilsFile.getInputStream()));

        }

        if (maps.get("autoCardFile") != null && this.cacheFCR.hasUploadedFile((String) maps.get("autoCardFile"))) {
            FileItem driverFile = this.cacheFCR.getUploadedFile((String) maps.get("autoCardFile"));

            this.docsDAO.saveDocumentPage(null, user.getPeopleMain().getId(), DocumentMedia.SCAN_TYPE_DRIVE,
                    null, 1, driverFile.getContentType(), driverFile.getName(), IOUtils.toByteArray(driverFile.getInputStream()));

        }

        return errors;
    }

    /**
     * Персональные данные пользователя + справочники для странички Персональные данные
     *
     * @param user - пользователь
     * @return map
     */
    public Map<String, Object> getPersonal(Users user) {
        Map<String, Object> map = new HashMap<>();

        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_CONTACT));

        map.put("email", peopleMain.getEmail().getValue());
        map.put("phone", peopleMain.getCellPhone().getValue());

        return map;
    }

    public List<ErrorData> saveAnketaData(Map<String, Object> map, Users user) throws PeopleException {

        if (!aiService.getCanAdd()) {
            throw new PeopleException("Запрет на редактирование");
        }

        List<ErrorData> ans = new ArrayList<>();
        if (StringUtils.isEmpty((String) map.get("phoneCode"))) {
            ans.add(new ErrorData("phoneCode", "Поле обязательно для заполнения"));
        } else {
            String p = peopleBean.findContactClient(PeopleContact.CONTACT_CELL_PHONE, user.getPeopleMain().getId());
            if ((String) map.get("phoneHash") != null &&
                    !((String) map.get("phoneHash")).equals(StaticFuncs.md5("salt" + p + "supersalt" + map.get("phoneCode") + "megasalt"))) {
                ans.add(new ErrorData("phoneCode", "Поле не соответствует формату"));
            }
        }
        if (ans.size() > 0) {
            return ans;
        }

        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_MISC, PeopleMain.Options.INIT_ADDRESS, PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_EMPLOYMENT));

        PeopleMisc misc = peopleMain.getPeopleMiscActive();

        if (map.get("children") != null) {
            misc.setChildren(Integer.valueOf((String) map.get("children")));
        }
        if (map.get("marriageId") != null) {
            misc.setMarriage(refBook.getMarriageType(Integer.valueOf((String) map.get("marriageId"))));
        }
        Date dateChange = new Date();

        peopleBean.changeMiscData(misc, peopleMain.getId(), Partner.CLIENT, dateChange);

        Employment employ = peopleMain.getCurrentEmployment();
        if (Convertor.toInteger(map.get("educationId")) != null) {
            if (refBook.getEducationType(Convertor.toInteger(map.get("educationId"))) != null) {
                employ.setEducation(refBook.getEducationType(Convertor.toInteger(map.get("educationId"))));
            }
        } else {
            employ.setEducation(null);
        }


        peopleBean.changeEmployment(employ, peopleMain.getId(), dateChange);

        return ans;
    }

    /**
     * удаляем учетную запись соц сети
     *
     * @param id - идентификатор
     * @throws PeopleException
     */
    public void makeArchive(Integer id) throws PeopleException {
        if (id != null) {
            peopleBean.changeContactActive(id);
        }
    }

    /**
     * Платежные данные для пользователя
     *
     * @param user - пользователь
     * @return - лист платежных данных
     */
    public List<AccountData> getAccounts(Users user) {
        PeopleMain ppl = peopleDAO.getPeopleMain(user.getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_ACCOUNTACTIVE));
        List<Account> lstAcc = ppl.getAccountsActive();

        return initAccounts(lstAcc);
    }

    /**
     * типы платежных данных
     *
     * @return - справочник платежных данных
     */
    public List<ReferenceData> getAccountTypes() {
        return userDataService.transportReferences(refBook.getAccountTypes());
    }

    /**
     * Создает новые платежные данне для пользователя
     *
     * @param data - плтежные данные
     * @param user - пользователь
     * @return - лист ошибок, если есть
     * @throws PeopleException
     */
    public List<ErrorData> createAccount(AccountData data, Users user) throws PeopleException {
        if (!aiService.getCanAdd()) {
            throw new PeopleException("Запрет на редактирование");
        }
        List<ErrorData> ans = new ArrayList<>();
        if (data.getCodeType() == null) {
            ans.add(new ErrorData("radio-type", "Поле обязательно для заполнения"));
        }
        if (!StringUtils.isEmpty(data.getAccountnumber())) {
            if (Convertor.toLong(data.getAccountnumber()) == null) {
                ans.add(new ErrorData("accountNumber", "Номер должен состоять только из цифр"));
            }
        }
        if (!StringUtils.isEmpty(data.getBik())) {
            if (Convertor.toInteger(data.getBik()) == null) {
                ans.add(new ErrorData("bik", "Номер должен состоять только из цифр"));
            }
        }
        if (!StringUtils.isEmpty(data.getCorrAccountNumber())) {
            if (Convertor.toLong(data.getCorrAccountNumber()) == null) {
                ans.add(new ErrorData("corrAccountNumber", "Номер должен состоять только из цифр"));
            }
        }
        if (!StringUtils.isEmpty(data.getCardNumber())) {
            if (Convertor.toLong(data.getCardNumber()) == null) {
                ans.add(new ErrorData("cardNumber", "Номер должен состоять только из цифр"));
            }
        }
        if (StringUtils.isEmpty(data.getPhoneCode())) {
            ans.add(new ErrorData("phoneCode", "Поле обязательно для заполнения"));
        } else {
            String p = peopleBean.findContactClient(PeopleContact.CONTACT_CELL_PHONE, user.getPeopleMain().getId());
            if (data.getSmsHash() != null &&
                    !data.getSmsHash().equals(StaticFuncs.md5("salt" + p + "supersalt" + data.getPhoneCode() + "megasalt"))) {
                ans.add(new ErrorData("phoneCode", "Поле не соответствует формату"));
            }
        }

        if (ans.size() > 0) {
            return ans;
        }

        peopleBean.newAccount(null, user.getPeopleMain().getId(), new Date(),
                null, null, data.getAccountnumber(), data.getCodeType(), data.getBik(),
                data.getCorrAccountNumber(), data.getCardNumber(), null, null, data.getCardName(),
                null, null, ActiveStatus.ACTIVE);

        return ans;
    }

    /**
     * Ищет данные по счету по его идентификатору
     *
     * @param accountId - идентификотор счета
     * @param user      - пользователь
     * @return - данные по счету
     */
    public AccountData getAccountById(Integer accountId, Users user) {
        int peopleId = user.getPeopleMain().getId();
        PeopleMain ppl = peopleDAO.getPeopleMain(peopleId, Utils.setOf(PeopleMain.Options.INIT_ACCOUNTACTIVE, PeopleMain.Options.INIT_ADDRESS));

        Account account = accountDAO.getAccount(accountId, null);

        return initAccount(account);
    }

    /**
     * Изменение данных по счету
     *
     * @param data -данные по счету
     * @param user - пользователь
     * @return - лист ошибок, если есть
     * @throws PeopleException
     */
    public List<ErrorData> changeAccount(AccountData data, Users user) throws PeopleException {
        if (!aiService.getCanAdd()) {
            throw new PeopleException("Запрет на редактирование");
        }
        List<ErrorData> ans = new ArrayList<>();

        if (!StringUtils.isEmpty(data.getAccountnumber())) {
            if (Convertor.toFloat(data.getAccountnumber()) == null) {
                ans.add(new ErrorData("accountNumber", "Номер должен состоять только из цифр"));
            }
        }
        if (!StringUtils.isEmpty(data.getBik())) {
            if (Convertor.toFloat(data.getBik()) == null) {
                ans.add(new ErrorData("bik", "Номер должен состоять только из цифр"));
            }
        }
        if (!StringUtils.isEmpty(data.getCorrAccountNumber())) {
            if (Convertor.toFloat(data.getCorrAccountNumber()) == null) {
                ans.add(new ErrorData("corrAccountNumber", "Номер должен состоять только из цифр"));
            }
        }
        if (!StringUtils.isEmpty(data.getCardNumber())) {
            if (Convertor.toFloat(data.getCardNumber()) == null) {
                ans.add(new ErrorData("cardNumber", "Номер должен состоять только из цифр"));
            }
        }
        if (StringUtils.isEmpty(data.getPhoneCode())) {
            ans.add(new ErrorData("phoneCode", "Поле обязательно для заполнения"));
        } else {
            String p = peopleBean.findContactClient(PeopleContact.CONTACT_CELL_PHONE, user.getPeopleMain().getId());
            if (data.getSmsHash() != null &&
                    !data.getSmsHash().equals(StaticFuncs.md5("salt" + p + "supersalt" + data.getPhoneCode() + "megasalt"))) {
                ans.add(new ErrorData("phoneCode", "Поле не соответствует формату"));
            }
        }

        if (ans.size() > 0) {
            return ans;
        }

        int peopleId = user.getPeopleMain().getId();
        PeopleMain ppl = peopleDAO.getPeopleMain(peopleId, Utils.setOf(PeopleMain.Options.INIT_ACCOUNTACTIVE, PeopleMain.Options.INIT_ADDRESS));

        Account account = accountDAO.getAccount(data.getId(), null);

        account.setAccountNumber(data.getAccountnumber());
        account.setBankName(data.getBankName());
        account.setBik(data.getBik());
        account.setCorrAccountNumber(data.getCorrAccountNumber());
        account.setCardName(data.getCardName());
        account.setCardNumber(data.getCardNumber());

        peopleBean.changeAccount(account, ppl.getId());

        return ans;
    }

    /**
     * удаление счета
     */
    public void removeAccount(Integer accountId) throws PeopleException {
        peopleBean.changeAccountActive(accountId);
    }

    /**
     * проверяет валидность смс кода, если все ок смс код из кеша убирается
     *
     * @param smsCode смс код
     * @return если все ок вернет null, иначе текст ошибки
     */
    public String checkSmsCode(String smsCode) {
        if (StringUtils.isEmpty(smsCode)) {
            return "Необходимо ввести код СМС";
        }

        if (!smsCode.equals(cacheService.getSmsCode())) {
            return "Неверный код СМС";
        }

        cacheService.setSmsCode(null);
        return null;
    }

    /**
     * проверяет валидность кода из почты, если все ок смс код из кеша убирается
     *
     * @param code код
     * @return если все ок вернет null, иначе текст ошибки
     */
    public String checkEmailCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return "Необходимо ввести код";
        }

        if (!code.equals(cacheService.getEmailCode())) {
            return "Неверный код СМС";
        }

        cacheService.setEmailCode(null);
        return null;
    }

    /**
     * возвращает рабочие данные для страницы изменения профиля
     *
     * @param user пользователь который авторизирован
     * @return данные по занятости
     */
    public ru.simplgroupp.rest.api.data.user.EmploymentData getEmploymentData(Users user) {
        PeopleMainData peopleMainData = userDataService.getPeopleMainData(user);
        ru.simplgroupp.rest.api.data.user.EmploymentData employmentData = userDataService.getEmploymentData(user);

        employmentData.setSmsNumber(peopleMainData.getContact().getPhone());

        return employmentData;
    }

    /**
     * сохраняет данные о занятости
     *
     * @param employmentData данные
     * @param user           авторизированный пользователь
     * @return возвращает OK если все хорошо
     * @throws PeopleException
     */
    public String saveEmploymentData(ru.simplgroupp.rest.api.data.user.EmploymentData employmentData, Users user) throws PeopleException {
        if (!aiService.isCanEdit()) {
            throw new PeopleException("Запрет на редактирование");
        }

        String error = checkSmsCode(employmentData.getSmsCode());
        if (error != null) {
            throw new PeopleException(error);
        }


        userDataService.saveEmployment(employmentData, user);
        userDataService.saveAddressData(employmentData.getWork(), FiasAddress.WORKING_ADDRESS, user);

        if (employmentData.getWork() == null) {
            userDataService.saveContactData(null, PeopleContact.CONTACT_WORK_PHONE, user);
        } else {
            userDataService.saveContactData(employmentData.getWork().getPhone(), PeopleContact.CONTACT_WORK_PHONE, user);
        }

        return "OK";
    }

    /**
     * сохраняет дополнительные данные человека (инн, снилс, машина, документы)
     *
     * @param additionalData данные
     * @param user           пользователь авторизированный
     * @return возвращает OK если все хорошо, иначе бросает исключения
     * @throws KassaException
     * @throws PeopleException
     * @throws IOException
     */
    public String saveAdditionalData(AdditionalData additionalData, Users user) throws KassaException,
            PeopleException, IOException {
        if (!aiService.isCanEdit()) {
            throw new PeopleException("Запрет на редактирование");
        }

        String error = checkSmsCode(additionalData.getSmsCode());
        if (error != null) {
            throw new PeopleException(error);
        }


        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(),
                Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_MISC,
                        PeopleMain.Options.INIT_SPOUSE, PeopleMain.Options.INIT_PEOPLE_CONTACT));


        PeopleMisc misc = peopleMain.getPeopleMiscActive();

        misc.setCar(additionalData.getCar());
        misc.setDriverLicense(additionalData.getDriverLicense());

        // был ли ранее установлен инн и снилс, чтобы при
        // каждом изменении ИНН и СНИЛС'a неначислять бонус
        boolean hasInnBefore = peopleMain.getInn() != null;
        boolean hasSnilsBefore = peopleMain.getSnils() != null;

        peopleBean.savePeopleMain(peopleMain.getEntity(), additionalData.getInn(), additionalData.getSnils());
        peopleBean.changePeopleData(null, misc, null, peopleMain, Partner.CLIENT, new Date());

        try {
            // добавляем бонус за добавление ИНН'a если у человека он не был устанвлен
            if (!hasInnBefore && peopleMain.getInn() != null) {
                peopleBean.addBonus(peopleMain.getId(), PeopleBonus.BONUS_CODE_INN_ADD, BaseCredit.OPERATION_IN);
            }
            // добавляем бонус за добавление СНИЛС'a если у человека он не установлен
            if (!hasSnilsBefore && peopleMain.getSnils() != null) {
                peopleBean.addBonus(peopleMain.getId(), PeopleBonus.BONUS_CODE_SNILS_ADD, BaseCredit.OPERATION_IN);
            }
        } catch (PeopleException pe) {
            logger.severe("Не удалось добавить бонусы " + pe);
        }


        if (additionalData.getPassportFile() != null && cacheFCR.hasUploadedFile(additionalData.getPassportFile())) {
            FileItem passportFile = cacheFCR.getUploadedFile(additionalData.getPassportFile());

            docsDAO.saveDocumentPage(null, user.getPeopleMain().getId(), DocumentMedia.SCAN_TYPE_PASP,
                    null, 1, passportFile.getContentType(), passportFile.getName(), IOUtils.toByteArray(passportFile.getInputStream()));
            try {
                peopleBean.addBonus(user.getPeopleMain().getId(), PeopleBonus.BONUS_CODE_DOC_ADD, BaseCredit.OPERATION_IN);
            } catch (PeopleException e) {
                logger.severe("Не удалось добавить бонус " + e);
            }
        }
        if (additionalData.getPassportRegistrationFile() != null && cacheFCR.hasUploadedFile(additionalData.getPassportRegistrationFile())) {
            FileItem passportRegistrationFile = cacheFCR.getUploadedFile(additionalData.getPassportRegistrationFile());

            docsDAO.saveDocumentPage(null, user.getPeopleMain().getId(), DocumentMedia.SCAN_TYPE_PASP,
                    null, 2, passportRegistrationFile.getContentType(), passportRegistrationFile.getName(), IOUtils.toByteArray(passportRegistrationFile.getInputStream()));
            try {
                peopleBean.addBonus(user.getPeopleMain().getId(), PeopleBonus.BONUS_CODE_DOC_ADD, BaseCredit.OPERATION_IN);
            } catch (PeopleException e) {
                logger.severe("Не удалось добавить бонус " + e);
            }
        }

        if (additionalData.getInnFile() != null && cacheFCR.hasUploadedFile(additionalData.getInnFile())) {
            FileItem innFile = cacheFCR.getUploadedFile(additionalData.getInnFile());

            docsDAO.saveDocumentPage(null, user.getPeopleMain().getId(), DocumentMedia.SCAN_TYPE_INN,
                    null, 1, innFile.getContentType(), innFile.getName(), IOUtils.toByteArray(innFile.getInputStream()));
            try {
                peopleBean.addBonus(user.getPeopleMain().getId(), PeopleBonus.BONUS_CODE_DOC_ADD, BaseCredit.OPERATION_IN);
            } catch (PeopleException e) {
                logger.severe("Не удалось добавить бонус " + e);
            }
        }

        if (additionalData.getSnilsFile() != null && cacheFCR.hasUploadedFile(additionalData.getSnilsFile())) {
            FileItem snilsFile = this.cacheFCR.getUploadedFile(additionalData.getSnilsFile());

            docsDAO.saveDocumentPage(null, user.getPeopleMain().getId(), DocumentMedia.SCAN_TYPE_SNILS,
                    null, 1, snilsFile.getContentType(), snilsFile.getName(), IOUtils.toByteArray(snilsFile.getInputStream()));
            try {
                peopleBean.addBonus(user.getPeopleMain().getId(), PeopleBonus.BONUS_CODE_DOC_ADD, BaseCredit.OPERATION_IN);
            } catch (PeopleException e) {
                logger.severe("Не удалось добавить бонус " + e);
            }
        }

        if (additionalData.getDriverLicenceFile() != null && cacheFCR.hasUploadedFile(additionalData.getDriverLicenceFile())) {
            FileItem driverFile = cacheFCR.getUploadedFile(additionalData.getDriverLicenceFile());

            docsDAO.saveDocumentPage(null, user.getPeopleMain().getId(), DocumentMedia.SCAN_TYPE_DRIVE,
                    null, 1, driverFile.getContentType(), driverFile.getName(), IOUtils.toByteArray(driverFile.getInputStream()));
            try {
                peopleBean.addBonus(user.getPeopleMain().getId(), PeopleBonus.BONUS_CODE_DOC_ADD, BaseCredit.OPERATION_IN);
            } catch (PeopleException e) {
                logger.severe("Не удалось добавить бонус " + e);
            }
        }

        return "OK";
    }

    /**
     * основные данные о пользователе (фио, др и пр)
     *
     * @param user пользователь
     * @return сущность которая содержит основные даннные пользователя
     */
    public PasswordData getPasswordData(Users user) {
        PeopleMainData peopleMainData = getPeopleMain(user);
        PasswordData passwordData = new PasswordData();

        passwordData.setSmsNumber(peopleMainData.getContact().getPhone());

        return passwordData;
    }

    /**
     * сохраняем данные пароля
     *
     * @param passwordData введенные данные
     * @param user         авторизированный пользователь
     * @return возвращает OK если все хорошо
     * @throws ParseException
     * @throws PeopleException
     * @throws KassaException
     */
    public String savePasswordData(PasswordData passwordData, Users user) throws ParseException, PeopleException, KassaException {
        String error = checkSmsCode(passwordData.getSmsCode());
        if (error != null) {
            throw new PeopleException(error);
        }


        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(),
                Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL,
                        PeopleMain.Options.INIT_PEOPLE_MISC, PeopleMain.Options.INIT_SPOUSE));

        String oldPassword = DigestUtils.md5Hex(passwordData.getOldPassword());

        if (!oldPassword.equals(user.getPassword())) {
            throw new PeopleException("старый пароль введен не верно");
        }

        if (passwordData.getNewPassword().isEmpty() || passwordData.getNewPassword2().isEmpty()) {
            throw new PeopleException("пароль не может быть пустым");
        }

        if (!passwordData.getNewPassword().equals(passwordData.getNewPassword2())) {
            throw new PeopleException("пароли не совпадают");
        }

        if (passwordData.getNewPassword().length() < 5) {
            throw new PeopleException("пароль должен быть минимум 5 символов длинной");
        }


        userBean.changePassword(peopleMain.getId(), passwordData.getNewPassword());

        return "OK";
    }

    /**
     * основные данные о пользователе (фио, др и пр)
     *
     * @param user пользователь
     * @return сущность которая содержит основные даннные пользователя
     */
    public ProfileData getProfileData(Users user) {
        PeopleMainData peopleMainData = userDataService.getPeopleMainData(user);
        ProfileData profileData = new ProfileData();

        profileData.setMainData(userDataService.getMainData(user));
        profileData.setPasswordData(new PasswordData());
        profileData.setSmsNumber(peopleMainData.getContact().getPhone());

        return profileData;
    }

    /**
     * сохраняет в данном случае только почту, телефон и пароль
     *
     * @param profileData данные пользователя
     * @param user        авторизированный пользователь
     * @return возвращает ОК если все хорошо, иначе бросается исключение
     */
    public String saveProfileData(ProfileData profileData, Users user, HttpServletRequest request) throws PeopleException,
            ParseException, KassaException, ServletException {
        String error = checkSmsCode(profileData.getSmsCode());
        if (error != null) {
            throw new PeopleException(error);
        }

        boolean passwordChaged = userDataService.savePasswordData(profileData.getPasswordData(), user);
        boolean emailChanged = userDataService.saveEmail(profileData.getMainData(), user);
        boolean phoneChanged = userDataService.savePhone(profileData.getMainData(), user);

        if (passwordChaged || emailChanged) {
            request.logout();
        }

        return "OK";
    }

    /**
     * персональные (или анкетные) данные человека
     *
     * @param user авторизированный пользователь
     * @return сущность с данными
     */
    public FormPersonalData getFormPersonalData(Users user) {
        PeopleMainData peopleMainData = userDataService.getPeopleMainData(user);
        FormPersonalData formPersonalData = new FormPersonalData();

        formPersonalData.setMainData(userDataService.getMainData(user));
        formPersonalData.setPassportData(peopleMainData.getPassport());
        formPersonalData.setMiscData(userDataService.getMiscData(user));
        formPersonalData.setEmploymentData(userDataService.getEmploymentData(user));
        formPersonalData.setSmsNumber(peopleMainData.getContact().getPhone());

        return formPersonalData;
    }

    /**
     * сохраняет персональные (или анкетные) данные человека
     *
     * @param personalData данные
     * @param user         пользователь авторизированный
     * @return возвращает OK если все хорошо
     */
    public String saveFormPersonalData(FormPersonalData personalData, Users user) throws PeopleException {
        if (!aiService.isCanEdit()) {
            throw new PeopleException("Запрет на редактирование");
        }

        String error = checkSmsCode(personalData.getSmsCode());
        if (error != null) {
            throw new PeopleException(error);
        }

        // заблокировано изменение паспортных и основных данных
//        userDataService.saveMainData(personalData.getMainData(), user);
//        userDataService.savePassport(personalData.getPassportData(), user);
        userDataService.saveMiscData(personalData.getMiscData(), user);
        userDataService.saveEmployment(personalData.getEmploymentData(), user);


        return "OK";
    }

    /**
     * возвращает данные по адресам человека
     *
     * @param user авторизированный человек
     * @return данные адресов
     */
    public FullAddressData getFullAddressData(Users user) {
        PeopleMain peopleMain = userDataService.getPeopleMain(user);
        PeopleMainData peopleMainData = userDataService.getPeopleMainData(peopleMain);

        FullAddressData fullAddressData = new FullAddressData();
        fullAddressData.setRegistration(peopleMainData.getAddressRegister());
        fullAddressData.setResidence(peopleMainData.getAddressResident());
        fullAddressData.setMisc(userDataService.getMiscData(user));


        ContactData additionalPhone = new ContactData();
        for (PeopleContact peopleContact : peopleMain.getPeopleContactsAll()) {
            if (peopleContact.getContact().getCodeInteger() == PeopleContact.CONTACT_DOPPHONE1 &&
                    peopleContact.getIsActive() == 1) {
                additionalPhone = new ContactData(peopleContact);
            }
        }

        fullAddressData.setAdditionalPhone(additionalPhone);
        fullAddressData.setSmsNumber(peopleMainData.getContact().getPhone());

        return fullAddressData;
    }

    /**
     * сохраняет данные об адресах
     *
     * @param fullAddressData данные об адресах
     * @param user            авторизрованный пользователь
     * @return если все хорошо возвращает ОК
     */
    public String saveFullAddressData(FullAddressData fullAddressData, Users user) throws PeopleException {
        if (!aiService.isCanEdit()) {
            throw new PeopleException("Запрет на редактирование");
        }

        String error = checkSmsCode(fullAddressData.getSmsCode());
        if (error != null) {
            throw new PeopleException(error);
        }

        userDataService.saveAddressData(fullAddressData.getRegistration(), FiasAddress.REGISTER_ADDRESS, user);

        if (fullAddressData.getRegistration().isSame()) {
            userDataService.saveAddressData(null, FiasAddress.RESIDENT_ADDRESS, user);
        } else {
            userDataService.saveAddressData(fullAddressData.getResidence(), FiasAddress.RESIDENT_ADDRESS, user);
        }
        userDataService.saveMiscData(fullAddressData.getMisc(), user);
        userDataService.saveContactData(fullAddressData.getResidence().getPhone(), PeopleContact.CONTACT_HOME_PHONE, user);
        userDataService.saveContactData(fullAddressData.getAdditionalPhone(), PeopleContact.CONTACT_DOPPHONE1, user);


        return "OK";
    }
}
