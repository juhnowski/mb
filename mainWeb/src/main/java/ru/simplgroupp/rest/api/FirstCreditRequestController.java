package ru.simplgroupp.rest.api;


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.data.firstcreditrequest.*;
import ru.simplgroupp.rest.api.service.FirstCreditRequestService;

import java.io.File;
import java.util.List;


@Path("/firstrequest")
@Stateless
public class FirstCreditRequestController {
    public static final String FILE_UPLOAD_FIELD_NAME = "file";
	
    @Inject private FirstCreditRequestService service;

    @GET
    @Path("calculateStakeUnknown.js")
    @Produces("text/javascript")
    public String getStakeProcedure() {
        return this.service.getStakeProcedure();
    }
    
    @POST
    @Path("auth")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonResult<String> auth() throws ServletException {
        this.service.auth();
        return new JsonResult<>();
    }

    @POST
    @Path("/saveRequest")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<String> saveRequest(FirstCreditRequestData data, @Context HttpServletRequest request) {
        try {
        	PeopleBehaviorData beh = new PeopleBehaviorData();
        	beh.setUserAgent(request.getHeader("User-Agent"));
        	beh.setClientIp(service.getRealIp(request));
            
        	data.setBehData(beh);
        	
        	this.service.saveRequest(data);	
        	return new JsonResult<>();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new JsonResult<>(ex);
        } 	
    }
    
    /**
     * Возвращает набор данных используемый для построения ползунков 
     * для выбора суммы и срока кредита в анкете
     * 
     * @return набор данных набор данных используемый для построения ползунков 
     * для выбора суммы и срока кредита в анкете.
     */
    @GET
    @Path("getSumsData")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<SumData> getLastCreditRequest(@Context UriInfo info) {
        Double initialSum = null;
        if (!StringUtils.isEmpty(info.getQueryParameters().getFirst("initialSum"))) {
            initialSum = Double.parseDouble(info.getQueryParameters().getFirst("initialSum"));
        }

        Integer initialDays = null;
        if (!StringUtils.isEmpty(info.getQueryParameters().getFirst("initialDays"))) {
            initialDays = Integer.parseInt(info.getQueryParameters().getFirst("initialDays"));
        }

        try {
            return new JsonResult<>(service.getRequestData(initialSum, initialDays));
        } catch (Exception ex) {
            ex.printStackTrace();
            return new JsonResult<>(ex);
        }
    }

    @GET
    @Path("hasPrevData")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonResult hasPrevData() {
        JsonResult res = new JsonResult();
        res.setSuccess(this.service.hasPrevData());
        return res;
    }

    @POST
    @Path("phone/confirmation/send")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonResult<String> phoneConfirmation(String msisdn) {
        this.service.sendPhoneConfirmation(msisdn);
        return new JsonResult<>();
    }

    @POST
    @Path("phone/confirmation/verify")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonResult<String> phoneConfirmationVerify(String msisdnCode) {
        JsonResult result = new JsonResult();
        result.setSuccess(this.service.verifyPhoneConfirmation(msisdnCode));
        return result;
    }

    @POST
    @Path("email/confirmation/send")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonResult<String> emaileConfirmation(String email) {
        this.service.sendEmailConfirmation(email);
        return new JsonResult<>();
    }

    @POST
    @Path("email/confirmation/verify")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonResult<String> emailConfirmationVerify(String emailCode) {
        JsonResult result = new JsonResult();
        result.setSuccess(this.service.verifyEmailConfirmation(emailCode));
        return result;
    }

    @GET
    @Path("info")
    @Produces(MediaType.APPLICATION_JSON)
    public InitialInfoData getInitialInfo() throws KassaException {
        return this.service.buildInfo();
    }

    @POST
    @Path("upload")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public String uploadFile(@Context HttpServletRequest request, @Context ServletContext servletContext) throws FileUploadException, KassaException {
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configure a repository (to ensure a secure temp location is used)
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Parse the request
        List<FileItem> items = upload.parseRequest(request);

        for(FileItem item : items) {
            if (!item.isFormField() && FILE_UPLOAD_FIELD_NAME.equals(item.getFieldName())) {
                return this.service.saveUploadedFile(item);
            }
        }

        return null;
    }

    @POST
    @Path("/validate/phone")
    public JsonResult validatePhone(String phone) throws KassaException {
        JsonResult result = new JsonResult();
        result.setSuccess(this.service.validatePhone(phone));
        return result;
    }

    @POST
    @Path("/validate/email")
    public JsonResult validateEmail(String email) throws KassaException {
        JsonResult result = new JsonResult();
        result.setSuccess(this.service.validateEmail(email));
        return result;
    }

    @POST
    @Path("/validate/passport")
    public JsonResult validatePassport(PeoplePassportData passportData) throws KassaException {
        JsonResult result = new JsonResult();
        result.setSuccess(this.service.validatePassport(passportData));
        return result;
    }

    @POST
    @Path("/validate/contacts")
    public JsonResult validateContacts(PeopleMainData mainData) throws KassaException {
        JsonResult result = new JsonResult();
        result.setSuccess(this.service.validateContacts(mainData));
        return result;
    }

    @POST
    @Path("/validate/location")
    public JsonResult validateLocation(@Context HttpServletRequest request) throws KassaException {
        JsonResult result = new JsonResult();
        result.setSuccess(this.service.validateLocation(service.getRealIp(request)));
        return result;
    }

    @POST
    @Path("/cache")
    public JsonResult cacheUserData(DataHolder data) throws KassaException {
        this.service.saveUserWork(data);
        return new JsonResult();
    }
}
