package ru.simplgroupp.webapp.manager.creditrequest.controller;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.OfficialDocumentsDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.MailBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.persistence.OfficialDocumentsEntity;
import ru.simplgroupp.transfer.OfficialDocuments;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.ProcessKeys;
import ru.simplgroupp.workflow.SignalRef;

public class EditTaskSignOfertaController extends EditCRActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2534232861119539846L;

	private static Logger logger = Logger.getLogger(EditTaskSignOfertaController.class.getName());
	
	@EJB
	AppBean appBean;
	
	@EJB
	private MailBeanLocal mailBean;
	
	@EJB
	private OfficialDocumentsDAO officialDocumentsDAO;
	
	@EJB
	private PeopleBeanLocal peopleBean;
	
	private String smsCode;
	private String sendedSmsCode;
	
	/**
	 * документ с офертой
	 */
	protected OfficialDocuments document;
	
	@PostConstruct
	public void init(){
		super.init();
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			 if (getCreditRequest()!=null&&getCreditRequest().getDateFill()!=null){
			      OfficialDocumentsEntity doc=officialDocumentsDAO.findOfficialDocumentCreditRequestDraft(getCreditRequest().getId(), 
			    		  getCreditRequest().getId(), OfficialDocuments.OFERTA_CREDIT);
			      //если нет документа для подписания, создаем
			      if (doc==null){
			    	  document=peopleBean.initOfficialDocument(getCreditRequest().getPeopleMain(), 
			    			  getCreditRequest().getId(),null,OfficialDocuments.OFERTA_CREDIT);
				      generateAgreement(new Date());
				
			      } else {
			          document=new OfficialDocuments(doc);
			      }
			  }
			
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Ошибка ", ex);
		    JSFUtils.handleAsError(facesCtx, null, ex);
		}	
		
	}
	
	public void signAgreement(ActionEvent event) {
		String readyAction = SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST_OFFLINE, null, ProcessKeys.MSG_ACCEPT);
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (StringUtils.isEmpty(smsCode)) {
            JSFUtils.handleAsError(facesCtx, "scode", new Exception("Необходимо ввести код СМС"));
        }
		try {
			if (document.getSmsCode().equals(smsCode)) {
				generateAgreement(getCreditRequest().getDateSign());
				document.setSmsCode(smsCode);
         	    document.setDocDate(getCreditRequest().getDateSign());
         	    document.setDocNumber(getCreditRequest().getUniqueNomer());
         	    officialDocumentsDAO.saveOfficialDocument(document);
			    appBean.ofertaSigned(getCreditRequest().getId(), readyAction);
			    facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, null, "actionexecuted");
			} else {
				JSFUtils.handleAsError(facesCtx, "scode", new KassaException("Неверный код СМС"));
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Ошибка ", ex);
		    JSFUtils.handleAsError(facesCtx, null, ex);
		}	
	}
	
	public void sendCodeListener(ActionEvent event) {
		 FacesContext facesCtx = FacesContext.getCurrentInstance();

	        try {

	          //  sendedSmsCode = GenUtils.genCode(6);
	        	sendedSmsCode = mailBean.generateCodeForSending();
	        	document.setSmsCode(sendedSmsCode);
	            getCreditRequest().setSmsCode(sendedSmsCode);
	            getCreditRequest().setDateSign(new Date());
	            logger.info("Sms code "+sendedSmsCode);
	            mailBean.sendSMSV2(getCreditRequest().getPeopleMain().getCellPhone().getValue(), "Код " + sendedSmsCode + ". Это код для подписания оферты на предоставление займа");

	        } catch (Exception ex) {
	            JSFUtils.handleAsError(facesCtx, null, new KassaException("Не удалось отправить смс. "));
	        }

	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getSendedSmsCode() {
		return sendedSmsCode;
	}

	public void setSendedSmsCode(String sendedSmsCode) {
		this.sendedSmsCode = sendedSmsCode;
	}
	
	public OfficialDocuments getDocument() {
		return document;
	}


	public void setDocument(OfficialDocuments document) {
		this.document = document;
	}

	private String generateAgreement(Date dateOferta) throws KassaException {
	    String agreement = kassaBean.generateAgreement(getCreditRequest(), dateOferta,1);
	    document.setDocText(agreement);
        kassaBean.changeAgreement(getCreditRequest(), dateOferta, agreement);
        return agreement;
    }
}
