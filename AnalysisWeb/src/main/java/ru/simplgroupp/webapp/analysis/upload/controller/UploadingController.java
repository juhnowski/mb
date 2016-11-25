package ru.simplgroupp.webapp.analysis.upload.controller;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import ru.simplgroupp.interfaces.ScoringEquifaxBeanLocal;
import ru.simplgroupp.interfaces.ScoringNBKIBeanLocal;
import ru.simplgroupp.interfaces.ScoringOkbCaisBeanLocal;
import ru.simplgroupp.interfaces.ScoringRsBeanLocal;
import ru.simplgroupp.interfaces.ScoringSkbBeanLocal;
import ru.simplgroupp.persistence.UploadingEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.util.MimeTypeKeys;
import ru.simplgroupp.util.XmlUtils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class UploadingController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1982374035475375966L;
	private static final Logger logger = Logger.getLogger(UploadingController.class.getName());
	
	/**
	 * партнер
	 */
	protected Integer partnerId;
	
	/**
	 * рабочий сервис или тестовый
	 */
	protected Boolean isWork=false;
	/**
	 * дата отправки - если пустая, то текущая
	 */
	protected Date sendingDate;
	/**
	 * коррекционный номер
	 */
	protected Integer corNomer;
	/**
	 * номера кредитов для коррекционной выгрузки
	 */
	protected String stringIds;
	/**
	 * таблица выгрузки
	 */
	protected UploadingEntity uploading;
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
	
	public Integer getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Integer partnerId) {
		this.partnerId = partnerId;
	}
	
	public ScoringRsBeanLocal getRsBean() {
		return rsBean;
	}

	public ScoringEquifaxBeanLocal getEquifaxBean() {
		return equifaxBean;
	}

	public ScoringOkbCaisBeanLocal getOkbBean() {
		return okbBean;
	}

	public ScoringSkbBeanLocal getSkbBean() {
		return skbBean;
	}

	public ScoringNBKIBeanLocal getNbki() {
		return nbki;
	}

	public Integer getCorNomer() {
		return corNomer;
	}

	public void setCorNomer(Integer corNomer) {
		this.corNomer = corNomer;
	}

	public void changePartner(ValueChangeEvent event){
		partnerId=Convertor.toInteger(event.getNewValue());
		logger.info("Партнер "+partnerId);  
	
    }

	public void changeDate(ValueChangeEvent event){
		sendingDate=(Date) event.getNewValue();
	}
	 
	public void changeNomer(ValueChangeEvent event){
		corNomer=Convertor.toInteger(event.getNewValue());
	}
	
	public void changeStringIds(ValueChangeEvent event){
		stringIds=(String) event.getNewValue();
	}
	
	public void changeWork(ValueChangeEvent event){
		isWork=(Boolean) event.getNewValue();
	}
	
   public void createTextForUpload() throws Exception{
	 logger.info("Партнер "+partnerId);  
	 logger.info("Дата "+sendingDate);
	 List<Integer> creditIds=new ArrayList<Integer>(0);
	 if (StringUtils.isNotEmpty(stringIds)){
		creditIds=Convertor.stringToIntegersList(stringIds); 
	 }
	 switch (partnerId) {
       case (1):{
    	   logger.info("Формируется файл для РС");
       	   //если выгружаем только заданные кредиты
       	   if (creditIds!=null&&creditIds.size()>0){
       		   uploading=rsBean.createFileForUpload(sendingDate, isWork,creditIds);
       	   } else {
       	       uploading=rsBean.createFileForUpload(sendingDate, isWork);
       	   }
       	 
       	 break;
       }
       case (2):{
    	   logger.info("Формируется файл для Эквифакс, кредиты");
    	   //если выгружаем только заданные кредиты
       	   if (creditIds!=null&&creditIds.size()>0){
       		   uploading=equifaxBean.createFileForUpload(sendingDate, isWork,creditIds); 
       	   } else {
               uploading=equifaxBean.createFileForUpload(sendingDate, isWork);
       	   }
          
       	   break;
       }
       case (3):{
    	   logger.info("Формируется файл для ОКБ");
    	   //если выгружаем только заданные кредиты
       	   if (creditIds!=null&&creditIds.size()>0){
               uploading=okbBean.createFileForUpload(sendingDate, isWork,creditIds);
       	   } else {
       		   uploading=okbBean.createFileForUpload(sendingDate, isWork); 
       	   }
           
       	   break;
       }
       case (4):{
    	   logger.info("Формируется файл для Эквифакс, кредитные заявки");
    	   uploading=equifaxBean.createFileForUploadCreditRequest(sendingDate, isWork);
          
       	   break;
       }
       case (5):{
    	   logger.info("Формируется файл для Эквифакс, коррекционный файл");
    	   uploading=equifaxBean.createCorrectionFileForUpload(sendingDate, corNomer, isWork);
          
       	   break;
       }
       case (6):{
    	   logger.info("Формируется файл для СКБ");
       	   //если выгружаем только заданные кредиты
       	   if (creditIds!=null&&creditIds.size()>0){
       		   uploading=skbBean.createFileForUpload(sendingDate, isWork,creditIds); 
       	   } else {
       	       uploading=skbBean.createFileForUpload(sendingDate, isWork);
       	   }
       	 
       	 break;
       }
       case (7):{
    	   logger.info("Формируется файл для Эквифакс, файл для удаления");
    	   uploading=equifaxBean.createDeleteFileForUpload(sendingDate, corNomer, isWork);
          
       	   break;
       }
       case (8):{
    	   logger.info("Формируется файл для НБКИ");
       	   //если выгружаем только заданные кредиты
       	   if (creditIds!=null&&creditIds.size()>0){
       		   uploading=nbki.createFileForUpload(sendingDate, isWork,creditIds);
       	   } else {
       	       uploading=nbki.createFileForUpload(sendingDate, isWork);
       	   }
       	 
       	 break;
       }
       default:break;
	 }
   }
   
   public void showTextForUpload() throws Exception{
		 switch (partnerId) {
	       case (1):
	       case (2):
	       case (4):
	       case (5):
	       case (6): {
	    	       	
	       	 if (uploading!=null){
	       	   File file = File.createTempFile("uploading", ".xml");
	       	   FileUtils.writeByteArrayToFile(file, uploading.getInfo().getBytes(XmlUtils.ENCODING_WINDOWS));
	       	   JSFUtils.downloadFile(file, MimeTypeKeys.XML);
	       	 }
	       	 break;
	       }
	      
	       case (3):{
	    	
	           if (uploading!=null){
	         	   File file = File.createTempFile("uploading", ".cds");
	         	   FileUtils.writeByteArrayToFile(file, uploading.getInfo().getBytes(XmlUtils.ENCODING_WINDOWS));
	         	   JSFUtils.downloadFile(file, MimeTypeKeys.TEXT);
	           }
	       	   break;
	       }
	       case (7):
	       case (8):{
	    	   if (uploading!=null){
	         	   File file = File.createTempFile("uploading", ".txt");
	         	   FileUtils.writeByteArrayToFile(file, uploading.getInfo().getBytes(XmlUtils.ENCODING_WINDOWS));
	         	   JSFUtils.downloadFile(file, MimeTypeKeys.TEXT);
	           }
	       	   break;
	       }
	       default:break;
		 }
	   }
   
   public void uploadKB() throws Exception{
	 switch (partnerId) {
		 case (1):{
	    	 rsBean.uploadHistory(uploading, sendingDate, isWork);
	       	 break;
	     }
	     case (2):
	     case (4):
	     case (5):
	     case (7):{
	        equifaxBean.uploadHistory(uploading, sendingDate, isWork);
	       	break;
	     }
	     case (3):{
	    	okbBean.uploadHistory(uploading, sendingDate, isWork); 
	       	break;
	     }
	     case (6):{
	    	 skbBean.uploadHistory(uploading, sendingDate, isWork);
	       	 break;
	     }
	     case (8):{
	    	 nbki.uploadHistory(uploading, sendingDate, isWork);
	       	 break;
	     }
	     default:break;
	  }
   }
 
	public Boolean getIsWork() {
	    return isWork;
    }

    public void setIsWork(Boolean isWork) {
	    this.isWork = isWork;
    }


	public Date getSendingDate() {
		return sendingDate;
	}

	public void setSendingDate(Date sendingDate) {
		this.sendingDate = sendingDate;
	}

	public UploadingEntity getUploading() {
		return uploading;
	}

	public void setUploading(UploadingEntity uploading) {
		this.uploading = uploading;
	}

	public String getStringIds() {
		return stringIds;
	}

	public void setStringIds(String stringIds) {
		this.stringIds = stringIds;
	}
	
}