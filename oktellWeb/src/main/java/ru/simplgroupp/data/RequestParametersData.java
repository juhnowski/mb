package ru.simplgroupp.data;

import org.json.simple.JSONObject;

import java.util.UUID;

/**
 * Абстрактный класс от которого наследуются все классы для параметров запроса
 * Содержит qid, который должен содержаться в каждом запросе\ответе
 * При отправке ответа qid генерируется случайный
 */
public abstract class RequestParametersData {
    private String qid;


    public RequestParametersData(JSONObject json) {
        this.qid = json.get("qid").toString();
    }

    public RequestParametersData() {
        this.qid = UUID.randomUUID().toString();
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public abstract String prettyJson();
}
