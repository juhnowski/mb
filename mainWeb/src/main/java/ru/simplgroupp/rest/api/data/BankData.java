package ru.simplgroupp.rest.api.data;

/**
 * Данные для обратного звонка
 */
public class BankData {
    /**
     * БИК
     */
    private String bik;
    /**
     * Корреспондентский счет
     */
    private String corAccount;
    /**
     * Название
     */
    private String name;


    public BankData() {
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getCorAccount() {
        return corAccount;
    }

    public void setCorAccount(String corAccount) {
        this.corAccount = corAccount;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }
}
