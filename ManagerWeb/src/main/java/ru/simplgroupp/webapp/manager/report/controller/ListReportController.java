package ru.simplgroupp.webapp.manager.report.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;

import ru.simplgroupp.interfaces.ReportBeanLocal;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.transfer.Report;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListReportController extends AbstractListController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8683156901068306254L;

	@EJB
	protected ReportBeanLocal reportBean;
	
	
	protected Integer reportType;
	protected String mimeType;
	protected String executor;
	protected String reportName;
	protected String code;
	
	@PostConstruct
	public void init() {
		super.init();
	}
	
	public void clearLsn(ActionEvent event) {
		reportType=null;
		mimeType=null;
		executor=null;
		reportName=null;
		code=null;
		refreshSearch();
	}
	
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel<Report> createModel() {
		return new ReportDataModel();
	}

	public class ReportDataModel extends StdRequestDataModel<Report>{

		public ReportDataModel() {
			super();
			multipleSorting = true;
		}
		
		@Override
		protected List<Report> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			List<Report> lst=reportBean.listReports(seqRange.getFirstRow(), seqRange.getRows(), null, reportType, mimeType, executor, reportName,code);
			return lst;
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return reportBean.countReports(reportType, mimeType, executor, reportName,code);
		}

		@Override
		public Report getRowData() {
			if (rowKey == null)
				return null;
			else {
				Report report=null;
				try {
					report=reportBean.getReport(((Number) rowKey).intValue(), null);
					return report;
				} catch (Exception e){
					return null;
				}
			}
		}
		
	}
	public ReportBeanLocal getReportBean() {
		return reportBean;
	}

	public void setReportBean(ReportBeanLocal reportBean) {
		this.reportBean = reportBean;
	}

	public Integer getReportType() {
		return reportType;
	}

	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	protected void nullIfEmpty() {
		if (JSFUtils.NULL_INT_VALUE.equals(reportType)) {
			reportType = null;
		}
	}
	
}
