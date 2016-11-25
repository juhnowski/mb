package ru.simplgroupp.data;

/**
 * Step6Data
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 23.04.14
 */
public class Step6Data {
    private String yandexcardnomer,bankaccount,bik,cardnomer,id;
    private Integer option,option2,accept;

    private String payonlineAmount;

    public Step6Data(){
        yandexcardnomer = cardnomer = bankaccount = bik = cardnomer = id = "";
        option = option2 = accept = null;
    }

    public String getYandexcardnomer() {
        return yandexcardnomer;
    }

    public void setYandexcardnomer(String yandexcardnomer) {
        this.yandexcardnomer = yandexcardnomer;
    }

    public String getCardnomer() {
        return cardnomer;
    }

    public void setCardnomer(String cardnomer) {
        this.cardnomer = cardnomer;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public Integer getOption2() {
        return option2;
    }

    public void setOption2(Integer option2) {
        this.option2 = option2;
    }

    public Integer getAccept() {
        return accept;
    }

    public void setAccept(Integer accept) {
        this.accept = accept;
    }

    public String getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(String bankaccount) {
        this.bankaccount = bankaccount;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayonlineAmount() {
        return payonlineAmount;
    }

    public void setPayonlineAmount(String payonlineAmount) {
        this.payonlineAmount = payonlineAmount;
    }
}
