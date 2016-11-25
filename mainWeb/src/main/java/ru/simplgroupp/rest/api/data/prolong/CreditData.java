package ru.simplgroupp.rest.api.data.prolong;

/**
 * Объект описывающий кредит.
 */
public class CreditData {
    private Double sumMainRemain;
    private Double creditPercent;

    /**
     * Сумма оставшегося основного долга.
     * @return сумма оставшегося основного долга.
     */
    public Double getSumMainRemain() {
        return sumMainRemain;
    }

    public void setSumMainRemain(Double sumMainRemain) {
        this.sumMainRemain = sumMainRemain;
    }

    /**
     * Процент по кредиту.
     * @return процент по кредиту.
     */
    public Double getCreditPercent() {
        return creditPercent;
    }

    public void setCreditPercent(Double creditPercent) {
        this.creditPercent = creditPercent;
    }
}
