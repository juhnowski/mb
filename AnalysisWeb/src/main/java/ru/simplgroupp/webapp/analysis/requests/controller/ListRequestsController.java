package ru.simplgroupp.webapp.analysis.requests.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.interfaces.service.RequestsService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.DateRange;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.transfer.Requests;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListRequestsController extends AbstractListController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2775564222923419853L;
	@EJB
	protected RequestsService requestsService;
	
	protected Integer prmCreditRequestId=1;
	protected Integer prmPartnerId;
	protected Integer prmStatusId;
	protected String  prmCreditRequestNumber;
	protected String  prmRequestGuid;
	protected DateRange prmRequestDate=new DateRange(null, null);
	protected DateRange prmResponseDate=new DateRange(null, null);
	protected Integer cntReq=0;
	
	@PostConstruct
	public void init() {
		super.init();
	}	
	
	public class RequestsDataModel extends StdRequestDataModel<Requests>{

		public RequestsDataModel(){
			super();
		}
		
		@Override
		protected List<Requests> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			List<Requests> lst=requestsService.listKBRequests(seqRange.getFirstRow(), seqRange.getRows(), prmCreditRequestId, 
					prmPartnerId, prmStatusId, 	prmRequestGuid, prmRequestDate, prmResponseDate, prmCreditRequestNumber);
			cntReq=lst.size();
			return lst;
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return requestsService.countKBRequests(prmCreditRequestId, prmPartnerId, prmStatusId, prmRequestGuid, prmRequestDate, prmResponseDate, prmCreditRequestNumber);
		}

		@Override
		public Requests getRowData() {
			if (rowKey == null) {
				return null;
			} else {
			  return requestsService.getRequests(((Number) rowKey).intValue());
			}
			
		}
		
	}
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel createModel() {
		return new RequestsDataModel();
	}

	public Integer getPrmCreditRequestId() {
		return prmCreditRequestId;
	}

	public void setPrmCreditRequestId(Integer prmCreditRequestId) {
		this.prmCreditRequestId = prmCreditRequestId;
	}

	public Integer getPrmPartnerId() {
		return prmPartnerId;
	}

	public void setPrmPartnerId(Integer prmPartnerId) {
		this.prmPartnerId = prmPartnerId;
	}

	public Integer getPrmStatusId() {
		return prmStatusId;
	}

	public void setPrmStatusId(Integer prmStatusId) {
		this.prmStatusId = prmStatusId;
	}

	public String getPrmCreditRequestNumber() {
		return prmCreditRequestNumber;
	}

	public void setPrmCreditRequestNumber(String prmCreditRequestNumber) {
		this.prmCreditRequestNumber = prmCreditRequestNumber;
	}

	public String getPrmRequestGuid() {
		return prmRequestGuid;
	}

	public void setPrmRequestGuid(String prmRequestGuid) {
		this.prmRequestGuid = prmRequestGuid;
	}

	public DateRange getPrmRequestDate() {
		return prmRequestDate;
	}

	public void setPrmRequestDate(DateRange prmRequestDate) {
		this.prmRequestDate = prmRequestDate;
	}

	public DateRange getPrmResponseDate() {
		return prmResponseDate;
	}

	public void setPrmResponseDate(DateRange prmResponseDate) {
		this.prmResponseDate = prmResponseDate;
	}

	public Integer getCntReq() {
		return cntReq;
	}

	public void setCntReq(Integer cntReq) {
		this.cntReq = cntReq;
	}

	protected void nullIfEmpty() {
		
		if (StringUtils.isBlank(prmRequestGuid)) {
			prmRequestGuid = null;
		}
		if (StringUtils.isBlank(prmCreditRequestNumber)) {
			prmCreditRequestNumber = null;
		}
		
		if (JSFUtils.NULL_INT_VALUE.equals(prmPartnerId)) {
			prmPartnerId = null;
		}	
		if (JSFUtils.NULL_INT_VALUE.equals(prmStatusId)) {
			prmStatusId = null;
		}	
	}	
	
	public void clearLsn(ActionEvent event) {
		prmCreditRequestId=1;
		prmPartnerId=null;
		prmStatusId=null;
		prmCreditRequestNumber=null;
		prmRequestGuid=null;
		prmRequestDate= new DateRange(null, null);
		prmResponseDate= new DateRange(null, null);
		refreshSearch();
	}	

	public void deleteItem(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Integer reqid = Convertor.toInteger(event.getComponent().getAttributes().get("reqid"));
		
		if (reqid!=null){
		  try {
			  requestsService.removeRequest(reqid);
			  refreshSearch();
		  } catch (Exception ex) {
			  JSFUtils.handleAsError(facesCtx, null, ex);
		  }
		}
	}	
}
