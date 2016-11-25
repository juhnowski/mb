package ru.simplgroupp.rest.api.data;

import org.hibernate.validator.constraints.NotBlank;
import ru.simplgroupp.validate.BankAccount;
import ru.simplgroupp.validate.Bik;
import ru.simplgroupp.validate.CorrAccount;
import ru.simplgroupp.validate.Kpp;

import java.io.Serializable;

/**
 * Bank account request
 */
@CorrAccount(valueField = "bankCorAccount", bikField = "bankBik")
@BankAccount(valueField = "account", bikField = "bankBik")
public class BankAccountRequest implements Serializable {

    private static final long serialVersionUID = -6133376706660687505L;

    @NotBlank
    @Bik
    private String bankBik;

    @NotBlank
    private String bankName;

    @NotBlank
    private String bankCorAccount;

    @NotBlank
    @Kpp
    private String bankKPP;

    @NotBlank
    private String account;

    public String getBankBik() {
        return bankBik;
    }

    public void setBankBik(String bankBik) {
        this.bankBik = bankBik;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCorAccount() {
        return bankCorAccount;
    }

    public void setBankCorAccount(String bankCorAccount) {
        this.bankCorAccount = bankCorAccount;
    }

    public String getBankKPP() {
        return bankKPP;
    }

    public void setBankKPP(String bankKPP) {
        this.bankKPP = bankKPP;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}