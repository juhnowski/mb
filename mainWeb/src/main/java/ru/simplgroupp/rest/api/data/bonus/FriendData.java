package ru.simplgroupp.rest.api.data.bonus;

import ru.simplgroupp.transfer.PeopleFriend;

import java.util.Date;

/**
 * Объект описывающий приглашенного друга
 */
public class FriendData {
    /**
     * id в базе
     */
    private Integer id;
    /**
     * имя
     */
    private String firstName;
    /**
     * фамилия
     */
    private String lastName;
    /**
     * почта
     */
    private String email;
    /**
     * телефон
     */
    private String phone;
    /**
     * доступность по телефону
     */
    private Boolean available;
    /**
     * дата изменений
     */
    private Date dateActual;


    public FriendData() {
    }

    /**
     * Создание объекта на основании объекта с описанием друга из бизнес-логики.
     *
     * @param pf объект с описанием друга из бизнес-логики.
     */
    public FriendData(PeopleFriend pf) {
        this(pf.getId(), pf.getName(), pf.getSurname(), pf.getEmail(), pf.getDateactual(), pf.getPhone(), pf.getAvailable());
    }

    /**
     * Создание объекта на основании списка параметров по другу.
     *
     * @param firstName  имя.
     * @param lastName   фамилия.
     * @param email      эектронная почта.
     * @param dateActual дата отправки приглашения
     * @param phone      телефон
     * @param available  доступен ли по телефону
     */
    public FriendData(Integer id, String firstName, String lastName, String email, Date dateActual, String phone, Boolean available) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateActual = dateActual;
        this.phone = phone;
        this.available = available;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateActual() {
        return dateActual;
    }

    public void setDateActual(Date dateActual) {
        this.dateActual = dateActual;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean isAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
