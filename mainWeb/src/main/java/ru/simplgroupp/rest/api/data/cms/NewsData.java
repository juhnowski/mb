package ru.simplgroupp.rest.api.data.cms;

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

    private List<News> news;


    public List<News> getNews() {
        return news;
    }


    public void setNews(List<News> news) {
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
