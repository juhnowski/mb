package ru.simplgroupp.rest.api.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.fias.ejb.IFIASService;
import ru.simplgroupp.fias.persistence.AddrObj;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.persistence.AddressEntity;
import ru.simplgroupp.rest.api.data.AccountData;
import ru.simplgroupp.rest.api.data.PeopleMainData;
import ru.simplgroupp.rest.api.data.PersonalData;
import ru.simplgroupp.rest.api.data.ReferenceData;
import ru.simplgroupp.rest.api.data.user.*;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Сервис для обработки данные личного кабинета
 */
@RequestScoped
public class UserSettingsDataService {
    private static Logger logger = Logger.getLogger(UserSettingsDataService.class.getName());

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
    private IFIASService fiasServ;

    @EJB
    private AddressBeanLocal addressBean;

    @Inject
    private UserSettingsCacheService cacheService;

    @Inject
    private AiService aiService;


    /**
     * данные пользователя
     *
     * @param user авторизированный пользователь
     * @return трансферный объект
     */
    public PeopleMain getPeopleMain(Users user) {
        return peopleDAO.getPeopleMain(user.getPeopleMain().getId(),
                Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_CONTACT,
                        PeopleMain.Options.INIT_PEOPLE_CONTACT_ALL, PeopleMain.Options.INIT_PEOPLE_MISC,
                        PeopleMain.Options.INIT_ADDRESS, PeopleMain.Options.INIT_SPOUSE,
                        PeopleMain.Options.INIT_DOCUMENT, PeopleMain.Options.INIT_EMPLOYMENT,
                        PeopleMain.Options.INIT_ACCOUNT));
    }

    /**
     * Все данные о пользователе для отправки в веб
     *
     * @param peopleMain трансферный объект пользователя
     * @return сущность которая содержит практически все даннные пользователя
     */
    public PeopleMainData getPeopleMainData(PeopleMain peopleMain) {
        PeopleMainData peopleMainWeb = new PeopleMainData();

        peopleMainWeb.setPersonal(initPersonalData(peopleMain));
        peopleMainWeb.setMisc(new MiscData(peopleMain.getPeopleMiscActive()));
        peopleMainWeb.setPassport(new PassportData(peopleMain.getActivePassport()));
        peopleMainWeb.setContact(initContactData(peopleMain));
        peopleMainWeb.setAccounts(initAccounts(peopleMain.getAccounts()));


        FiasAddress fiasAddress = peopleMain.getRegisterAddress();
        AddressData addressData = new AddressData();
        if (fiasAddress != null) {
            addressData = new AddressData(fiasAddress);
            addressData.setFiasAddress(initFiasAddress(fiasAddress));
        }
        PeopleContact addressPhone = peopleMain.getHomePhoneReg();
        if (addressPhone != null) {
            addressData.setPhone(new ContactData(addressPhone));
        }
        peopleMainWeb.setAddressRegister(addressData);


        fiasAddress = peopleMain.getResidentAddress();
        addressData = new AddressData();
        if (fiasAddress != null) {
            addressData = new AddressData(fiasAddress);
            addressData.setFiasAddress(initFiasAddress(fiasAddress));
        }
        addressPhone = peopleMain.getHomePhone();
        if (addressPhone != null) {
            addressData.setPhone(new ContactData(addressPhone));
        }
        peopleMainWeb.setAddressResident(addressData);


        EmploymentData employmentData = new EmploymentData(peopleMain.getCurrentEmployment());
        fiasAddress = peopleMain.getWorkAddress();
        addressData = null;
        if (fiasAddress != null) {
            addressData = new AddressData(fiasAddress);
            addressData.setFiasAddress(initFiasAddress(fiasAddress));

            addressPhone = peopleMain.getWorkPhone();
            if (addressPhone != null) {
                addressData.setPhone(new ContactData(addressPhone));
            }
        }
        employmentData.setWork(addressData);


        peopleMainWeb.setEmployment(employmentData);

        return peopleMainWeb;
    }

    /**
     * Все данные о пользователе для отправки в веб
     * удобный вызов метода, нужно просто передать авторизированного пользователя
     *
     * @param user авторизированный пользователь
     * @return данные пользователя
     */
    public PeopleMainData getPeopleMainData(Users user) {
        return getPeopleMainData(getPeopleMain(user));
    }

    private List<AccountData> initAccounts(List<Account> accounts) {
        List<AccountData> accountDatas = new ArrayList<>();
        for (Account account : accounts) {
            accountDatas.add(initAccount(account));
        }
        return accountDatas;
    }

    private String initFiasAddress(FiasAddress fiasAddress) {
        return initFiasAddress(fiasAddress.getEntity());
    }

    private String initFiasAddress(AddressEntity address) {
        StringBuilder workPlaceFias = new StringBuilder();

        if (StringUtils.isNotBlank(address.getStreet())) {
            if (workPlaceFias.length() > 0) {
                workPlaceFias.insert(0, ";");
            }
            AddrObj addrObj = fiasServ.findAddressObject(address.getStreet());
            if (addrObj != null) {
                workPlaceFias.insert(0, addrObj.getAOGUID());
            }
        }

        if (StringUtils.isNotBlank(address.getPlace())) {
            if (workPlaceFias.length() > 0) {
                workPlaceFias.insert(0, ";");
            }
            AddrObj addrObj = fiasServ.findAddressObject(address.getPlace());
            if (addrObj != null) {
                workPlaceFias.insert(0, addrObj.getAOGUID());
            }
        }

        if (StringUtils.isNotBlank(address.getCity())) {
            if (workPlaceFias.length() > 0) {
                workPlaceFias.insert(0, ";");
            }
            AddrObj addrObj = fiasServ.findAddressObject(address.getCity());
            if (addrObj != null) {
                workPlaceFias.insert(0, addrObj.getAOGUID());
            }
        }

        if (StringUtils.isNotBlank(address.getArea())) {
            if (workPlaceFias.length() > 0) {
                workPlaceFias.insert(0, ";");
            }
            AddrObj addrObj = fiasServ.findAddressObject(address.getArea());
            if (addrObj != null) {
                workPlaceFias.insert(0, addrObj.getAOGUID());
            }
        }

        if (StringUtils.isNotBlank(address.getRegion())) {
            if (workPlaceFias.length() > 0) {
                workPlaceFias.insert(0, ";");
            }
            AddrObj addrObj = fiasServ.findAddressObject(address.getRegion());
            if (addrObj != null) {
                workPlaceFias.insert(0, addrObj.getAOGUID());
            }
        }
        return workPlaceFias.toString();
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

    private ru.simplgroupp.rest.api.data.ContactData initContactData(PeopleMain peopleMain) {
        ru.simplgroupp.rest.api.data.ContactData contactData = new ru.simplgroupp.rest.api.data.ContactData();

        if (peopleMain.getEmail() != null) {
            contactData.setEmail(peopleMain.getEmail().getValue());
        }
        if (peopleMain.getCellPhone() != null) {
            contactData.setPhone(peopleMain.getCellPhone().getValue());
        }
        if (peopleMain.getVk() != null) {
            contactData.setVkId(peopleMain.getVk().getId());
            contactData.setVk(peopleMain.getVk().getValue());
        }
        if (peopleMain.getFb() != null) {
            contactData.setFbId(peopleMain.getFb().getId());
            contactData.setFb(peopleMain.getFb().getValue());
        }
        if (peopleMain.getMm() != null) {
            contactData.setMmId(peopleMain.getMm().getId());
            contactData.setMm(peopleMain.getMm().getValue());
        }
        if (peopleMain.getOdk() != null) {
            contactData.setOdkId(peopleMain.getOdk().getId());
            contactData.setOdk(peopleMain.getOdk().getValue());
        }

//        if (peopleMain.getPeopleMiscActive() != null) {
//            contactData.setCar(peopleMain.getPeopleMiscActive().getCar());
//        }
//
//        if(peopleMain.getPeopleMiscActive()!=null)
//        {
//        	contactData.setHasDriverDoc(peopleMain.getPeopleMiscActive().getDriverLicense());
//        }
//        contactData.setInn(peopleMain.getInn());
//        contactData.setSnils(peopleMain.getSnils());
//
//        List<DocumentMedia> docList = peopleMain.getDocumentMedia();
//        Integer passportid = refBook.findByCodeIntegerEntity(RefHeader.DOCUMENT_SCAN_TYPE, DocumentMedia.SCAN_TYPE_PASP).getId();
//        Integer innid = refBook.findByCodeIntegerEntity(RefHeader.DOCUMENT_SCAN_TYPE, DocumentMedia.SCAN_TYPE_INN).getId();
//        Integer snilsid = refBook.findByCodeIntegerEntity(RefHeader.DOCUMENT_SCAN_TYPE, DocumentMedia.SCAN_TYPE_SNILS).getId();
//        Integer driveid = refBook.findByCodeIntegerEntity(RefHeader.DOCUMENT_SCAN_TYPE, DocumentMedia.SCAN_TYPE_DRIVE).getId();
//        for(DocumentMedia documentMedia : docList){
//
//            if(documentMedia.getScanType().getId().equals(passportid)){
//                contactData.setPassportDoc(documentMedia.getId());
//            }
//            if(documentMedia.getScanType().getId().equals(driveid)){
//                contactData.setDriveDoc(documentMedia.getId());
//            }
//            if(documentMedia.getScanType().getId().equals(innid)){
//                contactData.setInnDoc(documentMedia.getId());
//            }
//            if(documentMedia.getScanType().getId().equals(snilsid)){
//                contactData.setSnilsDoc(documentMedia.getId());
//            }
//        }
        return contactData;
    }

    private PersonalData initPersonalData(PeopleMain peopleMain) {
        PersonalData personalData = new PersonalData();
        personalData.setSurname(peopleMain.getPeoplePersonalActive().getSurname());
        personalData.setName(peopleMain.getPeoplePersonalActive().getName());
        personalData.setMidname(peopleMain.getPeoplePersonalActive().getMidname());
        personalData.setBirthdate(Convertor.dateToString(peopleMain.getPeoplePersonalActive().getBirthDate(), "dd / MM / yyyy"));
        personalData.setBirthplace(peopleMain.getPeoplePersonalActive().getBirthPlace());
        personalData.setGenderId(peopleMain.getPeoplePersonalActive().getGender().getCodeInteger());
        personalData.setGender(peopleMain.getPeoplePersonalActive().getGender().getName());
        personalData.setChildren(peopleMain.getPeopleMiscActive().getChildren() == null ? 0 : peopleMain.getPeopleMiscActive().getChildren());
        personalData.setMarriageId(peopleMain.getPeopleMiscActive().getMarriage() == null ? null : peopleMain.getPeopleMiscActive().getMarriage().getCodeInteger());

//        personalData.setMarriage(peopleMain.getPeopleMiscActive().getMarriage() == null ? "" : peopleMain.getPeopleMiscActive().getMarriage().getName());
//        if (peopleMain.getSpouseActive() != null && peopleMain.getSpouseActive().getPeopleMainSpouse() != null && peopleMain.getSpouseActive().getPeopleMainSpouse().getPeoplePersonalActive() != null) {
//            PeopleMain marriage = peopleMain.getSpouseActive().getPeopleMainSpouse();
//            personalData.setSurnameMarriage(marriage.getPeoplePersonalActive().getSurname());
//            personalData.setNameMarriage(marriage.getPeoplePersonalActive().getName());
//            personalData.setMidnameMarriage(marriage.getPeoplePersonalActive().getMidname());
//            if (marriage.getPeoplePersonalActive().getBirthDate() != null) {
//                personalData.setDateBirthMarriage(Convertor.dateToString(marriage.getPeoplePersonalActive().getBirthDate(), "dd / MM / yyyy"));
//            }
//            if (marriage.getCellPhone() != null) {
//                personalData.setPhoneMarriage(marriage.getCellPhone().getValue());
//            }
//            if (peopleMain.getSpouseActive().getTypeWork() != null) {
//                personalData.setTypeworkMarriage(peopleMain.getSpouseActive().getTypeWork().getCodeInteger());
//            }
//            personalData.setDatabegMarriage(Convertor.dateToString(peopleMain.getSpouseActive().getDatabeg(), "dd / MM / yyyy"));
//        }
//        personalData.setCar(peopleMain.getPeopleMiscActive().getCar());
//        personalData.setInn(peopleMain.getInn());
//        personalData.setSnils(peopleMain.getSnils());

        return personalData;
    }

    public List<ReferenceData> transportReferences(List<Reference> references) {
        List<ReferenceData> referencesData = new ArrayList<>();
        for (Reference reference : references) {
            referencesData.add(new ReferenceData(reference));
        }

        return referencesData;
    }

    /**
     * основные данные человека
     *
     * @param user авторизированный пользователь
     * @return
     */
    public MainData getMainData(Users user) {
        PeopleMainData peopleMainData = getPeopleMainData(user);
        MainData mainData = new MainData(peopleMainData.getPersonal());

        mainData.setEmail(peopleMainData.getContact().getEmail());
        mainData.setPhone(peopleMainData.getContact().getPhone());
        mainData.setGenderTypes(transportReferences(refBook.getGenders()));

        return mainData;
    }

    /**
     * сохраняет основные данные человека (фио, др)
     *
     * @param mainData данные
     * @param user     пользователь авторизированный
     * @return возвращает OK если все хорошо
     */
    public String saveMainData(MainData mainData, Users user) throws PeopleException {
        if (mainData.getSurname().isEmpty()) {
            throw new PeopleException("surname - Поле обязательно для заполнения");
        }
        if (mainData.getName().isEmpty()) {
            throw new PeopleException("name - Поле обязательно для заполнения");
        }
        if (mainData.getMiddlename().isEmpty()) {
            throw new PeopleException("midname - Поле обязательно для заполнения");
        }
        if (mainData.getBirthday().isEmpty()) {
            throw new PeopleException("birthdate - Поле обязательно для заполнения");
        }
        if (mainData.getBirthplace().isEmpty()) {
            throw new PeopleException("birthplace - Поле обязательно для заполнения");
        }
        if (mainData.getGender() == null) {
            throw new PeopleException("gender - Поле обязательно для заполнения");
        }


        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(),
                Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_MISC, PeopleMain.Options.INIT_SPOUSE));

        PeoplePersonal personal = peopleMain.getPeoplePersonalActive();
        personal.setSurname(mainData.getSurname());
        personal.setName(mainData.getName());
        personal.setMidname(mainData.getMiddlename());
        personal.setBirthDate(Convertor.toDate(mainData.getBirthday(), "dd / MM / yyyy"));
        personal.setBirthPlace(mainData.getBirthplace());
        personal.setGender(refBook.getGender(mainData.getGender()));
        peopleBean.changePersonalData(personal, peopleMain.getId(), Partner.CLIENT, new Date());


        return "OK";
    }

    /**
     * возвращает рабочие данные для страницы изменения профиля
     *
     * @param user пользователь который авторизирован
     * @return данные по занятости
     */
    public EmploymentData getEmploymentData(Users user) {
        PeopleMainData peopleMainData = getPeopleMainData(user);
        EmploymentData employmentData = peopleMainData.getEmployment();

        employmentData.setEducationTypes(transportReferences(refBook.getEducationTypes()));
        employmentData.setEmployTypes(transportReferences(refBook.getEmployTypes()));
        employmentData.setOrganizationTypes(transportReferences(refBook.getOrganizationTypes()));
        employmentData.setProfessionTypes(transportReferences(refBook.getProfessionTypes()));
        employmentData.setExtSalaryTypes(transportReferences(refBook.getExtSalaryTypes()));
        employmentData.setDateStartWorkTypes(transportReferences(refBook.getTimeRanges()));


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
    public String saveEmploymentData(EmploymentData employmentData, Users user) throws PeopleException {
        saveEmployment(employmentData, user);
        saveAddressData(employmentData.getWork(), FiasAddress.WORKING_ADDRESS, user);

        if (employmentData.getWork() == null) {
            saveContactData(null, PeopleContact.CONTACT_WORK_PHONE, user);
        } else {
            saveContactData(employmentData.getWork().getPhone(), PeopleContact.CONTACT_WORK_PHONE, user);
        }

        return "OK";
    }

    /**
     * сохраняет данные о занятости
     *
     * @param employmentData данные
     * @param user           авторизированный пользователь
     * @return возвращает OK если все хорошо
     * @throws PeopleException
     */
    public String saveEmployment(EmploymentData employmentData, Users user) throws PeopleException {
        if (employmentData.getEducationId() == null) {
            throw new PeopleException("Образование обязательно для заполнения");
        }
        if (employmentData.getTypeWorkId() == null) {
            throw new PeopleException("Занятость обязательно для заполнения");
        }
        if (employmentData.getSalary() == null) {
            throw new PeopleException("Доход обязателен для заполнения");
        }


        //сохраняем информацию о занятости
        PeopleMain ppl = peopleDAO.getPeopleMain(user.getPeopleMain().getId(),
                Utils.setOf(PeopleMain.Options.INIT_EMPLOYMENT, PeopleMain.Options.INIT_ADDRESS,
                        PeopleMain.Options.INIT_PEOPLE_CONTACT));
        Employment currentEmployment = ppl.getCurrentEmployment();


        // если нет адреса работы то вообщем-то работы нету совсем
        // тогда создаем новую сущность где укажем только нужные параметры
        // иначе мы указываем данные с работы
        if (employmentData.getWork() == null) {
            currentEmployment = new Employment();
        } else {
            currentEmployment.setPlaceWork(employmentData.getPlaceWork());
            currentEmployment.setOccupation(employmentData.getOccupation());
            currentEmployment.setExtSalary(employmentData.getExtSalary());

            if (employmentData.getProfessionId() != null) {
                if (refBook.getOrganizationType(employmentData.getProfessionId()) != null) {
                    currentEmployment.setProfession(refBook.getOrganizationType(employmentData.getProfessionId()));
                }
            } else {
                currentEmployment.setProfession(null);
            }

            if (employmentData.getDateStartWorkId() != null) {
                if (refBook.getTimeRange(employmentData.getDateStartWorkId()) != null) {
                    currentEmployment.setDateStartWorkId(refBook.getTimeRange(employmentData.getDateStartWorkId()));
                }
            } else {
                currentEmployment.setDateStartWorkId(null);
            }

            if (StringUtils.isNotEmpty(employmentData.getDateStartWork())) {
                currentEmployment.setDateStartWork(Convertor.toDate(employmentData.getDateStartWork(), "dd / MM / yyyy"));
            } else {
                currentEmployment.setDateStartWork(null);
            }
        }


        // обязательные параметры
        if (employmentData.getEducationId() != null) {
            if (refBook.getEducationType(employmentData.getEducationId()) != null) {
                currentEmployment.setEducation(refBook.getEducationType(employmentData.getEducationId()));
            }
        }
        if (employmentData.getTypeWorkId() != null) {
            if (refBook.getEmployType(employmentData.getTypeWorkId()) != null) {
                currentEmployment.setTypeWork(refBook.getEmployType(employmentData.getTypeWorkId()));
            }
        }
        currentEmployment.setSalary(employmentData.getSalary());


        // доп параметры которые не зависят от занятости
        if (employmentData.getExtSalaryId() != null) {
            if (refBook.getExtSalaryType(employmentData.getExtSalaryId()) != null) {
                currentEmployment.setExtSalaryId(refBook.getExtSalaryType(employmentData.getExtSalaryId()));
            }

        } else {
            currentEmployment.setExtSalaryId(null);
        }

        currentEmployment.setExtSalary(employmentData.getExtSalary());
        currentEmployment.setExtCreditSum(employmentData.getExtCreditSum());


        peopleBean.changeEmployment(currentEmployment, user.getPeopleMain().getId(), new Date());

        return "OK";
    }

    /**
     * возвращает паспортные данные для страницы изменения профиля
     *
     * @param user пользователь который авторизирован
     * @return данные паспорта, адрес проживания, адрес прописки
     */
    public FullPassportData getFullPassportData(Users user) {
        PeopleMainData peopleMainData = getPeopleMainData(user);
        FullPassportData fullPassportData = new FullPassportData(peopleMainData);

        fullPassportData.setRealtyTypes(transportReferences(refBook.getRealtyTypes()));
        fullPassportData.getMisc().setMarriageTypes(transportReferences(refBook.getMarriageTypes()));
        fullPassportData.getMisc().setRegDateTypes(transportReferences(refBook.getTimeRanges()));

        return fullPassportData;
    }

    /**
     * сохраняет паспортные данные для страницы изменения профиля
     *
     * @param fullPassportData данные паспорта, адрес проживания, адрес прописки
     * @return строку OK если все ок, иначе бросает исключение
     */
    public String saveFullPassportData(FullPassportData fullPassportData, Users user) throws PeopleException {
//        savePassport(fullPassportData.getPassportData(), user);
        saveAddressData(fullPassportData.getRegistration(), FiasAddress.REGISTER_ADDRESS, user);

        if (fullPassportData.getRegistration().isSame()) {
            saveAddressData(null, FiasAddress.RESIDENT_ADDRESS, user);
        } else {
            saveAddressData(fullPassportData.getResidence(), FiasAddress.RESIDENT_ADDRESS, user);
        }
        saveMiscData(fullPassportData.getMisc(), user);

        saveContactData(fullPassportData.getRegistration().getPhone(), PeopleContact.CONTACT_HOME_REGISTER_PHONE, user);
        saveContactData(fullPassportData.getResidence().getPhone(), PeopleContact.CONTACT_HOME_PHONE, user);


        return "OK";
    }

    /**
     * сохраняет паспорт
     *
     * @param passport паспорт
     * @param user     пользователь
     * @return строку OK если все ок, иначе бросает исключение
     * @throws PeopleException бросает исключение при различных ошибках в данных
     */
    public String savePassport(PassportData passport, Users user) throws PeopleException {
        if (passport.getSeries().length() != 4) {
            throw new PeopleException("series - Поле обязательно для заполнения");
        }
        if (passport.getNumber().length() != 6) {
            throw new PeopleException("number - Поле обязательно для заполнения");
        }

        if (StringUtils.isEmpty(passport.getDocDate())) {
            throw new PeopleException("docDate - Поле обязательно для заполнения");
        } else {
            Date date = Convertor.toDate(passport.getDocDate(), "dd / MM / yyyy");
            if (!StaticFuncs.checkDate(date)) {
                throw new PeopleException("docDate - Паспорт не может быть выдан этой датой");
            }
        }
        if (StringUtils.isEmpty(passport.getDocOrg())) {
            throw new PeopleException("docOrg - Поле обязательно для заполнения");
        }
        if (StringUtils.isEmpty(passport.getDocOrgCode())) {
            throw new PeopleException("docOrgCode - Поле обязательно для заполнения");
        }


        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_DOCUMENT));

        //сохраняем документ
        Documents documents = peopleMain.getActivePassport();
        documents.setSeries(passport.getSeries());
        documents.setNumber(passport.getNumber());
        documents.setDocDate(Convertor.toDate(passport.getDocDate(), "dd / MM / yyyy"));
        documents.setDocOrg(passport.getDocOrg());
        documents.setDocOrgCode(passport.getDocOrgCode());
        documents.setIsUploaded(false);
        documents.setDateChange(new Date());

        peopleBean.changeDocument(documents, user.getPeopleMain().getId(), Partner.CLIENT);

        return "OK";
    }

    /**
     * остальные данные человека
     *
     * @param user авторизированный пользователь
     * @return данные человека
     */
    public MiscData getMiscData(Users user) {
        PeopleMainData peopleMainData = getPeopleMainData(user);
        MiscData miscData = peopleMainData.getMisc();

        miscData.setMarriageTypes(transportReferences(refBook.getMarriageTypes()));
        miscData.setRealtyTypes(transportReferences(refBook.getRealtyTypes()));

        return miscData;
    }

    /**
     * сохраняет остальные данные
     *
     * @param miscData данные
     * @param user     пользователь
     * @return возвращает OK если все хорошо, иначе бросает исключения
     * @throws PeopleException
     */
    public String saveMiscData(MiscData miscData, Users user) throws PeopleException {
        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(),
                Utils.setOf(PeopleMain.Options.INIT_PEOPLE_MISC, PeopleMain.Options.INIT_PEOPLE_CONTACT));


        PeopleMisc misc = peopleMain.getPeopleMiscActive();

        if (miscData.getRealty() == null) {
            misc.setRealty(null);
        } else {
            misc.setRealty(refBook.getRealtyType(miscData.getRealty()));
        }
        if (miscData.getRegDate() != null) {
            misc.setRegDate(Convertor.toDate(miscData.getRegDate(), "dd / MM / yyyy"));
        }
        if (miscData.getRealtyDate() != null) {
            misc.setRealtyDate(Convertor.toDate(miscData.getRealtyDate(), "dd / MM / yyyy"));
        }
        if (miscData.getChildren() != null) {
            misc.setChildren(miscData.getChildren());
        }
        if (miscData.getMarriage() != null) {
            misc.setMarriage(refBook.getMarriageType(miscData.getMarriage()));
        }

        if (miscData.getRegDateId() != null) {
            misc.setRegDateId(refBook.getTimeRange(miscData.getRegDateId()));
        }

        if (miscData.getRealtyDateId() != null) {
            misc.setRealtyDateId(refBook.getTimeRange(miscData.getRealtyDateId()));
        }

        peopleBean.changeMiscData(misc, peopleMain.getId(), Partner.CLIENT, new Date());


        return "OK";
    }

    /**
     * сохраняет адрес
     *
     * @param address адрес
     * @param user    авторизрованный пользователь
     * @return возвращает OK если все хорошо, иначе бросает исключения
     * @throws PeopleException
     */
    public String saveAddressData(AddressData address, int addressType, Users user) throws PeopleException {
        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(), null);

        if (address != null) {
            AddressEntity addressEntity = peopleBean.findAddressActive(peopleMain.getEntity(), addressType);

            if (addressEntity == null) {
                addressEntity = new AddressEntity();
                addressEntity.setPeopleMainId(peopleMain.getEntity());
                addressEntity.setPartnersId(refBook.getPartnerEntity(Partner.CLIENT));
                addressEntity.setAddrtype(refBook.findByCodeIntegerEntity(RefHeader.ADDRESS_TYPE, addressType));
                addressEntity.setDatabeg(new Date());
                addressEntity.setIsactive(ActiveStatus.ACTIVE);
                addressEntity.setCountry(refBook.getCountryEntity(BaseAddress.COUNTRY_RUSSIA));
            }

            AddrObj addressObject = fiasServ.findAddressObjectByAoguid(address.getFiasAddress());
            addressBean.fillAddress(addressEntity, addressObject);

            addressEntity.setHouse(address.getHouse());
            addressEntity.setCorpus(address.getKorpus());
            addressEntity.setBuilding(address.getBuilding());
            addressEntity.setFlat(address.getFlat());
            if (address.isSame() != null) {
                addressEntity.setIsSame((address.isSame()).equals(true) ? 1 : 0);
            }

            FiasAddress registerAddress = new FiasAddress(addressEntity);

            peopleBean.changeAddress(registerAddress, peopleMain.getId(), Partner.CLIENT, new Date());
        } else {
            AddressEntity addressEntity = peopleBean.findAddressActive(peopleMain.getEntity(), addressType);
            if (addressEntity != null) {
                peopleBean.closeAddress(addressEntity, new Date());
            }
        }


        return "OK";
    }

    /**
     * сохраняет телефонный номер в жилище
     *
     * @param contactData данные
     * @param contactType тип телефонного номера
     * @param user        пользователь
     * @return возвращает OK если все хорошо
     */
    public String saveContactData(ContactData contactData, int contactType, Users user) throws PeopleException {
        PeopleContact peoplePhone = peopleBean.initPeopleContact(user.getPeopleMain(), contactType);

        if (contactData != null && StringUtils.isNotEmpty(contactData.getValue())) {
            peoplePhone.setIsActive(ActiveStatus.ACTIVE);
            peoplePhone.setValue(contactData.getValue());
            peoplePhone.setAvailable(contactData.getAvailable());
            peopleBean.changeContact(peoplePhone, user.getPeopleMain().getId(), Partner.CLIENT);
        } else {
            if (peoplePhone.getId() != null) {
                peopleBean.changeContactActive(peoplePhone.getId());
            }
        }

        return "OK";
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
    public boolean savePasswordData(PasswordData passwordData, Users user) throws ParseException, PeopleException, KassaException {
        PeopleMain peopleMain = peopleDAO.getPeopleMain(user.getPeopleMain().getId(),
                Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL,
                        PeopleMain.Options.INIT_PEOPLE_MISC, PeopleMain.Options.INIT_SPOUSE));


        // если нету ни старого пароля и нет нового значит пароль просто не меняем и говори что все ок
        if (StringUtils.isEmpty(passwordData.getOldPassword()) && StringUtils.isEmpty(passwordData.getNewPassword())) {
            return false;
        }

        String oldPassword = DigestUtils.md5Hex(passwordData.getOldPassword());
        if (!oldPassword.equals(user.getPassword())) {
            throw new PeopleException("старый пароль введен не верно");
        }

        if (StringUtils.isEmpty(passwordData.getNewPassword()) || StringUtils.isEmpty(passwordData.getNewPassword2())) {
            throw new PeopleException("пароль не может быть пустым");
        }

        if (!passwordData.getNewPassword().equals(passwordData.getNewPassword2())) {
            throw new PeopleException("пароли не совпадают");
        }

        if (passwordData.getNewPassword().length() < 5) {
            throw new PeopleException("пароль должен быть минимум 5 символов длинной");
        }


        userBean.changePassword(peopleMain.getId(), passwordData.getNewPassword());

        return true;
    }

    /**
     * пытается сохранить новую почту человека используя хеш
     *
     * @param mainData данные основные вместе с почтой
     * @param user     авторизированный пользователь
     * @return возвращает всегда OK, но сохранняет когда возможно
     * @throws PeopleException
     */
    public boolean saveEmail(MainData mainData, Users user) throws PeopleException {
        if (!aiService.isCanEdit()) {
            logger.info("редактирование запрещено");
            return false;
        }

        // если или хеша нет или кода, проверять нечего
        if (StringUtils.isEmpty(mainData.getEmailHash()) || StringUtils.isEmpty(mainData.getEmailCode())) {
            return false;
        }

        String receivedHash = mainData.getEmailHash();
        String receivedCode = mainData.getEmailCode();
        String calculatedHash = StaticFuncs.md5("salt" + mainData.getEmail() + "supersalt" + receivedCode + "megasalt");

        if (!receivedHash.equals(calculatedHash)) {
            logger.info("хеш почты не верный");
            return false;
        }

        saveContactData(new ContactData(mainData.getEmail(), null), PeopleContact.CONTACT_EMAIL, user);


        return true;
    }

    /**
     * пытается сохранить новый мобильный телефон человека
     *
     * @param mainData данные с телефоном
     * @param user     авторизированный пользователь
     * @return возврщает всегда ОК, что бы убрать исключения
     * @throws PeopleException
     */
    public boolean savePhone(MainData mainData, Users user) throws PeopleException {
        if (!aiService.isCanEdit()) {
            logger.info("редактирование запрещено");
            return false;
        }


        if (StringUtils.isEmpty(mainData.getSmsHash()) || StringUtils.isEmpty(mainData.getSmsCode())) {
            return false;
        }

        String receivedHash = mainData.getSmsHash();
        String receivedCode = mainData.getSmsCode();
        String phone = Convertor.fromMask(mainData.getPhone());
        String calculatedHash = StaticFuncs.md5("salt" + phone + "supersalt" + receivedCode + "megasalt");

        if (!receivedHash.equals(calculatedHash)) {
            logger.info("хеш телефона не верный");
            return false;
        }

        saveContactData(new ContactData(phone, null), PeopleContact.CONTACT_CELL_PHONE, user);

        return true;
    }
}

