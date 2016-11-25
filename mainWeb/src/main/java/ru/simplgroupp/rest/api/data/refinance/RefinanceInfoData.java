package ru.simplgroupp.rest.api.data.refinance;

/**
 * Объект для заполнения страницы рефинансирования кредита.
 */
public class RefinanceInfoData {
    private boolean canRefinance;
    private boolean canRefinanceRun;
    private String msgCanRefinance;
    private String msgCanRefinanceRun;
    private Double refinanceAmount;
    private Double sumAll;
    private Double sumPercent;
    private Double sumBack;
    private String dataend;
    private Double sumMin;
    private Double sumMax;
    private Double stake;
    private Integer refinanceDays;
    private String agreement;

    /**
     * Признак разрешающий рефинансирования по кредиту.
     * @return true - рефинансирование разрешено, false - запрешено.
     */
    public boolean isCanRefinance() {
        return canRefinance;
    }

    public void setCanRefinance(boolean canRefinance) {
        this.canRefinance = canRefinance;
    }

    /**
     * Признак разрешаюший запуск рефинансирования по кредиту.
     * @return true - рефинансирование разрешено, false - запрешено.
     */
    public boolean isCanRefinanceRun() {
        return canRefinanceRun;
    }

    public void setCanRefinanceRun(boolean canRefinanceRun) {
        this.canRefinanceRun = canRefinanceRun;
    }

    /**
     * Сообщение с пояснением причин для случаев, когда рефинансирование по кредиту запрещено.
     * @return текст сообщения.
     */
    public String getMsgCanRefinance() {
        return msgCanRefinance;
    }

    public void setMsgCanRefinance(String msgCanRefinance) {
        this.msgCanRefinance = msgCanRefinance;
    }

    /**
     * Сообщение с пояснением причин для случаев, когда запуск рефинансирования по кредиту запрешен.
     * @return текст сообщения.
     */
    public String getMsgCanRefinanceRun() {
        return msgCanRefinanceRun;
    }

    public void setMsgCanRefinanceRun(String msgCanRefinanceRun) {
        this.msgCanRefinanceRun = msgCanRefinanceRun;
    }

    /**
     * Сумма рефинансирования по кредиту.
     * @return сумма рефинансирования по кредиту.
     */
    public Double getRefinanceAmount() {
        return refinanceAmount;
    }

    public void setRefinanceAmount(Double refinanceAmount) {
        this.refinanceAmount = refinanceAmount;
    }

    /**
     * Полная сумма к выплате по кредиту до рефинансирования.
     * @return полная сумма к выплате по кредиту до рефинансирования.
     */
    public Double getSumAll() {
        return sumAll;
    }
    public void setSumAll(Double sumAll) {
        this.sumAll = sumAll;
    }

    /**
     * Процент к выплате по кредиту после рефинансирования.
     * @return процент к выплате по кредиту после рефинансирования.
     */
    public Double getSumPercent() {
        return sumPercent;
    }

    public void setSumPercent(Double sumPercent) {
        this.sumPercent = sumPercent;
    }

    /**
     * Сумма к выплате по кредиту после рефинансирования.
     * @return сумма к выплате по кредиту после рефинансирования.
     */
    public Double getSumBack() {
        return sumBack;
    }

    public void setSumBack(Double sumBack) {
        this.sumBack = sumBack;
    }

    /**
     * Дата окончания выплат по кредиту.
     * @return дата окончания выплат по кредиту.
     */
    public String getDataend() {
        return dataend;
    }

    public void setDataend(String dataend) {
        this.dataend = dataend;
    }

    /**
     * Минимальная сумма рефинансирования.
     * @return минимальная сумма рефинансирования.
     */
    public Double getSumMin() {
        return sumMin;
    }

    public void setSumMin(Double sumMin) {
        this.sumMin = sumMin;
    }

    /**
     * Максимальная сумма рефинансирования.
     * @return максимальная сумма рефинансирования.
     */
    public Double getSumMax() {
        return sumMax;
    }

    public void setSumMax(Double sumMax) {
        this.sumMax = sumMax;
    }

    /**
     * Ставка рефинансирования.
     * @return ставка рефинансирования.
     */
    public Double getStake() {
        return stake;
    }

    public void setStake(Double stake) {
        this.stake = stake;
    }

    /**
     * Дата рефинасирования.
     * @return дата рефинасирования.
     */
    public Integer getRefinanceDays() {
        return refinanceDays;
    }

    public void setRefinanceDays(Integer refinanceDays) {
        this.refinanceDays = refinanceDays;
    }

    /**
     * Пользовательское соглашение.
     * @return текст соглашения.
     */
    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }
}
