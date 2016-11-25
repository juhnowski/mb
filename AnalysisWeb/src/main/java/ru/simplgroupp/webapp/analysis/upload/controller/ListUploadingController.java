package ru.simplgroupp.webapp.analysis.upload.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.ajax4jsf.model.SequenceRange;

import ru.simplgroupp.interfaces.service.UploadingService;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.DateRange;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Uploading;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListUploadingController extends AbstractListController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1177222315133510959L;
	
	@EJB
	UploadingService uploadService;
	
	/**
	 * партнер
	 */
	protected Integer prmPartnerId;
	/**
	 * статус выгрузки
	 */
	protected Integer prmStatus;
	/**
	 * номер выгрузки
	 */
	protected Integer  prmUploadId;
	/**
	 * дата выгрузки
	 */
	protected DateRange prmUploadingDate=new DateRange(null, null);
	/**
	 * дата ответа
	 */
	protected DateRange prmResponseDate=new DateRange(null, null);
	/**
	 * вид выгрузки
	 */
	protected Integer prmTypeId;
	
	
	@PostConstruct
	public void init() {
		super.init();
	}	
	
	public class UploadingDataModel extends StdRequestDataModel<Uploading>{

		public UploadingDataModel(){
			super();
		}
		
		@Override
		protected List<Uploading> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			return uploadService.listUploading(seqRange.getFirstRow(), seqRange.getRows(),null, prmUploadingDate, prmResponseDate, prmPartnerId, prmUploadId, prmStatus,prmTypeId);
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return uploadService.countUploading(prmUploadingDate, prmResponseDate, prmPartnerId, prmUploadId, prmStatus,prmTypeId);
		}

		@Override
		public Uploading getRowData() {
			if (rowKey == null) {
				return null;
			} else {
			  return uploadService.getUploading(((Number) rowKey).intValue(),Utils.setOf(Uploading.Options.INIT_ERROR));
			}
		}
		
	}
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel createModel() {
		return new UploadingDataModel();
	}

    protected void nullIfEmpty() {
	
	    if (JSFUtils.NULL_INT_VALUE.equals(prmPartnerId)) {
			prmPartnerId = null;
		}	
		if (JSFUtils.NULL_INT_VALUE.equals(prmStatus)) {
			prmStatus = null;
		}	
	
	}	
	
	public void clearLsn(ActionEvent event) {
		prmUploadId=null;
		prmPartnerId=null;
		prmStatus=null;
		prmTypeId=null;
		prmUploadingDate= new DateRange(null, null);
		prmResponseDate= new DateRange(null, null);
		refreshSearch();
	}	
	
	 public void changePartner(ValueChangeEvent event){
	     prmPartnerId=Convertor.toInteger(event.getNewValue());
	     
	 }
	
	 public void changeType(ValueChangeEvent event){
	     prmTypeId=Convertor.toInteger(event.getNewValue());
	     
	 }
	 
	public Integer getPrmPartnerId() {
		return prmPartnerId;
	}

	public void setPrmPartnerId(Integer prmPartnerId) {
		this.prmPartnerId = prmPartnerId;
	}

	public Integer getPrmStatus() {
		return prmStatus;
	}

	public void setPrmStatus(Integer prmStatus) {
		this.prmStatus = prmStatus;
	}

	public Integer getPrmUploadId() {
		return prmUploadId;
	}

	public void setPrmUploadId(Integer prmUploadId) {
		this.prmUploadId = prmUploadId;
	}

	public DateRange getPrmUploadingDate() {
		return prmUploadingDate;
	}

	public void setPrmUploadingDate(DateRange prmUploadingDate) {
		this.prmUploadingDate = prmUploadingDate;
	}

	public DateRange getPrmResponseDate() {
		return prmResponseDate;
	}

	public void setPrmResponseDate(DateRange prmResponseDate) {
		this.prmResponseDate = prmResponseDate;
	}

	public Integer getPrmTypeId() {
		return prmTypeId;
	}

	public void setPrmTypeId(Integer prmTypeId) {
		this.prmTypeId = prmTypeId;
	}
	

	
}
