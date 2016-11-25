package ru.simplgroupp.webapp.manager.payment.controller;

import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EditPaymentVerificationController extends AbstractSessionController implements Serializable {

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
				if (prms.containsKey("prmBusinessObjectId")) {
					prmPaymentId = Convertor.toInteger(prms.get("prmBusinessObjectId"));
					reloadPayment(facesCtx, prmPaymentId);
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
		prmCreditId = payment.getCredit().getId();
		prmPeopleId = payment.getCredit().getPeopleMain().getId();
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
		return "index?faces-redirect=true";
	}

	public String cancel() {
		return "index?faces-redirect=true";
	}

	public Integer getPrmPeopleId() {
		return prmPeopleId;
	}

	public void setPrmPeopleId(Integer prmPeopleId) {
		this.prmPeopleId = prmPeopleId;
	}
	
	
}
