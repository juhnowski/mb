package ru.simplgroup.data;

import java.util.List;

/**
 * страничка новостей
 */
public class NewsData extends AbstractCmsData {

    /**
     * тест  в шапке
     */
    private String headerContext;
    /**
     * Не нашли нужную информацию
     */
    private String feedback;

    /**
     * фотом обратной связи
     */
    private String fileName;

    private List<NewsW> news;


    public List<NewsW> getNews() {
        return news;
    }


    public void setNews(List<NewsW> news) {
        this.news = news;
    }


    public String getHeaderContext() {
        return headerContext;
    }


    public void setHeaderContext(String headerContext) {
        this.headerContext = headerContext;
    }


    public String getFeedback() {
        return feedback;
    }


    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
