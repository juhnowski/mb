package ru.simplgroupp.webapp.analysis.requests.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.interfaces.service.RequestsService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.FileUtil;
import ru.simplgroupp.transfer.Requests;
import ru.simplgroupp.util.MimeTypeKeys;
import ru.simplgroupp.util.XmlUtils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ViewRequestsController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2812791338371760953L;

	@EJB
	protected RequestsService requestsService;
	
	protected Integer reqId;
	protected Requests request;
	
	 @PostConstruct
	 public void init() 
		{
			FacesContext facesCtx = FacesContext.getCurrentInstance();
			if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
				if (reqId == null) {
					Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
					if (prms.containsKey("reqid")) {
						String ss = prms.get("reqid");
						reqId = Convertor.toInteger(ss);
					}
				}
				if (reqId != null) {
					reloadItem(facesCtx, reqId);
				}
			}
		}
		
		protected void reloadItem(FacesContext facesCtx, Integer refId) {
			request=requestsService.getRequests(refId);
		}

		public Integer getReqId() {
			return reqId;
		}

		public void setReqId(Integer reqId) {
			this.reqId = reqId;
		}

		public Requests getRequest() {
			return request;
		}
		
		public String close(){
			return "close";
		}
		
		 public void download(){
		    	FacesContext facesCtx = FacesContext.getCurrentInstance();
		    	try {
		    		//выгружаем запрос как xml файл, в кодировке utf-8
					File file = File.createTempFile("request", ".xml");
					String str=request.getResponseBodyString();
					if (request.getResponseBodyString().contains("xml")){
						str=XmlUtils.getXmlWithoutHeader(str, "<?xml");
					} 
					FileUtil.writeToFile(file, "<?xml version='1.0' encoding='UTF-8'?>"+str);
					JSFUtils.downloadFile(file, MimeTypeKeys.XML);
					
				} catch (IOException e) {
					JSFUtils.handleAsError(facesCtx, null, e);
				}
		    }
}
