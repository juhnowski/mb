package ru.simplgroupp.webapp.manager.creditrequest.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.WorkflowEngineBeanLocal;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.DateRange;
import ru.simplgroupp.toolkit.common.IntegerRange;
import ru.simplgroupp.toolkit.common.MoneyRange;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.Documents;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.util.UserUtil;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.controller.IEventListener;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.manager.creditrequest.model.AppBean;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.WorkflowObjectStateDef;

public class ListCreditRequestController extends AbstractListController implements Serializable, IEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	protected KassaBeanLocal kassa;
	
    @EJB
    protected WorkflowEngineBeanLocal wfEngine;	
	
    @EJB
	AppServiceBeanLocal appServ;
    
    @EJB
	AppBean appBean;
	
    @EJB
    CreditRequestDAO crDAO;
    
	protected Integer prmStatusId;
	/**
	 * номер заявки
	 */
	protected String prmUniqueNomer;
	protected String prmUniqueNomer1;
	protected String prmUniqueNomer2;
	/**
	 * дата заявки
	 */
	protected DateRange prmDateContest = new DateRange(null, null);	
	/**
	 * на сколько дней
	 */
	protected IntegerRange prmDays = new IntegerRange(null, null);
	/**
	 * какая сумма
	 */
	protected MoneyRange prmSums = new MoneyRange(null, null);
	/**
	 * причина отказа
	 */
	protected Integer prmRejectReasonId;
	/**
	 * одобрен 
	 */
	protected Integer prmAccepted;
	/**
	 * завершен
	 */
	protected Integer prmIsOver;
	/**
	 * дата окончания кредита
	 */
	protected DateRange prmCreditDateEnd = new DateRange(null, null);	
	/**
	 * фамилия
	 */
	protected String prmPeopleSurname;
	protected String prmPeopleSurname1;
	protected String prmPeopleSurname2;
	/**
	 * имя
	 */
	protected String prmPeopleName;
	protected String prmPeopleName1;
	protected String prmPeopleName2;
	/**
	 * отчество
	 */
	protected String prmPeopleMidname;
	
	protected Integer prmPeopleDocType;
	protected String prmPeopleDocSeries;
	protected String prmPeopleDocNomer;
	protected String prmPeopleEmail;
	protected String prmPeoplePhone;
	protected String prmPeoplePhone1;
	protected String prmPeoplePhone2;
	protected String prmPeopleSNILS;
	protected String prmPeopleINN;
	protected String prmTaskDef;
	protected Integer prmWayId;
	protected Boolean searchFull = false;
	
	protected List<WorkflowObjectStateDef> prmTaskDefValues;
	
//	protected ViewProcessStatesController psCtl;
	
	@PostConstruct
	public void init() {
		super.init();
	}
	
	@Override
	public void initData() throws Exception {
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
		if (prms.get("taskDef") != null) {
			prmTaskDef = prms.get("taskDef").replace('-', ':');			
		}
		
		List<String> roleNames = UserUtil.rolesToStrings(userCtl.getUser().getRoles());
		prmTaskDefValues = wfEngine.listTaskDefs(null, roleNames , CreditRequest.class.getName(), null);

	}
	
	public void clearLsn(ActionEvent event) {
		prmStatusId = null;
		prmUniqueNomer = null;
		prmDateContest = new DateRange(null, null);
		prmDays = new IntegerRange(null, null);
		prmSums = new MoneyRange(null, null);		
		prmRejectReasonId = null;
		prmWayId=null;
		prmAccepted = null;
		prmIsOver = null;
		prmCreditDateEnd = new DateRange(null, null);	
		prmPeopleSurname = null;
		prmPeopleSurname1 = null;
		prmPeopleSurname2 = null;
		prmPeopleName = null;
		prmPeopleName1 = null;
		prmPeopleName2 = null;
		prmPeopleMidname = null;
		prmUniqueNomer1 = null;
		prmUniqueNomer2 = null;
		prmPeopleDocType = null;
		prmPeopleDocNomer = null;
		prmPeopleEmail = null;
		prmPeoplePhone = null;
		prmPeoplePhone1 = null;
		prmPeoplePhone2 = null;
		prmPeopleSNILS = null;
		prmPeopleINN = null;
		
		refreshSearch();
	}
	
		
	public void deleteItem(ActionEvent event) {
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Integer id = 0;
		if ( event.getComponent().getAttributes().get("ccRequestId")!=null)	{
		 
		    id = Convertor.toInteger(event.getComponent().getAttributes().get("ccRequestId"));
		}
		if ( event.getComponent().getAttributes().get("prmBusinessObjectId")!=null)	{
			 
		    id = Convertor.toInteger(event.getComponent().getAttributes().get("prmBusinessObjectId"));
		}
		if (id!=0) {
		  try {
		    	
		    appBean.deleteCreditRequest(id);
		    refreshSearch();
		  } catch (KassaException e) {
			  JSFUtils.handleAsError(facesCtx, null, e);
		  }  catch (WorkflowException e) {
			  JSFUtils.handleAsError(facesCtx, null, e);
		  }
		}
	}
	
	protected void nullIfEmpty() {
		if (StringUtils.isBlank(prmUniqueNomer)) {
			prmUniqueNomer = null;
		}
		if (StringUtils.isBlank(prmPeopleSurname)) {
			prmPeopleSurname = null;
		}
		if (StringUtils.isBlank(prmPeopleSurname1)) {
			prmPeopleSurname1 = null;
		}
		if (StringUtils.isBlank(prmPeopleSurname2)) {
			prmPeopleSurname2 = null;
		}
		if (StringUtils.isBlank(prmPeopleName)) {
			prmPeopleName = null;
		}
		if (StringUtils.isBlank(prmPeopleName1)) {
			prmPeopleName1 = null;
		}
		if (StringUtils.isBlank(prmPeopleName2)) {
			prmPeopleName2 = null;
		}
		if (StringUtils.isBlank(prmPeopleMidname)) {
			prmPeopleMidname = null;
		}
		if (StringUtils.isBlank(prmUniqueNomer1)) {
			prmUniqueNomer1 = null;
		}
		if (StringUtils.isBlank(prmUniqueNomer2)) {
			prmUniqueNomer2 = null;
		}
		if (StringUtils.isBlank(prmPeopleDocNomer)) {
			prmPeopleDocNomer = null;
		}
		if (StringUtils.isBlank(prmPeopleDocSeries)) {
			prmPeopleDocSeries = null;
		}		
		if (StringUtils.isBlank(prmPeopleEmail)) {
			prmPeopleEmail = null;
		}	
		if (StringUtils.isBlank(prmPeoplePhone)) {
			prmPeoplePhone = null;
		}	
		if (StringUtils.isBlank(prmPeoplePhone1)) {
			prmPeoplePhone1 = null;
		}	
		if (StringUtils.isBlank(prmPeoplePhone2)) {
			prmPeoplePhone2 = null;
		}	
		if (StringUtils.isBlank(prmPeopleSNILS)) {
			prmPeopleSNILS = null;
		}	
		if (StringUtils.isBlank(prmPeopleINN)) {
			prmPeopleINN = null;
		}		
		if (JSFUtils.NULL_INT_VALUE.equals(prmStatusId)) {
			prmStatusId = null;
		}
		if (JSFUtils.NULL_INT_VALUE.equals(prmWayId)) {
			prmWayId = null;
		}
		if (JSFUtils.NULL_INT_VALUE.equals(prmRejectReasonId)) {
			prmRejectReasonId = null;
		}	
		if (JSFUtils.NULL_INT_VALUE.equals(prmAccepted)) {
			prmAccepted = null;
		}	
		if (JSFUtils.NULL_INT_VALUE.equals(prmIsOver)) {
			prmIsOver = null;
		}	
		if (JSFUtils.NULL_INT_VALUE.equals(prmPeopleDocType)) {
			prmPeopleDocType = null;
		}	
		if (prmDays != null && prmDays.getFrom() != null && prmDays.getFrom().intValue() == 0) {
			prmDays.setFrom(null);
		}
		if (prmDays != null && prmDays.getTo() != null && prmDays.getTo().intValue() == 0) {
			prmDays.setTo(null);
		}
		if (prmSums != null && prmSums.getFrom() != null && prmSums.getFrom().intValue() == 0) {
			prmSums.setFrom(null);
		}
		if (prmSums != null && prmSums.getTo() != null && prmSums.getTo().intValue() == 0) {
			prmSums.setTo(null);
		}		
		
	    if ( (prmPeopleDocNomer != null ) || (prmPeopleDocSeries != null ) ) {
			prmPeopleDocType = Documents.PASSPORT_RF;
		}
		
		if (StringUtils.isBlank(prmTaskDef)) {
			prmTaskDef = null;
		}
	}	

	public String dummy() {
		return null;
	}

	@Override
	public StdRequestDataModel<CreditRequest> createModel() {
		return new CreditRequestDataModel();
	}
	
	public class CreditRequestDataModel extends StdRequestDataModel<CreditRequest> {

		public CreditRequestDataModel() {
			super();
			multipleSorting = true;
		}
		
		@Override
		protected List<CreditRequest> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			
			Integer peopleMainId = null;
			prmPeopleSurname=StringUtils.isEmpty(prmPeopleSurname1)?prmPeopleSurname2:prmPeopleSurname1;
			prmPeopleName=StringUtils.isEmpty(prmPeopleName1)?prmPeopleName2:prmPeopleName1;
			prmUniqueNomer=StringUtils.isEmpty(prmUniqueNomer1)?prmUniqueNomer2:prmUniqueNomer1;
			prmPeoplePhone=StringUtils.isEmpty(prmPeoplePhone1)?prmPeoplePhone2:prmPeoplePhone1;
			return kassa.listCreditRequests(seqRange.getFirstRow(), seqRange.getRows(), sorting, null, peopleMainId, prmRejectReasonId, prmStatusId, prmAccepted, prmDateContest, prmDays, 
					prmSums, Utils.triStateToBoolean( prmIsOver ), prmCreditDateEnd, prmPeopleSurname, prmPeopleName, prmPeopleMidname, prmPeopleDocType, prmPeopleDocSeries, prmPeopleDocNomer, 
					prmPeopleEmail, prmPeoplePhone, prmPeopleSNILS, prmPeopleINN, prmUniqueNomer, prmTaskDef,prmWayId);
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			
			Integer peopleMainId = null;
			prmPeopleSurname=StringUtils.isEmpty(prmPeopleSurname1)?prmPeopleSurname2:prmPeopleSurname1;
			prmPeopleName=StringUtils.isEmpty(prmPeopleName1)?prmPeopleName2:prmPeopleName1;
			prmUniqueNomer=StringUtils.isEmpty(prmUniqueNomer1)?prmUniqueNomer2:prmUniqueNomer1;
			prmPeoplePhone=StringUtils.isEmpty(prmPeoplePhone1)?prmPeoplePhone2:prmPeoplePhone1;
			return kassa.countCreditRequests(peopleMainId, prmRejectReasonId, prmStatusId, prmAccepted, prmDateContest, prmDays, prmSums, Utils.triStateToBoolean( prmIsOver ), prmCreditDateEnd, prmPeopleSurname, 
					prmPeopleName, prmPeopleMidname, prmPeopleDocType, prmPeopleDocSeries, prmPeopleDocNomer, prmPeopleEmail, prmPeoplePhone, prmPeopleSNILS, prmPeopleINN, prmUniqueNomer, prmTaskDef,prmWayId);
		}

		@Override
		public CreditRequest getRowData() {
			if (rowKey == null)
				return null;
			else {
				CreditRequest ccRequest;
				try {
					ccRequest = crDAO.getCreditRequest(((Number) rowKey).intValue(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_CONTACT));
					return ccRequest;
				} catch (Exception e) {
					return null;
				}
	
			}
		}
		
	}

	public Integer getPrmStatusId() {
		return prmStatusId;
	}

	public void setPrmStatusId(Integer prmStatusId) {
		this.prmStatusId = prmStatusId;
	}

	public String getPrmUniqueNomer() {
		return prmUniqueNomer;
	}

	public void setPrmUniqueNomer(String prmUniqueNomer) {
		this.prmUniqueNomer = prmUniqueNomer;
	}

	public DateRange getPrmDateContest() {
		return prmDateContest;
	}

	public void setPrmDateContest(DateRange prmDateContest) {
		this.prmDateContest = prmDateContest;
	}

	public IntegerRange getPrmDays() {
		return prmDays;
	}

	public void setPrmDays(IntegerRange prmDays) {
		this.prmDays = prmDays;
	}

	public MoneyRange getPrmSums() {
		return prmSums;
	}

	public void setPrmSums(MoneyRange prmSums) {
		this.prmSums = prmSums;
	}

	public Integer getPrmRejectReasonId() {
		return prmRejectReasonId;
	}

	public void setPrmRejectReasonId(Integer prmRejectReasonId) {
		this.prmRejectReasonId = prmRejectReasonId;
	}

	public Integer getPrmAccepted() {
		return prmAccepted;
	}

	public void setPrmAccepted(Integer prmAccepted) {
		this.prmAccepted = prmAccepted;
	}

	public Integer getPrmIsOver() {
		return prmIsOver;
	}

	public void setPrmIsOver(Integer prmIsOver) {
		this.prmIsOver = prmIsOver;
	}

	public DateRange getPrmCreditDateEnd() {
		return prmCreditDateEnd;
	}

	public void setPrmCreditDateEnd(DateRange prmCreditDateEnd) {
		this.prmCreditDateEnd = prmCreditDateEnd;
	}

	public String getPrmPeopleSurname() {
		return prmPeopleSurname;
	}

	public void setPrmPeopleSurname(String prmPeopleSurname) {
		this.prmPeopleSurname = prmPeopleSurname;
	}

	public String getPrmPeopleSurname1() {
		return prmPeopleSurname1;
	}

	public void setPrmPeopleSurname1(String prmPeopleSurname1) {
		this.prmPeopleSurname1 = prmPeopleSurname1;
	}
	
	public String getPrmPeopleSurname2() {
		return prmPeopleSurname2;
	}

	public void setPrmPeopleSurname2(String prmPeopleSurname2) {
		this.prmPeopleSurname2 = prmPeopleSurname2;
	}
	
	public String getPrmPeopleName() {
		return prmPeopleName;
	}

	public void setPrmPeopleName(String prmPeopleName) {
		this.prmPeopleName = prmPeopleName;
	}

	public String getPrmPeopleName1() {
		return prmPeopleName1;
	}

	public void setPrmPeopleName1(String prmPeopleName1) {
		this.prmPeopleName1 = prmPeopleName1;
	}

	public String getPrmPeopleName2() {
		return prmPeopleName2;
	}

	public void setPrmPeopleName2(String prmPeopleName2) {
		this.prmPeopleName2 = prmPeopleName2;
	}

	public String getPrmPeopleMidname() {
		return prmPeopleMidname;
	}

	public void setPrmPeopleMidname(String prmPeopleMidname) {
		this.prmPeopleMidname = prmPeopleMidname;
	}

	public String getPrmUniqueNomer1() {
		return prmUniqueNomer1;
	}

	public void setPrmUniqueNomer1(String prmUniqueNomer1) {
		this.prmUniqueNomer1 = prmUniqueNomer1;
	}

	public String getPrmUniqueNomer2() {
		return prmUniqueNomer2;
	}

	public void setPrmUniqueNomer2(String prmUniqueNomer2) {
		this.prmUniqueNomer2 = prmUniqueNomer2;
	}

	public Integer getPrmPeopleDocType() {
		return prmPeopleDocType;
	}

	public void setPrmPeopleDocType(Integer prmPeopleDocType) {
		this.prmPeopleDocType = prmPeopleDocType;
	}

	public String getPrmPeopleDocNomer() {
		return prmPeopleDocNomer;
	}

	public void setPrmPeopleDocNomer(String prmPeopleDocNomer) {
		this.prmPeopleDocNomer = prmPeopleDocNomer;
	}

	public String getPrmPeopleDocSeries() {
		return prmPeopleDocSeries;
	}

	public void setPrmPeopleDocSeries(String prmPeopleDocSeries) {
		this.prmPeopleDocSeries = prmPeopleDocSeries;
	}

	public String getPrmPeopleEmail() {
		return prmPeopleEmail;
	}

	public void setPrmPeopleEmail(String prmPeopleEmail) {
		this.prmPeopleEmail = prmPeopleEmail;
	}

	public String getPrmPeoplePhone() {
		return prmPeoplePhone;
	}

	public void setPrmPeoplePhone(String prmPeoplePhone) {
		this.prmPeoplePhone = prmPeoplePhone;
	}

	public String getPrmPeoplePhone1() {
		return prmPeoplePhone1;
	}

	public void setPrmPeoplePhone1(String prmPeoplePhone1) {
		this.prmPeoplePhone1 = prmPeoplePhone1;
	}
	
	public String getPrmPeoplePhone2() {
		return prmPeoplePhone2;
	}

	public void setPrmPeoplePhone2(String prmPeoplePhone2) {
		this.prmPeoplePhone2 = prmPeoplePhone2;
	}
	
	public String getPrmPeopleSNILS() {
		return prmPeopleSNILS;
	}

	public void setPrmPeopleSNILS(String prmPeopleSNILS) {
		this.prmPeopleSNILS = prmPeopleSNILS;
	}

	public String getPrmPeopleINN() {
		return prmPeopleINN;
	}

	public void setPrmPeopleINN(String prmPeopleINN) {
		this.prmPeopleINN = prmPeopleINN;
	}
	
    public Integer getPrmWayId() {
		return prmWayId;
	}

	public void setPrmWayId(Integer prmWayId) {
		this.prmWayId = prmWayId;
	}

	
	@Override
	public void eventFired(String eventName, Object eventSource)
			throws Exception {
		if ("state.changed".equals(eventName)) {
//			psCtl = (ViewProcessStatesController) eventSource; 
//			processStateToCondition();
			refreshSearch();
		}
		// TODO Auto-generated method stub
		
	}

	public Boolean getSearchFull() {
		return searchFull;
	}

	public void setSearchFull(Boolean searchFull) {
		this.searchFull = searchFull;
	}

	public String getPrmTaskDef() {
		return prmTaskDef;
	}

	public void setPrmTaskDef(String prmTaskDef) {
		this.prmTaskDef = prmTaskDef;
	}

	public List<WorkflowObjectStateDef> getPrmTaskDefValues() {
		return prmTaskDefValues;
	}

}
