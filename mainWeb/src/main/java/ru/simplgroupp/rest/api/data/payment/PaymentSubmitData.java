package ru.simplgroupp.rest.api.data.payment;

/**
 * Объект инициации платежа
 */
public class PaymentSubmitData {
    /**
     * Список возможных типов платежей
     */
    public static enum Type {
        PAYONLINE, QIWI, YANDEX, CONTACT
    }

    private Type type;
    private Double paymentSum;
    private String accountNumber;

    /**
     * Тип платежа
     * @return тип платежа.
     */
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Сумма платежа.
     * @return сумма платежа.
     */
    public Double getPaymentSum() {
        return paymentSum;
    }

    public void setPaymentSum(Double paymentSum) {
        this.paymentSum = paymentSum;
    }

    /**
     * Номер счета кредита
     * @return
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
