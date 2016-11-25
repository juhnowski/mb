package ru.simplgroupp.rest.api.data;

import ru.simplgroupp.rest.api.data.payment.PaymentData;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.util.DatesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Объект для отображения на вебе данных о кредите
 */
public class CreditData {

    /**
     * id
     */
    private Integer id;
    /**
     * номер счета кредита
     */
    private String creditAccount;
    /**
     * сумма
     */
    private Double creditsum;
    /**
     * сумма возврата
     */
    private Double creditsumback;
    /**
     * дата начала
     */
    private String creditdatabeg;
    /**
     * дата окончания по графику
     */
    private String creditdataend;
    /**
     * фактическая дата окончания
     */
    private String creditdataendfact;
    /**
     * Статус
     */
    private String status;
    /**
     * Платежи по данному кредиту
     */
    private List<PaymentData> payments = new ArrayList<>();


    public CreditData(Credit credit) {
        this.creditAccount = credit.getCreditAccount();
        this.creditsum = credit.getCreditSum();
        this.creditdatabeg = credit.getDatabeg();
//        this.creditdataendfact = credit.getDataendFact();
        this.creditsumback = credit.getCreditSumBack();
        this.creditdataend = Convertor.dateToString(credit.getCreditDataEnd(), DatesUtils.FORMAT_ddMMYYYY);
        this.status = credit.getCreditStatus().getName();
        this.id = credit.getId();
    }


    public String getCreditAccount() {
        return creditAccount;
    }


    public void setCreditAccount(String creditAccount) {
        this.creditAccount = creditAccount;
    }


    public Double getCreditsum() {
        return creditsum;
    }


    public void setCreditsum(Double creditsum) {
        this.creditsum = creditsum;
    }


    public String getCreditdatabeg() {
        return creditdatabeg;
    }


    public void setCreditdatabeg(String creditdatabeg) {
        this.creditdatabeg = creditdatabeg;
    }


    public String getCreditdataendfact() {
        return creditdataendfact;
    }


    public void setCreditdataendfact(String creditdataendfact) {
        this.creditdataendfact = creditdataendfact;
    }


    public Double getCreditsumback() {
        return creditsumback;
    }


    public void setCreditsumback(Double creditsumback) {
        this.creditsumback = creditsumback;
    }


    public String getCreditdataend() {
        return creditdataend;
    }


    public void setCreditdataend(String creditdataend) {
        this.creditdataend = creditdataend;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public List<PaymentData> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentData> payments) {
        this.payments = payments;
    }
}
