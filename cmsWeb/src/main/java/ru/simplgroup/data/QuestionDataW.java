package ru.simplgroup.data;

/**
 * User: Parfenov
 * Date: 06.07.2015
 * Time: 0:45
 *
 *
 * Объект Вопросы
 */
public class QuestionDataW {

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


    /**
     * признак показывать в footer
     */
    private Boolean isFooter;

    private Integer idCategory;

    public QuestionDataW() {
    }


    public QuestionDataW(String name, String answer, Integer code, Boolean isMain, Boolean isFooter, Integer idCategory) {
        this.answer = answer;
        this.code = code;
        this.name = name;
        this.isMain = isMain;
        this.isFooter = isFooter;
        this.idCategory = idCategory;
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


    public Boolean getIsFooter() {
        return isFooter;
    }


    public void setIsFooter(Boolean isFooter) {
        this.isFooter = isFooter;
    }


    public Integer getIdCategory() {
        return idCategory;
    }


    public void setIdCategory(Integer idCategory) {
        this.idCategory = idCategory;
    }
}
