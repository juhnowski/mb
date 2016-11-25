package ru.simplgroupp.rest;

import org.apache.commons.lang.StringUtils;
import ru.simplgroupp.data.ErrorData;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.PeopleContact;
import ru.simplgroupp.transfer.creditrequestwizard.Step1Data;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.List;

/**
 * Валидатор шага 1
 */
@Singleton
public class Step1DataValidator {

    @EJB
    protected PeopleBeanLocal peopleBean;

    public void validate(Step1Data step1, CreditRequest creditRequest, List<ErrorData> errors) {
        if (StringUtils.isEmpty(step1.getLastName())) {
            errors.add(new ErrorData("lastName", "Поле обязательно для заполнения"));
        }
        if (StringUtils.isEmpty(step1.getFirstName())) {
            errors.add(new ErrorData("firstName", "Поле обязательно для заполнения"));
        }
        if (StringUtils.isEmpty(step1.getMiddleName())) {
            errors.add(new ErrorData("middleName", "Поле обязательно для заполнения"));
        }
        if (step1.getBirthday() == null) {
            errors.add(new ErrorData("birthday", "Поле обязательно для заполнения"));
        } else {

            Integer age = StaticFuncs.calculateAge(step1.getBirthday());
            if (age > 70 || age < 18) {
                errors.add(new ErrorData("birthday", "Мы не выдаем кредит лицам моложе 18 и старше 70 лет"));
            }
        }
        if (StringUtils.isEmpty(step1.getPhone())) {
            errors.add(new ErrorData("phone", "Поле обязательно для заполнения"));
        } else {
            if (!step1.getPhone().matches("^\\+[0-9\\)\\(\\s,\\-]+(\\+[0-9]+)?$")) {
                errors.add(new ErrorData("phone", "Поле не соответствует формату"));
            }
        }
        if (StringUtils.isEmpty(step1.getEmail())) {
            errors.add(new ErrorData("email", "Поле обязательно для заполнения"));
        } else {
            if (!step1.getEmail().matches("^^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                errors.add(new ErrorData("email", "Поле не соответствует формату"));
            }
        }
        if (StringUtils.isEmpty(step1.getPhoneCode())) {
            String oldPhone = peopleBean.findContactClient(PeopleContact.CONTACT_CELL_PHONE, creditRequest.getPeopleMain().getId());
            if (oldPhone == null || !oldPhone.equals(Convertor.fromMask(step1.getPhone()))) {
                errors.add(new ErrorData("phoneCode", "Поле обязательно для заполнения"));
            }
        } else {
            if (step1.getPhoneHash() != null &&
                    !step1.getPhoneHash().equals(StaticFuncs.md5("salt" + step1.getPhone() + "supersalt" + step1.getPhoneCode() + "megasalt"))) {
                errors.add(new ErrorData("phoneCode", "Поле не соответствует формату"));
            }
        }
        if (StringUtils.isEmpty(step1.getEmailCode())) {
            String oldEmail = peopleBean.findContactClient(PeopleContact.CONTACT_EMAIL, creditRequest.getPeopleMain().getId());
            if (oldEmail == null || !oldEmail.equals(step1.getEmail())) {
                errors.add(new ErrorData("emailCode", "Поле обязательно для заполнения"));
            }
        } else {
//            if (data.getEmailHash() != null &&
//                  !data.getEmailHash().equals(StaticFuncs.md5("salt" + data.getEmail() + "supersalt" + data.getEmail_code() + "megasalt"))) {
//                ans.getErrors().add(new ErrorData("email_code", "Не верный код подтверждения"));
//            }
        }
    }

}
