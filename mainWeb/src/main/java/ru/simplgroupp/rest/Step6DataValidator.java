package ru.simplgroupp.rest;

import org.apache.commons.lang.StringUtils;
import ru.simplgroupp.data.ErrorData;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.creditrequestwizard.Step6Data;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.List;

/**
 * Валидатор шага 6
 */
@Singleton
public class Step6DataValidator {

    @EJB
    protected PeopleBeanLocal peopleBean;

    public void validate(Step6Data step6, List<ErrorData> errors) {
        if (step6.getOption() == null || step6.getOption() <= 0) {
            errors.add(new ErrorData("option", "Необходимо выбрать одно из значений"));
        } else {
            switch (step6.getOption()) {
                case Account.BANK_TYPE:
                    if (StringUtils.isEmpty(step6.getBankaccount())) {
                        errors.add(new ErrorData("bankaccount", "Поле обязательно для заполнения"));
                    } else {
                        if (!StringUtils.isNumeric(step6.getBankaccount())) {
                            errors.add(new ErrorData("bankaccount", "Поле должно содержать цифры"));
                        }
                    }
                    if (StringUtils.isEmpty(step6.getBik())) {
                        errors.add(new ErrorData("bik", "Поле обязательно для заполнения"));
                    } else {
                        if (!StringUtils.isNumeric(step6.getBik())) {
                            errors.add(new ErrorData("bik", "Поле должно содержать цифры"));
                        }
                    }
                    if (StringUtils.isEmpty(step6.getCorrespondentAccount())) {
                        errors.add(new ErrorData("correspondentAccount", "Поле обязательно для заполнения"));
                    } else {
                        if (!StringUtils.isNumeric(step6.getCorrespondentAccount())) {
                            errors.add(new ErrorData("correspondentAccount", "Поле должно содержать цифры"));
                        }
                    }
                    break;
                case Account.CARD_TYPE:
                    if (StringUtils.isEmpty(step6.getCardnomer())) {
                        errors.add(new ErrorData("cardnomer", "Поле обязательно для заполнения"));
                    } else {
                        step6.setCardnomer(step6.getCardnomer().replace("-", ""));
                        if (!StringUtils.isNumeric(step6.getCardnomer())) {
                            errors.add(new ErrorData("cardnomer", "Поле должно содержать цифры"));
                        }
                    }
                    break;
                case Account.YANDEX_TYPE:
                    if (StringUtils.isEmpty(step6.getYandexcardnomer())) {
                        errors.add(new ErrorData("yandexcardnomer", "Поле обязательно для заполнения"));
                    } else {
                        if (!StringUtils.isNumeric(step6.getYandexcardnomer())) {
                            errors.add(new ErrorData("yandexcardnomer", "Поле должно содержать цифры"));
                        }
                    }
                    break;
                case Account.QIWI_TYPE:
                    if (StringUtils.isEmpty(step6.getQiwiPhone())) {
                        errors.add(new ErrorData("qiwi_phone", "Поле обязательно для заполнения"));
                    } else {
                        if (!step6.getQiwiPhone().matches("^\\+[0-9\\)\\(\\s,\\-]+(\\+[0-9]+)?$")) {
                            errors.add(new ErrorData("qiwi_phone", "Поле не соответствует формату"));
                        }
                    }
                    break;
                case Account.CONTACT_TYPE:
                    break;
                default:
                    errors.add(new ErrorData("option", "Необходимо выбрать одно из значений"));
            }
        }
    }

}
