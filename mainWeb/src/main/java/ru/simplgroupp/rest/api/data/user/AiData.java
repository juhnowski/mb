package ru.simplgroupp.rest.api.data.user;

/**
 * Разрешения на совершение действий
 */
public class AiData {
    private boolean canAdd;

    /**
     * можно ли редактировать данные профиля
     */
    private boolean canEdit;


    public AiData() {
    }

    public AiData(boolean canAdd, boolean canEdit) {
        this.canAdd = canAdd;
        this.canEdit = canEdit;
    }

    public boolean isCanAdd() {
        return canAdd;
    }

    public void setCanAdd(boolean canAdd) {
        this.canAdd = canAdd;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
