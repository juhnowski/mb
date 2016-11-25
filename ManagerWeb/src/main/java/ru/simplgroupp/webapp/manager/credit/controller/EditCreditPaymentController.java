package ru.simplgroupp.webapp.manager.credit.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class EditCreditPaymentController extends AbstractSessionController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3335881401853257217L;
	
	protected Integer prmPaymentId;
	protected Integer prmCreditId;
	protected Integer prmPeopleId;
	
	protected Payment payment;
	
	@EJB
	PaymentService payServ;
	
    @EJB
    PaymentDAO payDAO;	

	@PostConstruct
	public void init() {
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (prmPaymentId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("payid")) {
					String ss = prms.get("payid");
					prmPaymentId = Convertor.toInteger(ss);
				}	
				if (prms.containsKey("creditid")) {
					String ss = prms.get("creditid");
					prmCreditId = Convertor.toInteger(ss);
				}
				if (prms.containsKey("peopleid")) {
					String ss = prms.get("peopleid");
					prmPeopleId = Convertor.toInteger(ss);
				}				
			}
			if (prmPaymentId != null) {
				reloadPayment(facesCtx, prmPaymentId);
			}				
		}
	}
	
	public List<Account> getAccounts() {
		return payServ.listAccounts(prmPeopleId);
	}

	private void reloadPayment(FacesContext facesCtx, Integer prmPaymentId2) {
		payment = payDAO.getPayment(prmPaymentId2, Utils.setOf());
	}

	public Integer getPrmPaymentId() {
		return prmPaymentId;
	}

	public void setPrmPaymentId(Integer prmPaymentId) {
		this.prmPaymentId = prmPaymentId;
	}

	public Payment getPayment() {
		return payment;
	}
	
	public String save() {
		payServ.savePaymentWithAccount(payment);
		return "edit_cr?faces-redirect=true&creditid=" + prmCreditId.toString();
	}
	
	public String cancel() {
		return "edit_cr?faces-redirect=true&creditid=" + prmCreditId.toString();
	}
	
}
