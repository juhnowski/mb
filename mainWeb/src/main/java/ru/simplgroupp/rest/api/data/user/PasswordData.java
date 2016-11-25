package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.rest.api.data.AbstractUserData;

/**
 * Данные для изменения пароля пользователя
 */
public class PasswordData extends AbstractUserData {
    /**
     * старый пароль
     */
    private String oldPassword;

    /**
     * новый пароль
     */
    private String newPassword;

    /**
     * подтверждение пароля
     */
    private String newPassword2;


    public PasswordData() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }
}
