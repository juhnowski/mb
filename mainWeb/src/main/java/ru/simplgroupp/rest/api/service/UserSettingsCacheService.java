package ru.simplgroupp.rest.api.service;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 * Сервис отвечающий за кеширование данных на уровне HTTP сессии пользователя при работе с главной страницей личного кабинета.
 */
@SessionScoped
public class UserSettingsCacheService implements Serializable {
    /**
     * смс код отправленный клиенту
     */
    private String smsCode;

    /**
     * код отправленный на почту клиенту
     */
    private String emailCode;

    /**
     * специальный хеш код который содержит код и номер телефона
     * что бы избежать подмены телефона
     */
    private String smsHash;

    /**
     * специальный хеш код который содержит код и почту
     * что бы избежать подмены почты
     */
    private String emailHash;


    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    public String getSmsHash() {
        return smsHash;
    }

    public void setSmsHash(String smsHash) {
        this.smsHash = smsHash;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }
}
