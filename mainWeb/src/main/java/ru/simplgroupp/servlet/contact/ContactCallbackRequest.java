package ru.simplgroupp.servlet.contact;

import javax.xml.bind.annotation.*;

/**
 * Created by aro on 11.10.2014.
 */
@XmlRootElement(name = "REQUEST")
@XmlAccessorType(XmlAccessType.NONE)
public class ContactCallbackRequest {
    @XmlAttribute(name = "ACTION")
    private String action;
    @XmlAttribute(name = "STAMP")
    private String stamp;


    @XmlElement
    private String trnDate;
    @XmlElement
    private String trnReference;
    @XmlElement
    private String trnCurrency;
    @XmlElement
    private Double trnAmount;
    @XmlElement
    private String trnSendPoint;
    @XmlElement
    private String trnPickupPoint;
    @XmlElement
    private String sName;
    @XmlElement
    private String sLastName;
    @XmlElement
    private String sSurName;
    @XmlElement
    private String sBirthday;
    @XmlElement
    private String sBirthPlace;
    @XmlElement
    private String sResident;
    @XmlElement
    private String sCountryC;
    @XmlElement
    private String sCountry;
    @XmlElement
    private String sZipCode;
    @XmlElement
    private String sRegion;
    @XmlElement
    private String sCity;
    @XmlElement
    private String sAddress;

    @XmlElement
    private String sPhone;
    @XmlElement
    private String sIDtype;
    @XmlElement
    private String sIDnumber;
    @XmlElement
    private String sIDdate;
    @XmlElement
    private String sIDwhom;
    @XmlElement
    private String sIDwhomCode;
    @XmlElement
    private String sIDexpireDate;
    @XmlElement
    private String bName;
    @XmlElement
    private String bLastName;
    @XmlElement
    private String bSurName;

    @XmlElement
    private String bBirthday;
    @XmlElement
    private String bCountry;
    @XmlElement
    private String bZipCode;
    @XmlElement
    private String bRegion;
    @XmlElement
    private String bCity;
    @XmlElement
    private String bAddress;
    @XmlElement
    private String bPhone;
    @XmlElement
    private String bIDtype;
    @XmlElement
    private String bIDnumber;
    @XmlElement
    private String bIDdate;
    @XmlElement
    private String bIDwhom;
    @XmlElement
    private String bIDexpireDate;

    @XmlElement
    private String tAttr1;
    @XmlElement
    private String tAttr2;
    @XmlElement
    private String tAttr3;
    @XmlElement
    private String tAttr4;
    @XmlElement
    private String tAttr5;
    @XmlElement
    private String tAttr6;
    @XmlElement
    private String tAttr7;
    @XmlElement
    private String tAttr8;
    @XmlElement
    private String tAttr9;
    @XmlElement
    private String tAttr10;



    //метод Pay добавка
    @XmlElement
    private String ID;

    //метод CancelPay добавка
    @XmlElement
    private String paymentID;




    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public String getTrnDate() {
        return trnDate;
    }

    public void setTrnDate(String trnDate) {
        this.trnDate = trnDate;
    }

    public String getTrnReference() {
        return trnReference;
    }

    public void setTrnReference(String trnReference) {
        this.trnReference = trnReference;
    }

    public String getTrnCurrency() {
        return trnCurrency;
    }

    public void setTrnCurrency(String trnCurrency) {
        this.trnCurrency = trnCurrency;
    }

    public Double getTrnAmount() {
        return trnAmount;
    }

    public void setTrnAmount(Double trnAmount) {
        this.trnAmount = trnAmount;
    }

    public String getTrnSendPoint() {
        return trnSendPoint;
    }

    public void setTrnSendPoint(String trnSendPoint) {
        this.trnSendPoint = trnSendPoint;
    }

    public String getTrnPickupPoint() {
        return trnPickupPoint;
    }

    public void setTrnPickupPoint(String trnPickupPoint) {
        this.trnPickupPoint = trnPickupPoint;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsLastName() {
        return sLastName;
    }

    public void setsLastName(String sLastName) {
        this.sLastName = sLastName;
    }

    public String getsSurName() {
        return sSurName;
    }

    public void setsSurName(String sSurName) {
        this.sSurName = sSurName;
    }

    public String getsBirthday() {
        return sBirthday;
    }

    public void setsBirthday(String sBirthday) {
        this.sBirthday = sBirthday;
    }

    public String getsResident() {
        return sResident;
    }

    public void setsResident(String sResident) {
        this.sResident = sResident;
    }

    public String getsCountryC() {
        return sCountryC;
    }

    public void setsCountryC(String sCountryC) {
        this.sCountryC = sCountryC;
    }

    public String getsCountry() {
        return sCountry;
    }

    public void setsCountry(String sCountry) {
        this.sCountry = sCountry;
    }

    public String getsZipCode() {
        return sZipCode;
    }

    public void setsZipCode(String sZipCode) {
        this.sZipCode = sZipCode;
    }

    public String getsRegion() {
        return sRegion;
    }

    public void setsRegion(String sRegion) {
        this.sRegion = sRegion;
    }

    public String getsCity() {
        return sCity;
    }

    public void setsCity(String sCity) {
        this.sCity = sCity;
    }

    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public String getsIDtype() {
        return sIDtype;
    }

    public void setsIDtype(String sIDtype) {
        this.sIDtype = sIDtype;
    }

    public String getsIDnumber() {
        return sIDnumber;
    }

    public void setsIDnumber(String sIDnumber) {
        this.sIDnumber = sIDnumber;
    }

    public String getsIDdate() {
        return sIDdate;
    }

    public void setsIDdate(String sIDdate) {
        this.sIDdate = sIDdate;
    }

    public String getsIDwhom() {
        return sIDwhom;
    }

    public void setsIDwhom(String sIDwhom) {
        this.sIDwhom = sIDwhom;
    }

    public String getsIDwhomCode() {
        return sIDwhomCode;
    }

    public void setsIDwhomCode(String sIDwhomCode) {
        this.sIDwhomCode = sIDwhomCode;
    }

    public String getsIDexpireDate() {
        return sIDexpireDate;
    }

    public void setsIDexpireDate(String sIDexpireDate) {
        this.sIDexpireDate = sIDexpireDate;
    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    public String getbLastName() {
        return bLastName;
    }

    public void setbLastName(String bLastName) {
        this.bLastName = bLastName;
    }

    public String getbSurName() {
        return bSurName;
    }

    public void setbSurName(String bSurName) {
        this.bSurName = bSurName;
    }

    public String getbBirthday() {
        return bBirthday;
    }

    public void setbBirthday(String bBirthday) {
        this.bBirthday = bBirthday;
    }

    public String getbCountry() {
        return bCountry;
    }

    public void setbCountry(String bCountry) {
        this.bCountry = bCountry;
    }

    public String getbZipCode() {
        return bZipCode;
    }

    public void setbZipCode(String bZipCode) {
        this.bZipCode = bZipCode;
    }

    public String getbRegion() {
        return bRegion;
    }

    public void setbRegion(String bRegion) {
        this.bRegion = bRegion;
    }

    public String getbCity() {
        return bCity;
    }

    public void setbCity(String bCity) {
        this.bCity = bCity;
    }

    public String getbAddress() {
        return bAddress;
    }

    public void setbAddress(String bAddress) {
        this.bAddress = bAddress;
    }

    public String getbPhone() {
        return bPhone;
    }

    public void setbPhone(String bPhone) {
        this.bPhone = bPhone;
    }

    public String getbIDtype() {
        return bIDtype;
    }

    public void setbIDtype(String bIDtype) {
        this.bIDtype = bIDtype;
    }

    public String getbIDnumber() {
        return bIDnumber;
    }

    public void setbIDnumber(String bIDnumber) {
        this.bIDnumber = bIDnumber;
    }

    public String getbIDdate() {
        return bIDdate;
    }

    public void setbIDdate(String bIDdate) {
        this.bIDdate = bIDdate;
    }

    public String getbIDwhom() {
        return bIDwhom;
    }

    public void setbIDwhom(String bIDwhom) {
        this.bIDwhom = bIDwhom;
    }

    public String getbIDexpireDate() {
        return bIDexpireDate;
    }

    public void setbIDexpireDate(String bIDexpireDate) {
        this.bIDexpireDate = bIDexpireDate;
    }

    public String gettAttr1() {
        return tAttr1;
    }

    public void settAttr1(String tAttr1) {
        this.tAttr1 = tAttr1;
    }

    public String gettAttr2() {
        return tAttr2;
    }

    public void settAttr2(String tAttr2) {
        this.tAttr2 = tAttr2;
    }

    public String gettAttr3() {
        return tAttr3;
    }

    public void settAttr3(String tAttr3) {
        this.tAttr3 = tAttr3;
    }

    public String gettAttr4() {
        return tAttr4;
    }

    public void settAttr4(String tAttr4) {
        this.tAttr4 = tAttr4;
    }

    public String gettAttr5() {
        return tAttr5;
    }

    public void settAttr5(String tAttr5) {
        this.tAttr5 = tAttr5;
    }

    public String gettAttr6() {
        return tAttr6;
    }

    public void settAttr6(String tAttr6) {
        this.tAttr6 = tAttr6;
    }

    public String gettAttr7() {
        return tAttr7;
    }

    public void settAttr7(String tAttr7) {
        this.tAttr7 = tAttr7;
    }

    public String gettAttr8() {
        return tAttr8;
    }

    public void settAttr8(String tAttr8) {
        this.tAttr8 = tAttr8;
    }

    public String gettAttr9() {
        return tAttr9;
    }

    public void settAttr9(String tAttr9) {
        this.tAttr9 = tAttr9;
    }

    public String gettAttr10() {
        return tAttr10;
    }

    public void settAttr10(String tAttr10) {
        this.tAttr10 = tAttr10;
    }

    public String getsBirthPlace() {
        return sBirthPlace;
    }

    public void setsBirthPlace(String sBirthPlace) {
        this.sBirthPlace = sBirthPlace;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    @Override
    public String toString() {
        String eol = "\n";
        StringBuilder sb = new StringBuilder("tAttr1="+tAttr1);
        sb.append(eol).append("action="+action);
        sb.append(eol).append("stamp="+stamp);
        sb.append(eol).append("trnDate="+trnDate);
        sb.append(eol).append("trnReference="+trnReference);
        sb.append(eol).append("trnCurrency="+trnCurrency);
        sb.append(eol).append("trnAmount="+trnAmount);
        sb.append(eol).append("trnSendPoint="+trnSendPoint);
        sb.append(eol).append("trnPickupPoint="+trnPickupPoint);
        sb.append(eol).append("sName="+sName);
        sb.append(eol).append("sLastName="+sLastName);
        sb.append(eol).append("sSurName="+sSurName);
        sb.append(eol);
        return sb.toString();
    }
}

