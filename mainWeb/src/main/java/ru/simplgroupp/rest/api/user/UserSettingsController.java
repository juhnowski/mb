package ru.simplgroupp.rest.api.user;

import ru.simplgroupp.data.ErrorData;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.PeopleException;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.persistence.DocumentMediaEntity;
import ru.simplgroupp.rest.api.JsonResult;
import ru.simplgroupp.rest.api.data.AccountData;
import ru.simplgroupp.rest.api.data.PeopleMainData;
import ru.simplgroupp.rest.api.data.ReferenceData;
import ru.simplgroupp.rest.api.data.user.*;
import ru.simplgroupp.rest.api.service.UserService;
import ru.simplgroupp.rest.api.service.UserSettingsService;
import ru.simplgroupp.transfer.Users;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by PARFENOV on 18.05.2015.
 */
@Path("/usersettings")
public class UserSettingsController {
    private static Logger logger = Logger.getLogger(UserSettingsController.class.getName());
    public static final String FILE_UPLOAD_FIELD_NAME = "file";

    @EJB
    private UsersBeanLocal userBean;
    @Inject
    private UserSettingsService userSettingsService;
    @Inject
    private UserService userService;


    @POST
    @Path("/getUserName")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<String> getUserName(@Context HttpServletRequest request) {
        Users user = userService.getUser();
        return new JsonResult<>(userSettingsService.getUserName(user));
    }

    /**
     * Обязательный параметр token
     * Метод для отображения полных данные на главной страничке client/settings.shtml
     *
     * @return
     */
    @POST
    @Path("/getPeopleMain")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<PeopleMainData> getPeopleMain(@Context HttpServletRequest request) {
        Users user = userService.getUser();
        return new JsonResult<>(userSettingsService.getPeopleMain(user));

    }

    /**
     * Данный для ссылок на соц сети
     * Обязательный параметр token
     *
     * @return
     */
    @POST
    @Path("/socialConfig")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, String>> getConfig() {
        return new JsonResult<>(userSettingsService.getConfig());
    }

    @POST
    @Path("/getSocialData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getSocialData(@Context HttpServletRequest request) {
        Users user = userService.getUser();
        return new JsonResult<>(userSettingsService.getSocialData(user));
    }

    /**
     * отправить смс код Для внесения изменений владение данным аккаунтом
     */
    @POST
    @Path("/sendSmsCode")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<String> sendSmsCode(Map<String, String> map) {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.sendSmsCode(map.get("phone"), user));
        } catch (KassaException e) {
            return new JsonResult<>(e);
        }
    }

    @POST
    @Path("/verifySmsCode")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonResult<String> verifySmsCode(Map<String, String> map) {
        try {
            userService.getUser();
            return new JsonResult<>(userSettingsService.verifySmsCode(map.get("code")));
        } catch (PeopleException e) {
            return new JsonResult<>(e);
        }
    }

    @POST
    @Path("/sendEmailCode")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<String> sendEmailCode(Map<String, String> maps) {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.sendEmailCode(maps.get("email")));
        } catch (KassaException e) {
            return new JsonResult<>(e);
        }
    }

    @POST
    @Path("/verifyEmailCode")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonResult<String> verifyEmailCode(Map<String, String> map) {
        try {
            userService.getUser();
            return new JsonResult<>(userSettingsService.verifyEmailCode(map.get("code")));
        } catch (PeopleException e) {
            return new JsonResult<>(e);
        }
    }

    /**
     * Метод для сохранения данных client/editcontacts.shtml
     * Входные данные token, email, phone, phoneCode, phoneHash
     *
     * @param maps
     * @return
     */
    @POST
    @Path("/savePeopleContact")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<ErrorData>> savePeopleContact(Map<String, Object> maps, @Context HttpServletRequest request) {
        try {

            Users user = userService.getUser();

            return new JsonResult<>(userSettingsService.savePeopleContact(maps, user));
        } catch (KassaException e) {
            return new JsonResult<>(e);
        } catch (PeopleException e) {
            return new JsonResult<>(e);
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult<>(e);
        }
    }

    @POST
    @Path("/saveAnketa")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<ErrorData>> saveAnketa(Map<String, Object> maps, @Context HttpServletRequest request) {
        try {

            Users user = userService.getUser();

            return new JsonResult<>(userSettingsService.saveAnketaData(maps, user));
        } catch (PeopleException e) {
            return new JsonResult<>(e);
        }
    }

    /**
     * данные для формы Редактирования персональных данных
     * main/client/editpersonal.shtml
     * отдаем объект main - данные пользователя
     * справочники Пол, Занятость супруга, Семейное положение
     *
     * @param request
     * @return
     */
    @GET
    @Path("/getPersonal")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getPersonal(@Context HttpServletRequest request) {
        Users user = userService.getUser();

        return new JsonResult<>(userSettingsService.getPersonal(user));

    }

    /**
     * удаление учетки соц сети
     *
     * @param request
     * @return
     */
    @POST
    @Path("/makeArchive")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> makeArchive(Map<String, Integer> maps, @Context HttpServletRequest request) {
        Users user = userService.getUser();
        try {
            userSettingsService.makeArchive(maps.get("id"));
            return new JsonResult<>(Boolean.TRUE);
        } catch (PeopleException e) {
            return new JsonResult<>(e);
        }
    }

    /**
     * данные для формы ПЛАТЕЖНЫЕ ДАННЫЕ
     * main/client/listaccounts.shtml
     *
     * @param request
     * @return
     */
    @GET
    @Path("/getAccounts")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<AccountData>> getAccounts(@Context HttpServletRequest request) {
        Users user = userService.getUser();

        return new JsonResult<>(userSettingsService.getAccounts(user));

    }

    @GET
    @Path("/getAccountTypes")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<ReferenceData>> getAccountTypes(@Context HttpServletRequest request) {
        return new JsonResult<>(userSettingsService.getAccountTypes());

    }

    /**
     * Содание новых платежных данных
     *
     * @param request
     * @return
     */
    @POST
    @Path("/createAccount")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<ErrorData>> createAccount(AccountData data, @Context HttpServletRequest request) {
        Users user = userService.getUser();
        try {
            return new JsonResult<>(userSettingsService.createAccount(data, user));
        } catch (PeopleException e) {
            return new JsonResult<>(e);
        }
    }

    /**
     * вернем плтежные данные по id
     *
     * @param request
     * @return
     */
    @POST
    @Path("/getAccountById")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<AccountData> getAccountById(Map<String, Object> map, @Context HttpServletRequest request) {
        Users user = userService.getUser();
        String id = (String) map.get("accountId");
        return new JsonResult<>(userSettingsService.getAccountById(Integer.valueOf(id), user));
    }

    /**
     * изменение платежных данных
     *
     * @param request
     * @return
     */
    @POST
    @Path("/changeAccount")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<ErrorData>> changeAccount(AccountData data, @Context HttpServletRequest request) {
        Users user = userService.getUser();

        try {
            return new JsonResult<>(userSettingsService.changeAccount(data, user));
        } catch (PeopleException e) {
            return new JsonResult<>(e);
        }
    }

    /**
     * удаляем платежные данные по id
     *
     * @param request
     * @return
     */
    @POST
    @Path("/removeAccount")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> removeAccount(Map<String, Object> map, @Context HttpServletRequest request) {
        Users user = userService.getUser();
        Integer id = (Integer) map.get("accountId");
        try {
            userSettingsService.removeAccount(id);
            return new JsonResult<>(Boolean.TRUE);
        } catch (PeopleException e) {
            return new JsonResult<>(e);
        }
    }

    /**
     * удаляем скан документа
     *
     * @param request
     * @return
     */
    @POST
    @Path("/removeDocMedia")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> removeDocMedia(Map<String, Object> map, @Context HttpServletRequest request) {
        Users user = userService.getUser();
        Integer id = (Integer) map.get("id");
        userSettingsService.deleteDocumentMedia(id);
        return new JsonResult<>(Boolean.TRUE);

    }

    /**
     * скачиваем скан документа
     *
     * @param request
     * @return
     */
    @GET
    @Path("/downloadDocMedia")
    public Response downloadDocMedia(@QueryParam("id")int id,@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        Users user = userService.getUser();
//        Integer id = (Integer) map.get("id");
        DocumentMediaEntity media = userSettingsService.getDocumentMadia(id);
        return Response
                .ok(media.getMediadata(), media.getMimetype())
                .header("content-disposition","attachment; filename = " + media.getMediapath())
                .build();

    }

    @GET
    @Path("/formPersonalData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<FormPersonalData> getPersonalData() {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.getFormPersonalData(user));
        } catch (Exception e) {
            return new JsonResult<>(e);
        }
    }

    @POST
    @Path("/formPersonalData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<String> savePersonalData(FormPersonalData personalData) {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.saveFormPersonalData(personalData, user));
        } catch (Exception e) {
            return new JsonResult<>(e);
        }
    }

    @GET
    @Path("/addressData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<FullAddressData> getAddressData() {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.getFullAddressData(user));
        } catch (Exception e) {
            return new JsonResult<>(e);
        }
    }

    @POST
    @Path("/addressData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<String> saveAddressData(FullAddressData fullAddressData) {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.saveFullAddressData(fullAddressData, user));
        } catch (Exception e) {
            return new JsonResult<>(e);
        }
    }

    @GET
    @Path("/employmentData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<EmploymentData> getEmploymentData() {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.getEmploymentData(user));
        } catch (Exception e) {
            return new JsonResult<>(e);
        }
    }

    @POST
    @Path("/employmentData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<String> saveEmploymentData(EmploymentData employmentData) {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.saveEmploymentData(employmentData, user));
        } catch (Exception e) {
            return new JsonResult<>(e);
        }
    }

    @GET
    @Path("/profileData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<ProfileData> getProfileData() {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.getProfileData(user));
        } catch (Exception e) {
            return new JsonResult<>(e);
        }
    }

    @POST
    @Path("/profileData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<String> saveProfileData(ProfileData profileData, @Context HttpServletRequest request) {
        try {
            Users user = userService.getUser();
            return new JsonResult<>(userSettingsService.saveProfileData(profileData, user, request));
        } catch (Exception e) {
            logger.warning("исключение saveProfileData " + e);
            return new JsonResult<>(e);
        }
    }
}
