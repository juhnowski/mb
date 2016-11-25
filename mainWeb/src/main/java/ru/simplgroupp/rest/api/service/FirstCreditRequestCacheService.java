package ru.simplgroupp.rest.api.service;

import org.apache.commons.fileupload.FileItem;
import ru.simplgroupp.rest.api.data.firstcreditrequest.FirstCreditRequestData;
import ru.simplgroupp.rest.api.data.firstcreditrequest.InitialInfoData;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SessionScoped
public class FirstCreditRequestCacheService implements Serializable {
    private String msisdn;
    private String msisdnCode;
    private String email;
    private String emailCode;
    private FirstCreditRequestData initialData;
    private String authUsername;
    private Long authTimeStart;

    private Map<String, FileItem> uploadedFiles = new HashMap<>();

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMsisdnCode() {
        return msisdnCode;
    }

    public void setMsisdnCode(String code) {
        this.msisdnCode = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    public boolean hasUploadedFile(String key) {
        return this.uploadedFiles.containsKey(key);
    }

    public FileItem getUploadedFile(String key) {
        return this.uploadedFiles.get(key);
    }

    public void clearUploads() {
        this.uploadedFiles.clear();
    }

    public FirstCreditRequestData getInitialData() {
        return initialData;
    }

    public void setInitialData(FirstCreditRequestData initialData) {
        this.initialData = initialData;
    }
    
    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
        this.authTimeStart = this.authUsername == null ? null : new Date().getTime();
    }

    public Long getAuthTimeStart() {
        return authTimeStart;
    }
}
