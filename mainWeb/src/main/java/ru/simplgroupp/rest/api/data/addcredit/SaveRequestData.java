package ru.simplgroupp.rest.api.data.addcredit;

import java.util.Map;

/**
 * Объект описывающий запрос на выдачу нового кредита.
 */
public class SaveRequestData {
    private Double creditSum;
    private Integer creditDays;
    private Integer accountId;
    private Map<String, String> behavior;

    /**
     * Запрашиваемая сумма кредита.
     * @return запрашиваемая сумма кредита.
     */
    public Double getCreditSum() {
        return creditSum;
    }

    public void setCreditSum(Double creditSum) {
        this.creditSum = creditSum;
    }

    /**
     * Запрашиваемый срок кредита (кол-во дней).
     * @return запрашиваемый срок кредита (кол-во дней).
     */
    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    /**
     * ID счета для начисления кредита.
     * @return id счета для начисления кредита.
     */
    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    /**
     * Данные мониторинга активности пользователя запросившего кредит.
     * @return данные мониторинга активности пользователя запросившего кредит.
     */
    public Map<String, String> getBehavior() {
        return behavior;
    }

    public void setBehavior(Map<String, String> behavior) {
        this.behavior = behavior;
    }
}
