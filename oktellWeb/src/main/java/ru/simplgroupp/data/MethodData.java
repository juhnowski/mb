package ru.simplgroupp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Данные динамического метода
 */
public class MethodData {
    private String key;
    private String name;
    private String description;
    private String connectiontype;
    private String url;
    private Boolean defineuser;
    private Boolean allowcancel;
    private List<MethodParamData> inputparams;
    private List<MethodParamData> outputparams;


    public MethodData() {
    }

    public MethodData(String key, String name, String description, String connectionType, String url, Boolean defineUser, Boolean allowCancel,
                      List<MethodParamData> inputParams, List<MethodParamData> outputParams) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.connectiontype = connectionType;
        this.url = url;
        this.defineuser = defineUser;
        this.allowcancel = allowCancel;

        if (inputParams == null) {
            this.inputparams = new ArrayList<>();
        } else {
            this.inputparams = inputParams;
        }

        if (outputParams == null) {
            this.outputparams = new ArrayList<>();
        } else {
            this.outputparams = outputParams;
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectiontype() {
        return connectiontype;
    }

    public void setConnectiontype(String connectiontype) {
        this.connectiontype = connectiontype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<MethodParamData> getInputparams() {
        return inputparams;
    }

    public void setInputparams(List<MethodParamData> inputparams) {
        this.inputparams = inputparams;
    }

    public List<MethodParamData> getOutputparams() {
        return outputparams;
    }

    public void setOutputparams(List<MethodParamData> outputparams) {
        this.outputparams = outputparams;
    }

    public Boolean getDefineuser() {
        return defineuser;
    }

    public void setDefineuser(Boolean defineuser) {
        this.defineuser = defineuser;
    }

    public Boolean getAllowcancel() {
        return allowcancel;
    }

    public void setAllowcancel(Boolean allowcancel) {
        this.allowcancel = allowcancel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String prettyJson() {
        StringBuilder builder = new StringBuilder(
                "{\n" +
                        " \"key\": \"" + getKey() + "\",\n" +
                        " \"name\": \"" + getName() + "\",\n" +
                        " \"description\": \"" + getName() + "\",\n" +
                        " \"type\": \"" + getKey() + "\"\n" +
                        "}\n");

        return builder.toString();
    }
}
