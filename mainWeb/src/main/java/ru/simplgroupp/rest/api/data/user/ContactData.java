package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.transfer.PeopleContact;

/**
 * контакт пользователя
 */
public class ContactData {
    private Integer id;

    /**
     * значение контакта
     */
    private String value;

    /**
     * доступность
     */
    private Boolean available;


    public ContactData() {
    }

    public ContactData(PeopleContact peopleContact) {
        this.id = peopleContact.getId();
        this.value = peopleContact.getValue();
        this.available = peopleContact.getAvailable();
    }

    public ContactData(String value, Boolean available) {
        this.value = value;
        this.available = available;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getAvailable() {
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
