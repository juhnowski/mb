package ru.simplgroupp.controller;


import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.simplgroupp.data.*;
import ru.simplgroupp.service.OktellMainService;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("integration")
public class MainController {
    @Inject
    private OktellMainService oktellMainService;


    @POST
    @Path("main")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response testMethod(@FormParam("data") String data) throws IOException {
        String dataDecoded = null;
        if (data != null) {
            dataDecoded = new String(new Base64().decode(data.getBytes()));
            System.out.println("dataDecoded = " + dataDecoded);
        }

        RequestData req = buildRequest(dataDecoded);
        RequestData response = oktellMainService.processRequest(req);

        System.out.println("send = " + response.getMethod());

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);

        return Response.ok(mapper.writeValueAsString(response.toJSON())).build();
    }

    public RequestData buildRequest(String dataStr) {
        RequestData reqData = null;
        try {
            JSONArray dataJsonArray = (JSONArray) new JSONParser().parse(dataStr);
            String methodName = dataJsonArray.get(0).toString();
            JSONObject parameters = (JSONObject) dataJsonArray.get(1);

            RequestParametersData reqParamsData = null;
            reqData = new RequestData(methodName, null);

            switch (reqData.getMethod()) {
                case "whoareyou":
                    reqParamsData = new IamData(parameters);
                    break;
                case "iam":
                    reqParamsData = new IamData(parameters);
                    break;
                case "getavailablemethods":
                    reqParamsData = new EmptyParamData(parameters);
                    break;
                case "getavailableforms":
                    reqParamsData = new EmptyParamData(parameters);
                    break;
                case "executemethod":
                    reqParamsData = new ExecuteMethodData(parameters);
                    break;
            }

            reqData.setParameters(reqParamsData);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return reqData;
    }
}
