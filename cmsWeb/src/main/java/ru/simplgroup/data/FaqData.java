package ru.simplgroup.data;

import java.util.List;

/**
 * User: Parfenov
 * Date: 06.08.2015
 * Time: 0:40
 */
public class FaqData extends AbstractCmsData{


    /**
     * текст в заголовке
     */
    private String header;

    /**
     * имя файлы из ш
     */
    private String fileName;

    private List<CategoryQuestionW> categorys;

    private List<QuestionDataW> questions;


    public FaqData() {
    }


    public FaqData(String header, List<CategoryQuestionW> categorys, List<QuestionDataW> questions) {
        this.header = header;
        this.categorys = categorys;
        this.questions = questions;
    }


    public String getHeader() {
        return header;
    }


    public void setHeader(String header) {
        this.header = header;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public List<CategoryQuestionW> getCategorys() {
        return categorys;
    }


    public void setCategorys(List<CategoryQuestionW> categorys) {
        this.categorys = categorys;
    }


    public List<QuestionDataW> getQuestions() {
        return questions;
    }


    public void setQuestions(List<QuestionDataW> questions) {
        this.questions = questions;
    }
}
