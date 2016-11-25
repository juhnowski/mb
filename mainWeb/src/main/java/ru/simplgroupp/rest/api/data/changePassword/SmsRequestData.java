package ru.simplgroupp.rest.api.data.changePassword;

/**
 * Запрос на изменение пароля.
 */
public class SmsRequestData {
    private String smsCode;
    private String password;

    /**
     * SMS код подтверждения..
     * @return sms код подтверждения.
     */
    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    /**
     * Новый пароль.
     * @return новый пароль.
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
