package ru.simplgroupp.webapp.analysis.reference.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.transfer.RefHeader;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;

public class ListReferenceSystemController extends AbstractListController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @EJB
    protected ReferenceBooksLocal refBook;
    
    protected String name;
    
    @PostConstruct
	public void init() {
		super.init();
	}	
    
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name=name;
		
	}
	
	public void clearLsn(ActionEvent event) {
		name = null;
		refreshSearch();
	}	
	
	@Override
	public StdRequestDataModel<RefHeader> createModel() {
		return new RefHeaderDataModel();
	}

	public class RefHeaderDataModel extends StdRequestDataModel<RefHeader>{

		@Override
		protected List<RefHeader> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			if (StringUtils.isEmpty(name))
				name=null;
			List<RefHeader> lst=refBook.listRefHeaders(Partner.SYSTEM, name,true);
			return lst;
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			if (StringUtils.isEmpty(name))
				name=null;
			return refBook.countRefHeaders(Partner.SYSTEM, name,true);
		}

		@Override
		public RefHeader getRowData() {
			if (rowKey == null) {
				return null;
			} else {
				return refBook.getRefHeader(((Number) rowKey).intValue(), null);
			}
		}
		
		
	}
}
