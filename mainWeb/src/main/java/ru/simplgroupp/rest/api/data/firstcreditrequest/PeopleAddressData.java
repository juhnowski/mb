package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeopleAddressData implements Serializable {
    private FiasAddressData regAddress;
    private FiasAddressData liveAddress;
    private boolean regIsLive;
    private int liveRealty;


    public FiasAddressData getRegAddress() {
        return regAddress;
    }

    public void setRegAddress(FiasAddressData regAddress) {
        this.regAddress = regAddress;
    }

    public FiasAddressData getLiveAddress() {
        return liveAddress;
    }

    public void setLiveAddress(FiasAddressData liveAddress) {
        this.liveAddress = liveAddress;
    }

    public boolean isRegIsLive() {
        return regIsLive;
    }

    public void setRegIsLive(boolean regIsLive) {
        this.regIsLive = regIsLive;
    }

    public int getLiveRealty() {
        return liveRealty;
    }

    public void setLiveRealty(int liveRealty) {
        this.liveRealty = liveRealty;
    }
}
