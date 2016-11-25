package ru.simplgroupp.rest.api;

import ru.simplgroupp.rest.api.data.OfficialDocumentsData;
import ru.simplgroupp.rest.api.service.DocumentsService;
import ru.simplgroupp.transfer.OfficialDocuments;
import ru.simplgroupp.util.HtmlUtils;
import ru.simplgroupp.util.MimeTypeKeys;
import ru.simplgroupp.util.XmlUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@RequestScoped
@Path("/documents")
public class DocumentsController {

    private DocumentsService documentsService;

    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<OfficialDocumentsData>> getOfficialDocuments() {

        return new JsonResult<>(documentsService.getDocuments());
    }

    @GET
    @Path("/download")
    public Response download(@QueryParam("id") int id, @Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        OfficialDocuments document = documentsService.getDocumentById(id);
        if (document != null) {

            String oferta=HtmlUtils.makeHtmlFromText(XmlUtils.ENCODING_UTF,document.getDocText());
            return Response
                    .ok(oferta, MimeTypeKeys.HTML)
                    .header("content-disposition", "attachment; filename = oferta.html")
                    .build();
        } else return null;
    }

    public DocumentsController() {
    }

    @Inject
    public DocumentsController(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }
}
