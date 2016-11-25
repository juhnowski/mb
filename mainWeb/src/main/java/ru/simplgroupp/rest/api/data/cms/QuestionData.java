package ru.simplgroupp.rest.api.data.cms;

/**
 * User: Parfenov
 * Date: 06.07.2015
 * Time: 0:45
 *
 *
 * Объект Вопросы
 */
public class QuestionData {

    /**
     * наименование вопроса
     */
    private String name;

    /**
     * ответ на вопрос
     */
    private String  answer;

    /**
     * якорь на вопрос
     */
    private Integer code;

    /**
     * показать вопросы на главной страницеж
     */
    private Boolean isMain;


    public QuestionData() {
    }


    public QuestionData(String answer, String name, Integer code, Boolean isMain) {
        this.answer = answer;
        this.code = code;
        this.name = name;
        this.isMain = isMain;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getAnswer() {
        return answer;
    }


    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public Integer getCode() {
        return code;
    }


    public void setCode(Integer code) {
        this.code = code;
    }


    public Boolean getIsMain() {
        return isMain;
    }


    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
    }
}
