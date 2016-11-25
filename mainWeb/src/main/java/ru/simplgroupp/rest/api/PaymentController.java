package ru.simplgroupp.rest.api;

import ru.simplgroupp.client.common.rest.payment.WinpayController;
import ru.simplgroupp.client.common.rest.payment.YandexController;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Payment controller
 */
@Path("payment")
@Produces(MediaType.APPLICATION_JSON)
public class PaymentController {

    @Inject
    private WinpayController winpayController;

    @Inject
    private YandexController yandexController;

    @Path("winpay")
    public WinpayController getWinpayController() {
        return winpayController;
    }

    @Path("yandex")
    public YandexController getYandexController() {
        return yandexController;
    }
}
