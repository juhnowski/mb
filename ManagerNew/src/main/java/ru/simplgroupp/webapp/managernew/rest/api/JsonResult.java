package ru.simplgroupp.webapp.managernew.rest.api;

/**
 * Created by PARFENOV on 18.05.2015.
 * <p/>
 * ответ веб сервиса
 */
public class JsonResult<Container> {

    private boolean success; //успешно | не успешно
    private Container data; // данные
    private Exception error; //ошибка
    private String errorMessage; // сообщение об ошибке
    private Integer code; // код ошибки


    public JsonResult() {
        success = true;
    }


    public JsonResult(Container data) {
        success = true;
        this.data = data;
    }


    public JsonResult(Exception error) {
        success = false;
        this.error = error;
        this.errorMessage = error.getMessage();
        this.code = 500;
    }


    public JsonResult(Integer code, Exception error) {
        success = false;
        this.error = error;
        this.errorMessage = error.getMessage();
        this.code = code;
    }


    public boolean isSuccess() {
        return success;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }


    public Container getData() {
        return data;
    }


    public void setData(Container data) {
        this.data = data;
    }


    public Exception getError() {
        return error;
    }


    public void setError(Exception error) {
        this.error = error;
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public Integer getCode() {
        return code;
    }


    public void setCode(Integer code) {
        this.code = code;
    }
}
