package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.rest.api.data.AbstractUserData;
import ru.simplgroupp.rest.api.data.ReferenceData;

import java.util.List;

/**
 * даннные для "настройкки профиля"
 */
public class FormPersonalData extends AbstractUserData {
    /**
     * основные данные, включая телефон и email
     */
    private MainData mainData;

    /**
     * паспортные данные человека
     */
    private PassportData passportData;

    /**
     * остальные данные человека
     */
    private MiscData miscData;

    /**
     * данные работодателя, только в этом классе нас инетересует только образование
     */
    private EmploymentData employmentData;

    /**
     * id образования
     */
    private Integer education;

    /**
     * типы образования
     */
    private List<ReferenceData> educationTypes;


    public FormPersonalData() {
    }

    public MainData getMainData() {
        return mainData;
    }

    public void setMainData(MainData mainData) {
        this.mainData = mainData;
    }

    public PassportData getPassportData() {
        return passportData;
    }

    public void setPassportData(PassportData passportData) {
        this.passportData = passportData;
    }

    public MiscData getMiscData() {
        return miscData;
    }

    public void setMiscData(MiscData miscData) {
        this.miscData = miscData;
    }

    public List<ReferenceData> getEducationTypes() {
        return educationTypes;
    }

    public void setEducationTypes(List<ReferenceData> educationTypes) {
        this.educationTypes = educationTypes;
    }

    public Integer getEducation() {
        return education;
    }

    public void setEducation(Integer education) {
        this.education = education;
    }

    public EmploymentData getEmploymentData() {
        return employmentData;
    }

    public void setEmploymentData(EmploymentData employmentData) {
        this.employmentData = employmentData;
    }
}
