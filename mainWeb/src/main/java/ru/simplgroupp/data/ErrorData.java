package ru.simplgroupp.data;

/**
 * ErrorData информация об ошибке
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 25.04.14
 */
public class ErrorData {
    private String field,text,type;

    public ErrorData(){
        field = text = type = null;
    }

    public ErrorData(String field,String text){
        this.field = field;
        this.text = text;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Возвращает ошибку для редиректа
     * @return
     */
    public static ErrorData redirect(){
        return redirect(null);
    }

    /**
     * Возвращает ошибку для редиректа
     * @param text куда редиректим
     * @return
     */
    public static ErrorData redirect(String text){
        ErrorData err = new ErrorData();
        err.setText(text);
        err.setType("redirect");
        return err;
    }

    /**
     * Возвращает данные ошибки для алерта
     * @param text
     * @return
     */
    public static ErrorData alert(String text){
        ErrorData err = new ErrorData();
        err.setText(text);
        err.setType("alert");
        return err;
    }
}
