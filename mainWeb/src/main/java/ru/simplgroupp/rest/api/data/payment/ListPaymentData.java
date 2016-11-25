package ru.simplgroupp.rest.api.data.payment;

import java.io.Serializable;
import java.util.List;

public class ListPaymentData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     * есть кредит
     */
	private boolean hasCredit;
	/**
     * список платежей
     */
	private List<PaymentData> lstPay;

	public List<PaymentData> getLstPay() {
		return lstPay;
	}

	public void setLstPay(List<PaymentData> lstPay) {
		this.lstPay = lstPay;
	}

	public boolean isHasCredit() {
		return hasCredit;
	}

	public void setHasCredit(boolean hasCredit) {
		this.hasCredit = hasCredit;
	}

}
