package ru.simplgroupp.webapp.analysis.requests.controller;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.ScoringEquifaxBeanLocal;
import ru.simplgroupp.interfaces.ScoringOkbCaisBeanLocal;
import ru.simplgroupp.interfaces.ScoringRsBeanLocal;
import ru.simplgroupp.interfaces.service.RequestsService;
import ru.simplgroupp.persistence.RequestsEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ParseRequestsController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1610291180365957565L;
	private static final Logger logger = LoggerFactory.getLogger(ParseRequestsController.class);
	
	@EJB
	protected RequestsService requestsService;
	
	@EJB 
	protected CreditDAO creditDAO;
	
	/**
	 * партнер
	 */
	protected Integer partnerId=0;
	/**
	 * название ejb
	 */
	protected String ejbName;
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
	 * всего запросов
	 */
	protected Integer requestsSize;
	/**
	 * № текущего запроса
	 */
	protected Integer requestCurrent=0;
	
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

	public void changePartner(ValueChangeEvent event){
		partnerId=Convertor.toInteger(event.getNewValue());
		logger.info("Партнер "+partnerId);  
  }
	
	public void pasre(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		logger.info("Разбираем запросы партнера "+partnerId);
		if (partnerId!=null&&partnerId>0){
			logger.info("Удаляем кредиты партнера "+partnerId);
			//удалим кредиты, которые были в БД по запросам к этому партнеру
			try {
				creditDAO.removePartnerCredits(partnerId);
			} catch (KassaException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			}
			logger.info("Удалили кредиты партнера "+partnerId);
			List<RequestsEntity> listRequests=requestsService.listPartnersRequests(partnerId);
			requestsSize=listRequests.size();
			logger.info("Всего запросов "+requestsSize);
			requestCurrent=0;
			//если есть запросы этого партнера, будем их разбирать
			if (requestsSize>0){
				for (RequestsEntity req:listRequests){
				  requestCurrent+=1;	
				  switch (partnerId) {
		             case (13):{
		      	 
		             	try {
							rsBean.saveAnswer(req, req.getResponsebodystring());
						} catch (Exception e) {
							logger.error("Не удалось разобрать ответ РС "+req.getId()+", ошибка "+e);
						}
		       	 
		       	       break;
		             }
		             case (4):{
		    	  
		                try {
							equifaxBean.saveAnswer(req, req.getResponsebodystring());
						} catch (Exception e) {
							logger.error("Не удалось разобрать ответ Эквифакса "+req.getId()+", ошибка "+e);
						}
		          
		       	         break;
		             }
		             case (5):{
		    	  
		                try {
							okbBean.saveAnswer(req, req.getResponsebodystring());
						} catch (Exception e) {
							logger.error("Не удалось разобрать ответ ОКБ "+req.getId()+", ошибка "+e);
						}
		           
		       	         break;
		             }
		             default:break;
			       }
				}//end for
				logger.info("Закончился разбор запросов по партнеру "+partnerId);
			}//end list.size>0
		}//end if partner not null
	}

	public Integer getRequestsSize() {
		return requestsSize;
	}

	public void setRequestsSize(Integer requestsSize) {
		this.requestsSize = requestsSize;
	}

	public Integer getRequestCurrent() {
		return requestCurrent;
	}

	public void setRequestCurrent(Integer requestCurrent) {
		this.requestCurrent = requestCurrent;
	}
	
	
}
