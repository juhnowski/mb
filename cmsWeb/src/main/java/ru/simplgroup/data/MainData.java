package ru.simplgroup.data;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by PARFENOV on 29.07.2015.
 */
public class MainData extends AbstractCmsData{

    /**
     * Подходит ли вам ontime?
     */
    private List<LinkedHashMap<String, String>> yesOntime;

    /**
     * Название разделов в блоке Как это работает и описание
     */
    private List<LinkedHashMap<String, String>> howThisWork;


    /**
     * вверхнее фото
     */
    private String imgUp;

    /**
     * нижнее фото
     */
    private String imgDown;


    public List<LinkedHashMap<String, String>> getYesOntime() {
        return yesOntime;
    }


    public void setYesOntime(List<LinkedHashMap<String, String>> yesOntime) {
        this.yesOntime = yesOntime;
    }


    public String getImgUp() {
        return imgUp;
    }


    public void setImgUp(String imgUp) {
        this.imgUp = imgUp;
    }


    public String getImgDown() {
        return imgDown;
    }


    public void setImgDown(String imgDown) {
        this.imgDown = imgDown;
    }


    public List<LinkedHashMap<String, String>> getHowThisWork() {
        return howThisWork;
    }


    public void setHowThisWork(List<LinkedHashMap<String, String>> howThisWork) {
        this.howThisWork = howThisWork;
    }
}
