package ru.simplgroupp.rest.api.data;

import ru.simplgroupp.transfer.Reference;

/**
 *
 * объект для передачи справочников на web
 */
public class ReferenceData {
    private String id;
    private String value;


    public ReferenceData() {
    }

    public ReferenceData(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public ReferenceData(Reference reference) {
        this.id = reference.getCodeInteger().toString();
        this.value = reference.getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
