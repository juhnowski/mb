package ru.simplgroupp.rest.api.service;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.interfaces.CreditBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.BaseCredit;
import ru.simplgroupp.transfer.PeopleSums;
import ru.simplgroupp.transfer.Users;
import ru.simplgroupp.util.DatesUtils;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequestScoped
public class ListPeopleSumsService {

    @EJB
    private CreditBeanLocal creditBean;
    @EJB
    private CreditDAO creditDAO;


    /**
     * данные для таблиц страничка Копилка
     *
     * @param user - пользователь
     * @return - map с ключами
     * in - МОИ ОСТАВШИЕСЯ СУММЫ В СИСТЕМЕ
     * out - Мои израсходованные суммы
     */
    public Map<String, Object> getData(Users user) {
        Integer peopleId = user.getPeopleMain().getId();
        List<PeopleSums> lstSumsIn = creditBean.listPeopleSums(peopleId, BaseCredit.OPERATION_IN);
        List<PeopleSums> lstSumsOut = creditBean.listPeopleSums(peopleId, BaseCredit.OPERATION_OUT);


        Map<String, Object> res = new HashMap<>();
        List<Map<String, Object>> in = new ArrayList<>();
        for (PeopleSums peopleSums : lstSumsIn) {
            Map<String, Object> map = new HashMap<>();
            map.put("creditAccount", peopleSums.getCredit().getCreditAccount());
            map.put("amount", peopleSums.getAmount());
            map.put("eventDate", Convertor.dateToString(peopleSums.getEventDate(), DatesUtils.FORMAT_ddMMYYYY));
            map.put("operationType", peopleSums.getOperationType().getName());
            in.add(map);
        }
        res.put("in", in);
        List<Map<String, Object>> out = new ArrayList<>();
        for (PeopleSums peopleSums : lstSumsOut) {
            Map<String, Object> map = new HashMap<>();
            map.put("creditAccount", peopleSums.getCredit().getCreditAccount());
            map.put("amount", peopleSums.getAmount());
            map.put("eventDate", Convertor.dateToString(peopleSums.getEventDate(), DatesUtils.FORMAT_ddMMYYYY));
            map.put("operationType", peopleSums.getOperationType().getName());
            out.add(map);
        }
        res.put("out", out);

        return res;
    }


    /**
     * сумма в копилке
     */
    public Double getMySum(Users user){
        Double summ = creditDAO.getPeopleSumsInSystem(user.getPeopleMain().getId());
        return summ;
    }
}
