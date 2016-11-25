package ru.simplgroupp.webapp.analysis.bizact.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang3.StringUtils;

import ru.simplgroupp.dao.interfaces.BizActionDAO;
import ru.simplgroupp.interfaces.BizActionBeanLocal;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.BizAction;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;

public class ListBizActionController extends AbstractListController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4222330895415785901L;
	
	@EJB
	BizActionBeanLocal bizBean;
	
	@EJB
	BizActionDAO bizDAO;
	
	protected Boolean prmForProcess;
	protected String prmProcessDefKey;
	
	@PostConstruct
	public void init() {
		super.init();
	}	

	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	protected void nullIfEmpty() {
		if (StringUtils.isBlank(prmProcessDefKey)) {
			prmProcessDefKey = null;
		}
	}

	@Override
	public StdRequestDataModel<BizAction> createModel() {
		return new BizActionDataModel();
	}

	public Boolean getPrmForProcess() {
		return prmForProcess;
	}

	public void setPrmForProcess(Boolean prmForProcess) {
		this.prmForProcess = prmForProcess;
	}

	public class BizActionDataModel extends StdRequestDataModel<BizAction> {
		
		public BizActionDataModel() {
			super();
			nullIfEmpty();
		}

		@Override
		protected List<BizAction> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			
			return bizBean.listBizActions(seqRange.getFirstRow(), seqRange.getRows(), sorting, Utils.setOf(), null, null, null, null, null, null, null, null, null, prmForProcess, prmProcessDefKey);
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			return bizBean.countBizActions(null, null, null, null, null, null, null, null, null, prmForProcess, prmProcessDefKey);
		}

		@Override
		public BizAction getRowData() {
			if (rowKey == null) {
				return null;
			} else {
				return bizDAO.getBizAction(((Number) rowKey).intValue(), Utils.setOf());
			}
		}
		
	}

	public String getPrmProcessDefKey() {
		return prmProcessDefKey;
	}

	public void setPrmProcessDefKey(String prmProcessDefKey) {
		this.prmProcessDefKey = prmProcessDefKey;
	}
}
