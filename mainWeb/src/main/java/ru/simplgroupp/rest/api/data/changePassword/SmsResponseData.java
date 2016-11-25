package ru.simplgroupp.rest.api.data.changePassword;

/**
 * Ответ на изменение пароля.
 */
public class SmsResponseData {
    private boolean success;
    private String msg;

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
}
