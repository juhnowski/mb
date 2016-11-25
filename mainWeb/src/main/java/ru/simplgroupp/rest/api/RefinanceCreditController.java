package ru.simplgroupp.rest.api;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.data.refinance.RefinanceInfoData;
import ru.simplgroupp.rest.api.data.refinance.SmsRequestData;
import ru.simplgroupp.rest.api.data.refinance.SmsResponseData;
import ru.simplgroupp.rest.api.service.RefinanceCreditService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 * REST ресурс для обеспечения работы страницы рефинансирования кредита.
 */
@Path("refinance")
public class RefinanceCreditController {
    @Inject private RefinanceCreditService service;

    /**
     * Формирует объект для заполнения страницы рефинансирования кредита.
     * @return объект с данными для страницы.
     */
    @GET
    @Path("info")
    public RefinanceInfoData getInfo() {
        return service.buildInfo();
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

    @PUT
    @Path("agreement")
    public SmsResponseData recalcAgreement(SmsRequestData smsRequest) throws KassaException {
        return this.service.recalcAgreement(smsRequest);
    }

    /**
     * Создание рефинансирования кредита.
     * @param smsRequest объект с новыми параметрами по кредиту.
     * @return объект с результатами процесса создания рефинансирования кредита.
     */
    @POST
    @Path("save")
    public SmsResponseData save(SmsRequestData smsRequest) {
        return this.service.save(smsRequest);
    }
}
