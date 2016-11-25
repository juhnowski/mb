package ru.simplgroupp.rest.api.data;

import java.io.Serializable;
import java.util.Date;

public class OfficialDocumentsData implements Serializable {

    private Integer id;

    private String docNumber;

    private Date docDate;

    private Date dateChange;

    private String smsCode;

    private Integer isActive;

    private String docText;

    private ReferenceData documentTypeId;

    private PeopleMainData peopleMainId;

    private Integer anotherId;

    private CreditRequestData creditRequestId;

    private CreditData creditId;

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public Date getDateChange() {
        return dateChange;
    }

    public void setDateChange(Date dateChange) {
        this.dateChange = dateChange;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public Integer getActive() {
        return isActive;
    }

    public void setActive(Integer active) {
        isActive = active;
    }

    public String getDocText() {
        return docText;
    }

    public void setDocText(String docText) {
        this.docText = docText;
    }

    public ReferenceData getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(ReferenceData documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public PeopleMainData getPeopleMainId() {
        return peopleMainId;
    }

    public void setPeopleMainId(PeopleMainData peopleMainId) {
        this.peopleMainId = peopleMainId;
    }

    public Integer getAnotherId() {
        return anotherId;
    }

    public void setAnotherId(Integer anotherId) {
        this.anotherId = anotherId;
    }

    public CreditRequestData getCreditRequestId() {
        return creditRequestId;
    }

    public void setCreditRequestId(CreditRequestData creditRequestId) {
        this.creditRequestId = creditRequestId;
    }

    public CreditData getCreditId() {
        return creditId;
    }

    public void setCreditId(CreditData creditId) {
        this.creditId = creditId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
