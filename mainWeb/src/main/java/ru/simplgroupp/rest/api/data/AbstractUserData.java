package ru.simplgroupp.rest.api.data;

/**
 * объект с полем код для подтверждения изменения инфомации в личном кабинете
 */
public abstract class AbstractUserData {
    private String smsCode;
    private String smsHash;
    private String smsNumber;

    private String emailCode;
    private String emailHash;

    @Deprecated
    private String phoneCode;


    public String getSmsHash() {
        return smsHash;
    }

    public void setSmsHash(String smsHash) {
        this.smsHash = smsHash;
    }

    @Deprecated
    public String getPhoneCode() {
        return phoneCode;
    }

    @Deprecated
    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getSmsNumber() {
        return smsNumber;
    }

    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }
}
