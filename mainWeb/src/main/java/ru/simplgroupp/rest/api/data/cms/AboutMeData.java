package ru.simplgroupp.rest.api.data.cms;

import java.util.List;

/**
 * cms страника О нас
 */
public class AboutMeData extends AbstractCmsData{

    /**
     * О компании
     */
    private String company;
    /**
     * Наша команда
     */
    private List<PersonData> team;
    /**
     * приимущества
     */
    private String vantage;
    /**
     * цели и задачи
     */
    private String tasks;
    /**
     * Миссия компании
     */
    private String mission;
    /**
     * данные о человеке под блоком Миссия компании
     */
    private PersonData dir;

    /**
     * название фото в шапке
     */
    private String fileName;


    public String getCompany() {
        return company;
    }


    public void setCompany(String company) {
        this.company = company;
    }


    public List<PersonData> getTeam() {
        return team;
    }


    public void setTeam(List<PersonData> team) {
        this.team = team;
    }


    public String getVantage() {
        return vantage;
    }


    public void setVantage(String vantage) {
        this.vantage = vantage;
    }


    public String getTasks() {
        return tasks;
    }


    public void setTasks(String tasks) {
        this.tasks = tasks;
    }


    public String getMission() {
        return mission;
    }


    public void setMission(String mission) {
        this.mission = mission;
    }


    public PersonData getDir() {
        return dir;
    }


    public void setDir(PersonData dir) {
        this.dir = dir;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
