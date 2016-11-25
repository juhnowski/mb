package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeopleEmploymentData implements Serializable {

	private String experience;
	private String occupation;
	private String datestartwork;
	private String monthlyincome;
	private String workphone;
    private Boolean availablePhone;
	private String extincome;
    private Integer educationId;
    private Integer employmentId;
    private Integer professionId;
    private String salarydate;
    private Integer extsalarysource;
    private String workp;
    private Double additOutcomeSum; //сумма кредитных обязательств
    private AddressData placeWork =  new AddressData();// место работы
    
    public Integer getEducationId() {
        return educationId;
    }

    public void setEducationId(Integer educationId) {
        this.educationId = educationId;
    }

    public Integer getProfessionId() {
        return professionId;
    }


    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    public String getExperience() {
        return experience;
    }


    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getOccupation() {
        return occupation;
    }


    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

	public AddressData getPlaceWork() {
		return placeWork;
	}

	public void setPlaceWork(AddressData placeWork) {
		this.placeWork = placeWork;
	}

	public String getDatestartwork() {
		return datestartwork;
	}

	public void setDatestartwork(String datestartwork) {
		this.datestartwork = datestartwork;
	}

	public String getMonthlyincome() {
		return monthlyincome;
	}

	public void setMonthlyincome(String monthlyincome) {
		this.monthlyincome = monthlyincome;
	}

	public String getWorkphone() {
		return workphone;
	}

	public void setWorkphone(String workphone) {
		this.workphone = workphone;
	}

	public Boolean getAvailablePhone() {
		return availablePhone;
	}

	public void setAvailablePhone(Boolean availablePhone) {
		this.availablePhone = availablePhone;
	}

	public String getExtincome() {
		return extincome;
	}

	public void setExtincome(String extincome) {
		this.extincome = extincome;
	}

	public Integer getEmploymentId() {
		return employmentId;
	}

	public void setEmploymentId(Integer employmentId) {
		this.employmentId = employmentId;
	}

	public String getSalarydate() {
		return salarydate;
	}

	public void setSalarydate(String salarydate) {
		this.salarydate = salarydate;
	}

	public Integer getExtsalarysource() {
		return extsalarysource;
	}

	public void setExtsalarysource(Integer extsalarysource) {
		this.extsalarysource = extsalarysource;
	}

	public String getWorkp() {
		return workp;
	}

	public void setWorkp(String workp) {
		this.workp = workp;
	}

	public Double getAdditOutcomeSum() {
		return additOutcomeSum;
	}

	public void setAdditOutcomeSum(Double additOutcomeSum) {
		this.additOutcomeSum = additOutcomeSum;
	}


}
