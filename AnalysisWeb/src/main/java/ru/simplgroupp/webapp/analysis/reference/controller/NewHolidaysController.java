package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;

import ru.simplgroupp.interfaces.service.HolidaysService;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class NewHolidaysController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB
	protected HolidaysService hserv;
	 
	protected String newName;
	protected Integer newKind;
	protected Date newDatabeg;
	protected Date newDataend;
	
	public String addItem() {
		hserv.addHolidays(newDatabeg, newDataend, newKind, newName);
		return null;
	}
	
	public String getNewName(){
		return newName;
	}
	
	public void setNewName(String newName){
		this.newName=newName;
	}
	
	public Integer getNewKind(){
		return newKind;
	}
	
	public void setNewKind(Integer newKind){
		this.newKind=newKind;
	}
	
	public Date getNewDatabeg(){
		return newDatabeg;
	}
	
	public void setNewDatabeg(Date newDatabeg){
		this.newDatabeg=newDatabeg;
	}
	
	public Date getNewDataend(){
		return newDataend;
	}
	
	public void setNewDataend(Date newDataend){
		this.newDataend=newDataend;
	}

}
