package ru.simplgroupp.data;

/**
 * Параметр для динамического метода
 */
public class MethodParamData {
    private String key;
    private String name;
    private String description;
    private ParamType type;


    public MethodParamData() {
    }

    public MethodParamData(String key, String name, String description, ParamType type) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type.toString();
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public String prettyJson() {
        return "{\n" +
                " \"key\": \"" + getKey() + "\",\n" +
                " \"name\": \"" + getName() + "\",\n" +
                " \"description\": \"" + getDescription() + "\",\n" +
                " \"type\": \"" + getType().toString() + "\"\n" +
                "}\n";
    }

    public enum ParamType {
        STRING("string"),
        INTEGER("int"),
        BOOLEAN("bool"),
        DECIMAL("decimal"),
        FLOAT("float"),
        DOUBLE("double"),
        DATETIME("datetime"),
        LIST("list");

        private final String value;


        ParamType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
