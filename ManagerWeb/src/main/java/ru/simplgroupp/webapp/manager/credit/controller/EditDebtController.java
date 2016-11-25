package ru.simplgroupp.webapp.manager.credit.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.interfaces.service.CreditInfoService;
import ru.simplgroupp.persistence.CreditEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.BaseCredit;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.Debt;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class EditDebtController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8074535053340991265L;
	
	@EJB
	CreditInfoService creditInfo;
	
	@EJB
	CreditDAO creditDAO;
	
	protected Debt debt;
	
	Integer prmCreditId;
	Credit credit;
	
	@PostConstruct
	public void init() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			
				if (prmCreditId == null) {
					Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
					if (prms.containsKey("creditId")) {
						prmCreditId = Convertor.toInteger(prms.get("creditId"));
					}		
				}
				if (prmCreditId != null){
					reloadCredit(facesCtx,prmCreditId);
				}
		}		
	}

	private void reloadCredit(FacesContext facesCtx,Integer id){
		try {
			credit = creditDAO.getCredit(id, Utils.setOf(BaseCredit.Options.INIT_DEBTS));
			debt=credit.getCourtDebt();
		} catch (Exception e) {
			JSFUtils.handleAsError(facesCtx, null, e);
		}
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

	public Debt getDebt() {
		return debt;
	}

	public void setDebt(Debt debt) {
		this.debt = debt;
	}

	public String save(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
		    creditInfo.saveDebt(debt);
		    if (debt.getDateDecision()!=null){
			    
			    CreditEntity creditEntity=creditDAO.getCreditEntity(credit.getId());
			    //ставим дату решения или все же текущую дату?
			    creditEntity.setDateStatus(debt.getDateDecision());
			    creditDAO.saveCreditEntity(creditEntity);
			   
		    }
		} catch (Exception e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		}
		return "edit?faces-redirect=true&creditid=" + prmCreditId.toString();
	}

	public String close(){
		return "edit?faces-redirect=true&creditid=" + prmCreditId.toString();
	}
}
