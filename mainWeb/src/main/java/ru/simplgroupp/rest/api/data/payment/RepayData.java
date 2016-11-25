package ru.simplgroupp.rest.api.data.payment;

import java.io.Serializable;

public class RepayData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean canPay;
	
	private String msgCanPay;
	 /**
	 * номер счета кредита
	 */
    private String credit_creditAccount;
    /**
     * есть кредит
     */
	private boolean hasCredit;
	/**
     * сумма возврата
     */
    private Double sumBack;
     /**
     * сумма возврата минимальная
     */
    private Double sumBackMin;
    /**
     * сумма, которую можем заплатить бонусами
     */
    private Double sumBonusPay=0d;
    /**
     * сумма, которую можем заплатить бонусами от минимальной
     */
    private Double sumBonusPayMin=0d;
    
    private String paymentType;

    private String description;

    private Integer paymentId;

    private String qiwiPayUrl;

    private String qiwiPhone;

    private Long yandexShopId;

    private Long yandexScid;

    private String yandexUrl;

    private String payonlineUrl;

    private Integer payonlineMerchantId;
    private boolean hasProlong;
    private String formattedSumBack;
    private String payonlineSecurityKey;
    private String payonlineReturnUrl;
    private String payonlineFailUrl;
	private Integer people_id;
	private String people_email;
	
	public boolean isCanPay() {
		return canPay;
	}
	public void setCanPay(boolean canPay) {
		this.canPay = canPay;
	}
	public String getMsgCanPay() {
		return msgCanPay;
	}
	public void setMsgCanPay(String msgCanPay) {
		this.msgCanPay = msgCanPay;
	}
	public String getCredit_creditAccount() {
		return credit_creditAccount;
	}
	public void setCredit_creditAccount(String credit_creditAccount) {
		this.credit_creditAccount = credit_creditAccount;
	}
	public boolean isHasCredit() {
		return hasCredit;
	}
	public void setHasCredit(boolean hasCredit) {
		this.hasCredit = hasCredit;
	}
	public Double getSumBack() {
		return sumBack;
	}
	public void setSumBack(Double sumBack) {
		this.sumBack = sumBack;
	}
	public Double getSumBackMin() {
		return sumBackMin;
	}
	public void setSumBackMin(Double sumBackMin) {
		this.sumBackMin = sumBackMin;
	}
	public Double getSumBonusPay() {
		return sumBonusPay;
	}
	public void setSumBonusPay(Double sumBonusPay) {
		this.sumBonusPay = sumBonusPay;
	}
	public Double getSumBonusPayMin() {
		return sumBonusPayMin;
	}
	public void setSumBonusPayMin(Double sumBonusPayMin) {
		this.sumBonusPayMin = sumBonusPayMin;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
	public String getQiwiPayUrl() {
		return qiwiPayUrl;
	}
	public void setQiwiPayUrl(String qiwiPayUrl) {
		this.qiwiPayUrl = qiwiPayUrl;
	}
	public String getQiwiPhone() {
		return qiwiPhone;
	}
	public void setQiwiPhone(String qiwiPhone) {
		this.qiwiPhone = qiwiPhone;
	}
	public Long getYandexShopId() {
		return yandexShopId;
	}
	public void setYandexShopId(Long yandexShopId) {
		this.yandexShopId = yandexShopId;
	}
	public Long getYandexScid() {
		return yandexScid;
	}
	public void setYandexScid(Long yandexScid) {
		this.yandexScid = yandexScid;
	}
	public String getYandexUrl() {
		return yandexUrl;
	}
	public void setYandexUrl(String yandexUrl) {
		this.yandexUrl = yandexUrl;
	}
	public String getPayonlineUrl() {
		return payonlineUrl;
	}
	public void setPayonlineUrl(String payonlineUrl) {
		this.payonlineUrl = payonlineUrl;
	}
	public Integer getPayonlineMerchantId() {
		return payonlineMerchantId;
	}
	public void setPayonlineMerchantId(Integer payonlineMerchantId) {
		this.payonlineMerchantId = payonlineMerchantId;
	}
	public boolean isHasProlong() {
		return hasProlong;
	}
	public void setHasProlong(boolean hasProlong) {
		this.hasProlong = hasProlong;
	}
	public String getFormattedSumBack() {
		return formattedSumBack;
	}
	public void setFormattedSumBack(String formattedSumBack) {
		this.formattedSumBack = formattedSumBack;
	}
	public String getPayonlineSecurityKey() {
		return payonlineSecurityKey;
	}
	public void setPayonlineSecurityKey(String payonlineSecurityKey) {
		this.payonlineSecurityKey = payonlineSecurityKey;
	}
	public String getPayonlineReturnUrl() {
		return payonlineReturnUrl;
	}
	public void setPayonlineReturnUrl(String payonlineReturnUrl) {
		this.payonlineReturnUrl = payonlineReturnUrl;
	}
	public String getPayonlineFailUrl() {
		return payonlineFailUrl;
	}
	public void setPayonlineFailUrl(String payonlineFailUrl) {
		this.payonlineFailUrl = payonlineFailUrl;
	}
	public Integer getPeople_id() {
		return people_id;
	}
	public void setPeople_id(Integer people_id) {
		this.people_id = people_id;
	}
	public String getPeople_email() {
		return people_email;
	}
	public void setPeople_email(String people_email) {
		this.people_email = people_email;
	}
}
