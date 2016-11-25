package ru.simplgroupp.rest.api.data;

/**
 * Created by PARFENOV on 29.05.2015.
 *
 * контактные данные
 */
public class ContactData {

    private String phone;
    private String email;
    private Integer vkId;
    private String vk;
    private Integer odkId;
    private String odk;
    private Integer mmId;
    private String mm;
    private Integer fbId;
    private String fb;
    /**
     * есть ли авто
     */
    private Boolean car;
    /**
     * есть ли водительское удостоверение
     */
    private Boolean hasDriverDoc;
    /**
     * inn
     */
    private String inn;
    /**
     * снилс
     */
    private String snils;

    /**
     * ксерокопии документов
     */
    private Integer passportDoc;
    private Integer innDoc;
    private Integer snilsDoc;
    private Integer driveDoc;


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getVk() {
        return vk;
    }


    public void setVk(String vk) {
        this.vk = vk;
    }


    public String getOdk() {
        return odk;
    }


    public void setOdk(String odk) {
        this.odk = odk;
    }


    public String getMm() {
        return mm;
    }


    public void setMm(String mm) {
        this.mm = mm;
    }


    public String getFb() {
        return fb;
    }


    public void setFb(String fb) {
        this.fb = fb;
    }


    public Integer getVkId() {
        return vkId;
    }


    public void setVkId(Integer vkId) {
        this.vkId = vkId;
    }


    public Integer getOdkId() {
        return odkId;
    }


    public void setOdkId(Integer odkId) {
        this.odkId = odkId;
    }


    public Integer getMmId() {
        return mmId;
    }


    public void setMmId(Integer mmId) {
        this.mmId = mmId;
    }


    public Integer getFbId() {
        return fbId;
    }


    public void setFbId(Integer fbId) {
        this.fbId = fbId;
    }


    public Boolean getCar() {
        return car;
    }


    public void setCar(Boolean car) {
        this.car = car;
    }


    public String getInn() {
        return inn;
    }


    public void setInn(String inn) {
        this.inn = inn;
    }


    public String getSnils() {
        return snils;
    }


    public void setSnils(String snils) {
        this.snils = snils;
    }


	public Boolean getHasDriverDoc() {
		return hasDriverDoc;
	}


	public void setHasDriverDoc(Boolean hasDriverDoc) {
		this.hasDriverDoc = hasDriverDoc;
	}


    public Integer getPassportDoc() {
        return passportDoc;
    }


    public void setPassportDoc(Integer passportDoc) {
        this.passportDoc = passportDoc;
    }


    public Integer getInnDoc() {
        return innDoc;
    }


    public void setInnDoc(Integer innDoc) {
        this.innDoc = innDoc;
    }


    public Integer getSnilsDoc() {
        return snilsDoc;
    }


    public void setSnilsDoc(Integer snilsDoc) {
        this.snilsDoc = snilsDoc;
    }


    public Integer getDriveDoc() {
        return driveDoc;
    }


    public void setDriveDoc(Integer driveDoc) {
        this.driveDoc = driveDoc;
    }
}
