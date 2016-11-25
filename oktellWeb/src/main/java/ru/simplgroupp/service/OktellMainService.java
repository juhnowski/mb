package ru.simplgroupp.service;

import ru.simplgroupp.data.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class OktellMainService {
    @Inject
    private ExecuteMethodService executeMethodService;


    public RequestData processRequest(RequestData req) {
        switch (req.getMethod()) {
            case "whoareyou":
                return responseWhoAreYou(req);
            case "iam":
                return responseIAm(req);
            case "getavailablemethods":
                return responseGetAvailMethods(req);
            case "getavailableforms":
                return responseGetAvailForms(req);
            case "executemethod":
                return responseExecuteMethod(req);
        }

        return null;
    }

    public RequestData responseWhoAreYou(RequestData reqData) {
        IamData iam = new IamData();
        iam.setQid(UUID.randomUUID().toString());
        iam.setType("crm-server");
        iam.setName("Test CRM");
        iam.setVersion(0.2);

        return new RequestData("iam", iam);
    }

    public RequestData responseIAm(RequestData reqData) {
        IamData parameters = (IamData) reqData.getParameters();
        System.out.println("name is = " + parameters.getName());
        System.out.println("version is = " + parameters.getVersion());
        System.out.println("type is = " + parameters.getType());

        return new RequestData();
    }

    public RequestData responseGetAvailMethods(RequestData reqData) {
        List<MethodData> methods = new ArrayList<>();
        MethodData methodTest = new MethodData("testMethod", "Тестовый метод", "Тестирование работы без записи данных", "http",
                null, false, false, null, null);

        List<MethodParamData> inputParams = new ArrayList<>();
        inputParams.add(new MethodParamData("route", "Направление звонка", "1 - исходящий звонок, 2 - входящий звонок", MethodParamData.ParamType.INTEGER));
        inputParams.add(new MethodParamData("startCallDate", "Начало звонка", "Дата начала звонка", MethodParamData.ParamType.DATETIME));
        inputParams.add(new MethodParamData("endCallDate", "Конец звонка", "Дата окончания звонка", MethodParamData.ParamType.DATETIME));
        inputParams.add(new MethodParamData("status", "Статус звонка", "Статус совершенного звонка (пропущен, завершен и т.д.)", MethodParamData.ParamType.INTEGER));
        inputParams.add(new MethodParamData("phone", "Номер телефона", "Номер телефона на который звоним либо с которого звонят", MethodParamData.ParamType.STRING));
        inputParams.add(new MethodParamData("userId", "ID пользователя", "Идентификатор пользователя по которому его можно отличить", MethodParamData.ParamType.STRING));
        inputParams.add(new MethodParamData("record", "Запись разговора", "Ссылка на запись разговора", MethodParamData.ParamType.STRING));

        MethodData methodWriteCall = new MethodData("writeCall", "Сохранение совершенного звонка", "Сохранение совершенного звонка", "http",
                null, true, false, inputParams, null);

        methods.add(methodTest);
        methods.add(methodWriteCall);

        AvailableMethodsData availableMethodsData = new AvailableMethodsData(methods);
        availableMethodsData.setQid(UUID.randomUUID().toString());

        return new RequestData("availablemethods", availableMethodsData);
    }

    public RequestData responseGetAvailForms(RequestData reqData) {
        List<MethodData> methods = new ArrayList<>();
        AvailableFormsData availableFormsData = new AvailableFormsData(methods);
        availableFormsData.setQid(UUID.randomUUID().toString());

        return new RequestData("availableforms", availableFormsData);
    }

    public RequestData responseExecuteMethod(RequestData reqData) {
        ExecuteMethodData executeMethod = (ExecuteMethodData) reqData.getParameters();
        if ("testMethod".equals(executeMethod.getMethodkey())) {
            executeMethodService.testMethod();
            MethodResultData result = new MethodResultData(executeMethod.getExecutionid(), null);
            result.setMethodkey(executeMethod.getMethodkey());
            return new RequestData("methodresult", result);
        } else if ("writeCall".equals(executeMethod.getMethodkey())) {
            executeMethodService.writeCall(reqData);
            MethodResultData result = new MethodResultData(executeMethod.getExecutionid(), null);
            result.setMethodkey(executeMethod.getMethodkey());
            return new RequestData("methodresult", result);
        } else {
            System.out.println("unknown method");
        }

        return new RequestData();
    }
}
