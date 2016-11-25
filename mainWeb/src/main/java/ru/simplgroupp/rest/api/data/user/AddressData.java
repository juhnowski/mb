package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.transfer.FiasAddress;

import java.io.Serializable;

/**
 * Адрес
 */
public class AddressData implements Serializable {
    /**
     * строка адреса ФИАС, разделенная точкой с запятой, требуется для составления адреса в интерфейсе
     */
    private String fiasAddress;

    /**
     * Регион
     */
    private String regionName;

    /**
     * Город
     */
    private String cityName;

    /**
     * Село
     */
    private String placeName;

    /**
     * Район сельский
     */
    private String areaName;

    /**
     * номер дома
     */
    private String house;

    /**
     * строение
     */
    private String building;

    /**
     * корпус
     */
    private String korpus;

    /**
     * квартира
     */
    private String flat;

    private String descriptionToCity;
    
    private Boolean same;

    private ContactData phone;


    public AddressData() {
    }

    public AddressData(FiasAddress fiasAddress) {
        this.regionName = fiasAddress.getRegionName();
        this.areaName = fiasAddress.getAreaName();
        this.cityName = fiasAddress.getCityName();
        this.placeName = fiasAddress.getPlaceName();
        this.house = fiasAddress.getHouse();
        this.building = fiasAddress.getBuilding();
        this.korpus = fiasAddress.getCorpus();
        this.flat = fiasAddress.getFlat();
        this.same = fiasAddress.isSame();
        this.descriptionToCity=fiasAddress.getDescriptionToCity();
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getFiasAddress() {
        return fiasAddress;
    }

    public void setFiasAddress(String fiasAddress) {
        this.fiasAddress = fiasAddress;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getKorpus() {
        return korpus;
    }

    public void setKorpus(String korpus) {
        this.korpus = korpus;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public Boolean isSame() {
        return same;
    }

    public void setSame(Boolean same) {
        this.same = same;
    }

    public ContactData getPhone() {
        return phone;
    }

    public void setPhone(ContactData phone) {
        this.phone = phone;
    }
    
    public String getDescriptionToCity(){
    	return descriptionToCity;
    }
}
