package ru.simplgroupp.servlet.contact;

import javax.xml.bind.annotation.*;

/**
 * Created by aro on 11.10.2014.
 */
@XmlRootElement(name = "RESPONSE")
@XmlAccessorType(XmlAccessType.NONE)
public class ContactCallbackResponse {
    @XmlAttribute(name = "RE")
    private Integer re;
    @XmlAttribute(name = "ERR_TEXT")
    private String errText;
    @XmlAttribute(name = "Stamp")
    private String stamp;


    @XmlElement(name = "PaymentID")
    private Integer paymentID;
    @XmlElement
    private String trnAmount;
    @XmlElement(name = "PaymentInfo")
    private String paymentInfo;
    @XmlElement(name = "ErrorPayInfo")
    private String errorPayInfo;
    @XmlElement(name = "State")
    private Integer state;
    @XmlElement(name = "DetailPaymentInfo")
    private String detailPaymentInfo;

    public Integer getRe() {
        return re;
    }

    public void setRe(Integer re) {
        this.re = re;
    }

    public String getErrText() {
        return errText;
    }

    public void setErrText(String errText) {
        this.errText = errText;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public Integer getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(Integer paymentID) {
        this.paymentID = paymentID;
    }

    public String getTrnAmount() {
        return trnAmount;
    }

    public void setTrnAmount(String trnAmount) {
        this.trnAmount = trnAmount;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getErrorPayInfo() {
        return errorPayInfo;
    }

    public void setErrorPayInfo(String errorPayInfo) {
        this.errorPayInfo = errorPayInfo;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getDetailPaymentInfo() {
        return detailPaymentInfo;
    }

    public void setDetailPaymentInfo(String detailPaymentInfo) {
        this.detailPaymentInfo = detailPaymentInfo;
    }

    @Override
    public String toString() {
        String eol = "\n";
        StringBuilder sb = new StringBuilder("re="+re);
        sb.append(eol).append("errText="+errText);
        sb.append(eol).append("stamp="+stamp);
        sb.append(eol).append("paymentID="+paymentID);
        sb.append(eol).append("paymentInfo="+paymentInfo);
        sb.append(eol).append("errorPayInfo="+errorPayInfo);
        sb.append(eol).append("trnAmount="+trnAmount);
        sb.append(eol).append("state="+state);
        sb.append(eol);
        return sb.toString();
    }
}
