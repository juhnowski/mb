package ru.simplgroupp.webapp.manager.report.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class SimpleReportController extends AbstractSessionController  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2084340198618636373L;

	@EJB
	protected KassaBeanLocal kassa;
	
	private Double sumOut;
	private Double sumBack;
	private Double sumRequests;
	private Double sumRequestsRejected;
	private Double sumRequestsRefused;
	private Date reportDate=new Date();
	
	@PostConstruct
	public void init() {
		overview(reportDate);
	}
	
	private void overview(Date date) {
		Map<Integer,Double> sm=kassa.overviewSum(date);
		sumOut=sm.get(1);
		sumBack=sm.get(2);
		sumRequests=sm.get(3);
		sumRequestsRejected=sm.get(4);
		sumRequestsRefused=sm.get(5);
	}

	public Double getSumOut() {
		return sumOut;
	}

	public void setSumOut(Double sumOut) {
		this.sumOut = sumOut;
	}

	public Double getSumBack() {
		return sumBack;
	}

	public void setSumBack(Double sumBack) {
		this.sumBack = sumBack;
	}

	public Double getSumRequests() {
		return sumRequests;
	}

	public void setSumRequests(Double sumRequests) {
		this.sumRequests = sumRequests;
	}

	public Double getSumRequestsRejected() {
		return sumRequestsRejected;
	}

	public void setSumRequestsRejected(Double sumRequestsRejected) {
		this.sumRequestsRejected = sumRequestsRejected;
	}

	public Double getSumRequestsRefused() {
		return sumRequestsRefused;
	}

	public void setSumRequestsRefused(Double sumRequestsRefused) {
		this.sumRequestsRefused = sumRequestsRefused;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	
	public void changeDate(ValueChangeEvent event){
		reportDate=(Date) event.getNewValue();
	}
	
	public void recalc(ActionEvent event){
		overview(reportDate);
	}
}
