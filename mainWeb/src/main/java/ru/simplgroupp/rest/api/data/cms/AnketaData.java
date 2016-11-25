package ru.simplgroupp.rest.api.data.cms;

/**
 * Страничка анкета
 */
public class AnketaData extends AbstractCmsData{

    /**
     *Нужна помощь
     */

    private String help;

    /**
     * название фото
     */
    private String fileName;


    public String getHelp() {
        return help;
    }


    public void setHelp(String help) {
        this.help = help;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
