package ru.simplgroupp.rest.api.data.prolong;

/**
 * Ответ на формирование или подтверждение продления по кредиту.
 */
public class SmsResponseData {
    private boolean success;
    private String msg;
    private String agreement;
    private String msisdn;

    /**
     * Принак успешности операции.
     * @return true - успешнл, false - не успешно.
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Сообщение с описанием причины в случае не успешной операции.
     * @return сообщение с описанием причины в случае не успешной операции.
     */
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Обновленная оферта в случае успешной операции.
     * @return обновленная оферта в случае успешной операции.
     */
    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
}

