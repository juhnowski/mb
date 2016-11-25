package ru.simplgroupp.rest.api.data.refinance;

/**
 * Запрос на формирование или подтверждение рефинансирования по кредиту.
 */
public class SmsRequestData {
    private String smsCode;
    private double refinanceAmount;

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
     * Сумма запрашиваемого рефинансирования.
     * @return сумма запрашиваемого рефинансирования.
     */
    public double getRefinanceAmount() {
        return refinanceAmount;
    }

    public void setRefinanceAmount(double refinanceAmount) {
        this.refinanceAmount = refinanceAmount;
    }
}
