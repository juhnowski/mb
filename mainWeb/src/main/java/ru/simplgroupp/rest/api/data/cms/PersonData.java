package ru.simplgroupp.rest.api.data.cms;

/**
 * Данные человека
 */
public class PersonData {

    /**
     * ФИО
     */
    private String name;
    /**
     * должность
     */
    private String position;
    /**
     * фото
     */
    private String fileName;



    public PersonData() {
    }


    public PersonData(String name, String position, String fileName) {
        this.name = name;
        this.position = position;
        this.fileName = fileName;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getPosition() {
        return position;
    }


    public void setPosition(String position) {
        this.position = position;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
