package ru.simplgroupp.rest.api.data.cms;

import java.util.List;

/**
 * User: Parfenov
 * Date: 06.07.2015
 * Time: 0:48
 * <p/>
 * Объект категории вопросов
 */
public class CategoryQuestion {

    /**
     * наименование категории
     */
    private String name;
    /**
     * список вопросов
     */
    private List<QuestionData> questions;


    public CategoryQuestion() {
    }


    public CategoryQuestion(String name, List<QuestionData> questions) {
        this.name = name;
        this.questions = questions;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public List<QuestionData> getQuestions() {
        return questions;
    }


    public void setQuestions(List<QuestionData> questions) {
        this.questions = questions;
    }
}
