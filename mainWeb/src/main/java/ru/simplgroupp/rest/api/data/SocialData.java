package ru.simplgroupp.rest.api.data;

/**
 * SocialData
 * <p/>
 * Created by Andrey Unger aka Cobalt <unger1984@gmail.com>
 *
 * @since 28.04.14.
 */
public class SocialData {
    private String ok,vk,fb,mm,callBackUrl;

    public SocialData(){
        ok = vk = fb = mm = callBackUrl = "";
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getVk() {
        return vk;
    }

    public void setVk(String vk) {
        this.vk = vk;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }
}
