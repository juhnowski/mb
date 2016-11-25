package ru.simplgroupp.rest.api.data.antifraud;

public class AntifraudPage {

    String pageName;
    String requestId;
    boolean leaving;
    String sessionId;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isLeaving() {
        return leaving;
    }

    public void setLeaving(boolean leaving) {
        this.leaving = leaving;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
