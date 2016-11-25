package ru.simplgroupp.rest.api.data.cms;

import java.util.Date;

/**
 * новости
 */
public class News extends AbstractCmsData{

    /**
     * название
     */
    private String name;
    /**
     * Дата публикации
     */
    private Date date;
    /**
     * текст новости
     */
    private String text;
    /**
     * название фото
     */
    private String fileName;


    /**
     * id
     */
    private Integer id;


    public News() {
    }


    public News(String name, Date date, String text, String fileName, Integer id) {
        this.name = name;
        this.date = date;
        this.text = text;
        this.fileName = fileName;
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }
}
