package ru.simplgroup.controller;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import ru.simplgroup.data.*;
import ru.simplgroupp.persistence.cms.CmsImagesEntity;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер cms
 */
@Path("/cms")
public class CmsController {

    @Inject
    CmsService cmsService;


    @GET
    @Path("getMain")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getData(@Context HttpServletRequest request) {

        try {
            return new JsonResult<>(cmsService.getMain());
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult<>(e);
        }

    }

    @GET
    @Path("getMainQ")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getDataQ(@Context HttpServletRequest request) {

        try {
            return new JsonResult<>(cmsService.getMainFaq());
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult<>(e);
        }

    }

    @POST
    @Path("saveMain")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> saveMain(MainData mainData, @Context HttpServletRequest request) {
        try {
            cmsService.save(mainData);
            return new JsonResult<>(Boolean.TRUE);
        } catch (Exception e) {
            return new JsonResult<>(e);
        }

    }

    @GET
    @Path("getQuestionPagi")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getQuestionPagi(@QueryParam(value = "page") Integer page, @QueryParam(value = "pagesize") Integer pagesize,
                                                           @QueryParam(value = "codeCategory") Integer codeCategory, @Context HttpServletRequest request) {

        try {
            return new JsonResult<>(cmsService.getFaq(page, pagesize, codeCategory));
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult<>(e);
        }
    }

    @GET
    @Path("getQuestion")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getQuestion(@Context HttpServletRequest request) {

        try {
            return new JsonResult<>(cmsService.getFaq());
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult<>(e);
        }
    }

    @Path("uploadHeader")
    @POST
    @Consumes("multipart/form-data")
    @Produces({"application/json"})
    public JsonResult<Boolean> uploadHeader(MultipartFormDataInput formData) {

        try {
            cmsService.uploadHeader(formData);
            return new JsonResult<Boolean>(true);
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult<Boolean>(e);
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonResult<Boolean>(e);
        }
    }

    @POST
    @Path("saveQuestion")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> saveQuestion(FaqData questionPage, @Context HttpServletRequest request) {
        try {
            cmsService.saveFaq(questionPage);
            return new JsonResult<>(Boolean.TRUE);
        } catch (Exception e) {
            return new JsonResult<>(e);
        }

    }
    @POST
    @Path("saveCategory")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> saveCategory(List<CategoryQuestionW> categoryQuestions, @Context HttpServletRequest request) {
        try {
            cmsService.saveCategory(categoryQuestions);
            return new JsonResult<>(Boolean.TRUE);
        } catch (Exception e) {
            return new JsonResult<>(e);
        }

    }

    @POST
    @Path("saveQuestionData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Integer> saveQuestionData(QuestionDataW data, @Context HttpServletRequest request) throws IOException {


        return new JsonResult<>(cmsService.saveQuestion(data));
    }
    @GET
    @Path("getNewsNoImage")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getNewsNoImage(@QueryParam(value = "page") Integer page, @QueryParam(value = "pagesize") Integer pagesize, @Context HttpServletRequest request) throws IOException {

        return new JsonResult<>(cmsService.getNews(false, page, pagesize));

    }

    @POST
    @Path("saveNews")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> saveNews(NewsData newsData, @Context HttpServletRequest request) {
        try {
            cmsService.saveNews(newsData);
            return new JsonResult<>(Boolean.TRUE);
        } catch (Exception e) {
            return new JsonResult<>(e);
        }

    }

    @Path("removeQuestion")
    @POST
    @Produces({"application/json"})
    public JsonResult<Boolean> removeQuestion(Map<String, Object> map) {
        cmsService.removeQuestion((Integer) map.get("id"));
        return new JsonResult<>(true);
    }


    @Path("removeImage")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> removeImage(Map<String, Object> map) {
        cmsService.removeImage((Integer) map.get("id"));
        return new JsonResult<>(true);
    }

    @POST
    @Path("getQuestionById")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getQuestionById(@QueryParam(value = "id") Integer id, @Context HttpServletRequest request) throws IOException {

        return new JsonResult<>(cmsService.getQuestionById(id));

    }
    @Path("uploadNews")
    @POST
    @Consumes("multipart/form-data")
    @Produces({"application/json"})
    public JsonResult<Boolean> uploadNews(MultipartFormDataInput formData) {

        try {
            cmsService.uploadNews(formData);
            return new JsonResult<Boolean>(true);
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult<Boolean>(e);
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonResult<Boolean>(e);
        }
    }

    @Path("removeNews")
    @POST
    @Produces({"application/json"})
    public JsonResult<Boolean> removeNews(Map<String, Object> map) {
        cmsService.removeNews((Integer) map.get("id"));
        return new JsonResult<>(true);
    }

    @POST
    @Path("getNewsById")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getNewsById(@QueryParam(value = "id") Integer id, @Context HttpServletRequest request) throws IOException {

        return new JsonResult<>(cmsService.getNewsById(Integer.valueOf(id)));

    }

    @GET
    @Path("getNews")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getNews(@QueryParam(value = "page") Integer page, @QueryParam(value = "pagesize") Integer pagesize, @Context HttpServletRequest request) throws IOException {

        return new JsonResult<>(cmsService.getNews(true, page, pagesize));

    }
    @POST
    @Path("saveOneNews")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Integer> saveOneNews(NewsW news, @Context HttpServletRequest request) {
        try {
            return new JsonResult<>(cmsService.saveOneNews(news));
        } catch (Exception e) {
            return new JsonResult<>(e);
        }

    }
    @Path("removeNewsImg")
    @POST
    @Produces({"application/json"})
    public JsonResult<Boolean> removeNewsImg(Map<String, Object> map) {
        cmsService.removeNewsImg((Integer) map.get("id"));
        return new JsonResult<>(true);
    }
    @Path("removeNewsImgPreview")
    @POST
    @Produces({"application/json"})
    public JsonResult<Boolean> removeNewsImgPreview(Map<String, Object> map) {
        cmsService.removeNewsImgPreview((Integer) map.get("id"));
        return new JsonResult<>(true);
    }

    @POST
    @Path("saveNewsPreviewFoto")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<String> saveNewsPreviewFoto(LinkedHashMap<String, Object> param, @Context HttpServletRequest request) throws IOException {

        return new JsonResult<>(cmsService.savePreviewFoto((String)param.get("code"), (String)param.get("fileName"), (String) param.get("newsId")));

    }

    @GET
    @Path("getPoliticsNoImg")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getPoliticsNoImg(@Context HttpServletRequest request) throws IOException {

        return new JsonResult<>(cmsService.getPolitics(false));

    }
    @POST
    @Path("savePolitics")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> savePolitics(PoliticsData data, @Context HttpServletRequest request) {
        try {
            cmsService.savePolitics(data);
            return new JsonResult<>(Boolean.TRUE);
        } catch (Exception e) {
            return new JsonResult<>(e);
        }

    }
    @Path("removePolitics")
    @POST
    @Produces({"application/json"})
    public JsonResult<Boolean> removePolitics(Map<String, Object> map) {
        cmsService.removePolitics((Integer) map.get("id"));
        return new JsonResult<>(true);
    }

    @POST
    @Path("getPoliticsById")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getPoliticsById(@QueryParam(value = "id") Integer id, @Context HttpServletRequest request) throws IOException {

        return new JsonResult<>(cmsService.getPoliticsById(Integer.valueOf(id)));

    }
    @POST
    @Path("savePoliticsPoint")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Integer> savePoliticsPoint(PoliticsPointDataW news, @Context HttpServletRequest request) {
        try {
            return new JsonResult<>(cmsService.savePoliticsPoints(news));
        } catch (Exception e) {
            return new JsonResult<>(e);
        }

    }

    @Path("uploadPolitics")
    @POST
    @Consumes("multipart/form-data")
    @Produces({"application/json"})
    public JsonResult<Boolean> uploadPolitics(MultipartFormDataInput formData) {

        try {
            cmsService.uploadPolitics(formData);
            return new JsonResult<Boolean>(true);
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult<Boolean>(e);
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonResult<Boolean>(e);
        }
    }


    @GET
    @Path("getContacts")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<ContacntsData> getContacts(@Context HttpServletRequest request) throws IOException {

        return new JsonResult<>(cmsService.getContacts());

    }

    @POST
    @Path("saveContacts")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> saveContacts(ContacntsData contacsData, @Context HttpServletRequest request) {
        try {
            cmsService.saveContaacts(contacsData);
            return new JsonResult<>(Boolean.TRUE);
        } catch (Exception e) {
            return new JsonResult<>(e);
        }

    }

    @GET
    @Path("getAbout")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getAbout(@Context HttpServletRequest request) {

        try {
            return new JsonResult<>(cmsService.getAbout());
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResult<>(e);
        }

    }


    @POST
    @Path("saveAbout")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Boolean> saveAbout(AboutMeData aboutMeData, @Context HttpServletRequest request) {
        try {
            cmsService.saveAbout(aboutMeData);
            return new JsonResult<>(Boolean.TRUE);
        } catch (Exception e) {
            return new JsonResult<>(e);
        }

    }


    /**
     * скачиваем скан документа
     *
     * @param request
     * @return
     */
    @GET
    @Path("/downloadDoc")
    public Response downloadDoc(@QueryParam("fileName") String fileName, @Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        if (fileName == null || fileName.length() == 0) {
            return null;
        }
        CmsImagesEntity obj = cmsService.getPoliticsDoc(fileName);
        return obj == null ? null : Response
                .ok(obj.getFileData(), "text/pdf")
                .header("content-disposition", "attachment; filename*=UTF-8''" + UriBuilder.fromPath("{a}").build(fileName).toString())
                .build();

    }

}
