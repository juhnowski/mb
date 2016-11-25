package ru.simplgroupp.rest.api.data.payment;

import java.util.Date;

public class PaymentData {
	
	/**
     * Дата создания
     */
	private Date createDate;
	/**
     * Дата проведения
     */
	private Date processDate;
	/**
     * Сумма платежа
     */
	private Double amount;
	/**
     * Как платили
     */
	private String partner_realName;
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getProcessDate() {
		return processDate;
	}
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getPartner_realName() {
		return partner_realName;
	}
	public void setPartner_realName(String partner_realName) {
		this.partner_realName = partner_realName;
	}
	

}
