package ru.simplgroupp.rest.api.data;

import java.io.Serializable;
import java.util.Date;

public class OverviewData implements Serializable{
    	/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
	     * вид платежа (кредит, продление, рефинансирование)
	     */
		private Integer sumIdPay;

		/**
	     * есть кредит
	     */
		private boolean hasCredit;
		/**
	     * есть заявка
	     */
		private boolean hasCreditRequest;
		/**
	     * есть последняя заявка
	     */
		private boolean hasLastCreditRequest;
		/**
	     * может ли быть продление
	     */
		private boolean canProlong;
		/**
	     * может ли быть оплата продления
	     */
		private boolean canProlongPay;
		/**
	     * может ли быть рефинансирование
	     */
		private boolean canRefinance;
		/**
	     * может ли быть оплата рефинансирования
	     */
		private boolean canRefinancePay;
		/**
	     * может ли быть подписание оферты
	     */
		private boolean canSign;
		/**
	     * уникальный номер
	     */
	    private String lastCreditRequest_uniqueNomer;
		 /**
	     * дата согласия
	     */
		private Date lastCreditRequest_dateContest;
		/**
		 * время согласия
		 */
		private String lastCreditRequest_dateContestTime;
		/**
	     * Статус заявки
	     */
		private String lastCreditRequest_status;
		/**
		 * id кредита
		 */
	    private Integer credit_id;
		/**
		 * сумма
		 */
	    private Double credit_creditSum;
	    /**
		 * сумма возврата
		 */
	    private Double credit_creditSumBack;
	    /**
		 * процент кредита
		 */
	    private Double credit_creditPercent;
	    /**
		 * номер счета кредита
		 */
	    private String credit_creditAccount;
	    /**
		 * дата начала
		 */
	    private Date credit_creditDataBeg;
	    /**
		 * дата окончания по графику
		 */
	    private Date credit_creditDataEnd;

	    /**
		 * запись активная или нет
		 */
		private Integer credit_isActive;
		/**
	     * сумма заявки
	     */
	    private Double creditRequest_creditSum;
		/**
	     * уникальный номер
	     */
	    private String creditRequest_uniqueNomer;
		 /**
	     * дата согласия
	     */
		private Date creditRequest_dateContest;
		/**
		 * время согласия
		 */
		private String creditRequest_dateContestTime;
		/**
	     * Статус заявки
	     */
		private String creditRequest_status;
		/**
	     * оферта, текст
	     */
	    private String creditRequest_agreement;

	    /**
	     * была ли одобрена
	     */
	    private Integer creditRequest_accepted;
	    /**
    	 * смс-код подтверждения
    	 */
	    private String confirmSmsCode;
		/**
    	 * выплаченная сумма по кредиту
    	 */
    	private Double paySum;
    	/**
    	 * смс-код подтверждения заявки
    	 */
    	private String creditRequest_smsCode;
    	/**
    	 * есть ли продление у кредита
    	 */
    	private boolean hasProlongDraft;
    	/**
    	 * есть ли рефинансирование у кредита
    	 */
    	private boolean hasRefinanceDraft;
    	/**
    	 * есть ли просрочка у кредита
    	 */
    	private boolean isOverdue;
    	/**
    	 * сумма процентов для продления
    	 */
    	private Double percentSum;
    	/**
    	 * сумма для рефинансирования
    	 */
    	private Double refinanceSum;


		public Double getPaySum() {
			return paySum;
		}
		public void setPaySum(Double paySum) {
			this.paySum = paySum;
		}
		public String getConfirmSmsCode() {
			return confirmSmsCode;
		}
		public void setConfirmSmsCode(String confirmSmsCode) {
			this.confirmSmsCode = confirmSmsCode;
		}
		public boolean isHasProlongDraft() {
			return hasProlongDraft;
		}
		public void setHasProlongDraft(boolean hasProlongDraft) {
			this.hasProlongDraft = hasProlongDraft;
		}
		public boolean isHasRefinanceDraft() {
			return hasRefinanceDraft;
		}
		public void setHasRefinanceDraft(boolean hasRefinanceDraft) {
			this.hasRefinanceDraft = hasRefinanceDraft;
		}
		public boolean isOverdue() {
			return isOverdue;
		}
		public void setOverdue(boolean isOverdue) {
			this.isOverdue = isOverdue;
		}
		public Double getPercentSum() {
			return percentSum;
		}
		public void setPercentSum(Double percentSum) {
			this.percentSum = percentSum;
		}
		public Double getRefinanceSum() {
			return refinanceSum;
		}
		public void setRefinanceSum(Double refinanceSum) {
			this.refinanceSum = refinanceSum;
		}

		public boolean isHasCredit() {
			return hasCredit;
		}
		public void setHasCredit(boolean hasCredit) {
			this.hasCredit = hasCredit;
		}
		public boolean isHasCreditRequest() {
			return hasCreditRequest;
		}
		public void setHasCreditRequest(boolean hasCreditRequest) {
			this.hasCreditRequest = hasCreditRequest;
		}
		public boolean isHasLastCreditRequest() {
			return hasLastCreditRequest;
		}
		public void setHasLastCreditRequest(boolean hasLastCreditRequest) {
			this.hasLastCreditRequest = hasLastCreditRequest;
		}
		public boolean isCanProlong() {
			return canProlong;
		}
		public void setCanProlong(boolean canProlong) {
			this.canProlong = canProlong;
		}
		public boolean isCanProlongPay() {
			return canProlongPay;
		}
		public void setCanProlongPay(boolean canProlongPay) {
			this.canProlongPay = canProlongPay;
		}
		public boolean isCanRefinance() {
			return canRefinance;
		}
		public void setCanRefinance(boolean canRefinance) {
			this.canRefinance = canRefinance;
		}
		public boolean isCanRefinancePay() {
			return canRefinancePay;
		}
		public void setCanRefinancePay(boolean canRefinancePay) {
			this.canRefinancePay = canRefinancePay;
		}
		public String getCreditRequest_dateContestTime() {
			return creditRequest_dateContestTime;
		}
		public void setCreditRequest_dateContestTime(String creditRequest_dateContestTime) {
			this.creditRequest_dateContestTime = creditRequest_dateContestTime;
		}
		public boolean isCanSign() {
			return canSign;
		}
		public void setCanSign(boolean canSign) {
			this.canSign = canSign;
		}
		public Date getLastCreditRequest_dateContest() {
			return lastCreditRequest_dateContest;
		}
		public void setLastCreditRequest_dateContest(
				Date lastCreditRequest_dateContest) {
			this.lastCreditRequest_dateContest = lastCreditRequest_dateContest;
		}
		public String getLastCreditRequest_dateContestTime() {
			return lastCreditRequest_dateContestTime;
		}
		public void setLastCreditRequest_dateContestTime(String lastCreditRequest_dateContestTime) {
			this.lastCreditRequest_dateContestTime = lastCreditRequest_dateContestTime;
		}
		public String getLastCreditRequest_status() {
			return lastCreditRequest_status;
		}
		public void setLastCreditRequest_status(String lastCreditRequest_status) {
			this.lastCreditRequest_status = lastCreditRequest_status;
		}
		public Double getCredit_creditSum() {
			return credit_creditSum;
		}
		public void setCredit_creditSum(Double credit_creditSum) {
			this.credit_creditSum = credit_creditSum;
		}
		public Double getCredit_creditSumBack() {
			return credit_creditSumBack;
		}
		public void setCredit_creditSumBack(Double credit_creditSumBack) {
			this.credit_creditSumBack = credit_creditSumBack;
		}
		public Double getCredit_creditPercent() {
			return credit_creditPercent;
		}
		public void setCredit_creditPercent(Double credit_creditPercent) {
			this.credit_creditPercent = credit_creditPercent;
		}
		public String getCredit_creditAccount() {
			return credit_creditAccount;
		}
		public void setCredit_creditAccount(String credit_creditAccount) {
			this.credit_creditAccount = credit_creditAccount;
		}
		public Date getCredit_creditDataBeg() {
			return credit_creditDataBeg;
		}
		public void setCredit_creditDataBeg(Date credit_creditDataBeg) {
			this.credit_creditDataBeg = credit_creditDataBeg;
		}
		public Date getCredit_creditDataEnd() {
			return credit_creditDataEnd;
		}
		public void setCredit_creditDataEnd(Date credit_creditDataEnd) {
			this.credit_creditDataEnd = credit_creditDataEnd;
		}
		public Integer getCredit_isActive() {
			return credit_isActive;
		}
		public void setCredit_isActive(Integer credit_isActive) {
			this.credit_isActive = credit_isActive;
		}
		public Double getCreditRequest_creditSum() {
			return creditRequest_creditSum;
		}
		public void setCreditRequest_creditSum(Double creditRequest_creditSum) {
			this.creditRequest_creditSum = creditRequest_creditSum;
		}
		public String getCreditRequest_uniqueNomer() {
			return creditRequest_uniqueNomer;
		}
		public void setCreditRequest_uniqueNomer(
				String creditRequest_uniqueNomer) {
			this.creditRequest_uniqueNomer = creditRequest_uniqueNomer;
		}
		public Date getCreditRequest_dateContest() {
			return creditRequest_dateContest;
		}
		public void setCreditRequest_dateContest(
				Date creditRequest_dateContest) {
			this.creditRequest_dateContest = creditRequest_dateContest;
		}
		public String getCreditRequest_status() {
			return creditRequest_status;
		}
		public void setCreditRequest_status(String creditRequest_status) {
			this.creditRequest_status = creditRequest_status;
		}
		public String getCreditRequest_agreement() {
			return creditRequest_agreement;
		}
		public void setCreditRequest_agreement(String creditRequest_agreement) {
			this.creditRequest_agreement = creditRequest_agreement;
		}
		public String getLastCreditRequest_uniqueNomer() {
			return lastCreditRequest_uniqueNomer;
		}
		public void setLastCreditRequest_uniqueNomer(
				String lastCreditRequest_uniqueNomer) {
			this.lastCreditRequest_uniqueNomer = lastCreditRequest_uniqueNomer;
		}
		public Integer getSumIdPay() {
			return sumIdPay;
		}
		public void setSumIdPay(Integer sumIdPay) {
			this.sumIdPay = sumIdPay;
		}
		public Integer getCredit_id() {
			return credit_id;
		}
		public void setCredit_id(Integer credit_id) {
			this.credit_id = credit_id;
		}
		public Integer getCreditRequest_accepted() {
			return creditRequest_accepted;
		}
		public void setCreditRequest_accepted(Integer creditRequest_accepted) {
			this.creditRequest_accepted = creditRequest_accepted;
		}
		public String getCreditRequest_smsCode() {
			return creditRequest_smsCode;
		}
		public void setCreditRequest_smsCode(String creditRequest_smsCode) {
			this.creditRequest_smsCode = creditRequest_smsCode;
		}


}
