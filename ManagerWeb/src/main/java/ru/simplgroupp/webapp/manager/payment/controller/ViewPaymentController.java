package ru.simplgroupp.webapp.manager.payment.controller;

import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.plugins.payment.ContactPayPluginConfig;
import ru.simplgroupp.interfaces.ActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.ContactPayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.WinpayPayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.YandexPayBeanLocal;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.util.XmlUtils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.PluginExecutionContext;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class ViewPaymentController extends AbstractSessionController implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7554662992755432953L;

	private static final Logger logger = LoggerFactory.getLogger(ViewPaymentController.class.getName());
	
	@EJB
	protected PaymentService paymentService;

	@EJB
    private ActionProcessorBeanLocal actProc;
	
    @EJB
    private PaymentDAO payDAO;	
	
    @EJB(beanName="ContactPayBean")
	protected ContactPayBeanLocal contactPay;
    
    @EJB(beanName="YandexPayBean")
	protected YandexPayBeanLocal yandexPay;
    
    @EJB(beanName="WinpayPayBean")
	protected WinpayPayBeanLocal winpayPay;
    
	protected Integer paymentId;

	protected Payment payment;

    protected CreditRequest creditRequest;

    /**
     * ответ от платежного сервиса, если пытаемся запросить статус
     */
    protected String response;
    /**
     * статус строкой
     */
    protected String status;
    
    @PostConstruct
	public void init() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (paymentId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("id")) {
				   paymentId = Convertor.toInteger(prms.get("id"));
				}				
			}
			if (paymentId != null) {
				reloadCreditRequest(paymentId);
			}			
		}
	}
	
	protected void reloadCreditRequest(Integer prmId) {
        payment = payDAO.getPayment(prmId, Utils.setOf(Credit.Options.INIT_CREDIT_REQUEST
                , PeopleMain.Options.INIT_PEOPLE_PERSONAL
                , PeopleMain.Options.INIT_DOCUMENT
                , PeopleMain.Options.INIT_ADDRESS
                , PeopleMain.Options.INIT_EMPLOYMENT
                , PeopleMain.Options.INIT_PEOPLE_CONTACT
        ));
        creditRequest = payment.getCredit().getCreditRequest();
	}

	public Payment getPayment() {
		return payment;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

    public CreditRequest getCreditRequest() {
        return creditRequest;
    }

    public String cancel() {
        return "canceled";
    }

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
    
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void checkStatus(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		status="неизвестно";
		ActionContext context = actProc.createActionContext(null, true);
		//если это контакт
		if (payment.getPartner().getId()==Partner.CONTACT){
			
			if (payment.getPaymentType().getCodeInteger()==Payment.FROM_SYSTEM){
			  PluginExecutionContext plctx = new PluginExecutionContext(context.getPlugins().getPluginConfig(ContactPayBeanLocal.SYSTEM_NAME), null, 0, Collections.<String, Object>emptyMap(), context.getPluginState(ContactPayBeanLocal.SYSTEM_NAME));
	          contactPay = context.getPlugins().getContactPay();
	          try {
	            response=contactPay.doGetPaymentInfoString(getPaymentId(),(ContactPayPluginConfig) plctx.getPluginConfig());
	            Document doc=XmlUtils.getDocFromString(response);
	            if (doc!=null){
	            	Integer state=Convertor.toInteger(XmlUtils.getAttrubuteValue(XmlUtils.findElement(true, doc, "RESPONSE"),"STATE"));
	            	if (state!=null){
	            		switch (state){
	            		  case 0: {
	            			  status="Новый перевод, оплаченный отправителем";
	            			  break;
	            		  }
	            		  case 1: {
	            			  status="Перевод обрабатывается Клиринговым центром";
	            			  break;
	            		  }
	            		  case 2: {
	            			  status="Перевод приостановлен Клиринговым центром";
	            			  break;
	            		  }
	            		  case 3: {
	            			  status="Перевод отправлен в банк получателя";
	            			  break;
	            		  }
	            		  case 4: {
	            			  status="Перевод готов к выплате получателю";
	            			  break;
	            		  }
	            		  case 5: {
	            			  status="Перевод выплачивается получателю";
	            			  break;
	            		  }
	            		  case 6: {
	            			  status="Перевод выплачен получателю";
	            			  break;
	            		  }
	            		  case 7: {
	            			  status="Запрос на возврат отправлен в банк отправителя";
	            			  break;
	            		  }
	            		  case 8: {
	            			  status="Возвращённый перевод готов к выплате отправителю, но еще не выплачен";
	            			  break;
	            		  }
	            		  case 9: {
	            			  status="Возвращённый перевод выплачивается отправителю";
	            			  break;
	            		  }
	            		  case 10: {
	            			  status="Возвращённый перевод выплачен отправителю";
	            			  break;
	            		  }
	            		  case 101: {
	            			  status="Новый перевод, ожидает оплаты отправителем";
	            			  break;
	            		  }
	            		  case 100: {
	            			  status="Новый перевод, ожидает печати документов";
	            			  break;
	            		  }
	            		  default: {
	            			  status="неизвестный статус";
	            			  break;
	            		  }
	            		}
	            	}
	              }
	           } catch (Exception e) {
	        	  logger.error("Не удалось запросить статус платежа по Контакту, ошибка "+e.getMessage());
	        	  JSFUtils.handleAsError(facesCtx, null, e);
	          }
			} else {
				 status="нельзя запросить статус платежа клиента в систему";
			}
		}
		//если это яндекс
		if (payment.getPartner().getId()==Partner.YANDEX){
			if (payment.getPaymentType().getCodeInteger()==Payment.FROM_SYSTEM){
			    PluginExecutionContext plctx = new PluginExecutionContext(context.getPlugins().getPluginConfig(YandexPayBeanLocal.SYSTEM_NAME), null, 0, Collections.<String, Object>emptyMap(), context.getPluginState(YandexPayBeanLocal.SYSTEM_NAME));
		        yandexPay = context.getPlugins().getYandexPay();
		        try {
		             response=yandexPay.status(getPaymentId(), plctx);
		             Document doc=XmlUtils.getDocFromString(response);
		             if (doc!=null){
		        	     Integer state=Convertor.toInteger(XmlUtils.getAttrubuteValue(doc.getElementsByTagName("testDepositionResponse").item(0), "status"));
		        	     if (state!=null){
		        	    	 switch (state){
		            		  case 0: {
		            			  status="Перевод отправленный получателю";
		            			  break;
		            		  }
		            		  case 1: {
		            			  status="Ошибка при совершении платежа";
		            			  break;
		            		  }   
		            		  default: {
		            			  status="неизвестный статус";
		            			  break;
		            		  }
		            		}
		        	     }
		             }
		         
		        } catch (Exception e) {
		       	    logger.error("Не удалось запросить статус платежа по Яндексу, ошибка "+e.getMessage());
		      	    JSFUtils.handleAsError(facesCtx, null, e);
		        }
			} else {
				status="нельзя запросить статус платежа клиента в систему";
			}
		}
		//если это winpay
		if (payment.getPartner().getId()==Partner.WINPAY){
			
			if (payment.getPaymentType().getCodeInteger()==Payment.FROM_SYSTEM){
			  PluginExecutionContext plctx = new PluginExecutionContext(context.getPlugins().getPluginConfig(WinpayPayBeanLocal.SYSTEM_NAME), null, 0, Collections.<String, Object>emptyMap(), context.getPluginState(WinpayPayBeanLocal.SYSTEM_NAME));
			  winpayPay = context.getPlugins().getWinpayPay();
		      try {
		         response=winpayPay.status(getPaymentId(), plctx);
		         Document doc=XmlUtils.getDocFromString(response);
		         if (doc!=null){
		            String state=XmlUtils.getNodeValueText(XmlUtils.findChildInNode(doc, "response", "result", 0));
		            if (state.equalsIgnoreCase("PAID")){
		            	status="оплачено";
		            } else if (state.equalsIgnoreCase("NOT_PAID")){
		            	status="не оплачено";
		            } else if (state.equalsIgnoreCase("PROCESSING")){
		            	status="в обработке";
		            } else if (state.equalsIgnoreCase("Not enough money at account balance")){
		            	status="недостаточно денег на счете";
		            } else {
		            	status="неизвестно";
		            }
		         }
		        
		      } catch (Exception e) {
		       	  logger.error("Не удалось запросить статус платежа по WinPay, ошибка "+e.getMessage());
		      	  JSFUtils.handleAsError(facesCtx, null, e);
		      }
			} else {
				status="нельзя запросить статус платежа клиента в систему";
			}
		}
		
	}
}
