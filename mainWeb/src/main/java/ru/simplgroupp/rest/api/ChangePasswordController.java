package ru.simplgroupp.rest.api;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.data.changePassword.SmsRequestData;
import ru.simplgroupp.rest.api.data.changePassword.SmsResponseData;
import ru.simplgroupp.rest.api.service.ChangePasswordService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 * REST ресурс для обеспечения работы страницы смены пароля..
 */
@RequestScoped
@Path("/password/change")
public class ChangePasswordController {
    @Inject private ChangePasswordService service;

    /**
     * Отправить SMS код пользователю.
     * @param smsRequest объект с данными для смены пароля и отправки SMS кода.
     * @return объект с результатами отправки SMS кода.
     * @throws KassaException
     */
    @PUT
    @Path("sms/send")
    public SmsResponseData sendSms(SmsRequestData smsRequest) throws KassaException {
        return this.service.sendCodeLsn(smsRequest);
    }

    /**
     * Изменение пароля.
     * @param smsRequest объект с данными для смены пароля.
     * @return объект с результатами процесса изменения пароля.
     */
    @POST
    @Path("apply")
    public SmsResponseData save(SmsRequestData smsRequest) {
        return this.service.apply(smsRequest);
    }
}
