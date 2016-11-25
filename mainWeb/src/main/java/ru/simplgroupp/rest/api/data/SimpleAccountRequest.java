package ru.simplgroupp.rest.api.data;

import java.io.Serializable;

/**
 * Simple account request
 */
public class SimpleAccountRequest implements Serializable {

    private static final long serialVersionUID = 6378697215341020026L;

    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}