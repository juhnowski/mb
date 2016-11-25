package ru.simplgroupp.rest.api.data.bonus;

import java.io.Serializable;
import java.util.List;

/**
 * Объект содержащий информацию для формирования страницы с информацией по бонусным баллам.
 */
public class BonusInfoData implements Serializable {
 
	private static final long serialVersionUID = 1L;
	private Double bonusInviteAmount;
    private Integer bonusPayPercent;
    private Boolean bonusPay;
    private List<FriendData> friends;
    private List<BonusHistoryData> history;
    private double bonusAmount;

    /**
     * Количество бонусных баллов начисляемых за зарегестрировавшегося друга приглашенного пользователем.
     * @return количество бонусных баллов начисляемых за зарегестрировавшегося друга приглашенного пользователем.
     */
    public Double getBonusInviteAmount() {
        return bonusInviteAmount;
    }

    public void setBonusInviteAmount(Double bonusInviteAmount) {
        this.bonusInviteAmount = bonusInviteAmount;
    }

    /**
     * Максимальный прцент займа, который можно погасить бонусными баллами.
     * @return максимальный прцент займа, который можно погасить бонусными баллами.
     */
    public Integer getBonusPayPercent() {
        return bonusPayPercent;
    }

    public void setBonusPayPercent(Integer bonusPayPercent) {
        this.bonusPayPercent = bonusPayPercent;
    }

    /**
     * Индикатор - включено или нет использование бонусов при оплате.
     * @return true - включено, false - выключено.
     */
    public Boolean getBonusPay() {
        return bonusPay;
    }

    public void setBonusPay(Boolean bonusPay) {
        this.bonusPay = bonusPay;
    }

    /**
     * Список друзей приглашенных пользователем.
     * @return список друзей приглашенных пользователем.
     */
    public List<FriendData> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendData> friends) {
        this.friends = friends;
    }

    /**
     * Количество бонусных баллов на счету пользователя.
     * @return количество бонусных баллов на счету пользователя.
     */
    public double getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(double bonusAmount) {
        this.bonusAmount = bonusAmount;
    }
    
    /**
     * История начисления и трат бонусов
     * @return историю начисления и трат бонусов.
     */
	public List<BonusHistoryData> getHistory() {
		return history;
	}

	public void setHistory(List<BonusHistoryData> history) {
		this.history = history;
	}
}
