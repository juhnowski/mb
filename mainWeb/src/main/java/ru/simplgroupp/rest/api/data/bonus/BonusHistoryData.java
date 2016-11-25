package ru.simplgroupp.rest.api.data.bonus;

import java.io.Serializable;
import java.util.Date;

public class BonusHistoryData implements Serializable {
	private static final long serialVersionUID = 1L;
	 /**
     * дата
     */
    private Date eventDate;
    /**
     * кол-во бонусов
     */
    private Double amount;
    /**
     * название бонусов
     */
    private String bonusName;
    /**
     * начисление или снятие бонусов
     */
    private String operationName;
    
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getBonusName() {
		return bonusName;
	}
	public void setBonusName(String bonusName) {
		this.bonusName = bonusName;
	}
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}    
    
}
