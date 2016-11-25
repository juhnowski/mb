package ru.simplgroupp.webapp.manager.report.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.interfaces.ReportBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.Report;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ViewReportController extends AbstractSessionController  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2740278279568590413L;

	@EJB
	protected ReportBeanLocal reportBean;
	
	private Integer reportId;
	protected Report report;
	
	@PostConstruct
	public void init() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed())
		{
			if (reportId == null) {
		
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("reportid")) {
					reportId = Convertor.toInteger(prms.get("reportid"));
				}				
			}
			if (reportId != null) {
				reloadReport(facesCtx, reportId);
			}			
		}
	}
	
	protected void reloadReport(FacesContext facesCtx, Integer prmId) {
		try {
		    report=reportBean.getReport(prmId,null);
		} catch (Exception e) {
			JSFUtils.handleAsError(facesCtx, null, e);
		}
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}
	
	public String save() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
		    reportBean.saveReport(report);
		    return "success";
		} catch (Exception e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		}
	}
	
	public String close() {
		return "close";
	}
}
