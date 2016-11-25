package ru.simplgroupp.rest.api.data.antifraud;

public class AntifraudField {

    String fieldName;
    String fieldValueBefore;
    String fieldValueAfter;
    String requestId;
    String sessionId;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValueBefore() {
        return fieldValueBefore;
    }

    public void setFieldValueBefore(String fieldValueBefore) {
        this.fieldValueBefore = fieldValueBefore;
    }

    public String getFieldValueAfter() {
        return fieldValueAfter;
    }

    public void setFieldValueAfter(String fieldValueAfter) {
        this.fieldValueAfter = fieldValueAfter;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
