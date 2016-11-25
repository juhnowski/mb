package ru.simplgroupp.rest.api.service;

import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.toolkit.common.GenUtils;

import javax.ejb.*;
import java.util.Date;

/**
 * Сервис для генерации номеров.
 * За счет LockType.WRITE метод может вызвать только один поток, что избавляет от дублирования номеров.
 */
@LocalBean
@Singleton
@Lock(LockType.WRITE)
public class GeneratorService {
    public Integer findMaxCreditRequestNumber(Date dt) {
        return kassa.findMaxCreditRequestNumber(dt);
    }

    public String genUniqueNumber(Date date, Integer nn, Double pp) {
        return GenUtils.genUniqueNumber(date, nn, pp);
    }

    @EJB
    private KassaBeanLocal kassa;
}
