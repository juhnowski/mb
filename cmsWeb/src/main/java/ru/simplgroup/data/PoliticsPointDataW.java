package ru.simplgroup.data;

/**
 * User: Parfenov
 * Date: 02.07.2015
 * Time: 0:23
 */
public class PoliticsPointDataW extends AbstractOrderList {

    private Integer id;
    private String header;
    private String distr;
    private String fileName;


    public PoliticsPointDataW() {
    }


    public PoliticsPointDataW(Integer id, String header, String distr) {
        this.id = id;
        this.header = header;
        this.distr = distr;
    }


    public String getHeader() {
        return header;
    }


    public void setHeader(String header) {
        this.header = header;
    }


    public String getDistr() {
        return distr;
    }


    public void setDistr(String distr) {
        this.distr = distr;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }
}
