package ru.simplgroupp.rest.api.service;

import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.rest.api.data.bonus.FriendData;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.PeopleFriend;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.Users;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для обработки данные личного кабинета
 */
@RequestScoped
public class FriendsService {
    @Inject
    OverviewService overviewService;
    @EJB
    private PeopleDAO peopleDAO;
    @EJB
    private PeopleBeanLocal peopleBean;


    @PostConstruct
    private void init() throws KassaException {
        overviewService.init();
    }

    /**
     * Возвращает список друзей пользователя, не для бонусов
     *
     * @param user пользователь
     * @return мап для обработки в json
     */
    public Map<String, Object> getPeopleFriends(Users user) {
        Integer peopleMainId = user.getPeopleMain().getId();
        PeopleMain peopleMain = peopleDAO.getPeopleMain(peopleMainId, Utils.setOf(PeopleMain.Options.INIT_FRIENDS));

        List<FriendData> friends = new ArrayList<>();
        for (PeopleFriend pf : peopleMain.getPeopleFriendsNotForBonus()) {
            friends.add(new FriendData(pf));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("friends", friends);

        return map;
    }

    /**
     * Сохраняет нового друга не для бонусов.
     *
     * @param friend объект с информацией по добавляемому другу.
     * @throws Exception
     */
    public void addFriend(Users user, FriendData friend) throws KassaException {
        Integer peopleMainId = user.getPeopleMain().getId();

        boolean b = peopleBean.saveFriend(peopleMainId, friend.getFirstName(), friend.getLastName(), friend.getEmail(),
                friend.getPhone(), friend.isAvailable(), PeopleFriend.NOT_FOR_BONUS);

        if (!b) {
            throw new KassaException("Сохранение не удалось");
        }
    }

    /**
     * Обновляет данные друга
     *
     * @param friend новые данные
     */
    public void updateFriend(FriendData friend) throws KassaException {
        boolean update = peopleBean.updateFriend(friend.getId(), friend.getFirstName(), friend.getLastName(), friend.getEmail(),
                friend.getPhone(), friend.isAvailable());

        if (!update) {
            throw new KassaException("Обновление не удалось");
        }
    }

    /**
     * Удаляет друга
     *
     * @param friendId id друга
     */
    public void removeFriend(Integer friendId) {
        peopleDAO.deleteFriend(friendId);
    }
}





