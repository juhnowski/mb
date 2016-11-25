package ru.simplgroupp.rest.api.data;

/**
 * персональные данные
 */
public class PersonalData extends AbstractUserData {

    /**
     * фамилия
     */
    private String surname;
    /**
     * имя
     */
    private String name;
    /**
     * отчество
     */
    private String midname;

    /**
     * пол
     */
    private Integer genderId;
    private String gender;

    /**
     * дата рождения
     */
    private String birthdate;
    /**
     * место рождения
     */
    private String birthplace;

    /**
     * кол-во детей
     */
    private Integer children;

    /**
     * есть ли авто
     */
    private Boolean car;

    /**
     * Семейной положение
     */
    private Integer marriageId;
    private String marriage;

    /**
     * фамилия супруга
     */
    private String surnameMarriage;
    /**
     * имя супруга
     */
    private String nameMarriage;
    /**
     * отчество супруга
     */
    private String midnameMarriage;

    /***
     * дата рождения супруга
     */
    private String dateBirthMarriage;

    /**
     * телефон супруга
     */
    private String phoneMarriage;

    /**
     * занятость супруга
     */
    private Integer typeworkMarriage;

    /**
     * дата начала совместной жизни
     */
    private String databegMarriage;
    /**
     * inn
     */
    private String inn;
    /**
     * снилс
     */
    private String snils;


    /**
     * Контактный телефон
     */
    private String phone;
    /**
     * электронная почта
     */
    private String email;

    /**
     * новый пароль при смене
     */
    private String newPassword;
    private String newPasswordConfirm;
    private String oldPass;

    /**
     * верификация контактов
     */
    private String emailCode;
    private String emailCodeHash;
    private String phoneCodeVerif;
    private String phoneCodeVerifHash;


    public String getSurname() {
        return surname;
    }


    public void setSurname(String surname) {
        this.surname = surname;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getMidname() {
        return midname;
    }


    public void setMidname(String midname) {
        this.midname = midname;
    }


    public Integer getGenderId() {
        return genderId;
    }


    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getBirthdate() {
        return birthdate;
    }


    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }


    public String getBirthplace() {
        return birthplace;
    }


    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }


    public Integer getChildren() {
        return children;
    }


    public void setChildren(Integer children) {
        this.children = children;
    }


    public Boolean getCar() {
        return car;
    }


    public void setCar(Boolean car) {
        this.car = car;
    }


    public Integer getMarriageId() {
        return marriageId;
    }


    public void setMarriageId(Integer marriageId) {
        this.marriageId = marriageId;
    }


    public String getMarriage() {
        return marriage;
    }


    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }


    public String getSurnameMarriage() {
        return surnameMarriage;
    }


    public void setSurnameMarriage(String surnameMarriage) {
        this.surnameMarriage = surnameMarriage;
    }


    public String getNameMarriage() {
        return nameMarriage;
    }


    public void setNameMarriage(String nameMarriage) {
        this.nameMarriage = nameMarriage;
    }


    public String getMidnameMarriage() {
        return midnameMarriage;
    }


    public void setMidnameMarriage(String midnameMarriage) {
        this.midnameMarriage = midnameMarriage;
    }


    public String getDateBirthMarriage() {
        return dateBirthMarriage;
    }


    public void setDateBirthMarriage(String dateBirthMarriage) {
        this.dateBirthMarriage = dateBirthMarriage;
    }


    public String getPhoneMarriage() {
        return phoneMarriage;
    }


    public void setPhoneMarriage(String phoneMarriage) {
        this.phoneMarriage = phoneMarriage;
    }


    public Integer getTypeworkMarriage() {
        return typeworkMarriage;
    }


    public void setTypeworkMarriage(Integer typeworkMarriage) {
        this.typeworkMarriage = typeworkMarriage;
    }


    public String getDatabegMarriage() {
        return databegMarriage;
    }


    public void setDatabegMarriage(String databegMarriage) {
        this.databegMarriage = databegMarriage;
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


    public String getNewPassword() {
        return newPassword;
    }


    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }


    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }


    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }


    public String getOldPass() {
        return oldPass;
    }


    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }


    public String getEmailCode() {
        return emailCode;
    }


    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }


    public String getEmailCodeHash() {
        return emailCodeHash;
    }


    public void setEmailCodeHash(String emailCodeHash) {
        this.emailCodeHash = emailCodeHash;
    }


    public String getPhoneCodeVerif() {
        return phoneCodeVerif;
    }


    public void setPhoneCodeVerif(String phoneCodeVerif) {
        this.phoneCodeVerif = phoneCodeVerif;
    }


    public String getPhoneCodeVerifHash() {
        return phoneCodeVerifHash;
    }


    public void setPhoneCodeVerifHash(String phoneCodeVerifHash) {
        this.phoneCodeVerifHash = phoneCodeVerifHash;
    }
}
