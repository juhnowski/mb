package ru.simplgroupp.rest.api.data.prolong;

import java.io.Serializable;

/**
 * Объект для формирования страницы продления кредита.
 */
public class ProlongInfoData implements Serializable {
    private ProlongData prolong;
    private CreditData credit;
    private Boolean canProlong;
    private Boolean canProlongCancel;
    private String msgCanProlong;
    private Double sumMain;
    private Double sumOldStake;
    private Double sumStake;
    private Double sumAll;
    private Long dateEnd;
    private Integer creditDaysMin;
    private Integer creditDaysMax;
    private Integer days;
    private Integer daysf;

    /**
     * Формирование нового объекта.
     */
    public ProlongInfoData() {
        this.prolong = new ProlongData();
        this.credit = new CreditData();
    }

    /**
     * Объект описывающий продление кредита.
     * @return объект описывающий продление кредита.
     */
    public ProlongData getProlong() {
        return prolong;
    }

    public void setProlong(ProlongData prolong) {
        this.prolong = prolong;
    }

    /**
     * Объект описывающий кредит.
     * @return объект описывающий кредит.
     */
    public CreditData getCredit() {
        return credit;
    }

    public void setCredit(CreditData credit) {
        this.credit = credit;
    }

    /**
     * Признак доступности продления кредита.
     * @return true - продление доступно, false - продление не доступно.
     */
    public Boolean getCanProlong() {
        return canProlong;
    }

    public void setCanProlong(Boolean canProlong) {
        this.canProlong = canProlong;
    }

    /**
     * Признак доступности отмены кредита.
     * @return true - отмена доступна, false - отмена не доступна.
     */
    public Boolean getCanProlongCancel() {
        return canProlongCancel;
    }

    public void setCanProlongCancel(Boolean canProlongCancel) {
        this.canProlongCancel = canProlongCancel;
    }

    /**
     * Комментарий с описанием пречины не доступности продления кредита.
     * @return комментарий с описанием пречины не доступности продления кредита.
     */
    public String getMsgCanProlong() {
        return msgCanProlong;
    }

    public void setMsgCanProlong(String msgCanProlong) {
        this.msgCanProlong = msgCanProlong;
    }

    /**
     * Сумма кредиту до рефинансирования.
     * @return сумма кредиту до рефинансирования.
     */
    public Double getSumMain() {
        return sumMain;
    }

    public void setSumMain(Double sumMain) {
        this.sumMain = sumMain;
    }

    /**
     * Проценты к выплате по кредиту до рефинансирования.
     * @return проценты к выплате по кредиту до рефинансирования.
     */
    public Double getSumOldStake() {
        return sumOldStake;
    }

    public void setSumOldStake(Double sumOldStake) {
        this.sumOldStake = sumOldStake;
    }

    /**
     * Проценты к выплате по кредиту после рефинансирования.
     * @return проценты к выплате по кредиту после рефинансирования.
     */
    public Double getSumStake() {
        return sumStake;
    }

    public void setSumStake(Double sumStake) {
        this.sumStake = sumStake;
    }

    /**
     * Полная сумма выплат по кредиту после рефинансирования.
     * @return полная сумма выплат по кредиту после рефинансирования.
     */
    public Double getSumAll() {
        return sumAll;
    }

    public void setSumAll(Double sumAll) {
        this.sumAll = sumAll;
    }

    /**
     * Дата окончания выплат по кредиту.
     * @return дата окончания выплат по кредиту.
     */
    public Long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Long dateEnd) {
        this.dateEnd = dateEnd;
    }

    /**
     * Минимально возможное количество дней прдления кредита.
     * @return минимально возможное количество дней прдления кредита.
     */
    public Integer getCreditDaysMin() {
        return creditDaysMin;
    }

    public void setCreditDaysMin(Integer creditDaysMin) {
        this.creditDaysMin = creditDaysMin;
    }

    /**
     * Максимально возможное количество дней прдления кредита.
     * @return максимально возможное количество дней прдления кредита.
     */
    public Integer getCreditDaysMax() {
        return creditDaysMax;
    }

    public void setCreditDaysMax(Integer creditDaysMax) {
        this.creditDaysMax = creditDaysMax;
    }

    /**
     * Количество дней для оплаты процентов по продлению.
     * @return количество дней для оплаты процентов по продлению.
     */
    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    /**
     * Необходимое количество дней до окончания кредита для продления.
     * @return необходимое количество дней до окончания кредита для продления.
     */
    public Integer getDaysf() {
        return daysf;
    }

    public void setDaysf(Integer daysf) {
        this.daysf = daysf;
    }
}
