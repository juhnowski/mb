package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeoplePaymentData implements Serializable {
	private String yandexcardnomer;

    private String bankaccount;

    private String bik;

    private String correspondentAccount;

    private String cardnomer;

    private Integer option;

    private Integer option2;

    private Integer accept;

    private String payonlineAmount;

    private String payonlineSum;

    private String qiwiPhone;

    private boolean isVerifySum = false;

    private Boolean contest;

	public String getYandexcardnomer() {
		return yandexcardnomer;
	}

	public void setYandexcardnomer(String yandexcardnomer) {
		this.yandexcardnomer = yandexcardnomer;
	}

	public String getBankaccount() {
		return bankaccount;
	}

	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}

	public String getBik() {
		return bik;
	}

	public void setBik(String bik) {
		this.bik = bik;
	}

	public String getCorrespondentAccount() {
		return correspondentAccount;
	}

	public void setCorrespondentAccount(String correspondentAccount) {
		this.correspondentAccount = correspondentAccount;
	}

	public String getCardnomer() {
		return cardnomer;
	}

	public void setCardnomer(String cardnomer) {
		this.cardnomer = cardnomer;
	}

	public Integer getOption() {
		return option;
	}

	public void setOption(Integer option) {
		this.option = option;
	}

	public Integer getOption2() {
		return option2;
	}

	public void setOption2(Integer option2) {
		this.option2 = option2;
	}

	public Integer getAccept() {
		return accept;
	}

	public void setAccept(Integer accept) {
		this.accept = accept;
	}

	public String getPayonlineAmount() {
		return payonlineAmount;
	}

	public void setPayonlineAmount(String payonlineAmount) {
		this.payonlineAmount = payonlineAmount;
	}

	public String getPayonlineSum() {
		return payonlineSum;
	}

	public void setPayonlineSum(String payonlineSum) {
		this.payonlineSum = payonlineSum;
	}

	public String getQiwiPhone() {
		return qiwiPhone;
	}

	public void setQiwiPhone(String qiwiPhone) {
		this.qiwiPhone = qiwiPhone;
	}

	public boolean isVerifySum() {
		return isVerifySum;
	}

	public void setVerifySum(boolean isVerifySum) {
		this.isVerifySum = isVerifySum;
	}

	public Boolean getContest() {
		return contest;
	}

	public void setContest(Boolean contest) {
		this.contest = contest;
	}
}
