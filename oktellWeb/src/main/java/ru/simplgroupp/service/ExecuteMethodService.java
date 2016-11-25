package ru.simplgroupp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.data.ExecuteMethodData;
import ru.simplgroupp.data.RequestData;
import ru.simplgroupp.interfaces.CallsLocal;
import ru.simplgroupp.persistence.CallsEntity;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@RequestScoped
public class ExecuteMethodService {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteMethodService.class);

    @EJB
    private CallsLocal callsBean;


    public boolean testMethod() {
        logger.info("TEST METHOD WORKING!");
        return true;
    }

    public boolean writeCall(RequestData reqData) {
        ExecuteMethodData executeMethodData = (ExecuteMethodData) reqData.getParameters();
        Map<String, Object> inputParameters = executeMethodData.getInputparameters();

        try {
            if (!inputParameters.containsKey("startCallDate")) {
                throw new Exception("startCallDate отсутствует");
            }
            if (!inputParameters.containsKey("endCallDate")) {
                throw new Exception("endCallDate отсутствует");
            }
            if (!inputParameters.containsKey("status")) {
                throw new Exception("status отсутствует");
            }
            if (!inputParameters.containsKey("status")) {
                throw new Exception("status отсутствует");
            }
            if (!inputParameters.containsKey("userId")) {
                throw new Exception("userId отсутствует");
            }
            if (!inputParameters.containsKey("phone")) {
                throw new Exception("phone отсутствует");
            }
            if (!inputParameters.containsKey("route")) {
                throw new Exception("route отсутствует");
            }


            //2015-10-29 15:21:27
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

            Date startCall = format.parse(inputParameters.get("startCallDate").toString());
            Date endCall = format.parse(executeMethodData.getInputparameters().get("endCallDate").toString());


            Integer status = new Integer(executeMethodData.getInputparameters().get("status").toString());
            String userId = executeMethodData.getInputparameters().get("userId").toString();

            // телефон приходит с первой 8кой 10ти значный
            // телефон надо проверить на длину, и вместо первой 8ки вставить 7
            String phoneRaw = executeMethodData.getInputparameters().get("phone").toString();
            if (phoneRaw.length() < 10) {
                throw new Exception("телефон на который звонили слишком короткий, телефон должен быть 10ти значный");
            }
            String phone = "7" + phoneRaw.substring(phoneRaw.length() - 10);

            // входящий или исходящий звонок
            boolean incoming = true;
            if (new Integer(executeMethodData.getInputparameters().get("route").toString()) == 2) {
                incoming = false;
            }


            CallsEntity callsEntity = callsBean.saveCall(startCall, endCall, status, phone, incoming, userId);
            logger.info("звонок сохранен " + callsEntity);
        } catch (Exception e) {
            logger.error("ошибка парсинга " + e);
        }


        return true;
    }
}
