package ru.simplgroupp.rest.api.service;


import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.util.HTTPUtils;

import javax.enterprise.context.RequestScoped;
import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@RequestScoped
public class ReCaptchaService {
    private static final String secret = "6LfzEAsTAAAAAICaSZzaNVQsHbAc7uf8_8t0CDPz";


    public void verify(String gRecaptchaResponse) throws Exception {
        String url = "https://www.google.com/recaptcha/api/siteverify?" +
                "response=" + gRecaptchaResponse + "&secret=" + secret;


        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();


        byte[] response = null;
        InputStream instrm;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                instrm = conn.getInputStream();
            } else {
                instrm = conn.getErrorStream();
            }

            IOUtils.copy(instrm, bos);
            response = bos.toByteArray();
        } catch (IOException e) {
            throw new Exception("Ошибка url запроса", e);
        } finally {
            IOUtils.closeQuietly(bos);
        }


        if (response == null) {
            throw new Exception("Ответ запроса пуст");
        }

        String answer = new String(response);
        JSONParser parser = new JSONParser();
        JSONObject jsonAnswer;

        try {
            jsonAnswer = (JSONObject) parser.parse(answer);
        } catch (ParseException e) {
            throw new PeopleException("Не удалось разобрать ответ от ReCaptcha " + answer);
        }

        if (jsonAnswer == null) {
            throw new Exception("Распарсенный ответ пуст");
        }

        if (!Boolean.valueOf(jsonAnswer.get("success").toString())) {
            throw new Exception("Капча не пройдена " + jsonAnswer.get("error-codes").toString());
        }
    }
}
