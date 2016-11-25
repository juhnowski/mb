package ru.simplgroupp.rest.api.data.cms;

import java.util.List;

/**
 * Управление сатов
 * Редактируемые данные для главной страницы
 */
public class MainData extends AbstractCmsData{

    /**
     * текст в шапке
     */
    private String header;

    /**
     * Название разделов в блоке Как это работает
     */
    private List<String> howThisWork;

    /**
     * Список вопрос в раздел Есть вопросы? Мы готовы ответить
     */
    private List<LinkData> questions;

    /**
     * лист фото
     */
    private List<ImageCms> images;

    public String getHeader() {
        return header;
    }


    public void setHeader(String header) {
        this.header = header;
    }


    public List<String> getHowThisWork() {
        return howThisWork;
    }


    public void setHowThisWork(List<String> howThisWork) {
        this.howThisWork = howThisWork;
    }


    public List<LinkData> getQuestions() {
        return questions;
    }


    public void setQuestions(List<LinkData> questions) {
        this.questions = questions;
    }


    public List<ImageCms> getImages() {
        return images;
    }


    public void setImages(List<ImageCms> images) {
        this.images = images;
    }
}
