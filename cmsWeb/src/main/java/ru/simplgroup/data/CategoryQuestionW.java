package ru.simplgroup.data;

/**
 * User: Parfenov
 * Date: 06.07.2015
 * Time: 0:48
 * <p/>
 * Объект категории вопросов
 */
public class CategoryQuestionW {

    /**
     * наименование категории
     */
    private String name;

    /**
     * якорь на Категорию
     */
    private Integer code;
    /**
     * показывать в footer
     */
    private Boolean isFooter;


    public CategoryQuestionW() {
    }


    public CategoryQuestionW(String name, Integer code, Boolean isFooter) {
        this.name = name;
        this.code = code;
        this.isFooter = isFooter;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Integer getCode() {
        return code;
    }


    public void setCode(Integer code) {
        this.code = code;
    }


    public Boolean getIsFooter() {
        return isFooter;
    }


    public void setIsFooter(Boolean isFooter) {
        this.isFooter = isFooter;
    }
}
