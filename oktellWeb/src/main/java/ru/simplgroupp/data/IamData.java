package ru.simplgroupp.data;

import org.json.simple.JSONObject;

public class IamData extends RequestParametersData {
    private String type;
    private String name;
    private Double version;
    private String build;


    public IamData(JSONObject json) {
        super(json);

        if (json.containsKey("type")) {
            this.type = json.get("type").toString();
        }

        if (json.containsKey("name")) {
            this.name = json.get("name").toString();
        }

        if (json.containsKey("version")) {
            this.version = new Double(json.get("version").toString());
        }

        if (json.containsKey("build")) {
            this.build = json.get("build").toString();
        }
    }

    public IamData() {
        super();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    @Override
    public String prettyJson() {
        return "{\n" +
               " \"qid\": \"" + getQid() + "\",\n" +
               " \"type\": \"" + getType() + "\",\n" +
               " \"name\": \"" + getName() + "\",\n" +
               " \"version\": " + getVersion() + ",\n" +
               " \"build\": \"" + getBuild() + "\"\n" +
               "}\n";
    }
}
