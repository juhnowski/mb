package ru.simplgroupp.rest.api.data;

import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.RepaymentSchedule;
import ru.simplgroupp.util.DatesUtils;

/**
 * Объект для веб отображаения в таблице График платежей Личного кабинета
 */
public class RepaymentScheduleData {

    /**
     * дата начала
     */
    private String databeg;
    /**
     * дата окончания
     */
    private String dataend;
    /**
     * сумма к возврату
     */
    private Double creditsumback;
    /**
     * активная запись или нет
     */
    private Integer isactive;
    private String active;
    /**
     * причина окончания записи
     */
    private Integer reasonEndId;
    private String reasonEnd;


    public RepaymentScheduleData(RepaymentSchedule repaymentSchedule) {
        this.databeg = Convertor.dateToString(repaymentSchedule.getDatabeg(), DatesUtils.FORMAT_ddMMYYYY);
        this.dataend = Convertor.dateToString(repaymentSchedule.getDataend(), DatesUtils.FORMAT_ddMMYYYY);
        this.creditsumback = repaymentSchedule.getCreditsumback();
        this.isactive = repaymentSchedule.getIsActive();
        this.active = repaymentSchedule.getIsActive() != null && repaymentSchedule.getIsActive() == 1 ? "активная" : "закрытая";
        this.reasonEndId = repaymentSchedule.getReasonEndId();
        this.reasonEnd = switchReasonEnd(repaymentSchedule.getReasonEndId());
    }


    private String switchReasonEnd(Integer reasonEnd) {
        String res = "";
        if (reasonEnd != null) {
            switch (reasonEnd) {
                case 1:
                    res = "платеж";
                    break;
                case 2:
                    res = "продление";
                    break;
                case 3:
                    res = "погашение займа";
                    break;
            }
        }
        return res;
    }


    public String getDatabeg() {
        return databeg;
    }


    public void setDatabeg(String databeg) {
        this.databeg = databeg;
    }


    public String getDataend() {
        return dataend;
    }


    public void setDataend(String dataend) {
        this.dataend = dataend;
    }


    public Double getCreditsumback() {
        return creditsumback;
    }


    public void setCreditsumback(Double creditsumback) {
        this.creditsumback = creditsumback;
    }


    public Integer getIsactive() {
        return isactive;
    }


    public void setIsactive(Integer isactive) {
        this.isactive = isactive;
    }


    public String getActive() {
        return active;
    }


    public void setActive(String active) {
        this.active = active;
    }


    public Integer getReasonEndId() {
        return reasonEndId;
    }


    public void setReasonEndId(Integer reasonEndId) {
        this.reasonEndId = reasonEndId;
    }


    public String getReasonEnd() {
        return reasonEnd;
    }


    public void setReasonEnd(String reasonEnd) {
        this.reasonEnd = reasonEnd;
    }
}
