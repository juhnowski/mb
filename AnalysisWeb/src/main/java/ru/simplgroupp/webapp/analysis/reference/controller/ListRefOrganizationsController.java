package ru.simplgroupp.webapp.analysis.reference.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.interfaces.service.OrganizationService;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.transfer.Organization;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListRefOrganizationsController extends AbstractListController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9058588285904623667L;
	
	@EJB
    OrganizationService orgService;
	
	protected Integer isActive;
	protected String name;
	protected String inn;
	protected String kpp;
	protected String ogrn;
	protected String phone;
	protected String email;
	protected Integer orgTypeId;
	
	@PostConstruct
	public void init() {
		super.init();
	}	
	
	public Integer getIsActive() {
		return isActive;
	}
		
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInn() {
		return inn;
	}
	public void setInn(String inn) {
		this.inn = inn;
	}
	public String getKpp() {
		return kpp;
	}
	public void setKpp(String kpp) {
		this.kpp = kpp;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getOgrn() {
		return ogrn;
	}

	public void setOgrn(String ogrn) {
		this.ogrn = ogrn;
	}

	public Integer getOrgTypeId() {
		return orgTypeId;
	}

	public void setOrgTypeId(Integer orgTypeId) {
		this.orgTypeId = orgTypeId;
	}

	private void nullIfEmpty(){
    	if (StringUtils.isEmpty(name)){
    		name=null;
    	}
    	if (StringUtils.isEmpty(inn)){
    		inn=null;
    	}
    	if (StringUtils.isEmpty(kpp)){
    		kpp=null;
    	}
    	if (StringUtils.isEmpty(phone)){
    		phone=null;
    	}
    	if (StringUtils.isEmpty(email)){
    		email=null;
    	}
    	if (JSFUtils.NULL_INT_VALUE.equals(isActive)) {
			isActive = null;
		}	
    }
	
    public void clearLsn(ActionEvent event) {
		name=null;
		inn=null;
		kpp=null;
		phone=null;
		email=null;
		isActive=null;
		refreshSearch();
	}
    
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public StdRequestDataModel createModel() {
		
		return new RefOrganizationsDataModel();
	}
	
	public class RefOrganizationsDataModel extends StdRequestDataModel<Organization>{
		
		public RefOrganizationsDataModel(){
			super();
		}

		@Override
		protected List<Organization> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			return orgService.listOrganizations(name, inn, kpp, ogrn,email, phone, isActive,orgTypeId);
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return orgService.countOrganizations(name, inn, kpp, ogrn,email, phone, isActive,orgTypeId);
		}

		@Override
		public Organization getRowData() {
			if (rowKey == null) {
				return null;
			} else {
				return orgService.findOrganization(((Number) rowKey).intValue());
			}
		}
	}
	
}
