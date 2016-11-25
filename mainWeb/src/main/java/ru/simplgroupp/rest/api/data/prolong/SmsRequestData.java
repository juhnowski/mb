package ru.simplgroupp.rest.api.data.prolong;

/**
 * Запрос на формирование или подтверждение продления по кредиту.
 */
public class SmsRequestData {
    private String smsCode;
    private Integer longDays;
    private Double sumAll;

    /**
     * SMS код подтверждения. При запросе на форимрование продления поле игнорируется. При запросе на подтвержение учитывается.
     * @return sms код подтверждения.
     */
    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    /**
     * Количество дней на которое продляется кредит.
     * @return количество дней на которое продляется кредит.
     */
    public Integer getLongDays() {
        return longDays;
    }

    public void setLongDays(Integer longDays) {
        this.longDays = longDays;
    }

    /**
     * Полная сумма к выплате по кредиту после продления.
     * @return полная сумма к выплате по кредиту после продления.
     */
    public Double getSumAll() {
        return sumAll;
    }

    public void setSumAll(Double sumAll) {
        this.sumAll = sumAll;
    }
}
