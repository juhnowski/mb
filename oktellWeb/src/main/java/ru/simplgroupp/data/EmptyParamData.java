package ru.simplgroupp.data;

import org.json.simple.JSONObject;

/**
 * Когда у нас нет никаких параметров кроме qid
 */
public class EmptyParamData extends RequestParametersData {
    public EmptyParamData(JSONObject json) {
        super(json);
    }

    @Override
    public String prettyJson() {
        return null;
    }
}
