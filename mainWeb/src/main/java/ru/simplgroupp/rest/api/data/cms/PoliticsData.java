package ru.simplgroupp.rest.api.data.cms;

import java.util.List;

/**
 * User: Parfenov
 * Date: 01.07.2015
 * Time: 23:20
 */
public class PoliticsData extends AbstractCmsData {

    /**
     * текст в заголовке
     */
    private String headerText;
    /**
     * тест к ссылке обратной связи
     */
    private String feelback;
    /**
     * имя картинки в обратной связи
     */
    private String fileName;

    private List<PoliticsPointData> dataList;


    public String getHeaderText() {
        return headerText;
    }


    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }


    public String getFeelback() {
        return feelback;
    }


    public void setFeelback(String feelback) {
        this.feelback = feelback;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public List<PoliticsPointData> getDataList() {
        return dataList;
    }


    public void setDataList(List<PoliticsPointData> dataList) {
        this.dataList = dataList;
    }
}
