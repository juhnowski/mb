package ru.simplgroupp.rest.api.data;

import ru.simplgroupp.transfer.FiasAddress;

/**
 * Адрес
 */
public class AddressData {

    /**
     * region
     */
    private String regionName;
    /**
     * район сельский
     */
    private String areaName;
    /**
     * город
     */
    private String cityName;
    /**
     * село, деревня
     */
    private String placeName;
    /**
     * улица
     */
    private String streetName;
    /**
     * дом
     */
    private String house;
    /**
     * квартира
     */
    private String flat;
    /**
     * совпадает с адресом регистрации
     */
    private Integer same;


    public AddressData(FiasAddress fiasAddress) {

        this.regionName = fiasAddress.getRegionName();
        this.areaName = fiasAddress.getAreaName();
        this.cityName = fiasAddress.getCityName();
        this.placeName = fiasAddress.getPlaceName();
        this.streetName = fiasAddress.getStreetName();
        this.house = fiasAddress.getHouse();
        this.flat = fiasAddress.getFlat();
        this.same = fiasAddress.getEntity().getIsSame();
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


    public String getStreetName() {
        return streetName;
    }


    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }


    public String getHouse() {
        return house;
    }


    public void setHouse(String house) {
        this.house = house;
    }


    public String getFlat() {
        return flat;
    }


    public void setFlat(String flat) {
        this.flat = flat;
    }


    public Integer getSame() {
        return same;
    }


    public void setSame(Integer same) {
        this.same = same;
    }
}
