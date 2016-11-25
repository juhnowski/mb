package ru.simplgroupp.rest.api.service;

import org.apache.commons.lang.StringUtils;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.MailBeanLocal;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.rest.api.data.changePassword.SmsRequestData;
import ru.simplgroupp.rest.api.data.changePassword.SmsResponseData;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.PeopleMain;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Сервис обеспечивающий работу страницы изменения пароля.
 */
@RequestScoped
public class ChangePasswordService {
    private static Logger logger = Logger.getLogger(ChangePasswordService.class.getName());

    @Inject private UserService userSrv;
    @Inject private ChangePasswordCacheService cache;
    @Inject private UsersBeanLocal userBean;
    @Inject private PeopleDAO peopleDAO;
    @Inject private MailBeanLocal mailBean;

    /**
     * Отправить SMS код пользователю.
     * @param smsRequest объект с данными для отправки SMS кода.
     * @return объект с результатами отправки SMS кода.
     * @throws KassaException
     */
    public SmsResponseData sendCodeLsn(SmsRequestData smsRequest) throws KassaException {
        String msg = null;

        PeopleMain people = this.peopleDAO.getPeopleMain(this.userSrv.getUser().getPeopleMain().getId(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_CONTACT));
        try {
        	String smsCode = mailBean.generateCodeForSending();
            //String smsCode = GenUtils.genCode(6);
            this.cache.setSmsCode(smsCode);
            logger.info("Sms code " + smsCode);
            mailBean.sendSMSV2(people.getCellPhone().getValue(), "Код " + smsCode
                    + ". Это код для подтверждения редактирования личных данных в системе Растороп.");
        } catch (Exception ex) {
            msg = "Не удалось отправить смс. ";
        }

        SmsResponseData smsResponse = new SmsResponseData();
        if (msg != null) {
            smsResponse.setSuccess(false);
            smsResponse.setMsg(msg);
        } else {
            smsResponse.setSuccess(true);
        }

        return smsResponse;
    }

    /**
     * Изменение пароля.
     * @param smsRequest объект с параметрами для изменения пароля..
     * @return объект с результатом изменения пароля.
     */
    public SmsResponseData apply(SmsRequestData smsRequest) {
        String originalSmsCode = this.cache.getSmsCode();
        String submittedSmsCode = smsRequest.getSmsCode();

        String msg = null;
        if (StringUtils.isEmpty(originalSmsCode)) {
            msg = "Необходимо получить код СМС";
        } else if (StringUtils.isEmpty(submittedSmsCode)) {
            msg = "Необходимо ввести код СМС";
        } else if (!originalSmsCode.equals(submittedSmsCode)) {
            msg = "Неверный код СМС";
        } else if (StringUtils.isEmpty(smsRequest.getPassword())) {
            msg = "Необходимо задать пароль";
        }

        SmsResponseData smsResponse = new SmsResponseData();
        if (msg != null) {
            smsResponse.setSuccess(false);
            smsResponse.setMsg(msg);
        } else {
            this.userBean.changePassword(this.userSrv.getUser().getPeopleMain().getId(), smsRequest.getPassword());
            smsResponse.setSuccess(true);
        }

        return smsResponse;
    }
}
