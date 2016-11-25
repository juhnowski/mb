package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeopleFamilyData implements Serializable {
    private int famStatus;
    private int childCount;

    public int getFamStatus() {
        return famStatus;
    }

    public void setFamStatus(int famStatus) {
        this.famStatus = famStatus;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }
}
