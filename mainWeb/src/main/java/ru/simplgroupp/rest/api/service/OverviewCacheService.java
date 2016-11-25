package ru.simplgroupp.rest.api.service;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 * Сервис отвечающий за кеширование данных на уровне HTTP сессии пользователя при работе с главной страницей личного кабинета.
 */
@SessionScoped
public class OverviewCacheService implements Serializable {
    private String smsCode;

    /**
     * Возвращает кешированный SMS код отправленный клиенту.
     * @return sms код.
     */
    public String getSmsCode() {
        return smsCode;
    }

    /**
     * Кеширует SMS код отправленный клиенту.
     * @param smsCode sms код.
     */
    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }
}
