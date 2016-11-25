package ru.simplgroupp.rest.api.data;

import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.util.DatesUtils;

/**
 * Объект для веб отображения данных по заявкам на займ
 */
public class CreditRequestData {

    /**
     * уникальный номер
     */
    private String uniquenomer;
    /**
     * дата согласия
     */
    private String datecontest;
    /**
     * сумма заявки
     */
    private Double creditsum;
    /**
     * дней заявки
     */
    private Integer creditdays;
    /**
     * статус заявки
     */
    private String status;


    public CreditRequestData(CreditRequest creditRequest) {
        this.uniquenomer = creditRequest.getUniqueNomer();
        this.datecontest = Convertor.dateToString(creditRequest.getDateContest(), DatesUtils.FORMAT_ddMMYYYY);
        this.creditsum = creditRequest.getCreditSum();
        this.creditdays = creditRequest.getCreditDays();
        this.status = creditRequest.getStatus().getName();
    }


    public String getUniquenomer() {
        return uniquenomer;
    }


    public void setUniquenomer(String uniquenomer) {
        this.uniquenomer = uniquenomer;
    }


    public String getDatecontest() {
        return datecontest;
    }


    public void setDatecontest(String datecontest) {
        this.datecontest = datecontest;
    }


    public Double getCreditsum() {
        return creditsum;
    }


    public void setCreditsum(Double creditsum) {
        this.creditsum = creditsum;
    }


    public Integer getCreditdays() {
        return creditdays;
    }


    public void setCreditdays(Integer creditdays) {
        this.creditdays = creditdays;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }
}
