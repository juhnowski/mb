package ru.simplgroupp.rest.api;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.data.prolong.ProlongInfoData;
import ru.simplgroupp.rest.api.data.prolong.SmsRequestData;
import ru.simplgroupp.rest.api.data.prolong.SmsResponseData;
import ru.simplgroupp.rest.api.service.ProlongCreditService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 * REST ресурс для обеспечения работы страницы продления кредита.
 */
@RequestScoped
@Path("/prolong")
public class ProlongCreditController {
    private ProlongCreditService service;

    /**
     * Формирует объект для заполнения страницы продления кредита.
     * @return объект с данными для страницы.
     * @throws KassaException
     */
    @GET
    @Path("/info/")
    public ProlongInfoData getInfo() throws KassaException {
        return this.service.buildInfo();
    }

    /**
     * Обновление пользовательского соглашения.
     * @param smsRequest объект с данными для формирования новых параметров кредита.
     * @return объект с новым соглашением.
     */
    @PUT
    @Path("/agreement/")
    public SmsResponseData recalcAgreement(SmsRequestData smsRequest) throws KassaException {
        return this.service.recalcAgreement(smsRequest);
    }

    /**
     * Отправить SMS код пользователю.
     * @param smsRequest объект с данными для формирования новых параметров кредита и отправки SMS кода.
     * @return объект с результатами отправки SMS кода.
     * @throws KassaException
     */
    @PUT
    @Path("sms/send")
    public SmsResponseData sendSms(SmsRequestData smsRequest) throws KassaException {
        return this.service.sendCodeLsn(smsRequest);
    }

    /**
     * Создание продления кредита.
     * @param smsRequest объект с новыми параметрами по кредиту.
     * @return объект с результатами процесса создания продления кредита.
     */
    @POST
    @Path("save")
    public SmsResponseData save(SmsRequestData smsRequest) {
        return this.service.save(smsRequest);
    }

    public ProlongCreditController() {}

    @Inject
    public ProlongCreditController(ProlongCreditService service) {
        this.service = service;
    }
}
