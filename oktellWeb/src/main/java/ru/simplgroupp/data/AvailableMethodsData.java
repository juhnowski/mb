package ru.simplgroupp.data;

import java.util.List;

/**
 * Список методов который поддерживаем
 */
public class AvailableMethodsData extends RequestParametersData {
    private List<MethodData> methods;


    public AvailableMethodsData(List<MethodData> methods) {
        super();
        this.methods = methods;
    }

    public List<MethodData> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodData> methods) {
        this.methods = methods;
    }

    @Override
    public String prettyJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        String separator = "";
        for (MethodData data : methods) {
            builder.append(separator).append("\n").append(data.prettyJson());
            separator = ",";
        }

        builder.append("]");

        return "{\n" +
                " \"qid\": \"" + getQid() + "\",\n" +
                " \"methods\": " + builder.toString() + "\n" +
                "}\n";
    }
}
