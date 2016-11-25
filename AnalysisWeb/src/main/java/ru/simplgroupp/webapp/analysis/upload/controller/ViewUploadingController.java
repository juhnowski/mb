package ru.simplgroupp.webapp.analysis.upload.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import ru.simplgroupp.dao.interfaces.UploadingDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.ScoringEquifaxBeanLocal;
import ru.simplgroupp.interfaces.ScoringNBKIBeanLocal;
import ru.simplgroupp.interfaces.ScoringOkbCaisBeanLocal;
import ru.simplgroupp.interfaces.ScoringRsBeanLocal;
import ru.simplgroupp.interfaces.ScoringSkbBeanLocal;
import ru.simplgroupp.interfaces.service.UploadingService;
import ru.simplgroupp.persistence.UploadingEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.FileUtil;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.transfer.Uploading;
import ru.simplgroupp.util.MimeTypeKeys;
import ru.simplgroupp.util.XmlUtils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ViewUploadingController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8565687655148286591L;
	private static final Logger logger = Logger.getLogger(ViewUploadingController.class.getName());
	
	@EJB
	UploadingService uploadService;
	
	@EJB
	UploadingDAO uploadingDAO;
	
	protected Integer reqId;
	/**
	 * объект с данными выгрузки
	 */
	protected Uploading uploading;
	/**
	 * рабочий червис или тестовый
	 */
	protected Boolean isWork=false;
	
	/**
	 * бин для РС
	 */
	@EJB(beanName="ScoringRsBean")
	protected ScoringRsBeanLocal rsBean;
	/**
	 * бин для Эквифакс
	 */
	@EJB(beanName="ScoringEquifaxBean")
	protected ScoringEquifaxBeanLocal equifaxBean;
	/**
	 * бин для ОКБ
	 */
	@EJB(beanName="ScoringOkbCaisBean")
	protected ScoringOkbCaisBeanLocal okbBean;
	/**
	 * бин для СКБ
	 */
	@EJB(beanName="ScoringSkbBean")
	protected ScoringSkbBeanLocal skbBean;
	/**
	 * бин для НБКИ
	 */
	@EJB(beanName="ScoringNBKIBean")
	protected ScoringNBKIBeanLocal nbki;
	/**
	 * дата отправки - если пустая, то текущая
	 */
	protected Date sendingDate;
	
	 @PostConstruct
	 public void init() 
		{
			FacesContext facesCtx = FacesContext.getCurrentInstance();
			if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
				if (reqId == null) {
					Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
					if (prms.containsKey("reqid")) {
					    reqId = Convertor.toInteger(prms.get("reqid"));
					}
				}
				if (reqId != null) {
					reloadItem(facesCtx, reqId);
				}
			}
		}
		
	 protected void reloadItem(FacesContext facesCtx, Integer reqId) {
		 uploading=uploadService.getUploading(reqId, Utils.setOf(Uploading.Options.INIT_ERROR));
	 }

	public String close(){
		return "close";
	}
		
	public Integer getReqId() {
		return reqId;
	}

	public void setReqId(Integer reqId) {
		this.reqId = reqId;
	}

	public Uploading getUploading() {
		return uploading;
	}

	public void setUploading(Uploading uploading) {
		this.uploading = uploading;
	}
	 
	 public void downloadInfo(){
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
	    	try {
	    		
				File file;
				String str=uploading.getInfo();
				if (uploading.getInfo().contains("xml")){
					//выгружаем запрос как xml файл, в кодировке utf-8
					file=File.createTempFile("upload", ".xml");
					str=XmlUtils.getXmlWithoutHeader(str, "<?xml");
					FileUtil.writeToFile(file, "<?xml version='1.0' encoding='UTF-8'?>"+str);
					JSFUtils.downloadFile(file, MimeTypeKeys.XML);
				} else {
					//выгружаем запрос как текстовый файл, только для ОКБ
					file=File.createTempFile("upload", ".txt");
					FileUtil.writeToFile(file, str);
					JSFUtils.downloadFile(file, MimeTypeKeys.TEXT);
				}
							
			} catch (IOException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			}
	    }
	 
	 public void downloadReport(){
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
	    	try {
	    		
				String str=uploading.getReport();
				File file=File.createTempFile("upload", ".xml");
				if (uploading.getReport().contains("xml")){
					//выгружаем ответ как xml файл, в кодировке utf-8
					str=XmlUtils.getXmlWithoutHeader(str, "<?xml");
				} 
				FileUtil.writeToFile(file, "<?xml version='1.0' encoding='UTF-8'?>"+str);
				JSFUtils.downloadFile(file, MimeTypeKeys.XML);			
			} catch (IOException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			}
	    }
	 
	 public void check(){
		 FacesContext facesCtx = FacesContext.getCurrentInstance();
		 UploadingEntity uploadingEntity=uploadingDAO.getUploadingEntity(reqId);
		 //русский стандарт
		 if (uploading.getPartner().getId()==Partner.RSTANDARD){
			 try {
				 
				rsBean.checkUploadStatus(uploadingEntity,isWork);
			 } catch (KassaException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			 }
		 }
		 //скб
		 if (uploading.getPartner().getId()==Partner.SKB){
			 
			 try {
				skbBean.checkUploadStatus(uploadingEntity,isWork);
			 } catch (KassaException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			 }
		 }
		 //эквифакс
		 if (uploading.getPartner().getId()==Partner.EQUIFAX){
			 
			 try {
				 equifaxBean.checkUploadStatus(uploadingEntity,isWork);
			 } catch (KassaException e) {
				 JSFUtils.handleAsError(facesCtx, null, e);
			 }
		 }
		//нбки
		 if (uploading.getPartner().getId()==Partner.NBKI){
			 
			 try {
				 
				nbki.checkUploadStatus(uploadingEntity,isWork);
			 } catch (KassaException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			 }
		 }
	 }

	 public void upload(){
		 FacesContext facesCtx = FacesContext.getCurrentInstance();
		 if (uploading.getPartner().getId()==Partner.RSTANDARD){
			 
			 try {
				rsBean.uploadHistory(uploading.getEntity(), sendingDate, isWork);
			 } catch (KassaException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			 }
		 }
		 if (uploading.getPartner().getId()==Partner.SKB){
			 
			 try {
				skbBean.uploadHistory(uploading.getEntity(), sendingDate, isWork);
			 } catch (KassaException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			 }
		 }
		 if (uploading.getPartner().getId()==Partner.EQUIFAX){
			 
			 try {
				 equifaxBean.uploadHistory(uploading.getEntity(), sendingDate, isWork);
			 } catch (KassaException e) {
				 JSFUtils.handleAsError(facesCtx, null, e);
			 }
		 }
		 if (uploading.getPartner().getId()==Partner.OKB_CAIS){
			 
			 try {
				 okbBean.uploadHistory(uploading.getEntity(), sendingDate, isWork);
			 } catch (KassaException e) {
				 JSFUtils.handleAsError(facesCtx, null, e);
			 }
		 }
		 if (uploading.getPartner().getId()==Partner.NBKI){
			 
			 try {
				 
				nbki.uploadHistory(uploading.getEntity(), sendingDate, isWork);
			 } catch (KassaException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			 }
		 }
	 }
	 
	public Boolean getIsWork() {
		return isWork;
	}

	public void setIsWork(Boolean isWork) {
		this.isWork = isWork;
	}
	 
	public void changeWork(ValueChangeEvent event){
		isWork=(Boolean) event.getNewValue();
		logger.info("Сервис рабочий "+isWork);
	}
	
	public void changeDate(ValueChangeEvent event){
		sendingDate=(Date) event.getNewValue();
		logger.info("Дата выгрузки "+sendingDate);
	}
	
	public Date getSendingDate() {
		return sendingDate;
	}

	public void setSendingDate(Date sendingDate) {
		this.sendingDate = sendingDate;
	}
}
