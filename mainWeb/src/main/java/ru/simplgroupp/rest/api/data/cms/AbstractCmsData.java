package ru.simplgroupp.rest.api.data.cms;

/**
 * ОБхект для описания общих полей, для всех страничек
 */
public class AbstractCmsData {

    /**
     * metatag
     */
    private String title;
    private String keywords;
    private String description;


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getKeywords() {
        return keywords;
    }


    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}
