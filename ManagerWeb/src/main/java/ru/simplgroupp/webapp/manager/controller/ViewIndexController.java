package ru.simplgroupp.webapp.manager.controller;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.transfer.CreditStatus;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ViewIndexController extends AbstractSessionController  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	protected KassaBeanLocal kassa;

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
	
	public void init() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		overview();
	}
	
	public boolean getCanViewStat() {
		return this.userCtl.hasRole("admin");
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

}
