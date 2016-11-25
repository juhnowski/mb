package ru.simplgroupp.rest.api.data;

import ru.simplgroupp.transfer.Employment;

import java.io.Serializable;

/**
 * Created by PARFENOV on 29.05.2015.
 * <p/>
 * Образование занятость
 */
public class EmploymentData implements Serializable {

    private Integer educationId;//образование
    private String education;//образование
    private Integer typeWorkId;//id занятость
    private String typeWork;//занятость
    private Double salary;// зарплата
    private String realty;// недвижимости в собственности
    private Integer professionId;//id профессии
    private String profession;// профессии
    private Integer durationId;//id частоты получения зп
    private String duration;//частота получения зп
    private Integer extSalaryId;//id источник дополнительного дохода
    private Double extSalary;//дополнительный доход
    private String experience;// дата начала трудовой деятельности
    private String placeWork;// место работы
    private String occupation;//должность
    private String dateStartWork;//дата начала работы в должности
    private String workPhone; //рабочий телефон
    private Boolean available;// не звонить на рабочий телефон
    private Boolean isAdditOutcomeSumExists; //сумма кредитных обязательств
    private Double additOutcomeSum; //сумма кредитных обязательств
    private String salaryDate; //дата следующей зарплаты
    private Integer professionTypeId;//id типа профессии 

    public EmploymentData() {}

    public EmploymentData(Employment employment) {
        this.educationId = employment.getEducation().getCodeInteger();
//        this.education = employment.getEducation().getName();
//        this.typeWorkId = employment.getTypeWork().getCodeInteger();
//        this.typeWork = employment.getTypeWork().getName();
//        this.salary = employment.getSalary();
//        //может быть null, если человек пенсионер или безработный
//        if (employment.getProfession() != null) {
//            this.professionId = employment.getProfession().getCodeInteger();
//            this.profession = employment.getProfession().getName();
//        }
//        this.durationId = employment.getDuration().getCodeInteger();
//        this.duration = employment.getDuration().getName();
//        //может быть null
//        if(employment.getExtSalaryId()!=null) {
//            this.extSalaryId = employment.getExtSalaryId().getCodeInteger();
//        }
//        this.extSalary = employment.getExtSalary();
//        this.experience = Convertor.dateToString(employment.getExperience(), "dd / MM / yyyy");
//        //может быть null, если человек пенсионер или безработный
//        this.placeWork = employment.getPlaceWork();
//        this.occupation = employment.getOccupation();
//        //может быть null, если человек пенсионер или безработный
//        if (employment.getDateStartWork() != null) {
//            this.dateStartWork = Convertor.dateToString(employment.getDateStartWork(), "dd / MM / yyyy");
//        }

    }


    public Integer getEducationId() {
        return educationId;
    }


    public void setEducationId(Integer educationId) {
        this.educationId = educationId;
    }


    public String getEducation() {
        return education;
    }


    public void setEducation(String education) {
        this.education = education;
    }


    public Integer getTypeWorkId() {
        return typeWorkId;
    }


    public void setTypeWorkId(Integer typeWorkId) {
        this.typeWorkId = typeWorkId;
    }


    public String getTypeWork() {
        return typeWork;
    }


    public void setTypeWork(String typeWork) {
        this.typeWork = typeWork;
    }


    public Double getSalary() {
        return salary;
    }


    public void setSalary(Double salary) {
        this.salary = salary;
    }


    public String getRealty() {
        return realty;
    }


    public void setRealty(String realty) {
        this.realty = realty;
    }


    public Integer getProfessionId() {
        return professionId;
    }


    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }


    public String getProfession() {
        return profession;
    }


    public void setProfession(String profession) {
        this.profession = profession;
    }


    public Integer getDurationId() {
        return durationId;
    }


    public void setDurationId(Integer durationId) {
        this.durationId = durationId;
    }


    public Integer getExtSalaryId() {
        return extSalaryId;
    }


    public void setExtSalaryId(Integer extSalaryId) {
        this.extSalaryId = extSalaryId;
    }


    public Double getExtSalary() {
        return extSalary;
    }


    public void setExtSalary(Double extSalary) {
        this.extSalary = extSalary;
    }


    public String getExperience() {
        return experience;
    }


    public void setExperience(String experience) {
        this.experience = experience;
    }


    public String getPlaceWork() {
        return placeWork;
    }


    public void setPlaceWork(String placeWork) {
        this.placeWork = placeWork;
    }


    public String getOccupation() {
        return occupation;
    }


    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }


    public String getDateStartWork() {
        return dateStartWork;
    }


    public void setDateStartWork(String dateStartWork) {
        this.dateStartWork = dateStartWork;
    }


    public String getWorkPhone() {
        return workPhone;
    }


    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }


    public Boolean getAvailable() {
        return available;
    }


    public void setAvailable(Boolean available) {
        this.available = available;
    }


    public String getDuration() {
        return duration;
    }


    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Boolean getIsAdditOutcomeSumExists() {
        return isAdditOutcomeSumExists;
    }

    public void setIsAdditOutcomeSumExists(Boolean isAdditOutcomeSumExists) {
        this.isAdditOutcomeSumExists = isAdditOutcomeSumExists;
    }

    public Double getAdditOutcomeSum() {
		return additOutcomeSum;
	}

	public void setAdditOutcomeSum(Double additOutcomeSum) {
		this.additOutcomeSum = additOutcomeSum;
	}

	public String getSalaryDate() {
		return salaryDate;
	}

	public void setSalaryDate(String salaryDate) {
		this.salaryDate = salaryDate;
	}

	public Integer getProfessionTypeId() {
		return professionTypeId;
	}

	public void setProfessionTypeId(Integer professionTypeId) {
		this.professionTypeId = professionTypeId;
	}
}
