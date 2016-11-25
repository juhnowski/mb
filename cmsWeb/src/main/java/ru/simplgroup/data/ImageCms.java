package ru.simplgroup.data;

/**
 *  Фото которые мы грузим в cms
 */
public class ImageCms {

    /**
     * индентификатор CmsImageEntity
     */
    private Integer id;
    /**
     * название файда
     */
    private String fileName;
    /**
     * данный
     * байты конвертируемые в стринг
     */
    private String data;


    public ImageCms() {
    }


    public ImageCms(Integer id, String fileName, String data) {
        this.id = id;
        this.fileName = fileName;
        this.data = data;
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getData() {
        return data;
    }


    public void setData(String data) {
        this.data = data;
    }
}
