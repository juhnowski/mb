package ru.simplgroupp.webapp.analysis.eventlog.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.toolkit.common.DateRange;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.EventLog;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListLogController extends AbstractListController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	protected EventLogService eventLog;
	
	protected Integer prmRole;
	protected Integer prmUserType;
	protected Integer prmUserId;
	protected String prmSurname;
	protected String prmCrequestNumber;
	protected Integer prmEventCode;
	protected String prmDescription;
	protected String prmOs;
	protected String prmBrowser;
	protected String prmGeoPlace;
	protected String prmIpaddress;
	protected DateRange prmDate=new DateRange(null, null);
	
	public Integer getPrmRole() {
		return prmRole;
	}

	public void setPrmRole(Integer prmRole) {
		this.prmRole = prmRole;
	}

	public Integer getPrmUserType() {
		return prmUserType;
	}

	public void setPrmUserType(Integer prmUserType) {
		this.prmUserType = prmUserType;
	}

	public Integer getPrmUserId() {
		return prmUserId;
	}

	public void setPrmUserId(Integer prmUserId) {
		this.prmUserId = prmUserId;
	}
	
	public String getPrmSurname() {
		return prmSurname;
	}

	public void setPrmSurname(String prmSurname) {
		this.prmSurname = prmSurname;
	}

	public String getPrmCrequestNumber() {
		return prmCrequestNumber;
	}

	public void setPrmCrequestNumber(String prmCrequestNumber) {
		this.prmCrequestNumber = prmCrequestNumber;
	}

	public Integer getPrmEventCode() {
		return prmEventCode;
	}

	public void setPrmEventCode(Integer prmEventCode) {
		this.prmEventCode = prmEventCode;
	}

	public String getPrmDescription() {
		return prmDescription;
	}

	public void setPrmDescription(String prmDescription) {
		this.prmDescription = prmDescription;
	}

	public String getPrmOs() {
		return prmOs;
	}

	public void setPrmOs(String prmOs) {
		this.prmOs = prmOs;
	}

	public String getPrmBrowser() {
		return prmBrowser;
	}

	public void setPrmBrowser(String prmBrowser) {
		this.prmBrowser = prmBrowser;
	}

	public String getPrmGeoPlace() {
		return prmGeoPlace;
	}

	public void setPrmGeoPlace(String prmGeoPlace) {
		this.prmGeoPlace = prmGeoPlace;
	}

	public DateRange getPrmDate() {
		return prmDate;
	}

	public void setPrmDate(DateRange prmDate) {
		this.prmDate = prmDate;
	}

	@PostConstruct
	public void init() {
		super.init();
	}	
	
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel createModel() {
		
		return new EventLogDataModel();
	}

	protected void nullIfEmpty() {
		if (StringUtils.isBlank(prmGeoPlace)) {
			prmGeoPlace = null;
		}
		if (StringUtils.isBlank(prmOs)) {
			prmOs = null;
		}
		if (StringUtils.isBlank(prmBrowser)) {
			prmBrowser = null;
		}
		if (StringUtils.isBlank(prmIpaddress)) {
			prmIpaddress = null;
		}		
		if (StringUtils.isBlank(prmCrequestNumber)) {
			prmCrequestNumber = null;
		}
		if (StringUtils.isBlank(prmDescription)) {
			prmDescription = null;
		}
		if (StringUtils.isBlank(prmSurname)) {
			prmSurname = null;
		}
		if (JSFUtils.NULL_INT_VALUE.equals(prmUserType)) {
			prmUserType = null;
		}	
		if (JSFUtils.NULL_INT_VALUE.equals(prmEventCode)) {
			prmEventCode = null;
		}	
	}	
	
	public void clearLsn(ActionEvent event) {
		prmGeoPlace = null;
		prmOs=null;
		prmIpaddress=null;
		prmBrowser = null;
		prmCrequestNumber=null;
		prmDescription = null;
		prmSurname=null;
		prmUserType = null;
		prmEventCode = null;
		prmDate= new DateRange(null, null);
		refreshSearch();
	}	
	
	public class EventLogDataModel extends StdRequestDataModel<EventLog>{

		public EventLogDataModel(){
			super();
		}
		
		@Override
		protected List<EventLog> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			return eventLog.listLogs(seqRange.getFirstRow(), seqRange.getRows(),null, prmUserId, prmRole, prmUserType, prmSurname, prmCrequestNumber, null, prmEventCode, prmDescription, prmOs, prmBrowser, prmGeoPlace, prmDate, null, prmIpaddress);
			
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return eventLog.countLogs( prmUserId, prmRole, prmUserType, prmSurname, prmCrequestNumber, null, prmEventCode, prmDescription, prmOs, prmBrowser, prmGeoPlace, prmDate, null, prmIpaddress);
			
		}

		@Override
		public EventLog getRowData() {
			if (rowKey == null) {
				return null;
			} else {
			  return eventLog.getEventLog(((Number) rowKey).intValue(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
			}
		}
		
	}

	public String getPrmIpaddress() {
		return prmIpaddress;
	}

	public void setPrmIpaddress(String prmIpaddress) {
		this.prmIpaddress = prmIpaddress;
	}
}
