package ru.simplgroupp.rest.api.data;

/**
 * Created by PARFENOV on 29.05.2015.
 *
 * ПЛАТЕЖНЫЕ ДАННЫЕ
 */
public class AccountData extends AbstractUserData{

    private Integer id;
    private String name;
    private Integer codeType;
    private Integer active;
    private String corrAccountNumber;
    private String cardName;
    private String cardNumber;
    private String bankName;
    /**
     * номер счета
     */
    private String accountnumber;
    /**
     * бик банка
     */
    private String bik;


    private String cardNumberMasked;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Integer getCodeType() {
        return codeType;
    }


    public void setCodeType(Integer codeType) {
        this.codeType = codeType;
    }


    public Integer getActive() {
        return active;
    }


    public void setActive(Integer active) {
        this.active = active;
    }


    public String getAccountnumber() {
        return accountnumber;
    }


    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }


    public String getBik() {
        return bik;
    }


    public void setBik(String bik) {
        this.bik = bik;
    }


    public String getCardNumberMasked() {
        return cardNumberMasked;
    }


    public void setCardNumberMasked(String cardNumberMasked) {
        this.cardNumberMasked = cardNumberMasked;
    }

    public String getCorrAccountNumber() {
        return corrAccountNumber;
    }


    public void setCorrAccountNumber(String corrAccountNumber) {
        this.corrAccountNumber = corrAccountNumber;
    }


    public String getCardName() {
        return cardName;
    }


    public void setCardName(String cardName) {
        this.cardName = cardName;
    }


    public String getCardNumber() {
        return cardNumber;
    }


    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }


    public String getBankName() {
        return bankName;
    }


    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
