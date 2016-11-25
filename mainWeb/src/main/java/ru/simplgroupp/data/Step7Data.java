package ru.simplgroupp.data;

/**
 * Step7Data
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 24.04.14
 */
public class Step7Data {
    private Integer creditdays,creditsum;
    private String smsCode,id,phonehash,agreement;
    private Double stake;

    private Double stakeMin;

    private Double stakeMax = null;

    private Double creditSumMin;

    private Double creditSumMax;

    private Integer creditDaysMin;

    private Integer creditDaysMax;

    public Step7Data(){
        phonehash = id = smsCode = agreement = "";
        creditdays = creditsum = null;
        stake = null;
    }

    public Integer getCreditdays() {
        return creditdays;
    }

    public void setCreditdays(Integer creditdays) {
        this.creditdays = creditdays;
    }

    public Integer getCreditsum() {
        return creditsum;
    }

    public void setCreditsum(Integer creditsum) {
        this.creditsum = creditsum;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhonehash() {
        return phonehash;
    }

    public void setPhonehash(String phonehash) {
        this.phonehash = phonehash;
    }

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
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
}
