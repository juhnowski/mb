package ru.simplgroupp.webapp.manager.report.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.interfaces.ActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.plugins.payment.ContactPayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.WinpayPayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.YandexPayBeanLocal;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.transfer.Balance;
import ru.simplgroupp.transfer.CreditStatus;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.PluginExecutionContext;

public class StatisticsController extends AbstractSessionController  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3162630321938918033L;
	private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class.getName());
	
	@EJB
	protected KassaBeanLocal kassa;

	@EJB
	PaymentService paymentService;
	
	@EJB
	protected ReferenceBooksLocal refBook;
	
	@EJB
    private ActionProcessorBeanLocal actProc;
	
	@EJB(beanName="ContactPayBean")
	protected ContactPayBeanLocal contactPay;
    
    @EJB(beanName="YandexPayBean")
	protected YandexPayBeanLocal yandexPay;
    
    @EJB(beanName="WinpayPayBean")
	protected WinpayPayBeanLocal winpayPay;
    
	private Integer cnt_filled;
	private Integer cnt_rejected;
	private Integer cnt_court;
	private Integer cnt_decision;
	private Integer cnt_process;
	private Integer cnt_collector;
	private Integer cnt_closed;
	private Integer cnt_refused;
	
	private Double sum_out;
	private Double sum_back;
	private List<Balance> lstBalance;
	
	@PostConstruct
	public void init() {
		overview();
		reloadBalance();
	}
	
	private void overview() {
		Map<Integer,Integer> mp =kassa.overview();
		cnt_filled=mp.get(CreditStatus.FILLED);
		cnt_rejected=mp.get(CreditStatus.REJECTED);
		cnt_court=mp.get(CreditStatus.FOR_COURT);
		cnt_decision=mp.get(CreditStatus.DECISION);
		cnt_process=mp.get(CreditStatus.IN_PROCESS);
		cnt_collector=mp.get(CreditStatus.FOR_COLLECTOR);
		cnt_closed=mp.get(CreditStatus.CLOSED);
		cnt_refused=mp.get(CreditStatus.CLIENT_REFUSE);
				
		Map<Integer,Double> sm=kassa.overviewSum();
		sum_out=sm.get(1);
		sum_back=sm.get(2);
				
  }
	
	private void reloadBalance(){
		clearBalance();
		List<Reference> accountTypes=refBook.getAccountTypes();
		if (accountTypes.size()>0){
			for (Reference accountType:accountTypes){
			   Balance balance=paymentService.findLastBalance(null, accountType.getCodeInteger());
			   if (balance!=null){
				   lstBalance.add(balance);
			   }
			}
		}
	}
	
	public Integer getCnt_filled(){
		return cnt_filled;
	}
	public Integer getCnt_rejected(){
		return cnt_rejected;
	}
	public Integer getCnt_court(){
		return cnt_court;
	}
	public Integer getCnt_decision(){
		return cnt_decision;
	}
	public Integer getCnt_process(){
		return cnt_process;
	}
	public Integer getCnt_collector(){
		return cnt_collector;
	}
	public Integer getCnt_closed(){
		return cnt_closed;
	}
	public Integer getCnt_refused(){
		return cnt_refused;
	}
	
	public Double getSum_out(){
		return sum_out;
	}

	public Double getSum_back(){
		return sum_back;
	}

	public List<Balance> getLstBalance() {
		return lstBalance;
	}

	public void setLstBalance(List<Balance> lstBalance) {
		this.lstBalance = lstBalance;
	}

	private void clearBalance(){
		lstBalance=new ArrayList<Balance>(0);
	}
	
	public void newBalance(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		ActionContext context = actProc.createActionContext(null, true);
		//Yandex
		PluginExecutionContext plctx = new PluginExecutionContext(context.getPlugins().getPluginConfig(YandexPayBeanLocal.SYSTEM_NAME), null, 0, Collections.<String, Object>emptyMap(), context.getPluginState(YandexPayBeanLocal.SYSTEM_NAME));
        yandexPay = context.getPlugins().getYandexPay();
        try {
            yandexPay.balance(plctx);
         } catch (Exception e) {
        	logger.error("Не удалось получить баланс по Яндексу, ошибка "+e.getMessage());
            JSFUtils.handleAsError(facesCtx, null, e);
        }
        //WinPay
        plctx = new PluginExecutionContext(context.getPlugins().getPluginConfig(WinpayPayBeanLocal.SYSTEM_NAME), null, 0, Collections.<String, Object>emptyMap(), context.getPluginState(WinpayPayBeanLocal.SYSTEM_NAME));
		winpayPay = context.getPlugins().getWinpayPay();
        try {
			winpayPay.balance(plctx);
         } catch (Exception e) {
        	logger.error("Не удалось получить баланс по WinPay, ошибка "+e.getMessage());
        	JSFUtils.handleAsError(facesCtx, null, e);
        }
        //Contact
        plctx = new PluginExecutionContext(context.getPlugins().getPluginConfig(ContactPayBeanLocal.SYSTEM_NAME), null, 0, Collections.<String, Object>emptyMap(), context.getPluginState(ContactPayBeanLocal.SYSTEM_NAME));
        contactPay = context.getPlugins().getContactPay();
        try {
            contactPay.doCheckRest(plctx);
         } catch (Exception e) {
        	logger.error("Не удалось получить баланс по Контакту, ошибка "+e.getMessage());
        	JSFUtils.handleAsError(facesCtx, null, e);
        }
        reloadBalance();
	}
}
