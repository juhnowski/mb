package ru.simplgroupp.data;

/**
 * Данные по адресу для внутреннего использования
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since  22.04.14.
 */
public class AddressData {
    private String region,lplace,city,area,street;

    public void AddressData(){
        region = lplace = city = area = street = "";
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLplace() {
        return lplace;
    }

    public void setLplace(String lplace) {
        this.lplace = lplace;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
