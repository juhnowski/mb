package ru.simplgroupp.data;

/**
 * Step8Data
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 24.04.14
 */
public class Step8Data {
    private Integer gender;
    private String name,midname,id,templink;

    public Step8Data(){
        gender = null;
        name = midname = id = templink = "";
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMidname() {
        return midname;
    }

    public void setMidname(String midname) {
        this.midname = midname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplink() {
        return templink;
    }

    public void setTemplink(String templink) {
        this.templink = templink;
    }
}
