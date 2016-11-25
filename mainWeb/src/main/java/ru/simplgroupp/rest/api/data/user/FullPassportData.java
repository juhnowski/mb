package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.rest.api.data.AbstractUserData;
import ru.simplgroupp.rest.api.data.PeopleMainData;
import ru.simplgroupp.rest.api.data.ReferenceData;

import java.util.List;

/**
 * Паспортные данные пользователя
 */
public class FullPassportData extends AbstractUserData {
    /**
     * адрес регистрации
     */
    private AddressData registration;

    /**
     * адрес проживания
     */
    private AddressData residence;

    /**
     * паспорт
     */
    private PassportData passport;

    /**
     * остальные данные
     */
    private MiscData misc;

    /**
     * типы занятия жилого помещения
     */
    private List<ReferenceData> realtyTypes;


    public FullPassportData() {
    }

    public FullPassportData(PeopleMainData peopleMainData) {
        this.passport = peopleMainData.getPassport();
        this.registration = peopleMainData.getAddressRegister();
        this.residence = peopleMainData.getAddressResident();
        this.misc = peopleMainData.getMisc();
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

    public PassportData getPassport() {
        return passport;
    }

    public void setPassport(PassportData passport) {
        this.passport = passport;
    }

    public MiscData getMisc() {
        return misc;
    }

    public void setMisc(MiscData misc) {
        this.misc = misc;
    }

    public List<ReferenceData> getRealtyTypes() {
        return realtyTypes;
    }

    public void setRealtyTypes(List<ReferenceData> realtyTypes) {
        this.realtyTypes = realtyTypes;
    }

    public static FullPassportData make() {
        return new FullPassportData();
    }
}
