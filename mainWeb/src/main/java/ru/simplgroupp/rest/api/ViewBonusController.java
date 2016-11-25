package ru.simplgroupp.rest.api;

import java.util.List;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.data.bonus.BonusHistoryData;
import ru.simplgroupp.rest.api.data.bonus.BonusInfoData;
import ru.simplgroupp.rest.api.data.bonus.FriendData;
import ru.simplgroupp.rest.api.service.BonusService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * REST ресурс для обеспечения работы страницы бонусов.
 */
@Path("/bonus")
@Stateless
public class ViewBonusController  {
    private BonusService service;

    /**
     * Формирует объект для заполнения страницы.
     * @return
     */
    @GET
    @Path("/info/")
    public JsonResult<BonusInfoData> getInfo() {
        return new JsonResult<BonusInfoData>(this.service.buildInfo());
    }

    /**
     * Сохраняет запрос на приглашение друга.
     * @param friend данные приглашаемого человека.
     * @throws KassaException
     */
    @POST
    @Path("/friends/")
    public void friendSubmit(FriendData friend) throws KassaException {
        this.service.saveFriend(friend);
    }

    /**
     * Сохраняет параметры работы с бонусами.
     * @param bonusPay использовать или нет бонусы при оплате кредитов. true - использовать, false - не использовать.
     */
    @POST
    @Path("/props/")
    public void saveBonusPay(boolean bonusPay) {
        this.service.saveBonusPay(bonusPay);
    }


    public ViewBonusController() {}

    @Inject
    public ViewBonusController(BonusService service) {
        this.service = service;
    }
}
