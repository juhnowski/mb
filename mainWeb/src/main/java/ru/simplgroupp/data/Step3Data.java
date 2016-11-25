package ru.simplgroupp.data;

/**
 * Step3Data
 * Данные передаваемые на третьем шаге в json
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 22.04.14.
 */
public class Step3Data {

    private String
            regDate,
            regphone,
            phone,
            livingFias,
            homeFact,
            builderFact,
            korpusFact,
            apartmentFact,
            id,
            groundTitle;

    private Integer
            matuche,
            ground,
            regavailable,
            available;

    public Step3Data() {
        regDate = regphone = phone = livingFias = homeFact = builderFact = korpusFact = apartmentFact = id = groundTitle = "";
        matuche = ground = regavailable = available = null;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegphone() {
        return regphone;
    }

    public void setRegphone(String regphone) {
        this.regphone = regphone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHomeFact() {
        return homeFact;
    }

    public void setHomeFact(String homeFact) {
        this.homeFact = homeFact;
    }

    public String getBuilderFact() {
        return builderFact;
    }

    public void setBuilderFact(String builderFact) {
        this.builderFact = builderFact;
    }

    public String getKorpusFact() {
        return korpusFact;
    }

    public void setKorpusFact(String korpusFact) {
        this.korpusFact = korpusFact;
    }

    public String getApartmentFact() {
        return apartmentFact;
    }

    public void setApartmentFact(String apartmentFact) {
        this.apartmentFact = apartmentFact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMatuche() {
        return matuche;
    }

    public void setMatuche(Integer matuche) {
        this.matuche = matuche;
    }

    public Integer getGround() {
        return ground;
    }

    public void setGround(Integer ground) {
        this.ground = ground;
    }

    public Integer getRegavailable() {
        return regavailable;
    }

    public void setRegavailable(Integer regavailable) {
        this.regavailable = regavailable;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer aviable) {
        this.available = aviable;
    }

    public String getGroundTitle() {
        return groundTitle;
    }

    public void setGroundTitle(String groundTitle) {
        this.groundTitle = groundTitle;
    }

    public String getLivingFias() {
        return livingFias;
    }

    public void setLivingFias(String livingFias) {
        this.livingFias = livingFias;
    }
}
