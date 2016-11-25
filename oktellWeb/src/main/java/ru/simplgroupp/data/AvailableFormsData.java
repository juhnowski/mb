package ru.simplgroupp.data;

import java.util.List;

public class AvailableFormsData extends RequestParametersData {
    private List<MethodData> forms;


    public AvailableFormsData(List<MethodData> forms) {
        super();
        this.forms = forms;
    }

    public List<MethodData> getForms() {
        return forms;
    }

    public void setForms(List<MethodData> forms) {
        this.forms = forms;
    }

    @Override
    public String prettyJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        String separator = "";
        for (MethodData data : forms) {
            builder.append(separator).append("\n").append(data.prettyJson());
            separator = "],";
        }

        return builder.toString();
    }
}
