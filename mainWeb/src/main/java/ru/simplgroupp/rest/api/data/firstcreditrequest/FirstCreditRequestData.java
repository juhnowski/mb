package ru.simplgroupp.rest.api.data.firstcreditrequest;

import ru.simplgroupp.rest.api.data.EmploymentData;

import java.io.Serializable;
import java.util.List;

/**
 * Объект для хранения данных о заявке (анкете) на сайте
 */
public class FirstCreditRequestData implements Serializable {

    private Integer requestId;
    private PeopleMainData mainData;
    private PeopleBehaviorData behData;
    private PeoplePaymentData payData;
    private SumData sumData;
    private PeopleSocialData socData;
    private PeopleAgreements agreeData;
    private PeoplePassportData passportData;
    private PeopleFamilyData famData;
    private PeopleAddressData addrData;
    private EmploymentData empData;
    private FiasAddressData jobAddrData;
    private PeopleCreditOtherBank crOtherData;
    private String creditPurpose;
    private List<PeopleFriendData> friends;
    private PeopleDopDocsData dopDocsData;


    public PeopleMainData getMainData() {
        return mainData;
    }

    public void setMainData(PeopleMainData mainData) {
        this.mainData = mainData;
    }

    public PeopleBehaviorData getBehData() {
        return behData;
    }

    public void setBehData(PeopleBehaviorData behData) {
        this.behData = behData;
    }

    public PeoplePaymentData getPayData() {
        return payData;
    }

    public void setPayData(PeoplePaymentData payData) {
        this.payData = payData;
    }

    public SumData getSumData() {
        return sumData;
    }

    public void setSumData(SumData sumData) {
        this.sumData = sumData;
    }

    public PeopleSocialData getSocData() {
        return socData;
    }

    public void setSocData(PeopleSocialData socData) {
        this.socData = socData;
    }

    public PeopleAgreements getAgreeData() {
        return agreeData;
    }

    public void setAgreeData(PeopleAgreements agreeData) {
        this.agreeData = agreeData;
    }

    public PeoplePassportData getPassportData() {
        return passportData;
    }

    public void setPassportData(PeoplePassportData passportData) {
        this.passportData = passportData;
    }

    public PeopleFamilyData getFamData() {
        return famData;
    }

    public void setFamData(PeopleFamilyData famData) {
        this.famData = famData;
    }

    public PeopleAddressData getAddrData() {
        return addrData;
    }

    public void setAddrData(PeopleAddressData addrData) {
        this.addrData = addrData;
    }

    public EmploymentData getEmpData() {
        return empData;
    }

    public void setEmpData(EmploymentData empData) {
        this.empData = empData;
    }

    public PeopleCreditOtherBank getCrOtherData() {
        return crOtherData;
    }

    public void setCrOtherData(PeopleCreditOtherBank crOtherData) {
        this.crOtherData = crOtherData;
    }

    public String getCreditPurpose() {
        return creditPurpose;
    }

    public void setCreditPurpose(String creditPurpose) {
        this.creditPurpose = creditPurpose;
    }

    public List<PeopleFriendData> getFriends() {
        return friends;
    }

    public void setFriends(List<PeopleFriendData> friends) {
        this.friends = friends;
    }

    public PeopleDopDocsData getDopDocsData() {
        return dopDocsData;
    }

    public void setDopDocsData(PeopleDopDocsData dopDocsData) {
        this.dopDocsData = dopDocsData;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public FiasAddressData getJobAddrData() {
        return jobAddrData;
    }

    public void setJobAddrData(FiasAddressData jobAddrData) {
        this.jobAddrData = jobAddrData;
    }
}
