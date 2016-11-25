package ru.simplgroupp.webapp.manager.credit.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.ActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.CreditBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.BaseCredit;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class NewPaymentToController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6917311740181738775L;

	private Integer prmCreditId;
	
	private Credit credit;
	private Integer prmAccountId;
	private Double prmAmount;
	private Date prmCreateDate;
	private Integer prmPaySumTypeCode;
	
	@EJB
	KassaBeanLocal kassaBean;
	
	@EJB
	CreditBeanLocal creditBean;	
	
    @EJB
    CreditDAO creditDAO;	
	
	@EJB
	ActionProcessorBeanLocal actProc;
	
	@PostConstruct
	public void init() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			try {
				if (prmCreditId == null) {
					Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
					if (prms.containsKey("creditid")) {
						prmCreditId = Convertor.toInteger(prms.get("creditid"));
					}		
				}
				if (prmCreditId != null) {
					reloadCredit(facesCtx, prmCreditId);
				}
			} catch (KassaException e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			}
		}		
	}
	
	public List<Account> getAccounts() {
		return credit.getPeopleMain().getAccounts();
	}
	
    protected void reloadCredit(FacesContext facesCtx, Integer prmId) throws KassaException {
        credit = creditDAO.getCredit(prmId, Utils.setOf( PeopleMain.Options.INIT_ACCOUNT, BaseCredit.Options.INIT_PEOPLE));
            
        prmAmount = credit.getCreditSum();
        prmCreateDate = new Date();
        prmPaySumTypeCode = Payment.MAIN_SUM_TO_CLIENT;

    }
    
    public List<SelectItem> getPaySumTypes() {
    	ArrayList<SelectItem> lst = new ArrayList<SelectItem>(2);
    	lst.add(new SelectItem(Payment.MAIN_SUM_TO_CLIENT, "Основная сумма, выплаченная клиенту"));
    	lst.add(new SelectItem(Payment.MONEY_BACK, "Возврат"));
    	return lst;
    }
    
    public String create() {
    	
    	actProc.createPaymentToClient(credit.getId(), prmAccountId, prmAmount, prmCreateDate, prmPaySumTypeCode);
    	// TODO
    	return "/views/credit/index?faces-redirect=true";
    }
    
    public String cancel() {
    	return "/views/credit/index?faces-redirect=true";
    }

	public Integer getPrmCreditId() {
		return prmCreditId;
	}

	public void setPrmCreditId(Integer prmCreditId) {
		this.prmCreditId = prmCreditId;
	}

	public Credit getCredit() {
		return credit;
	}

	public Integer getPrmAccountId() {
		return prmAccountId;
	}

	public void setPrmAccountId(Integer prmAccountId) {
		this.prmAccountId = prmAccountId;
	}

	public Double getPrmAmount() {
		return prmAmount;
	}

	public void setPrmAmount(Double prmAmount) {
		this.prmAmount = prmAmount;
	}

	public Date getPrmCreateDate() {
		return prmCreateDate;
	}

	public void setPrmCreateDate(Date prmCreateDate) {
		this.prmCreateDate = prmCreateDate;
	}

	public Integer getPrmPaySumTypeCode() {
		return prmPaySumTypeCode;
	}

	public void setPrmPaySumTypeCode(Integer prmPaySumTypeCode) {
		this.prmPaySumTypeCode = prmPaySumTypeCode;
	}	
}
