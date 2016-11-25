package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.rest.api.data.AbstractUserData;
import ru.simplgroupp.rest.api.data.PersonalData;
import ru.simplgroupp.rest.api.data.ReferenceData;

import java.util.List;

/**
 * Основные данные пользователя
 */
public class MainData extends AbstractUserData {
    private String surname;

    private String name;

    private String middlename;

    private String birthday;

    private String birthplace;

    private Integer gender;

    private String email;

    private String phone;

    private List<ReferenceData> genderTypes;


    public MainData() {
    }

    public MainData(PersonalData personalData) {
        this.surname = personalData.getSurname();
        this.name = personalData.getName();
        this.middlename = personalData.getMidname();
        this.birthday = personalData.getBirthdate();
        this.birthplace = personalData.getBirthplace();
        this.gender = personalData.getGenderId();
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public List<ReferenceData> getGenderTypes() {
        return genderTypes;
    }

    public void setGenderTypes(List<ReferenceData> genderTypes) {
        this.genderTypes = genderTypes;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
