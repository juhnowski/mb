package ru.simplgroup.data;

/**
 * Created by Rustem.Saidaliyev on 20.01.2016.
 *
 * CMS страница О нас
 */
public class AboutMeData extends AbstractCmsData{

    /* О компании */
    private String company;
    /* Мы проще */
    private String simply;
    /* Мы быстрее */
    private String quickly;
    /* Ответвтенность */
    private String responsibility;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSimply() {
        return simply;
    }

    public void setSimply(String simply) {
        this.simply = simply;
    }

    public String getQuickly() {
        return quickly;
    }

    public void setQuickly(String quickly) {
        this.quickly = quickly;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }
}
