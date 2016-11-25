package ru.simplgroupp.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Главный класс который содержит имя метода и параметры
 */
public class RequestData {
    private String method;
    private RequestParametersData parameters;


    public RequestData() {
    }

    public RequestData(String method, RequestParametersData parameters) {
        this.method = method;
        this.parameters = parameters;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RequestParametersData getParameters() {
        return parameters;
    }

    public void setParameters(RequestParametersData parameters) {
        this.parameters = parameters;
    }

    public List<Object> toJSON() {
        List<Object> response = new LinkedList<>();
        response.add(method);
        response.add(parameters);

        return response;
    }

    public String prettyJson() {
        return "[\n" +
                    "\"" + getMethod() + "\",\n" +
                    parameters.prettyJson() +
                "]";
    }
}
