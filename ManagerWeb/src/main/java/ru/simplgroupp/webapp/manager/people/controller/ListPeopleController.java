package ru.simplgroupp.webapp.manager.people.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.toolkit.common.DateRange;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;

public class ListPeopleController extends AbstractListController implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3725954392911909966L;

	@EJB
	protected KassaBeanLocal kassa;
	
	@EJB
	protected PeopleBeanLocal people;
	
	@EJB
	PeopleDAO peopleDAO;
	 
	protected String prmSurname;
	protected String prmName;
	protected String prmMidname;
	protected String prmSurname1;
	protected String prmName1;
	protected String prmMidname1;
	protected String prmSurname2;
	protected String prmName2;
	protected String prmMidname2;
	protected DateRange prmDateBirth = new DateRange(null, null);
	protected String prmPaspNumber;
	protected String prmPaspSeries;
	protected String prmInn;
	protected String prmEmail;
	protected String prmCellPhone;
	protected String prmSnils;
	protected Boolean searchFull;
	
	@PostConstruct
	public void init() {
		super.init();
	}
	
	public class PeopleMainDataModel extends StdRequestDataModel<PeopleMain>{

		public PeopleMainDataModel(){
			super();
			multipleSorting = true;
		}
		
		@Override
		protected List<PeopleMain> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			prmSurname=StringUtils.isEmpty(prmSurname1)?prmSurname2:prmSurname1;
			prmName=StringUtils.isEmpty(prmName1)?prmName2:prmName1;
			prmMidname=StringUtils.isEmpty(prmMidname1)?prmMidname2:prmMidname1;
			return people.listPeopleMain(seqRange.getFirstRow(), seqRange.getRows(), sorting,null, prmDateBirth, prmSurname, prmName, prmMidname, prmPaspNumber, prmPaspSeries, prmInn, prmSnils,prmEmail,prmCellPhone);
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			prmSurname=StringUtils.isEmpty(prmSurname1)?prmSurname2:prmSurname1;
			prmName=StringUtils.isEmpty(prmName1)?prmName2:prmName1;
			prmMidname=StringUtils.isEmpty(prmMidname1)?prmMidname2:prmMidname1;
			return people.countPeopleMain(prmDateBirth, prmSurname, prmName, prmMidname, prmPaspNumber, prmPaspSeries, prmInn, prmSnils,prmEmail,prmCellPhone);
		}

		@Override
		public PeopleMain getRowData() {
			if (rowKey == null)
				return null;
			else {
				PeopleMain pplmain=peopleDAO.getPeopleMain(((Number) rowKey).intValue(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL,PeopleMain.Options.INIT_DOCUMENT,PeopleMain.Options.INIT_PEOPLE_CONTACT,
						PeopleMain.Options.INIT_CREDIT,PeopleMain.Options.INIT_DOCUMENT));
				return pplmain;
			}
			
		}
		
	}
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel createModel() {
	  return new PeopleMainDataModel();
	}

	public String getPrmSurname() {
		return prmSurname;
	}

	public void setPrmSurname(String prmSurname) {
		this.prmSurname = prmSurname;
	}
	
	public String getPrmName() {
		return prmName;
	}

	public void setPrmName(String prmName) {
		this.prmName = prmName;
	}
	
	public String getPrmMidname() {
		return prmMidname;
	}

	public void setPrmMidname(String prmMidname) {
		this.prmMidname = prmMidname;
	}
	
	public String getPrmSurname1() {
		return prmSurname1;
	}

	public void setPrmSurname1(String prmSurname1) {
		this.prmSurname1 = prmSurname1;
	}
	
	public String getPrmName1() {
		return prmName1;
	}

	public void setPrmName1(String prmName1) {
		this.prmName1 = prmName1;
	}
	
	public String getPrmMidname1() {
		return prmMidname1;
	}

	public void setPrmMidname1(String prmMidname1) {
		this.prmMidname1 = prmMidname1;
	}
	
	public String getPrmSurname2() {
		return prmSurname2;
	}

	public void setPrmSurname2(String prmSurname2) {
		this.prmSurname2 = prmSurname2;
	}
	
	public String getPrmName2() {
		return prmName2;
	}

	public void setPrmName2(String prmName2) {
		this.prmName2 = prmName2;
	}
	
	public String getPrmMidname2() {
		return prmMidname2;
	}

	public void setPrmMidname2(String prmMidname2) {
		this.prmMidname2 = prmMidname2;
	}
	

	public DateRange getPrmDateBirth() {
		return prmDateBirth;
	}

	public void setPrmDateBirth(DateRange prmDateBirth) {
		this.prmDateBirth = prmDateBirth;
	}
	
	public String getPrmPaspNumber() {
		return prmPaspNumber;
	}

	public void setPrmPaspNumber(String prmPaspNumber) {
		this.prmPaspNumber = prmPaspNumber;
	}
	
	public String getPrmPaspSeries() {
		return prmPaspSeries;
	}

	public void setPrmPaspSeries(String prmPaspSeries) {
		this.prmPaspSeries = prmPaspSeries;
	}
	
	public String getPrmInn() {
		return prmInn;
	}

	public void setPrmInn(String prmInn) {
		this.prmInn = prmInn;
	}
	
	public String getPrmEmail() {
		return prmEmail;
	}

	public void setPrmEmail(String prmEmail) {
		this.prmEmail = prmEmail;
	}
	
	public String getPrmCellPhone() {
		return prmCellPhone;
	}

	public void setPrmCellPhone(String prmCellPhone) {
		this.prmCellPhone = prmCellPhone;
	}
	
	public String getPrmSnils() {
		return prmSnils;
	}

	public void setPrmSnils(String prmSnils) {
		this.prmSnils = prmSnils;
	}
	
	public Boolean getSearchFull() {
		return searchFull;
	}

	public void setSearchFull(Boolean searchFull) {
		this.searchFull = searchFull;
	}

	protected void nullIfEmpty() {
		
		if (StringUtils.isBlank(prmSurname)) {
			prmSurname = null;
		}
		if (StringUtils.isBlank(prmName)) {
			prmName = null;
		}
		if (StringUtils.isBlank(prmMidname)) {
			prmMidname = null;
		}
		if (StringUtils.isBlank(prmSurname1)) {
			prmSurname1 = null;
		}
		if (StringUtils.isBlank(prmName1)) {
			prmName1 = null;
		}
		if (StringUtils.isBlank(prmMidname1)) {
			prmMidname1 = null;
		}
		if (StringUtils.isBlank(prmSurname2)) {
			prmSurname2 = null;
		}
		if (StringUtils.isBlank(prmName2)) {
			prmName2 = null;
		}
		if (StringUtils.isBlank(prmMidname2)) {
			prmMidname2 = null;
		}
		if (StringUtils.isBlank(prmPaspNumber)) {
			prmPaspNumber = null;
		}
		if (StringUtils.isBlank(prmPaspSeries)) {
			prmPaspSeries = null;
		}
		if (StringUtils.isBlank(prmInn)) {
			prmInn = null;
		}
		if (StringUtils.isBlank(prmSnils)) {
			prmSnils = null;
		}
		
		if (StringUtils.isBlank(prmEmail)) {
			prmEmail = null;
		}
		
		if (StringUtils.isBlank(prmCellPhone)) {
			prmCellPhone = null;
		}
	}
	
	public String dummy() {
		return null;
	}
	
	public void clearLsn(ActionEvent event) 
	{
		prmDateBirth = new DateRange(null, null);
		prmSurname=null;
		prmName=null;
		prmMidname=null;
		prmSurname1=null;
		prmName1=null;
		prmMidname1=null;
		prmSurname2=null;
		prmName2=null;
		prmMidname2=null;
		prmPaspNumber=null;
		prmPaspSeries=null;
		prmInn=null;
		prmSnils=null;
		prmEmail=null;
		prmCellPhone=null;
		
		refreshSearch();
	}
}
