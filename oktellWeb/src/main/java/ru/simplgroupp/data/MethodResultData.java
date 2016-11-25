package ru.simplgroupp.data;

import java.util.HashMap;
import java.util.Map;

public class MethodResultData extends RequestParametersData {
    private String methodkey;
    private String executionid;
    private Map<String, String> outputparameters;


    public MethodResultData(String executionid, Map<String, String> outputparameters) {
        super();

        this.executionid = executionid;

        if (outputparameters != null) {
            this.outputparameters = outputparameters;
        } else {
            this.outputparameters = new HashMap<>();
        }
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

    public Map<String, String> getOutputparameters() {
        return outputparameters;
    }

    public void setOutputparameters(Map<String, String> outputparameters) {
        this.outputparameters = outputparameters;
    }

    @Override
    public String prettyJson() {
        return null;
    }
}
