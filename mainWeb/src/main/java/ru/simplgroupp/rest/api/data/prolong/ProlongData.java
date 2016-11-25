package ru.simplgroupp.rest.api.data.prolong;

/**
 * Объект описывающий продление кредита.
 */
public class ProlongData {
    private Integer longDays;
    private String agreement;

    /**
     * Колличество дней на которое запрашивается продление.
     * @return колличество дней на которое запрашивается продление.
     */
    public Integer getLongDays() {
        return longDays;
    }

    public void setLongDays(Integer longDays) {
        this.longDays = longDays;
    }

    /**
     * Пользовательское соглашение.
     * @return пользовательское соглашение.
     */
    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }
}
