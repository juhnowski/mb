package ru.simplgroupp.rest.api.service;

import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.rest.api.data.bonus.BonusHistoryData;
import ru.simplgroupp.rest.api.data.bonus.BonusInfoData;
import ru.simplgroupp.rest.api.data.bonus.FriendData;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Сервис отвечающий за работу страницы бонусов
 */
@RequestScoped
public class BonusService implements Serializable {

	private static final long serialVersionUID = 1L;
    private PeopleBeanLocal peopleBean;
    private PeopleDAO peopleDAO;
    private RulesBeanLocal rulesBean;
    private ReferenceBooksLocal refBooks;
    private UserService userServ;

    /**
     * Формирует объект для заполнения страницы бонусов
     * @return объект с данными для страницы.
     */
    public BonusInfoData buildInfo() {
        Users user = userServ.getUser();
        Integer peopleMainId = user.getPeopleMain().getId();
        PeopleMain peopleMain = peopleDAO.getPeopleMain(peopleMainId, Utils.setOf(PeopleMain.Options.INIT_FRIENDS, PeopleMain.Options.INIT_CREDIT, PeopleMain.Options.INIT_BONUS));


        BonusInfoData info = new BonusInfoData();

        info.setBonusAmount(peopleDAO.getPeopleBonusInSystem(peopleMain.getId()));
        info.setBonusInviteAmount(refBooks.getBonusByCodeInteger(PeopleBonus.BONUS_CODE_INVITE).getAmount());
        info.setBonusPayPercent(Convertor.toInteger(Convertor.toDouble(rulesBean.getBonusConstants().get(CreditCalculatorBeanLocal.BONUS_PAY_PERCENT)) * 100));
        info.setBonusPay(Utils.triStateToBoolean(peopleDAO.getUserBonusPayProperties(peopleMainId)));

        List<FriendData> friends = new ArrayList<>();
        for (PeopleFriend pf : peopleMain.getPeopleFriendsForBonus()) {
            friends.add(new FriendData(pf));
        }
        info.setFriends(friends);
        
        List<BonusHistoryData> hist = new ArrayList<>();
        for (PeopleBonus pb : peopleMain.getPeoplebonus()) {
        	BonusHistoryData bdata = new BonusHistoryData();
        	bdata.setBonusName(pb.getBonus().getName());
        	bdata.setEventDate(pb.getEventDate());
        	bdata.setAmount(pb.getAmount());
        	bdata.setOperationName(pb.getOperation().getName());
        	hist.add(bdata);
        }

        Collections.sort(hist, new Comparator<BonusHistoryData>() {

            @Override
            public int compare(BonusHistoryData o1, BonusHistoryData o2) {
                return o1.getEventDate().compareTo(o2.getEventDate());
            }
        });
        
        info.setHistory(hist);

        return info;
    }

    /**
     * Сохраняет запрос на добавление друга.
     * @param friend объект с информацией по добавляемому другу.
     * @throws KassaException
     */
    public void saveFriend(FriendData friend) throws KassaException {
        Users user = userServ.getUser();
        Integer peopleMainId = user.getPeopleMain().getId();

        boolean b = this.peopleBean.saveFriend(peopleMainId, friend.getFirstName(),
                friend.getLastName(), friend.getEmail(), Convertor.fromMask(friend.getPhone()),false,PeopleFriend.FOR_BONUS);

        if (!b) {
            throw new KassaException("Вы уже отправляли приглашение этому человеку");
        }
    }

    /**
     * Сохраняет признак использования бонусов при оплате кредитов.
     * @param bonusPay признак использования бонусов, true - использовать, false - не использовать.
     */
    public void saveBonusPay(boolean bonusPay) {
        Users user = userServ.getUser();
        Integer peopleMainId = user.getPeopleMain().getId();

        peopleBean.saveUserBonusProperties(peopleMainId, Utils.booleanToTriState(bonusPay));
    }

    public BonusService() {}

    @Inject
    public BonusService(UserService userServ, PeopleBeanLocal peopleBean, PeopleDAO peopleDAO, RulesBeanLocal rulesBean, ReferenceBooksLocal refBooks) {
        this.peopleBean = peopleBean;
        this.peopleDAO = peopleDAO;
        this.rulesBean = rulesBean;
        this.refBooks = refBooks;
        this.userServ = userServ;
    }
}