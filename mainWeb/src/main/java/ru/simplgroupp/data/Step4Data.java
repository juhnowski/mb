package ru.simplgroupp.data;

/**
 * Step3Data
 * Данные передаваемые на четвертом шаге в json
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 22.04.14.
 */
public class Step4Data {

    private String experience,workplace,occupation,datestartwork,monthlyincome,jobFias,home,builder,korpus,workphone,extincome,id;
    private Integer education,employment,profession,salarydate,available,car, extsalarysource;
    private String educationTitle,employmentTitle,professionTitle,salarydateTitle,extsalarysourceTitle;

    public Step4Data(){
        experience = workplace = occupation = datestartwork = monthlyincome = home = builder = korpus = workphone = extincome = id = "";
        education = employment = profession = salarydate = available = car = extsalarysource = null;
        educationTitle = employmentTitle = professionTitle = salarydateTitle = extsalarysourceTitle = "";
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
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

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getBuilder() {
        return builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public String getKorpus() {
        return korpus;
    }

    public void setKorpus(String korpus) {
        this.korpus = korpus;
    }

    public String getWorkphone() {
        return workphone;
    }

    public void setWorkphone(String workphone) {
        this.workphone = workphone;
    }

    public String getExtincome() {
        return extincome;
    }

    public void setExtincome(String extincome) {
        this.extincome = extincome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getEducation() {
        return education;
    }

    public void setEducation(Integer education) {
        this.education = education;
    }

    public Integer getEmployment() {
        return employment;
    }

    public void setEmployment(Integer employment) {
        this.employment = employment;
    }

    public Integer getProfession() {
        return profession;
    }

    public void setProfession(Integer profession) {
        this.profession = profession;
    }

    public Integer getSalarydate() {
        return salarydate;
    }

    public void setSalarydate(Integer salarydate) {
        this.salarydate = salarydate;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getCar() {
        return car;
    }

    public void setCar(Integer car) {
        this.car = car;
    }

    public String getEducationTitle() {
        return educationTitle;
    }

    public void setEducationTitle(String educationTitle) {
        this.educationTitle = educationTitle;
    }

    public String getEmploymentTitle() {
        return employmentTitle;
    }

    public void setEmploymentTitle(String employmentTitle) {
        this.employmentTitle = employmentTitle;
    }

    public String getProfessionTitle() {
        return professionTitle;
    }

    public void setProfessionTitle(String professionTitle) {
        this.professionTitle = professionTitle;
    }

    public String getSalarydateTitle() {
        return salarydateTitle;
    }

    public void setSalarydateTitle(String salarydateTitle) {
        this.salarydateTitle = salarydateTitle;
    }

    public Integer getExtsalarysource() {
        return extsalarysource;
    }

    public void setExtsalarysource(Integer extsalarysource) {
        this.extsalarysource = extsalarysource;
    }

    public String getExtsalarysourceTitle() {
        return extsalarysourceTitle;
    }

    public void setExtsalarysourceTitle(String extsalarysourceTitle) {
        this.extsalarysourceTitle = extsalarysourceTitle;
    }

    public String getJobFias() {
        return jobFias;
    }

    public void setJobFias(String jobFias) {
        this.jobFias = jobFias;
    }
}
