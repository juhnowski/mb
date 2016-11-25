package ru.simplgroupp.rest.api.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.persistence.PeopleMainEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Users;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class SocialService {
    private static final Logger logger = LoggerFactory.getLogger(SocialService.class);

    @EJB
    private KassaBeanLocal kassaBean;

    @EJB
    private PeopleBeanLocal peopleBean;


    public String fb(String code) {
        String userId = null;

        Map<String, String> conf = kassaBean.getSocNetworkApp();
        if (code != null) {
            try {
                HttpClient client = new DefaultHttpClient();
                String st = "https://graph.facebook.com/oauth/access_token?client_id=" + conf.get("fb") + "&client_secret=" + conf.get("fbsecret") + "&code=" + code;
                st += "&redirect_uri=" + conf.get("callBackUrl") + "fb";
                logger.info(st);
                HttpGet request = new HttpGet(st);
                HttpResponse response = client.execute(request);

                HttpEntity entity = response.getEntity();
                String entityContents = EntityUtils.toString(entity);
                logger.info(entityContents);

                String access_token = null;
                String[] params = entityContents.split("&");
                for (int i = 0; i < params.length; i++) {
                    String[] vals = params[i].split("=");
                    if (vals.length == 2) {
                        if (vals[0].equals("access_token")) {
                            access_token = vals[1];
                        }
                    }
                }
                if (access_token != null) {
                    String st1 = "https://graph.facebook.com/me?access_token=" + access_token;
                    logger.info(st1);
                    HttpGet request1 = new HttpGet(st1);
                    HttpResponse response1 = client.execute(request1);

                    HttpEntity entity1 = response1.getEntity();
                    String entityContents1 = EntityUtils.toString(entity1);
                    logger.info(entityContents1);

                    Gson gson = new Gson();
                    HashMap<String, Object> data = gson.fromJson(entityContents1, HashMap.class);
                    userId = data.get("id").toString();
                    logger.info("FB RESULT = " + userId);
                } else {
                    logger.info("error access_token");
                }
            } catch (Exception ex) {
                logger.info("fb token exception", ex);
            }
        }
        logger.info("SOCIAL FB code=" + code);


        return userId;
    }

    public String vk(String accessToken, String userId) {
        logger.info("SOCIAL VK token = " + accessToken);
        logger.info("SOCIAL VK user = " + userId);


        return userId;
    }

    public String ml(String accessToken, String mailruVid) {
        String socialId = null;

        Map<String, String> conf = kassaBean.getSocNetworkApp();

        logger.info("SOCIAL ML access_token = " + accessToken);
        logger.info("SOCIAL ML x_mailru_vid = " + mailruVid);
        if (accessToken != null) {
            try {
                HttpClient client = new DefaultHttpClient();
                String query = "app_id=" + conf.get("mm") + "&method=users.getInfo&secure=1&session_key=" + accessToken;// + "&uids=" + x_mailru_vid;
                String sign = Convertor.toDigest("app_id=" + conf.get("mm") + "method=users.getInfosecure=1session_key=" + accessToken + conf.get("mmsecret"));
                String st = "http://www.appsmail.ru/platform/api?" + query + "&sig=" + sign;
                logger.info("SOCIAL ML request = " + st);
                HttpGet request = new HttpGet(st);
                HttpResponse response = client.execute(request);

                HttpEntity entity = response.getEntity();
                String entityContents = EntityUtils.toString(entity);
                logger.info(entityContents);

                Gson gson = new Gson();
                ArrayList<LinkedTreeMap<String, Object>> data = gson.fromJson(entityContents, ArrayList.class);
                socialId = data.get(0).get("link").toString();
                logger.info("SOCIAL ML RESULT = " + socialId);
            } catch (Exception ex) {
                logger.warn("SOCIAL mailru social exception", ex);
            }
        }

        return socialId;
    }

    public String ok(String error, String code) {
        String socialId = null;
        Map<String, String> conf = kassaBean.getSocNetworkApp();
        logger.info("SOCIAL OK error=" + error);
        logger.info("SOCIAL OK code=" + code);

        if (code != null) {
            try {
                HttpClient client = new DefaultHttpClient();
                String st = "http://api.odnoklassniki.ru/oauth/token.do?client_id=" + conf.get("odk") + "&client_secret=" + conf.get("odksecret") + "&code=" + code;
                st += "&redirect_uri=" + conf.get("callBackUrl") + "ok&grant_type=authorization_code";
                logger.info(st);
                HttpPost request = new HttpPost(st);
                HttpResponse response = client.execute(request);

                HttpEntity entity = response.getEntity();
                String entityContents = EntityUtils.toString(entity);
                logger.info(entityContents);

                Gson gson = new Gson();
                HashMap<String, Object> tdata = gson.fromJson(entityContents, HashMap.class);
                logger.info("OK access_token = " + tdata.get("access_token"));
                String access_token = (String) tdata.get("access_token");

                st = "http://api.odnoklassniki.ru/fb.do?access_token=" + access_token + "&method=users.getCurrentUser&application_key=" + conf.get("odkpublic");
                st += "&sig=" + StaticFuncs.md5("application_key=" + conf.get("odkpublic") + "method=users.getCurrentUser" + StaticFuncs.md5(access_token + conf.get("odksecret")).toLowerCase()).toLowerCase();
                logger.info(st);
                HttpGet request1 = new HttpGet(st);
                response = client.execute(request1);

                entity = response.getEntity();
                entityContents = EntityUtils.toString(entity);
                logger.info(entityContents);

                HashMap<String, Object> data = gson.fromJson(entityContents, HashMap.class);
                socialId = data.get("uid").toString();
                logger.info("OK RESULT = " + socialId);
            } catch (Exception ex) {
                logger.warn("ok exception", ex);
            }
        }

        return socialId;
    }

    public void saveSocialId(Users user, String socialId, Integer type) {
        PeopleMainEntity people = user.getPeopleMain().getEntity();

        try {
            peopleBean.setPeopleContactClient(people, type, socialId, false);
        } catch (Exception e) {
            logger.warn("save people contact exception", e);
        }
    }
}
