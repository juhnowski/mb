package ru.simplgroupp.webapp.manager.creditrequest.controller;

import ru.simplgroupp.dao.interfaces.AntifraudSuspicionDAO;
import ru.simplgroupp.dao.interfaces.CallsDAO;
import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.dao.interfaces.ProductDAO;
import ru.simplgroupp.interfaces.*;

import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Call;
import ru.simplgroupp.transfer.AIModel;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.PhonePaySummary;
import ru.simplgroupp.util.ProductKeys;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ViewCreditRequestController extends AbstractSessionController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -698394068981942650L;

    @EJB
    protected KassaBeanLocal kassa;

    @EJB
    protected WorkflowBeanLocal workflow;

    @EJB
    protected ActionProcessorBeanLocal actProc;

    @EJB
    protected AntifraudSuspicionDAO antifraudSuspicionDAO;

    @EJB
    protected ReferenceBooksLocal referenceBooks;

    @EJB
    protected CreditRequestDAO crDAO;

    @EJB
    protected CallsDAO callsDAO;

    protected Integer prmCreditRequestId;

    protected CreditRequest creditRequest;

    protected List<Call> callList;

	@EJB
	ModelBeanLocal modelBean;
	
	@EJB
	ProductBeanLocal productBean;
	
	@EJB
	private CreditCalculatorBeanLocal creditCalc;
	
	@EJB
	ProductDAO productDAO;
	
	protected List<Map> lastCalcValues;

	protected AIModel lastModel;	
	protected Double creditSumMin;
	protected Double creditSumMax;
	
    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            if (prmCreditRequestId == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("ccRequestId")) {
                    prmCreditRequestId = Convertor.toInteger(prms.get("ccRequestId"));
                }
            }
            if (prmCreditRequestId != null) {
                reloadCreditRequest(facesCtx, prmCreditRequestId);
                reloadCalls();
            }
        }
    }

    protected void reloadCreditRequest(FacesContext facesCtx, Integer prmId) {
        try {
            creditRequest = crDAO.getCreditRequest(prmId, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL,
                    PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_PEOPLE_CONTACT_ALL,
                    PeopleMain.Options.INIT_PEOPLE_MISC, PeopleMain.Options.INIT_ADDRESS, PeopleMain.Options.INIT_DOCUMENT,
                    PeopleMain.Options.INIT_EMPLOYMENT, CreditRequest.Options.INIT_CREDIT,
                    CreditRequest.Options.INIT_CREDIT_HISTORY, CreditRequest.Options.INIT_EMPLOYMENT,
                    CreditRequest.Options.INIT_LOGS, CreditRequest.Options.INIT_ADDRESS, PeopleMain.Options.INIT_BLACKLIST,
                    CreditRequest.Options.INIT_DOCUMENT, CreditRequest.Options.INIT_REQUESTS,PeopleMain.Options.INIT_CREDIT,
                    CreditRequest.Options.INIT_SCORING, PeopleMain.Options.INIT_SPOUSE, CreditRequest.Options.INIT_ANSWERS,
                    CreditRequest.Options.INIT_VERIF, PeopleMain.Options.INIT_NEGATIVE, CreditRequest.Options.INIT_BEHAVIOR,
                    CreditRequest.Options.INIT_BEHAVIORSOURCE, PeopleMain.Options.INIT_DEBT, PeopleMain.Options.INIT_DOCUMENTMEDIA,
                    CreditRequest.Options.INIT_PHONESUMMARY, CreditRequest.Options.INIT_OFFICIAL_DOCUMENT,
                    CreditRequest.Options.INIT_ANTIFRAUD, CreditRequest.Options.INIT_ANTIFRAUD_SUSPICION,
                    CreditRequest.Options.INIT_ANTIFRAUD_OCCASION, PeopleMain.Options.INIT_CREDIT_REQUEST,
                    PhonePaySummary.Options.INIT_PHONEPAYS,CreditRequest.Options.INIT_SUMMARY));

			lastCalcValues = modelBean.listLastParamValues(CreditRequest.class.getName(), prmId);
			if (lastCalcValues.size() > 0) {
				lastModel = modelBean.getLastModel((Integer) (lastCalcValues.get(0).get("aiModelParamId")));
			} else {
				lastModel = null;
			}          
			Integer productId=null;
			if (getCreditRequest().getProduct()!=null){
				productId=getCreditRequest().getProduct().getId();
			} else if (productDAO.getProductDefault()!=null){
				productId=productDAO.getProductDefault().getId();
			}
			if (productId!=null){					
			    Map<String, Object> limits =productBean.getNewRequestProductConfig(productId);
			    Map<String, Object> cparams = creditCalc.calcCreditParams(getCreditRequest().getPeopleMain().getId(), limits);
		        creditSumMin=Convertor.toDouble(cparams.get(ProductKeys.CREDIT_SUM_MIN));
		        creditSumMax=Convertor.toDouble(cparams.get(ProductKeys.CREDIT_SUM_MAX));
			} else {
				creditSumMin=new Double(1000);
				creditSumMax=new Double(10000);
			}
        } catch (Exception e) {
            JSFUtils.handleAsError(facesCtx, null, e);
        }

    }

    protected void reloadCalls() {
        List<Call> calls = callsDAO.getCallsByPeople(creditRequest.getPeopleMain().getId(),
                Utils.setOf(Call.Options.INIT_USER, PeopleMain.Options.INIT_PEOPLE_PERSONAL));

        if (calls.size() > 0) {
            callList = calls;
        }
    }

    public CreditRequest getCreditRequest() {
        return creditRequest;
    }

    public Integer getPrmCreditRequestId() {
        return prmCreditRequestId;
    }

    public void setPrmCreditRequestId(Integer prmCreditRequestId) {
        this.prmCreditRequestId = prmCreditRequestId;
    }

    public List<Call> getCallList() {
        return callList;
    }

	public List<Map> getLastCalcValues() {
		return lastCalcValues;
	}

	public AIModel getLastModel() {
		return lastModel;
	}
	
	public Double getCreditSumMin() {
		return creditSumMin;
	}

	public void setCreditSumMin(Double creditSumMin) {
		this.creditSumMin = creditSumMin;
	}

	public Double getCreditSumMax() {
		return creditSumMax;
	}

	public void setCreditSumMax(Double creditSumMax) {
		this.creditSumMax = creditSumMax;
	}
}
