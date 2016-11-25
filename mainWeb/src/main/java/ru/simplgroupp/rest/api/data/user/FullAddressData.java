package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.rest.api.data.AbstractUserData;
import ru.simplgroupp.rest.api.data.PeopleMainData;
import ru.simplgroupp.rest.api.data.ReferenceData;

import java.util.List;

/**
 * Паспортные данные пользователя
 */
public class FullAddressData extends AbstractUserData {
    /**
     * адрес регистрации
     */
    private AddressData registration;

    /**
     * адрес проживания
     */
    private AddressData residence;

    /**
     * остальные данные
     */
    private MiscData misc;

    /**
     * дополнительный телефон по которому можно связаться
     */
    private ContactData additionalPhone;


    public FullAddressData() {
    }

    public AddressData getRegistration() {
        return registration;
    }

    public void setRegistration(AddressData registration) {
        this.registration = registration;
    }

    public AddressData getResidence() {
        return residence;
    }

    public void setResidence(AddressData residence) {
        this.residence = residence;
    }

    public MiscData getMisc() {
        return misc;
    }

    public void setMisc(MiscData misc) {
        this.misc = misc;
    }

    public ContactData getAdditionalPhone() {
        return additionalPhone;
    }

    public void setAdditionalPhone(ContactData additionalPhone) {
        this.additionalPhone = additionalPhone;
    }
}
