package ru.simplgroupp.webapp.manager.creditrequest.controller;

import org.apache.commons.collections.comparators.BooleanComparator;
import org.apache.commons.io.FileUtils;
import ru.simplgroupp.dao.interfaces.AntifraudOccasionDAO;
import ru.simplgroupp.dao.interfaces.BizActionDAO;
import ru.simplgroupp.dao.interfaces.DocumentsDAO;
import ru.simplgroupp.dao.interfaces.OfficialDocumentsDAO;
import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.EnvironmentSnapshot;
import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.exception.ActionRuntimeException;
import ru.simplgroupp.interfaces.BizActionBeanLocal;
import ru.simplgroupp.interfaces.BizActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.persistence.DocumentMediaEntity;
import ru.simplgroupp.persistence.Misc;
import ru.simplgroupp.persistence.OfficialDocumentsEntity;
import ru.simplgroupp.persistence.antifraud.AntifraudOccasionEntity;
import ru.simplgroupp.rules.AbstractContext;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.FileUtil;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.transfer.antifraud.AntifraudOccasion;
import ru.simplgroupp.util.AppUtil;
import ru.simplgroupp.util.HtmlUtils;
import ru.simplgroupp.util.MimeTypeKeys;
import ru.simplgroupp.util.XmlUtils;
import ru.simplgroupp.webapp.manager.controller.AppDataController;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.WorkflowObjectState;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class EditCreditRequestController extends ViewCreditRequestController {
    private static final long serialVersionUID = 6431749732140688476L;

    private static Logger logger = Logger.getLogger(EditCreditRequestController.class.getName());

    @EJB
    protected AppBean appBean;

    @EJB
    protected AppServiceBeanLocal appServ;

    @EJB
    protected BizActionBeanLocal bizBean;

    @EJB
    protected BizActionProcessorBeanLocal bizProc;

    @EJB
    protected BizActionDAO bizDAO;

    @EJB
    protected DocumentsDAO documentsDAO;

    @EJB
    protected OfficialDocumentsDAO officialDocumentsDAO;

    @EJB
    protected AntifraudOccasionDAO antifraudOccasionDAO;

    protected Integer rejectReasonCode;

    protected String rejectComment;

    protected Integer statusCode;

    protected List<WorkflowObjectState> actionStates;

    protected List<WorkflowObjectState> actionTaskStates;

    protected EnvironmentSnapshot snapshot;

    protected ActionContext actionContext;

    protected AppDataController appDataCtl;

    protected List<Misc> listMisc;

    private Map<AntifraudOccasion, Boolean> antifraudOccasionsMap;


    @Override
    protected void reloadCreditRequest(FacesContext facesCtx, Integer prmId) {
        super.reloadCreditRequest(facesCtx, prmId);
        reloadActionStates();
        reloadHunterObjects();
        listMisc = kassa.findMiscVarsCreditRequest(prmId);
    }

    private void reloadActionStates() {
        AbstractContext context = AppUtil.getDefaultContext(creditRequest, CreditRequest.class.getName());
        context.setCurrentUser(userCtl.getUser());

        snapshot = appServ.getSnapshot(context, CreditRequest.class.getName(), true, true);
        actionStates = snapshot.getStates();
//		actionStates = workflow.getProcWfActions(CreditRequest.class.getName(), creditRequest.getId(), null, true);
        actionTaskStates = new ArrayList<WorkflowObjectState>(actionStates.size());
        for (WorkflowObjectState stat : actionStates) {
            if (stat.getTask() != null) {
                actionTaskStates.add(stat);
            }
        }
    }

    private void reloadHunterObjects() {
        antifraudOccasionsMap = new HashMap<>();
        List<AntifraudOccasion> antifraudOccasions = antifraudOccasionDAO.find(creditRequest.getId(), null, null, null,
                null, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
        for (Reference hunterObject : referenceBooks.getHunterObjects()) {
            AntifraudOccasion saveOccasion = null;
            for (AntifraudOccasion occasion : antifraudOccasions) {
                if (occasion.getHunterObject().equals(hunterObject)) {
                    saveOccasion = occasion;
                }
            }

            boolean status = true;
            if (saveOccasion == null || saveOccasion.getDataEnd() != null) {
                saveOccasion = new AntifraudOccasion(antifraudOccasionDAO.buildEntity(userCtl.getUser().getId(), creditRequest.getId(),
                        creditRequest.getPeopleMain().getId(), hunterObject.getCode(), AntifraudOccasion.STATUS_MANUAL_SUSPECT, new Date(), null, null));
                status = false;
            }
            antifraudOccasionsMap.put(saveOccasion, status);
        }
    }

    public void updateOccasions() {
        List<AntifraudOccasionEntity> forSave = new ArrayList<>();
        List<AntifraudOccasionEntity> forDelete = new ArrayList<>();
        for (Map.Entry<AntifraudOccasion, Boolean> entry : antifraudOccasionsMap.entrySet()) {
            if (entry.getValue()) {
                // отмечен галкой, добавляем\обновляем
                forSave.add(entry.getKey().getEntity());
            } else if (!entry.getValue() && entry.getKey().getId() != null) {
                // не отмечен галкой, удаляем, точнее сам решит удалять или нет
                forDelete.add(entry.getKey().getEntity());
            }
        }

        for (AntifraudOccasionEntity entity : forSave) {
            antifraudOccasionDAO.save(entity);
        }

        for (AntifraudOccasionEntity entity : forDelete) {
            antifraudOccasionDAO.delete(entity.getId());
        }

        reloadHunterObjects();
    }

    public String save() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            kassa.changeStatus(getPrmCreditRequestId(), statusCode, rejectComment);
            return "list?faces-redirect=true";
        } catch (Exception ex) {
            JSFUtils.handleAsError(facesCtx, null, ex);
            return null;
        }
    }

    public String saveCollector() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            kassa.changeStatus(getPrmCreditRequestId(), CreditStatus.FOR_COLLECTOR, rejectComment);
            return "list?faces-redirect=true";
        } catch (Exception ex) {
            JSFUtils.handleAsError(facesCtx, null, ex);
            return null;
        }
    }

    public String saveCourt() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            kassa.changeStatus(getPrmCreditRequestId(), CreditStatus.FOR_COURT, rejectComment);
            return "list?faces-redirect=true";
        } catch (Exception ex) {
            JSFUtils.handleAsError(facesCtx, null, ex);
            return null;
        }
    }

    public String cancel() {
        return "canceled";
    }

    public String callRS() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            logger.info("Русский стандарт, ручной запрос");
            actionContext = actProc.createActionContext(null, true);
            actProc.runPlugin("rusStandart", CreditRequest.class.getName(), creditRequest.getId(), null, actionContext);
            reloadCreditRequest(facesCtx, creditRequest.getId());
        } catch (ActionRuntimeException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        } catch (ActionException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        }
        return null;
    }

    public String callCais() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            logger.info("кредитные истории ОКБ, ручной запрос");
            actionContext = actProc.createActionContext(null, true);
            actProc.runPlugin("okbCais", CreditRequest.class.getName(), creditRequest.getId(), null, actionContext);
            reloadCreditRequest(facesCtx, creditRequest.getId());
        } catch (ActionRuntimeException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        } catch (ActionException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        }
        return null;
    }

    public String callEquifax() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            logger.info("кредитные истории Equifax, ручной запрос");
            actionContext = actProc.createActionContext(null, true);
            actProc.runPlugin("equifax", CreditRequest.class.getName(), creditRequest.getId(), null, actionContext);
            reloadCreditRequest(facesCtx, creditRequest.getId());
        } catch (ActionRuntimeException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        } catch (ActionException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        }
        return null;
    }

    public String callIdv() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            logger.info("верификация ОКБ, ручной запрос");
            actionContext = actProc.createActionContext(null, true);
            actProc.runPlugin("okbIdv", CreditRequest.class.getName(), creditRequest.getId(), null, actionContext);
            reloadCreditRequest(facesCtx, creditRequest.getId());
        } catch (ActionRuntimeException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        } catch (ActionException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        }
        return null;
    }

    public String callNbki() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            logger.info("кредитные истории НБКИ, ручной запрос");
            actionContext = actProc.createActionContext(null, true);
            actProc.runPlugin("nbki", CreditRequest.class.getName(), creditRequest.getId(), null, actionContext);
            reloadCreditRequest(facesCtx, creditRequest.getId());
        } catch (ActionRuntimeException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        } catch (ActionException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
            return null;
        }
        return null;
    }

	public String callSociohub(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			logger.info("Социохаб, ручной запрос");
			actionContext = actProc.createActionContext(null, true);			
			actProc.runPlugin("sociohub", CreditRequest.class.getName(), creditRequest.getId(), null, actionContext);
			reloadCreditRequest(facesCtx, creditRequest.getId());
		} catch (ActionRuntimeException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		} catch (ActionException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		}
		return null;
	}
	
    public void call() {

    }

    public void assignMeLsn(ActionEvent event) {
        String ntaskid = (String) event.getComponent().getAttributes().get("wfotaskId");

        workflow.assignTaskToUser(ntaskid, userCtl.getUser());
        reloadActionStates();
    }

    public void assignAllLsn(ActionEvent event) {
        String ntaskid = (String) event.getComponent().getAttributes().get("wfotaskId");

        workflow.removeTaskFromUser(ntaskid);
        reloadActionStates();
    }

    public void downloadImage(ActionEvent event) {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        Integer id = Convertor.toInteger(event.getComponent().getAttributes().get("mediaid"));
        try {
            DocumentMediaEntity docMedia = documentsDAO.getDocumentMedia(id);
            if (docMedia != null) {
                //выгружаем картинку как файл
                String fileExt = "jpg";
                if (docMedia.getMediapath().lastIndexOf(".") > 0) {
                    fileExt = docMedia.getMediapath().substring(docMedia.getMediapath().lastIndexOf("."));
                }
                File file = File.createTempFile("image", fileExt);
                FileUtils.writeByteArrayToFile(file, docMedia.getMediadata());
                JSFUtils.downloadFile(file, docMedia.getMimetype());

            }
        } catch (IOException e) {
            JSFUtils.handleAsError(facesCtx, null, e);
        }
    }

    public void downloadDocument(ActionEvent event) {

        FacesContext facesCtx = FacesContext.getCurrentInstance();
        Integer id = Convertor.toInteger(event.getComponent().getAttributes().get("docid"));
        try {
            OfficialDocumentsEntity document = officialDocumentsDAO.getOfficialDocumentEntity(id);
            if (document != null) {
                downloadOfertaAsHtml(document.getDocText());
            }

        } catch (Exception e) {
            JSFUtils.handleAsError(facesCtx, null, e);
        }
    }

    private void downloadOfertaAsHtml(String oferta) throws Exception {
        //выгружаем оферту как html файл
        File file = File.createTempFile("oferta", ".html");
        FileUtil.writeToFile(file, HtmlUtils.makeHtmlFromText(XmlUtils.ENCODING_UTF, oferta));
        JSFUtils.downloadFile(file, MimeTypeKeys.HTML);
    }

    public void downloadFile() {

        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            downloadOfertaAsHtml(creditRequest.getAgreement());
        } catch (Exception e) {
            JSFUtils.handleAsError(facesCtx, null, e);
        }
    }

    public void updateCalls() {
        for (Call call : callList) {
            callsDAO.saveCallsEntity(call.getEntity());
        }

        reloadCalls();
    }

    public Integer getRejectReasonCode() {
        return rejectReasonCode;
    }

    public void setRejectReasonCode(Integer rejectReasonCode) {
        this.rejectReasonCode = rejectReasonCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getRejectComment() {
        return rejectComment;
    }

    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment;
    }

    public List<WorkflowObjectState> getActionStates() {
        return actionStates;
    }

    public List<WorkflowObjectState> getActionTaskStates() {
        return actionTaskStates;
    }

    public AppDataController getAppDataCtl() {
        return appDataCtl;
    }

    public void setAppDataCtl(AppDataController appDataCtl) {
        this.appDataCtl = appDataCtl;
    }

    public List<Misc> getListMisc() {
        return listMisc;
    }

    public Map<AntifraudOccasion, Boolean> getAntifraudOccasionsMap() {
        return antifraudOccasionsMap;
    }

    public List<AntifraudOccasion> getItemKeys() {
        List<AntifraudOccasion> keys = new ArrayList<AntifraudOccasion>();
        keys.addAll(antifraudOccasionsMap.keySet());
        return keys;
    }
}
