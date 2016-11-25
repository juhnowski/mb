package ru.simplgroupp.rest.api.data.addcredit;

/**
 * Объект описывающий счет для перевода средств по новому кредиту
 */
public class AccountInfoData {
    private int id;
    private String description;

    /**
     * ID счета.
     * @return id счета.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Описание счета.
     * @return описание счета.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
