package ru.simplgroupp.rest.api;

import org.apache.commons.lang.StringUtils;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.data.addcredit.NewCreditInfoData;
import ru.simplgroupp.rest.api.data.addcredit.SaveRequestData;
import ru.simplgroupp.rest.api.service.NewCreditService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.util.CalcUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * REST ресурс для обеспечения работы страницы выдачи нового кредита.
 */
@RequestScoped
@Path("/addcredit")
public class NewCreditController {
    private NewCreditService service;

    public NewCreditController() {}

    /**
     * Формирует объект для заполнения страницы выдачи нового кредита.
     * @param summStr запрашиваемая сумма кредита.
     * @param daysStr запрашиваемое количество дней нового кредита.
     * @return объект с данными для страницы.
     * @throws KassaException
     */
    @GET
    @Path("/info/")
    public NewCreditInfoData getInfo(@QueryParam("summ") String summStr, @QueryParam("days") String daysStr) throws KassaException {
        Double summ = StringUtils.isNotEmpty(summStr) ? Convertor.toDouble(summStr, CalcUtils.dformat) : null;
        Integer days = StringUtils.isNotEmpty(daysStr) ? Convertor.toInteger(daysStr) : null;

        return this.service.buildInfo(summ, days);
    }

    /**
     * Создает запрос на выдачу кредита.
     * @param saveRequest объект с параметрами запрашиваемого кредита.
     * @throws Exception
     */
    @POST
    public void save(SaveRequestData saveRequest) throws Exception {
        this.service.save(saveRequest);
    }

    @Inject
    public NewCreditController(NewCreditService service) {
        this.service = service;
    }
}
