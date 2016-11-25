package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class SumData implements Serializable {
	
	/**
     * Id продукта
     */
	private Integer productId;
	private Integer creditDays;

	/**
     * сумма кредита
     */
    private Double creditSum;

    private Double creditSumMin;

    private Double creditSumMax;

    private Integer creditDaysMin;

    private Integer creditDaysMax;

    private Double stake;

    private Double stakeMin;

    private Double stakeMax = null;

    private Integer additionalDayPayment;
    
	public Integer getCreditDays() {
		return creditDays;
	}

	public void setCreditDays(Integer creditDays) {
		this.creditDays = creditDays;
	}

	public Double getCreditSum() {
		return creditSum;
	}

	public void setCreditSum(Double creditSum) {
		this.creditSum = creditSum;
	}

	public Double getCreditSumMin() {
		return creditSumMin;
	}

	public void setCreditSumMin(Double creditSumMin) {
		this.creditSumMin = creditSumMin;
	}

	public Double getCreditSumMax() {
		return creditSumMax;
	}

	public void setCreditSumMax(Double creditSumMax) {
		this.creditSumMax = creditSumMax;
	}

	public Integer getCreditDaysMin() {
		return creditDaysMin;
	}

	public void setCreditDaysMin(Integer creditDaysMin) {
		this.creditDaysMin = creditDaysMin;
	}

	public Integer getCreditDaysMax() {
		return creditDaysMax;
	}

	public void setCreditDaysMax(Integer creditDaysMax) {
		this.creditDaysMax = creditDaysMax;
	}

	public Double getStake() {
		return stake;
	}

	public void setStake(Double stake) {
		this.stake = stake;
	}

	public Double getStakeMin() {
		return stakeMin;
	}

	public void setStakeMin(Double stakeMin) {
		this.stakeMin = stakeMin;
	}

	public Double getStakeMax() {
		return stakeMax;
	}

	public void setStakeMax(Double stakeMax) {
		this.stakeMax = stakeMax;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getAdditionalDayPayment() {
        return additionalDayPayment;
    }

    public void setAdditionalDayPayment(Integer additionalDayPayment) {
        this.additionalDayPayment = additionalDayPayment;
    }
}
