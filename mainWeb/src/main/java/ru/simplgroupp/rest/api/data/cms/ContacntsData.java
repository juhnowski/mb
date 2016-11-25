package ru.simplgroupp.rest.api.data.cms;

/**
 * User: Parfenov
 * Date: 03.07.2015
 * Time: 1:45
 *
 * CMS Контакты
 */
public class ContacntsData extends AbstractCmsData{

    private String address;
    private String phone;
    private String email;
    private String vk;
    private String tw;
    private String fb;
    private String ok;


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getVk() {
        return vk;
    }


    public void setVk(String vk) {
        this.vk = vk;
    }


    public String getTw() {
        return tw;
    }


    public void setTw(String tw) {
        this.tw = tw;
    }


    public String getFb() {
        return fb;
    }


    public void setFb(String fb) {
        this.fb = fb;
    }


    public String getOk() {
        return ok;
    }


    public void setOk(String ok) {
        this.ok = ok;
    }
}
