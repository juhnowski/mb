package ru.simplgroupp.rest.api.data.addcredit;

import java.util.List;

/**
 * Объект для формирования страницы запроса нового кредита
 */
public class NewCreditInfoData {
    private boolean allowed;
    private String discardMessage;
    private int creditSum;
    private int creditSumMin;
    private int creditSumMax;
    private int creditDays;
    private int creditDaysMin;
    private int creditDaysMax;
    private double stake;
    private double stakeMin;
    private double stakeMax;
    private List<AccountInfoData> accounts;
    private Integer additionalDayPayment;

    /**
     * Разрешена или нет выдача кретита.
     * @return если true - выдача кредита разрешена, если false - выдача кредита запрещена.
     */
    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    /**
     * Сообщение с описанием запрета на выдачу кредита.
     * @return сообщение с описанием запрета на выдачу кредита.
     */
    public String getDiscardMessage() {
        return discardMessage;
    }

    public void setDiscardMessage(String discardMessage) {
        this.discardMessage = discardMessage;
    }

    /**
     * Стартовое значение суммы кредита.
     * @return сумма кредита.
     */
    public int getCreditSum() {
        return creditSum;
    }

    public void setCreditSum(int creditSum) {
        this.creditSum = creditSum;
    }

    /**
     * Минимальное значение суммы кредита.
     * @return минимальное значение суммы кредита.
     */
    public int getCreditSumMin() {
        return creditSumMin;
    }

    public void setCreditSumMin(int creditSumMin) {
        this.creditSumMin = creditSumMin;
    }

    /**
     * Максимальное значение суммы кредита.
     * @return максимальное значение суммы кредита.
     */
    public int getCreditSumMax() {
        return creditSumMax;
    }

    public void setCreditSumMax(int creditSumMax) {
        this.creditSumMax = creditSumMax;
    }

    /**
     * Начальное кол-во дней на которые выдается кредит.
     * @return начальное кол-во дней на которые выдается кредит.
     */
    public int getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(int creditDays) {
        this.creditDays = creditDays;
    }

    /**
     * Минимальное кол-во дней на которые выдается кредит.
     * @return минимальное кол-во дней на которые выдается кредит.
     */
    public int getCreditDaysMin() {
        return creditDaysMin;
    }

    public void setCreditDaysMin(int creditDaysMin) {
        this.creditDaysMin = creditDaysMin;
    }

    /**
     * Максимальное кол-во дней на которые выдается кредит.
     * @return максимальное кол-во дней на которые выдается кредит.
     */
    public int getCreditDaysMax() {
        return creditDaysMax;
    }

    public void setCreditDaysMax(int creditDaysMax) {
        this.creditDaysMax = creditDaysMax;
    }

    /**
     * Начальная ставка по кредиту.
     * @return начальная ставка по кредиту.
     */
    public double getStake() {
        return stake;
    }

    public void setStake(double stake) {
        this.stake = stake;
    }

    /**
     * Минимальная ставка по кредиту.
     * @return минимальная ставка по кредиту.
     */
    public double getStakeMin() {
        return stakeMin;
    }

    public void setStakeMin(double stakeMin) {
        this.stakeMin = stakeMin;
    }

    /**
     * Максимальная ставка по кредиту.
     * @return максимальная ставка по кредиту.
     */
    public double getStakeMax() {
        return stakeMax;
    }

    public void setStakeMax(double stakeMax) {
        this.stakeMax = stakeMax;
    }

    /**
     * Список счетов доступных для перевода средств по новому кредиту.
     * @return список счетов доступных для перевода средств по новому кредиту.
     */
    public List<AccountInfoData> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountInfoData> accounts) {
        this.accounts = accounts;
    }

    public Integer getAdditionalDayPayment() {
        return additionalDayPayment;
    }

    public void setAdditionalDayPayment(Integer additionalDayPayment) {
        this.additionalDayPayment = additionalDayPayment;
    }
}
