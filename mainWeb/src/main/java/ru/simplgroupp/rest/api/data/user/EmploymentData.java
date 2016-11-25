package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.rest.api.data.AbstractUserData;
import ru.simplgroupp.rest.api.data.ReferenceData;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Employment;

import java.util.List;

/**
 * Рабочие данные пользователя
 */
public class EmploymentData extends AbstractUserData {
    private Integer educationId;

    /**
     * вид деятельности
     */
    private Integer typeWorkId;

    /**
     * профессия
     */
    private Integer professionId;

    /**
     * должность
     */
    private String occupation;

    /**
     * тип должности
     */
    private Integer occupationId;

    /**
     * место работы
     */
    private String placeWork;

    /**
     * дата начала работы
     */
    private String dateStartWork;

    /**
     * период даты начала работы
     */
    private Integer dateStartWorkId;

    /**
     * зарплата
     */
    private Double salary;

    /**
     * дата следующей запрплаты
     */
    private String nextSalaryDate;

    /**
     * адрес работы
     */
    private AddressData work;

    /**
     * тип дополнительного дохода
     */
    private Integer extSalaryId;

    /**
     * сумма дополнительного дохода
     */
    private Double extSalary;

    /**
     * дополнительные кредитные обязательства
     */
    private Double extCreditSum;

    private List<ReferenceData> educationTypes;
    private List<ReferenceData> employTypes;
    private List<ReferenceData> organizationTypes;
    private List<ReferenceData> professionTypes;
    private List<ReferenceData> extSalaryTypes;
    private List<ReferenceData> dateStartWorkTypes;


    public EmploymentData() {
    }

    public EmploymentData(Employment employment) {
        this.educationId = employment.getEducation().getCodeInteger();

        this.typeWorkId = employment.getTypeWork().getCodeInteger();

        // может быть null, если человек пенсионер или безработный
        if (employment.getProfession() != null) {
            this.professionId = employment.getProfession().getCodeInteger();
        }
        this.occupation = employment.getOccupation();

        if (employment.getOccupationId() != null) {
            this.occupationId = employment.getOccupationId().getCodeInteger();
        }

        this.placeWork = employment.getPlaceWork();
        if (employment.getDateStartWork() != null) {
            this.dateStartWork = Convertor.dateToString(employment.getDateStartWork(), "dd / MM / yyyy");
        }
        this.salary = employment.getSalary();
        this.nextSalaryDate = Convertor.dateToString(employment.getNextSalaryDate(), "dd / MM / yyyy");

        if (employment.getExtSalaryId() != null) {
            this.extSalaryId = employment.getExtSalaryId().getCodeInteger();
            this.extSalary = employment.getExtSalary();
        }

        if (employment.getDateStartWorkId() != null) {
            this.dateStartWorkId = employment.getDateStartWorkId().getCodeInteger();
        }

        this.extCreditSum = employment.getExtCreditSum();
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPlaceWork() {
        return placeWork;
    }

    public void setPlaceWork(String placeWork) {
        this.placeWork = placeWork;
    }

    public String getDateStartWork() {
        return dateStartWork;
    }

    public void setDateStartWork(String dateStartWork) {
        this.dateStartWork = dateStartWork;
    }

    public Integer getExtSalaryId() {
        return extSalaryId;
    }

    public void setExtSalaryId(Integer extSalaryId) {
        this.extSalaryId = extSalaryId;
    }

    public Double getExtCreditSum() {
        return extCreditSum;
    }

    public void setExtCreditSum(Double extCreditSum) {
        this.extCreditSum = extCreditSum;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Double getExtSalary() {
        return extSalary;
    }

    public void setExtSalary(Double extSalary) {
        this.extSalary = extSalary;
    }

    public List<ReferenceData> getEducationTypes() {
        return educationTypes;
    }

    public void setEducationTypes(List<ReferenceData> educationTypes) {
        this.educationTypes = educationTypes;
    }

    public List<ReferenceData> getEmployTypes() {
        return employTypes;
    }

    public void setEmployTypes(List<ReferenceData> employTypes) {
        this.employTypes = employTypes;
    }

    public List<ReferenceData> getProfessionTypes() {
        return professionTypes;
    }

    public void setProfessionTypes(List<ReferenceData> professionTypes) {
        this.professionTypes = professionTypes;
    }

    public List<ReferenceData> getExtSalaryTypes() {
        return extSalaryTypes;
    }

    public void setExtSalaryTypes(List<ReferenceData> extSalaryTypes) {
        this.extSalaryTypes = extSalaryTypes;
    }

    public Integer getTypeWorkId() {
        return typeWorkId;
    }

    public void setTypeWorkId(Integer typeWorkId) {
        this.typeWorkId = typeWorkId;
    }

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    public Integer getEducationId() {
        return educationId;
    }

    public void setEducationId(Integer educationId) {
        this.educationId = educationId;
    }

    public AddressData getWork() {
        return work;
    }

    public void setWork(AddressData work) {
        this.work = work;
    }

    public String getNextSalaryDate() {
        return nextSalaryDate;
    }

    public void setNextSalaryDate(String nextSalaryDate) {
        this.nextSalaryDate = nextSalaryDate;
    }

    public Integer getDateStartWorkId() {
        return dateStartWorkId;
    }

    public void setDateStartWorkId(Integer dateStartWorkId) {
        this.dateStartWorkId = dateStartWorkId;
    }

    public List<ReferenceData> getDateStartWorkTypes() {
        return dateStartWorkTypes;
    }

    public void setDateStartWorkTypes(List<ReferenceData> dateStartWorkTypes) {
        this.dateStartWorkTypes = dateStartWorkTypes;
    }

    public List<ReferenceData> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<ReferenceData> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public Integer getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(Integer occupationId) {
        this.occupationId = occupationId;
    }
}
