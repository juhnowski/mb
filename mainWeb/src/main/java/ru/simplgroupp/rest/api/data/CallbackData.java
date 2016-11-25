package ru.simplgroupp.rest.api.data;

/**
 * Данные для обратного звонка
 */
public class CallbackData {
    /**
     * имя
     */
    private String firstName;
    /**
     * фамилия
     */
    private String lastName;
    /**
     * телефон
     */
    private String phone;
    /**
     * почта
     */
    private String email;
    /**
     * текст сообщения
     */
    private String message;
    /**
     * тип лица
     */
    private String clientType;


    public CallbackData() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
