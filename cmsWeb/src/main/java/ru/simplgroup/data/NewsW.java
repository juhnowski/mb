package ru.simplgroup.data;

/**
 * новости
 */
public class NewsW extends AbstractCmsData{

    /**
     * название
     */
    private String name;
    /**
     * Дата публикации
     */
    private String date;
    /**
     * текст новости
     */
    private String text;
    /**
     * название фото
     */
    private String fileName;

    private String fileNamePreview;
    /**
     * id
     */
    private Integer id;

    /**
     * количество лайков
     */
    private  Integer likes;


    public NewsW() {
    }


    public NewsW(String name, String date, String text, String fileName, Integer id) {
        this.name = name;
        this.date = date;
        this.text = text;
        this.fileName = fileName;
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
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


    public Integer getLikes() {
        return likes;
    }


    public void setLikes(Integer likes) {
        this.likes = likes;
    }


    public String getFileNamePreview() {
        return fileNamePreview;
    }


    public void setFileNamePreview(String fileNamePreview) {
        this.fileNamePreview = fileNamePreview;
    }
}
