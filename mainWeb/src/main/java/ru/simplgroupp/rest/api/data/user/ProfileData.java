package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.rest.api.data.AbstractUserData;
import ru.simplgroupp.rest.api.data.PeopleMainData;
import ru.simplgroupp.rest.api.data.ReferenceData;

import java.util.List;

/**
 * даннные для "настройкки профиля"
 */
public class ProfileData extends AbstractUserData {
    /**
     * основные данные, включая телефон и email
     */
    private MainData mainData;

    /**
     * данные для пароля
     */
    private PasswordData passwordData;


    public ProfileData() {
    }

    public MainData getMainData() {
        return mainData;
    }

    public void setMainData(MainData mainData) {
        this.mainData = mainData;
    }

    public PasswordData getPasswordData() {
        return passwordData;
    }

    public void setPasswordData(PasswordData passwordData) {
        this.passwordData = passwordData;
    }
}
