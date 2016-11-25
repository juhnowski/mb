package ru.simplgroupp.rest.api.data;

import ru.simplgroupp.rest.api.data.user.MiscData;
import ru.simplgroupp.rest.api.data.user.PassportData;
import ru.simplgroupp.rest.api.data.user.AddressData;
import ru.simplgroupp.rest.api.data.user.EmploymentData;

import java.util.List;

/**
 * Данные пользователя
 */
public class PeopleMainData {
    /**
     * Персональные данные
     */
    private PersonalData personal;

    /**
     * адрес регистрации
     */
    private AddressData addressRegister;

    /**
     * адрес проживания
     */
    private AddressData addressResident;

    /**
     * паспорт
     */
    private PassportData passport;

    /**
     * образование занятость
     */
    private EmploymentData employment;

    /**
     * контактная информация
     */
    private ContactData contact;

    /**
     * плтежные данные
     */
    private List<AccountData> accounts;

    /**
     * другие параметры (количество детей, холост и т.д.)
     */
    private MiscData misc;


    public PersonalData getPersonal() {
        return personal;
    }

    public void setPersonal(PersonalData personal) {
        this.personal = personal;
    }

    public AddressData getAddressRegister() {
        return addressRegister;
    }

    public void setAddressRegister(AddressData addressRegister) {
        this.addressRegister = addressRegister;
    }

    public PassportData getPassport() {
        return passport;
    }

    public void setPassport(PassportData passport) {
        this.passport = passport;
    }

    public EmploymentData getEmployment() {
        return employment;
    }

    public void setEmployment(EmploymentData employment) {
        this.employment = employment;
    }

    public ContactData getContact() {
        return contact;
    }

    public void setContact(ContactData contact) {
        this.contact = contact;
    }

    public List<AccountData> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountData> accounts) {
        this.accounts = accounts;
    }

    public AddressData getAddressResident() {
        return addressResident;
    }

    public void setAddressResident(AddressData addressResident) {
        this.addressResident = addressResident;
    }

    public MiscData getMisc() {
        return misc;
    }

    public void setMisc(MiscData misc) {
        this.misc = misc;
    }
}
