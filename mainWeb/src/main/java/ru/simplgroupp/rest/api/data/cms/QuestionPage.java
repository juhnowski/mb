package ru.simplgroupp.rest.api.data.cms;

import java.util.List;

/**
 * User: Parfenov
 * Date: 06.07.2015
 * Time: 0:50
 * <p/>
 * страница вопросов и ответов
 */
public class QuestionPage extends AbstractCmsData {

    /**
     * Вопросы и ответы
     */
    private String header;
    /**
     * ссылка обратной связт
     */
    private String feedback;

    /**
     * фотом обратной связи
     */
    private String fileName;

    private List<CategoryQuestion> categorys;


    public QuestionPage() {
    }


    public QuestionPage(List<CategoryQuestion> categorys,String header, String feedback) {
        this.header = header;
        this.feedback = feedback;
        this.categorys = categorys;
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


    public List<CategoryQuestion> getCategorys() {
        return categorys;
    }


    public void setCategorys(List<CategoryQuestion> categorys) {
        this.categorys = categorys;
    }


    public String getHeader() {
        return header;
    }


    public void setHeader(String header) {
        this.header = header;
    }
}
