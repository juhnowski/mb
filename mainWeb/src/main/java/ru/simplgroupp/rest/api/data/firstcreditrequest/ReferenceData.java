package ru.simplgroupp.rest.api.data.firstcreditrequest;

import ru.simplgroupp.transfer.Reference;

import java.io.Serializable;

public class ReferenceData implements Serializable {
    private String name;
    private String code;

    public ReferenceData(Reference ref) {
        this.name = ref.getName();
        this.code = ref.getCodeInteger() != null ?  Integer.toString(ref.getCodeInteger()) : (
                ref.getCode() != null ? ref.getCode() : null
                );
    }

    public ReferenceData() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
