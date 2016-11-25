package ru.simplgroupp.rest.api.data.cms;

/**
 * Управление сайтом
 * Объект описывает ссылки
 */
public class LinkData {

    /**
     * Наименование
     */
    private String name;
    /**
     * href
     */
    private String link;
    /**
     * активная ли ссылка
     * отобрать | не отображать
     */
    private Boolean active;


    public LinkData() {
    }


    public LinkData(String name, String link, Boolean active) {
        this.name = name;
        this.link = link;
        this.active = active;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getLink() {
        return link;
    }


    public void setLink(String link) {
        this.link = link;
    }


    public Boolean getActive() {
        return active;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }
}
