package ru.simplgroupp.data;

import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Запрос из внешней системы на выполнение динамического метода
 */
public class ExecuteMethodData extends RequestParametersData {
    private String userlogin;
    private String userid;
    private String methodkey;
    private String executionid;
    private Map<String, Object> inputparameters;
    private String waitresponsems;


    public ExecuteMethodData(JSONObject json) {
        super(json);

        if (json.containsKey("userlogin")) {
            this.userlogin = json.get("userlogin").toString();
        }

        if (json.containsKey("userid")) {
            this.userid = json.get("userid").toString();
        }

        if (json.containsKey("methodkey")) {
            this.methodkey = json.get("methodkey").toString();
        }

        if (json.containsKey("executionid")) {
            this.executionid = json.get("executionid").toString();
        }

        if (json.containsKey("inputparameters")) {
            this.inputparameters = (Map<String, Object>) json.get("inputparameters");
        }

        if (json.containsKey("waitresponsems")) {
            this.waitresponsems = json.get("waitresponsems").toString();
        }
    }

    public String getUserlogin() {
        return userlogin;
    }

    public void setUserlogin(String userlogin) {
        this.userlogin = userlogin;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMethodkey() {
        return methodkey;
    }

    public void setMethodkey(String methodkey) {
        this.methodkey = methodkey;
    }

    public String getExecutionid() {
        return executionid;
    }

    public void setExecutionid(String executionid) {
        this.executionid = executionid;
    }

    public Map<String, Object> getInputparameters() {
        return inputparameters;
    }

    public void setInputparameters(Map<String, Object> inputparameters) {
        this.inputparameters = inputparameters;
    }

    public String getWaitresponsems() {
        return waitresponsems;
    }

    public void setWaitresponsems(String waitresponsems) {
        this.waitresponsems = waitresponsems;
    }

    @Override
    public String prettyJson() {
        return null;
    }
}
