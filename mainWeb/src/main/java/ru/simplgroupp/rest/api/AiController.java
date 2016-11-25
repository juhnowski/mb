package ru.simplgroupp.rest.api;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.data.user.AiData;
import ru.simplgroupp.rest.api.service.AiService;
import ru.simplgroupp.rest.api.service.OverviewService;
import ru.simplgroupp.rest.api.service.UserService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Отдает в веб  разрешения на совершения действий
 */
@Path("/ai")
public class AiController {
    @Inject
    private UserService userServ;

    @Inject
    private OverviewService overviewService;

    @Inject
    private AiService aiService;


    @PostConstruct
    private void init() throws KassaException {
        overviewService.init();
    }

    /**
     * возвращает права для клиента (может ли взять кредит, может ли редактировать данные и тд)
     */
    @GET
    @Path("/getData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<AiData> getData() {
        userServ.getUser();

        AiData aiData = new AiData(aiService.getCanAdd(), aiService.isCanEdit());
        return new JsonResult<>(aiData);
    }
}
