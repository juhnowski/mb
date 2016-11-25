package ru.simplgroup.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.util.Base64;
import ru.simplgroup.data.*;
import ru.simplgroupp.dao.interfaces.cms.CmsServiceDAO;
import ru.simplgroupp.persistence.cms.*;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestScoped
public class CmsService {

    @EJB
    private CmsServiceDAO cmsServiceDAO;


    public Map<String, Object> getMain() throws IOException {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.MAIN.name(), DefaultData.PROJECT_NAME);

        //если null значит заполняем дефолтными данными и отправляем в бд
        if (page == null) {
            page = new CmsPageEntity();

            return initMainData(page);
        } else {
            MainData mainData = null;
            try {
                Gson gson = new Gson();
                mainData = gson.fromJson(page.getBody(), MainData.class);
            } catch (JsonSyntaxException e) {
                return initMainData(page);
            }

            Map<String, Object> data = new HashMap<>();
            List<CmsImagesEntity> image = cmsServiceDAO.getCmsImageByPageId(page.getId());
            if (image.size() > 0) {
                List<ImageCms> imageCmses = toListImageCms(image);
                data.put("images", imageCmses);

            }
            data.put("main", mainData);

            return data;
        }
    }


    public Map<String, Object> getMainFaq() throws IOException {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.MAIN.name(), DefaultData.PROJECT_NAME);

        //если null значит заполняем дефолтными данными и отправляем в бд
        if (page == null) {
            page = new CmsPageEntity();

            return initMainData(page);
        } else {
            MainData mainData = null;
            try {
                Gson gson = new Gson();
                mainData = gson.fromJson(page.getBody(), MainData.class);
            } catch (JsonSyntaxException e) {
                return initMainData(page);
            }

            Map<String, Object> data = new HashMap<>();
            List<CmsImagesEntity> image = cmsServiceDAO.getCmsImageByPageId(page.getId());
            if (image.size() > 0) {
                List<ImageCms> imageCmses = toListImageCms(image);
                data.put("images", imageCmses);

            }
            data.put("main", mainData);
            List<CmsQuestionGroupEntity> cmsQuestionGroupEntities = cmsServiceDAO.getCmsQuestionGroupEntityAll(DefaultData.PROJECT_NAME);
            FaqData qp = (FaqData) getFaq().get("main");
            List<LinkData> questions = new ArrayList<>();
            if (cmsQuestionGroupEntities != null) {
                for (CmsQuestionGroupEntity cmsQuestionGroupEntity : cmsQuestionGroupEntities) {
                    if (cmsQuestionGroupEntity.getOnMainShow() != null &&
                            cmsQuestionGroupEntity.getOnMainShow() == 1) {
                        questions.add(new LinkData(cmsQuestionGroupEntity.getName(),
                                cmsQuestionGroupEntity.getId().toString(), true));
                    }
                    List<CmsQuestionsEntity> сmsQuestionsEntities = cmsServiceDAO.getCmsQuestionByGroupId(cmsQuestionGroupEntity.getId());
                    for (CmsQuestionsEntity cmsQuestionsEntity : сmsQuestionsEntities) {
                        if (cmsQuestionsEntity.getOnMainShow() != null &&
                                cmsQuestionsEntity.getOnMainShow() == 1) {
                            questions.add(new LinkData(cmsQuestionsEntity.getQuestion(),
                                    cmsQuestionsEntity.getId().toString(), true));
                        }
                    }
                }
            }
            data.put("questions", questions);
            return data;
        }
    }


    public Map<String, Object> getFaq() throws IOException {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.FAQ.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();
        //если null значит заполняем дефолтными данными и отправляем в бд
        if (page == null) {
            page = new CmsPageEntity();
            initFaqData(page);

            return getFaq();
        } else {
            FaqData nd = gson.fromJson(page.getBody(), FaqData.class);
            Map<String, Object> map = new HashMap<>();
            List<CmsImagesEntity> i = cmsServiceDAO.getCmsImageByPageId(page.getId());
            List<ImageCms> imageCmses = toListImageCms(i);
            map.put("image", imageCmses);
            List<CategoryQuestionW> categoryQuestions = new ArrayList<>();
            List<CmsQuestionGroupEntity> cmsQuestionGroupEntities = cmsServiceDAO.getCmsQuestionGroupEntityAll(DefaultData.PROJECT_NAME);
            List<QuestionDataW> newsList = new ArrayList<>();
            for (CmsQuestionGroupEntity cmsQuestionGroupEntity : cmsQuestionGroupEntities) {
                categoryQuestions.add(transportCmsQuestionGroupToCategory(cmsQuestionGroupEntity));
                List<CmsQuestionsEntity> cmsQuestionsEntities = cmsServiceDAO.getCmsQuestionByGroupId(cmsQuestionGroupEntity.getId());
                for (CmsQuestionsEntity cmsQuestionsEntity : cmsQuestionsEntities) {
                        newsList.add(transportCmsQuestionToQuestion(cmsQuestionsEntity));
                }
            }
            nd.setCategorys(categoryQuestions);
            nd.setQuestions(newsList);

            map.put("main", nd);

            return map;
        }
    }


    private CategoryQuestionW transportCmsQuestionGroupToCategory(CmsQuestionGroupEntity cmsQuestionGroupEntity) {
        CategoryQuestionW categoryQuestionW = new CategoryQuestionW();
        categoryQuestionW.setCode(cmsQuestionGroupEntity.getId());
        categoryQuestionW.setIsFooter(cmsQuestionGroupEntity.getOnFooterShow() != null ? cmsQuestionGroupEntity.getOnFooterShow() == 1 ? true : false :
                false);
        categoryQuestionW.setName(cmsQuestionGroupEntity.getName());

        return categoryQuestionW;
    }


    private QuestionDataW transportCmsQuestionToQuestion(CmsQuestionsEntity cmsQuestionsEntity) {
        QuestionDataW questionDataW = new QuestionDataW();
        questionDataW.setName(cmsQuestionsEntity.getQuestion());
        questionDataW.setCode(cmsQuestionsEntity.getId());
        questionDataW.setIsFooter(cmsQuestionsEntity.getOnFooterShow() != null ? cmsQuestionsEntity.getOnFooterShow() == 1 ? true : false : false);
        questionDataW.setAnswer(cmsQuestionsEntity.getAnswer());
        if (cmsQuestionsEntity.getGroup() != null) {
            questionDataW.setIdCategory(cmsQuestionsEntity.getGroup().getId());
        }
        questionDataW.setIsMain(cmsQuestionsEntity.getOnMainShow() != null ? cmsQuestionsEntity.getOnMainShow() == 1 ? true : false : false);

        return questionDataW;
    }


    public Map<String, Object> getFaq(Integer pages, Integer pagesize, Integer codeCategory) throws IOException {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.FAQ.name(), DefaultData.PROJECT_NAME);

        //если null значит заполняем дефолтными данными и отправляем в бд
        if (page == null) {
            page = new CmsPageEntity();

            return initFaqData(page);
        } else {
            FaqData faqData = null;
            try {
                Gson gson = new Gson();
                faqData = gson.fromJson(page.getBody(), FaqData.class);
            } catch (JsonSyntaxException e) {
                return initMainData(page);
            }

            Map<String, Object> data = new HashMap<>();
            List<CmsImagesEntity> image = cmsServiceDAO.getCmsImageByPageId(page.getId());
            if (image.size() > 0) {
                List<ImageCms> imageCmses = toListImageCms(image);
                data.put("images", imageCmses);

            }
            List<CategoryQuestionW> categoryQuestions = new ArrayList<>();
            List<CmsQuestionGroupEntity> cmsQuestionGroupEntities = cmsServiceDAO.getCmsQuestionGroupEntityAll(DefaultData.PROJECT_NAME);
            for (CmsQuestionGroupEntity cmsQuestionGroupEntity : cmsQuestionGroupEntities) {
                categoryQuestions.add(transportCmsQuestionGroupToCategory(cmsQuestionGroupEntity));
            }


            data.put("total", cmsServiceDAO.countCmsQuestions(DefaultData.PROJECT_NAME));
            if (pages != null) {
                int i = (pages - 1) * pagesize;
                List<QuestionDataW> newsList = new ArrayList<>();
                List<CmsQuestionsEntity> cmsQuestionsEntities = cmsServiceDAO.getCmsQuestionPagi(DefaultData.PROJECT_NAME, pagesize, i);
                for (CmsQuestionsEntity cmsQuestionsEntity : cmsQuestionsEntities) {
                    if((codeCategory != -1 && cmsQuestionsEntity.getGroup().getId().equals(codeCategory)) || codeCategory == -1) {
                        newsList.add(transportCmsQuestionToQuestion(cmsQuestionsEntity));
                    }
                }
                data.put("question", newsList);
            }
            data.put("category", categoryQuestions);
            faqData.setCategorys(null);
            faqData.setQuestions(null);
            data.put("main", faqData);
            return data;
        }
    }


    public void saveFaq(FaqData faqData) {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.FAQ.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();

        mainField(page, faqData);

        FaqData old = gson.fromJson(page.getBody(), FaqData.class);
        faqData.setCategorys(old.getCategorys());
        faqData.setQuestions(old.getQuestions());
        page.setBody(gson.toJson(faqData));

        cmsServiceDAO.save(page);
    }


    public void removeQuestion(Integer id) {
        CmsQuestionsEntity question = (CmsQuestionsEntity) cmsServiceDAO.findById(CmsQuestionsEntity.class, id);
        cmsServiceDAO.remove(question);
    }


    private CmsQuestionGroupEntity transportCategoryToCmsQuestionGroup(CategoryQuestionW categoryQuestionW) {
        CmsQuestionGroupEntity cmsQuestionGroupEntity = new CmsQuestionGroupEntity();
        cmsQuestionGroupEntity.setId(categoryQuestionW.getCode());
        cmsQuestionGroupEntity.setOnFooterShow(categoryQuestionW.getIsFooter() == null ? 0 : categoryQuestionW.getIsFooter() ? 1 : 0);
        cmsQuestionGroupEntity.setName(categoryQuestionW.getName());
        cmsQuestionGroupEntity.setProjectName(DefaultData.PROJECT_NAME);
        cmsQuestionGroupEntity.setDateChange(new Date());

        return cmsQuestionGroupEntity;
    }


    public void saveCategory(List<CategoryQuestionW> categoryQuestions) {
        List<CmsQuestionGroupEntity> cmsQuestionGroupEntities = cmsServiceDAO.getCmsQuestionGroupEntityAll(DefaultData.PROJECT_NAME);
        Set<Integer> integers = new HashSet<>();
        if (categoryQuestions != null) {
            for (CategoryQuestionW categoryQuestion : categoryQuestions) {
                CmsQuestionGroupEntity cmsQuestionGroupEntity = transportCategoryToCmsQuestionGroup(categoryQuestion);
                if (cmsQuestionGroupEntity.getId() == null) {
                    cmsQuestionGroupEntity.setCreateDate(new Date());
                    cmsServiceDAO.create(cmsQuestionGroupEntity);
                } else {
                    integers.add(cmsQuestionGroupEntity.getId());
                    cmsServiceDAO.edit(cmsQuestionGroupEntity);
                }
            }
        }
        for (CmsQuestionGroupEntity cmsQuestionGroupEntity : cmsQuestionGroupEntities) {
            if (!integers.contains(cmsQuestionGroupEntity.getId())) {

                List<CmsQuestionsEntity> cmsQuestionsEntities = cmsServiceDAO.getCmsQuestionByGroupId(cmsQuestionGroupEntity.getId());
                for(CmsQuestionsEntity cmsQuestionsEntity : cmsQuestionsEntities){
                    cmsServiceDAO.remove(cmsQuestionsEntity);
                }

                cmsServiceDAO.remove(cmsQuestionGroupEntity);
            }
        }
    }


    private List<ImageCms> toListImageCms(List<CmsImagesEntity> image) {
        List<ImageCms> imageCmses = new ArrayList<>();
        Set<String> fileNames = new HashSet<>();
        for (CmsImagesEntity imagesEntity : image) {
            if (!fileNames.contains(imagesEntity.getFileName())) {
                ImageCms imageCms = new ImageCms(imagesEntity.getId(), imagesEntity.getFileName(), org.jboss.resteasy.util.Base64.encodeBytes(imagesEntity.getFileData()));
                imageCmses.add(imageCms);
                fileNames.add(imageCms.getFileName());
            }
        }
        return imageCmses;
    }


    public Map<String, Object> getQuestionById(Integer id) throws IOException {
        List<CmsQuestionGroupEntity> cmsQuestionGroupEntities = cmsServiceDAO.getCmsQuestionGroupEntityAll(DefaultData.PROJECT_NAME);
        List<CategoryQuestionW> categoryQuestionWs = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        for (CmsQuestionGroupEntity cmsQuestionGroupEntity : cmsQuestionGroupEntities) {
            categoryQuestionWs.add(transportCmsQuestionGroupToCategory(cmsQuestionGroupEntity));
        }
        CmsQuestionsEntity cmsQuestionsEntity = new CmsQuestionsEntity();
        if (id != null) {
            cmsQuestionsEntity = (CmsQuestionsEntity) cmsServiceDAO.findById(CmsQuestionsEntity.class, id);
        }
        QuestionDataW questionDataW = transportCmsQuestionToQuestion(cmsQuestionsEntity);

        map.put("main", questionDataW);

        map.put("category", categoryQuestionWs);
        return map;
    }


    private CmsQuestionsEntity transportQuestionToCmsQuestion(QuestionDataW questionDataW) {
        CmsQuestionsEntity cmsQuestionsEntity = new CmsQuestionsEntity();
        cmsQuestionsEntity.setQuestion(questionDataW.getName());
        cmsQuestionsEntity.setId(questionDataW.getCode());
        cmsQuestionsEntity.setOnFooterShow(questionDataW.getIsFooter() == null ? 0 : questionDataW.getIsFooter() ? 1 : 0);
        cmsQuestionsEntity.setAnswer(questionDataW.getAnswer());
        if (questionDataW.getIdCategory() != null) {
            cmsQuestionsEntity.setGroup((CmsQuestionGroupEntity) cmsServiceDAO.findById(CmsQuestionGroupEntity.class, questionDataW.getIdCategory()));
        }
        cmsQuestionsEntity.setOnMainShow(questionDataW.getIsMain() == null ? 0 : questionDataW.getIsMain() ? 1 : 0);
        cmsQuestionsEntity.setDateChange(new Date());
        return cmsQuestionsEntity;
    }


    public Integer saveQuestion(QuestionDataW data) {
        CmsQuestionsEntity cmsQuestionsEntity = transportQuestionToCmsQuestion(data);
        if (cmsQuestionsEntity.getId() == null) {
            cmsQuestionsEntity.setCreateDate(new Date());
            cmsServiceDAO.create(cmsQuestionsEntity);
        } else {
            cmsServiceDAO.edit(cmsQuestionsEntity);
        }

        return cmsQuestionsEntity.getId();
    }


    private Map<String, Object> initMainData(CmsPageEntity page) throws IOException {
        Gson gson = new Gson();
        MainData mainData = new MainData();
        mainData.setHowThisWork(DefaultData.howWork);
        mainData.setYesOntime(DefaultData.yesOntime);

        page.setType(CMS_PAGES_TYPE.MAIN.name());
        Map<String, Object> data = new HashMap<>();
        List<CmsImagesEntity> image = cmsServiceDAO.getCmsImageByPageId(page.getId());
        if (image == null || image.size() == 0) {
            List<CmsImagesEntity> cmsImagesEntities = new ArrayList<>();
            CmsImagesEntity imagesEntity = imageDefault("/cms/header.jpg", "header.jpg");
            mainData.setImgUp(imagesEntity.getFileName());
            imagesEntity.setCmsPage(page);
            cmsImagesEntities.add(imagesEntity);
            CmsImagesEntity imagesEntity2 = imageDefault("/cms/staistic-block.jpg", "staistic-block.jpg");
            mainData.setImgDown(imagesEntity2.getFileName());
            imagesEntity2.setCmsPage(page);
            cmsImagesEntities.add(imagesEntity2);
            page.setImages(cmsImagesEntities);

            List<ImageCms> imageCmses = toListImageCms(cmsImagesEntities);
            data.put("images", imageCmses);
        }

        page.setBody(gson.toJson(mainData));
        page.setProjectName(DefaultData.PROJECT_NAME);
        cmsServiceDAO.save(page);
        data.put("main", mainData);
        return data;
    }


    private Map<String, Object> initFaqData(CmsPageEntity page) throws IOException {
        Gson gson = new Gson();
        FaqData faqData = new FaqData();
        faqData = DefaultData.FAQ_DATA;

        Map<String, Object> data = new HashMap<>();
        if (page.getId() != null) {
            List<CmsImagesEntity> image = cmsServiceDAO.getCmsImageByPageId(page.getId());
            if (image == null || image.size() == 0) {
                List<CmsImagesEntity> cmsImagesEntities = new ArrayList<>();
                CmsImagesEntity imagesEntity = imageDefault("/cms/faq.jpg", "faq.jpg");
                faqData.setFileName(imagesEntity.getFileName());
                imagesEntity.setCmsPage(page);
                cmsImagesEntities.add(imagesEntity);
                page.setImages(cmsImagesEntities);

                List<ImageCms> imageCmses = toListImageCms(cmsImagesEntities);
                data.put("images", imageCmses);
            }
        }

        page.setType(CMS_PAGES_TYPE.FAQ.name());
        page.setProjectName(DefaultData.PROJECT_NAME);
        page.setBody(gson.toJson(faqData));
        cmsServiceDAO.save(page);
        data.put("main", faqData);
        return data;
    }


    private CmsImagesEntity imageDefault(String path, String name) throws IOException {
        //из ресурсов тащим картинки поумолчанию
        InputStream is = getClass().getResourceAsStream(path);

        byte[] bytes = IOUtils.toByteArray(is);

        CmsImagesEntity imagesEntity = new CmsImagesEntity();
        imagesEntity.setFileData(bytes);
        imagesEntity.setFileName(name);
        return imagesEntity;
    }


    private void mainField(CmsPageEntity pageEntity, AbstractCmsData data) {
        pageEntity.setTitle(data.getTitle());
        pageEntity.setDescription(data.getDescription());
        pageEntity.setKeywords(data.getKeywords());
    }


    public void save(MainData mainData) {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.MAIN.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();
        mainField(page, mainData);
        page.setBody(gson.toJson(mainData));

        cmsServiceDAO.save(page);
    }


    public void uploadHeader(MultipartFormDataInput formData) throws Exception {
        bodyUpload(formData, CMS_PAGES_TYPE.MAIN);
    }


    private Integer bodyUpload(MultipartFormDataInput formData, CMS_PAGES_TYPE type) throws Exception {
        Map<String, List<InputPart>> map = formData.getFormDataMap();

        for (Map.Entry<String, List<InputPart>> entry : map.entrySet()) {
            MultivaluedMap<String, String> headers = entry.getValue().get(0).getHeaders();
            /**
             * если элемент имеет тип application/octet-stream - это
             * прикрепленный файл
             */
            if (headers.get("Content-Type") != null && (headers.get("Content-Type").get(0).indexOf("image") >= 0
                    || headers.get("Content-Type").get(0).indexOf("pdf") >= 0)) {
                String fileName = getFileName(headers);
                List<InputPart> inputParts = entry.getValue();
                for (InputPart inputPart : inputParts) {

                    InputStream inputStream = inputPart.getBody(InputStream.class, null);

                    byte[] bytes = IOUtils.toByteArray(inputStream);

                    CmsPageEntity page = cmsServiceDAO.getCmsPageByType(type.name(), DefaultData.PROJECT_NAME);

                    if (page == null) {
                        page = new CmsPageEntity();
                        page.setType(type.name());
                        page.setProjectName(DefaultData.PROJECT_NAME);
                        cmsServiceDAO.save(page);
                    } else {
//                        List<CmsImagesEntity> l = cmsServiceDAO.getCmsImageByPageId(page.getId());
//
//                        if (findmageByName(l, fileName)) {
//                            return null;
//                        }
                    }


                    CmsImagesEntity cmsImagesEntity = new CmsImagesEntity();
                    cmsImagesEntity.setCmsPage(page);
                    cmsImagesEntity.setFileName(fileName);
                    cmsImagesEntity.setFileData(bytes);

                    cmsServiceDAO.saveImage(cmsImagesEntity);
                    return cmsImagesEntity.getId();
                }

            } else {
//                throw new Exception("Не удалось загрузить файл");
            }
        }
        return null;
    }


    private boolean findmageByName(List<CmsImagesEntity> cmsImagesEntities, String fileName) {
        if (cmsImagesEntities != null) {
            for (CmsImagesEntity imagesEntity : cmsImagesEntities) {
                if (imagesEntity.getFileName().equals(fileName)) {
                    return true;
                }
            }
        }
        return false;
    }


    private static String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                try {
                    return URLDecoder.decode(finalFileName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return "unknown";
    }


    private CmsNewsEntity transformWebToEntityNews(NewsW news, CmsNewsEntity cmsNewsEntity) {
        cmsNewsEntity.setId(news.getId());
        cmsNewsEntity.setDescription(news.getDescription());
        cmsNewsEntity.setTitle(news.getTitle());
        cmsNewsEntity.setKeywords(news.getKeywords());
        cmsNewsEntity.setName(news.getName());
        cmsNewsEntity.setBody(news.getText());
        cmsNewsEntity.setProjectName(DefaultData.PROJECT_NAME);
        cmsNewsEntity.setDateChange(new Date());

        if (news.getDate() != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            try {
                cmsNewsEntity.setCreateDate(format.parse(news.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return cmsNewsEntity;
    }


    private NewsW transformEntityToWebNews(NewsW news, CmsNewsEntity cmsNewsEntity) {
        news.setId(cmsNewsEntity.getId());
        news.setDescription(cmsNewsEntity.getDescription());
        news.setTitle(cmsNewsEntity.getTitle());
        news.setKeywords(cmsNewsEntity.getKeywords());
        news.setName(cmsNewsEntity.getName());
        news.setText(cmsNewsEntity.getBody());
        if (cmsNewsEntity.getCreateDate() != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            news.setDate(format.format(cmsNewsEntity.getCreateDate()));
        }

        return news;
    }


    /**
     * @param withImageNews - загружать картинки новостей
     * @return
     * @throws IOException pages = номер сстраницы - отображаем по три
     */
    public Map<String, Object> getNews(Boolean withImageNews, Integer pages, Integer pagesize) throws IOException {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.NEWS.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();
        //если null значит заполняем дефолтными данными и отправляем в бд
        if (page == null) {
            page = new CmsPageEntity();
            List<NewsW> newsList = DefaultData.NEWS_LIST;
            List<CmsImagesEntity> cmsImagesEntities = new ArrayList<>();

            NewsData newsData = new NewsData();
//            newsData.setNews(newsList);
            CmsImagesEntity imagesEntity = imageDefault("/cms/support.png", "support.png");
            imagesEntity.setCmsPage(page);
            cmsImagesEntities.add(imagesEntity);
            newsData.setFileName("support.png");
            newsData.setFeedback("<a href=\"/\">Войти в чат</a> или позвоните в отдел поддержки, <a href=\"tel:88008834354\">8 800 883 43 54</a>");
            newsData.setHeaderContext("НОВОСТИ");
            page.setType(CMS_PAGES_TYPE.NEWS.name());
            page.setBody(gson.toJson(newsData));
            page.setImages(cmsImagesEntities);
            page.setProjectName(DefaultData.PROJECT_NAME);
            cmsServiceDAO.save(page);

            for (NewsW news : newsList) {
//                CmsImagesEntity imagesEntity = imageDefault("/cms/news-1.jpg", "new-1.jpg");
//                imagesEntity.setCmsPage(page);
//                imagesEntity.setCmsPage(page);
//                cmsImagesEntities.add(imagesEntity);
//                news.setFileName("new-1.jpg");
                cmsServiceDAO.saveImage(imagesEntity);
                CmsNewsEntity cmsNewsEntity = new CmsNewsEntity();
                transformWebToEntityNews(news, cmsNewsEntity);

                cmsNewsEntity.setImgId(imagesEntity.getId());

                cmsServiceDAO.create(cmsNewsEntity);
            }

            return getNews(withImageNews, pages, pagesize);
        } else {
            if (pagesize == null) {
                pagesize = 3;
            }
            NewsData nd = gson.fromJson(page.getBody(), NewsData.class);
            Map<String, Object> map = new HashMap<>();
            map.put("total", cmsServiceDAO.countCmsNews(DefaultData.PROJECT_NAME));
            List<ImageCms> imageCmsesForNews = new ArrayList<>();
            if (pages != null) {
                int i = (pages - 1) * pagesize;
                List<NewsW> newsList = new ArrayList<>();
                List<CmsNewsEntity> news = cmsServiceDAO.getCmsNewsEntityPagi(DefaultData.PROJECT_NAME, pagesize, i);
                for (CmsNewsEntity cmsNewsEntity : news) {
                    NewsW newsW = new NewsW();
                    transformEntityToWebNews(newsW, cmsNewsEntity);
                    if (withImageNews) {
                        if (cmsNewsEntity.getImgId() != null) {
                            CmsImagesEntity img = (CmsImagesEntity) cmsServiceDAO.findById(CmsImagesEntity.class, cmsNewsEntity.getImgId());
                            ImageCms imageCms = new ImageCms(img.getId(), img.getFileName(), org.jboss.resteasy.util.Base64.encodeBytes(img.getFileData()));
                            imageCmsesForNews.add(imageCms);
                            newsW.setFileName(imageCms.getFileName());
                        }
                        if (cmsNewsEntity.getPreviewImgId() != null) {
                            CmsImagesEntity img = (CmsImagesEntity) cmsServiceDAO.findById(CmsImagesEntity.class, cmsNewsEntity.getPreviewImgId());
                            ImageCms imageCms = new ImageCms(img.getId(), img.getFileName(), org.jboss.resteasy.util.Base64.encodeBytes(img.getFileData()));
                            imageCmsesForNews.add(imageCms);
                            newsW.setFileNamePreview(imageCms.getFileName());
                        }
                    }
                    newsList.add(newsW);
                }
                nd.setNews(newsList);
            }

            if (!isEmpty(nd.getFileName())) {
            	System.out.println(nd.getFileName());
                List<CmsImagesEntity> q1 = cmsServiceDAO.getCmsImageByPageId(page.getId());
                for (CmsImagesEntity entity : q1) {
                    if (entity.getFileName().equals(nd.getFileName())) {
                        ImageCms imageCms = new ImageCms(entity.getId(), entity.getFileName(), org.jboss.resteasy.util.Base64.encodeBytes(entity.getFileData()));
                        imageCmsesForNews.add(imageCms);

                        break;
                    }
                }
            }
            map.put("image", imageCmsesForNews);
            map.put("news", nd);

            return map;
        }
    }


    public void saveNews(NewsData newsData) {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.NEWS.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();

        mainField(page, newsData);
        NewsData old = gson.fromJson(page.getBody(), NewsData.class);
        if (old.getNews() != null) {
            newsData.setNews(old.getNews());
        }
        page.setBody(gson.toJson(newsData));

        cmsServiceDAO.save(page);
    }


    public void uploadNews(MultipartFormDataInput formData) throws Exception {
        Integer imgId =  bodyUpload(formData, CMS_PAGES_TYPE.NEWS);
        Map<String, List<InputPart>> map = formData.getFormDataMap();

        for (Map.Entry<String, List<InputPart>> entry : map.entrySet()) {
            MultivaluedMap<String, String> headers = entry.getValue().get(0).getHeaders();

            if (headers.get("Content-Disposition") != null && (headers.get("Content-Disposition").get(0).indexOf("newsId") >= 0)) {
                Integer newsId = Integer.parseInt(entry.getValue().get(0).getBodyAsString());

                CmsNewsEntity cmsNewsEntity = (CmsNewsEntity) cmsServiceDAO.findById(CmsNewsEntity.class, newsId);
                cmsNewsEntity.setImgId(imgId);

                cmsServiceDAO.edit(cmsNewsEntity);
            }
        }
    }


    public void removeNews(Integer id) {
        CmsNewsEntity cmsNewsEntity = (CmsNewsEntity) cmsServiceDAO.findById(CmsNewsEntity.class, id);
        Integer imgId = cmsNewsEntity.getImgId();
        Integer imgIdPreview = cmsNewsEntity.getPreviewImgId();
        cmsServiceDAO.remove(cmsNewsEntity);
        if (imgId != null) {
            CmsImagesEntity img = (CmsImagesEntity) cmsServiceDAO.findById(CmsImagesEntity.class, imgId);
            cmsServiceDAO.removeImage(img.getId());
        }
        if (imgIdPreview != null) {
            CmsImagesEntity img = (CmsImagesEntity) cmsServiceDAO.findById(CmsImagesEntity.class, imgIdPreview);
            cmsServiceDAO.removeImage(img.getId());
        }
    }


    public Map<String, Object> getNewsById(Integer id) throws IOException {
        Gson gson = new Gson();

        Map<String, Object> map = new HashMap<>();
        List<CmsNewsEntity> listNews = cmsServiceDAO.getCmsNewsEntityAll(DefaultData.PROJECT_NAME);
        for (int j = 0; j < listNews.size(); j++) {
            CmsNewsEntity n = listNews.get(j);
            NewsW newsW = new NewsW();
            transformEntityToWebNews(newsW, n);
            List<ImageCms> imageCmseses = new ArrayList<>();
            if (n.getImgId() != null) {
                CmsImagesEntity img = (CmsImagesEntity) cmsServiceDAO.findById(CmsImagesEntity.class, n.getImgId());
                ImageCms imageCms = new ImageCms(img.getId(), img.getFileName(), org.jboss.resteasy.util.Base64.encodeBytes(img.getFileData()));
                imageCmseses.add(imageCms);
                newsW.setFileName(imageCms.getFileName());
            }
            if (n.getPreviewImgId() != null) {
                CmsImagesEntity img = (CmsImagesEntity) cmsServiceDAO.findById(CmsImagesEntity.class, n.getPreviewImgId());
                ImageCms imageCms = new ImageCms(img.getId(), img.getFileName(), org.jboss.resteasy.util.Base64.encodeBytes(img.getFileData()));
                imageCmseses.add(imageCms);
                newsW.setFileNamePreview(imageCms.getFileName());
            }
            if (n.getId().equals(id)) {
                map.put("news", newsW);
                if (j > 0) {
                    map.put("prev", listNews.get(j - 1).getId());
                }
                if (j < (listNews.size() - 1)) {
                    map.put("next", listNews.get(j + 1).getId());
                }
                map.put("image", imageCmseses);
                return map;
            }
        }

        return null;
    }


    public Integer saveOneNews(NewsW news) {
        CmsNewsEntity cmsNewsEntity = null;
        if (news.getId() == null) {
            cmsNewsEntity = new CmsNewsEntity();
            transformWebToEntityNews(news, cmsNewsEntity);
            cmsServiceDAO.create(cmsNewsEntity);
        } else {
            cmsNewsEntity = (CmsNewsEntity) cmsServiceDAO.findById(CmsNewsEntity.class, news.getId());
            transformWebToEntityNews(news, cmsNewsEntity);

            cmsServiceDAO.edit(cmsNewsEntity);

        }

        return cmsNewsEntity.getId();
    }


    private ImageCms findImage(List<ImageCms> imageCmses, String fileName) {
        if (isEmpty(fileName)) {
            return null;
        }
        for (ImageCms imageCms : imageCmses) {
            if (imageCms.getFileName().equals(fileName)) {
                return imageCms;
            }
        }
        return null;
    }

    private PoliticsPointDataW transportCmsDocumentToPoliticsPoint(CmsDocumentsEntity cmsDocumentsEntity) {
        PoliticsPointDataW politicsPointDataW = new PoliticsPointDataW();
        politicsPointDataW.setDistr(cmsDocumentsEntity.getBody());
        politicsPointDataW.setId(cmsDocumentsEntity.getId());
        politicsPointDataW.setHeader(cmsDocumentsEntity.getName());
        politicsPointDataW.setOrderList(cmsDocumentsEntity.getOrderInd());
        politicsPointDataW.setFileName(cmsDocumentsEntity.getPdfUrl());

        return politicsPointDataW;
    }

    public Map<String, Object> getPolitics(Boolean withImageNews) throws IOException {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.POLITICS.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();
        //если null значит заполняем дефолтными данными и отправляем в бд
        if (page == null) {
            page = new CmsPageEntity();
            List<CmsImagesEntity> cmsImagesEntities = new ArrayList<>();
            PoliticsData politicsData = new PoliticsData();
            politicsData.setHeaderText(DefaultData.POLITICS_HEADER);

            politicsData.setDataList(DefaultData.politics);

            page.setType(CMS_PAGES_TYPE.POLITICS.name());
            page.setBody(gson.toJson(politicsData));
            page.setImages(cmsImagesEntities);
            page.setProjectName(DefaultData.PROJECT_NAME);
            cmsServiceDAO.save(page);

            return getPolitics(withImageNews);
        } else {
            PoliticsData nd = gson.fromJson(page.getBody(), PoliticsData.class);
            Map<String, Object> map = new HashMap<>();
            List<CmsImagesEntity> i = cmsServiceDAO.getCmsImageByPageId(page.getId());
            List<CmsDocumentsEntity> cmsDocumentsEntities = cmsServiceDAO.getCmsDocumentEntityAll(DefaultData.PROJECT_NAME);
            List<PoliticsPointDataW> politicsPointDataWs = new ArrayList<>();
            for(CmsDocumentsEntity cmsDocumentsEntity : cmsDocumentsEntities){
                politicsPointDataWs.add(transportCmsDocumentToPoliticsPoint(cmsDocumentsEntity));
            }
            sortengTeam(politicsPointDataWs);
            nd.setDataList(politicsPointDataWs);
            map.put("main", nd);

            if (withImageNews) {
                List<ImageCms> imageCmses = toListImageCms(i);
                map.put("image", imageCmses);
            } else {
//                if (nd.getFileName() != null) {
//                    for (CmsImagesEntity entity : i) {
//                        if (entity.getFileName().equals(nd.getFileName())) {
//                            List<ImageCms> imageCmses = new ArrayList<>();
//                            ImageCms imageCms = new ImageCms(entity.getId(), entity.getFileName(), org.jboss.resteasy.util.Base64.encodeBytes(entity.getFileData()));
//                            imageCmses.add(imageCms);
//                            map.put("image", imageCmses);
//                            break;
//                        }
//                    }
//                }
            }

            return map;
        }
    }


    /**
     * кортировка списка команды
     */
    private <T extends AbstractOrderList> void sortengTeam(List<T> data) {

        Collections.sort(data, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1.getOrderList() == null && o2.getOrderList() == null) {
                    return 0;
                }
                if (o1.getOrderList() == null) {
                    return 1;
                }
                if (o2.getOrderList() == null) {
                    return -1;
                }
                return o1.getOrderList().compareTo(o2.getOrderList());
            }

        });

    }


    public ContacntsData getContacts() throws IOException {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.CONTACTS.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();
        //если null значит заполняем дефолтными данными и отправляем в бд
        if (page == null) {
            page = new CmsPageEntity();
            ContacntsData contacntsData = new ContacntsData();
            contacntsData.setAddress(DefaultData.CONTACTS_ADDRESS);
            contacntsData.setEmail(DefaultData.CONTACTS_EMAIL);
            contacntsData.setPhone(DefaultData.CONTACTS_PHONE);

            page.setType(CMS_PAGES_TYPE.CONTACTS.name());
            page.setBody(gson.toJson(contacntsData));
            page.setProjectName(DefaultData.PROJECT_NAME);
            cmsServiceDAO.save(page);

            return getContacts();
        } else {
            ContacntsData nd = gson.fromJson(page.getBody(), ContacntsData.class);
            return nd;
        }
    }

    public Map<String, Object> getAbout() throws IOException {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.ABOUTME.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();
        //если null значит заполняем дефолтными данными и отправляем в бд
        if (page == null) {
            page = new CmsPageEntity();

            AboutMeData aboutMeData = new AboutMeData();
            aboutMeData.setCompany(DefaultData.ABOUT_ME_COMPANY);
            aboutMeData.setSimply(DefaultData.ABOUT_ME_SIMPLY);
            aboutMeData.setQuickly(DefaultData.ABOUT_ME_QUICKLY);
            aboutMeData.setResponsibility(DefaultData.ABOUT_ME_RESPONSIBILITY);

            page.setType(CMS_PAGES_TYPE.ABOUTME.name());

            page.setBody(gson.toJson(aboutMeData));

            page.setProjectName(DefaultData.PROJECT_NAME);
            cmsServiceDAO.save(page);

            return getAbout();
        } else {
            AboutMeData nd = gson.fromJson(page.getBody(), AboutMeData.class);
            Map<String, Object> map = new HashMap<>();
            map.put("about", nd);
            return map;
        }
    }

    public void removeImage(Integer id) {
        cmsServiceDAO.removeImage(id);
    }

    public void removeNewsImg(Integer newsId) {
       /* CmsNewsEntity news = (CmsNewsEntity) cmsServiceDAO.findById(CmsNewsEntity.class, newsId);
        CmsImagesEntity cmsImagesEntity = (CmsImagesEntity) cmsServiceDAO.findById(CmsImagesEntity.class, news.getImgId());
        news.setImgId(null);
        cmsServiceDAO.edit(news);
        cmsServiceDAO.removeImage(cmsImagesEntity.getId());
        if (news.getPreviewImgId() != null) {
            Integer prevforo = news.getPreviewImgId();
            news.setPreviewImgId(null);
            cmsServiceDAO.edit(news);
            CmsImagesEntity cmsImagesEntityPreview = (CmsImagesEntity) cmsServiceDAO.findById(CmsImagesEntity.class, prevforo);
            cmsServiceDAO.removeImage(cmsImagesEntityPreview.getId());
        }*/
        cmsServiceDAO.removePreviewImagePerOneTransaction(newsId);
        cmsServiceDAO.removeImagePerOneTransaction(newsId);

    }


    public void removeNewsImgPreview(Integer newsId) {
        /*CmsNewsEntity news = (CmsNewsEntity) cmsServiceDAO.findById(CmsNewsEntity.class, newsId);
        Integer prevforo = news.getPreviewImgId();
        news.setPreviewImgId(null);
        cmsServiceDAO.edit(news);
        if (prevforo != null) {
            CmsImagesEntity cmsImagesEntityPreview = (CmsImagesEntity) cmsServiceDAO.findById(CmsImagesEntity.class, prevforo);
            cmsServiceDAO.removeImage(cmsImagesEntityPreview.getId());
        }*/
        cmsServiceDAO.removePreviewImagePerOneTransaction(newsId);
    }

    public String savePreviewFoto(String code, String fileName, String newsId) throws IOException {
        byte[] bytes = Base64.decode(code.replaceAll("data:image/png;base64,", ""));

        String newname = fileName.substring(0, fileName.lastIndexOf(".")) + "_preview" + fileName.substring(fileName.lastIndexOf("."));

        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.NEWS.name(), DefaultData.PROJECT_NAME);

        CmsImagesEntity cmsImagesEntity = new CmsImagesEntity();
        cmsImagesEntity.setCmsPage(page);
        cmsImagesEntity.setFileName(newname);
        cmsImagesEntity.setFileData(bytes);
        cmsServiceDAO.saveImage(cmsImagesEntity);

        CmsNewsEntity cmsNewsEntity = (CmsNewsEntity) cmsServiceDAO.findById(CmsNewsEntity.class, Integer.parseInt(newsId));
        cmsNewsEntity.setPreviewImgId(cmsImagesEntity.getId());

        cmsServiceDAO.edit(cmsNewsEntity);

        return newname;
    }


    public void savePolitics(PoliticsData politicsData) {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.POLITICS.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();

        mainField(page, politicsData);
        PoliticsData old = gson.fromJson(page.getBody(), PoliticsData.class);
        if (old.getDataList() != null) {
            politicsData.setDataList(old.getDataList());
        }
        page.setBody(gson.toJson(politicsData));

        cmsServiceDAO.save(page);
    }


    public void removePolitics(Integer id) {
        CmsDocumentsEntity cmsDocumentsEntity = (CmsDocumentsEntity) cmsServiceDAO.findById(CmsDocumentsEntity.class, id);
        if(cmsDocumentsEntity!=null) {
            CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.POLITICS.name(), DefaultData.PROJECT_NAME);

            List<CmsImagesEntity> img = cmsServiceDAO.getCmsImageByPageId(page.getId());
            for (CmsImagesEntity imagesEntity : img) {
                if (imagesEntity.getFileName().equals(cmsDocumentsEntity.getPdfUrl())) {
                    cmsServiceDAO.removeImage(imagesEntity.getId());
                    break;
                }
            }
            cmsServiceDAO.remove(cmsDocumentsEntity);
        }
    }


    public Map<String, Object> getPoliticsById(Integer id) throws IOException {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.POLITICS.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();
        if (page == null) {
            return null;
        } else {
            Map<String, Object> map = new HashMap<>();
            CmsDocumentsEntity cmsDocumentsEntity = (CmsDocumentsEntity) cmsServiceDAO.findById(CmsDocumentsEntity.class, id);
            PoliticsPointDataW n = transportCmsDocumentToPoliticsPoint(cmsDocumentsEntity);

            map.put("main", n);
            List<CmsImagesEntity> i = cmsServiceDAO.getCmsImageByPageId(page.getId());
            for (CmsImagesEntity imageCmses : i) {
                if (imageCmses.getFileName().equals(n.getFileName())) {
                    map.put("image", new ImageCms(imageCmses.getId(), null, null));
                    break;
                }
            }
            return map;


        }
    }

    private CmsDocumentsEntity transportPoliticsPointCToCmsDocument(PoliticsPointDataW politicsPointDataW) {
        CmsDocumentsEntity cmsDocumentsEntity = new CmsDocumentsEntity();
        cmsDocumentsEntity.setBody(politicsPointDataW.getDistr());
        cmsDocumentsEntity.setDateChange(new Date());
        cmsDocumentsEntity.setId(politicsPointDataW.getId());
        cmsDocumentsEntity.setName(politicsPointDataW.getHeader());
        cmsDocumentsEntity.setOrderInd(politicsPointDataW.getOrderList());
        cmsDocumentsEntity.setProjectName(DefaultData.PROJECT_NAME);
        cmsDocumentsEntity.setPdfUrl(politicsPointDataW.getFileName());

        return cmsDocumentsEntity;
    }

    public Integer savePoliticsPoints(PoliticsPointDataW news) {
        CmsDocumentsEntity cmsDocumentsEntity = transportPoliticsPointCToCmsDocument(news);
        if (cmsDocumentsEntity.getId() == null) {
            cmsDocumentsEntity.setCreateDate(new Date());
            cmsServiceDAO.create(cmsDocumentsEntity);
        } else {
            cmsServiceDAO.edit(cmsDocumentsEntity);
        }

        return cmsDocumentsEntity.getId();
    }


    public void uploadPolitics(MultipartFormDataInput formData) throws Exception {
        bodyUpload(formData, CMS_PAGES_TYPE.POLITICS);
    }


    public void saveContaacts(ContacntsData contacntsData) {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.CONTACTS.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();
        mainField(page, contacntsData);
        page.setBody(gson.toJson(contacntsData));

        cmsServiceDAO.save(page);
    }


    public void saveAbout(AboutMeData aboutMeData) {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.ABOUTME.name(), DefaultData.PROJECT_NAME);
        Gson gson = new Gson();
        mainField(page, aboutMeData);
        page.setBody(gson.toJson(aboutMeData));

        cmsServiceDAO.save(page);
    }


    public CmsImagesEntity getPoliticsDoc(String fileName) {
        CmsPageEntity page = cmsServiceDAO.getCmsPageByType(CMS_PAGES_TYPE.POLITICS.name(), DefaultData.PROJECT_NAME);
        List<CmsImagesEntity> i = cmsServiceDAO.getCmsImageByPageId(page.getId());
        for (CmsImagesEntity imageCmses : i) {
            if (imageCmses.getFileName().equals(fileName)) {
                return imageCmses;
            }
        }
        return null;
    }
    
    public boolean isEmpty(String s)
    {
    	if(s==null)
    		return true;
    	if(s.trim().length()==0)
    		return true;
    	return false;
    }
}


