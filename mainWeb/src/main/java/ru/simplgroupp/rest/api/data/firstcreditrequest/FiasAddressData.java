package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class FiasAddressData implements Serializable {
    private String fiasId;
    private String home;
    private String builder;
    private String korpus;
    private String apartment;
    private String realtyDate;
    private String residentPhone;
    private String phone;


    public String getFiasId() {
        return fiasId;
    }

    public void setFiasId(String fiasId) {
        this.fiasId = fiasId;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getBuilder() {
        return builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public String getKorpus() {
        return korpus;
    }

    public void setKorpus(String korpus) {
        this.korpus = korpus;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getRealtyDate() {
        return realtyDate;
    }

    public void setRealtyDate(String realtyDate) {
        this.realtyDate = realtyDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResidentPhone() {
        return residentPhone;
    }

    public void setResidentPhone(String residentPhone) {
        this.residentPhone = residentPhone;
    }
}
