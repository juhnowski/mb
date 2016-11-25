package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import ru.simplgroupp.interfaces.service.HolidaysService;
import ru.simplgroupp.persistence.HolidaysEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.webapp.controller.AbstractSessionController;


public class HolidaysController  extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @EJB
    protected HolidaysService hserv;
   
    protected Integer month;
    protected Integer year;
    protected String name;
    protected Integer kind;
    protected Integer cnt;
    protected List<HolidaysEntity> lstHolidays;
    
    @PostConstruct
    public void init() {
    	FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()){
   		  getLstHolidays();
   		  cnt=hserv.countHolidays(month, year, name, kind);
		}
   	}	
  
    public List<HolidaysEntity> getLstHolidays(){
    	cnt=hserv.countHolidays(month, year, name, kind);
    	return hserv.listHolidays(month, year, name, kind);
    }
    
    public void changeMonth(ValueChangeEvent event){
	     month=(Integer) event.getNewValue();

	 }
    
    public void changeYear(ValueChangeEvent event){
	     year=(Integer) event.getNewValue();

	 }
    
    public void handleEvent(AjaxBehaviorEvent event) {
    	HtmlInputText ht=(HtmlInputText) event.getComponent();
    	month=(Integer) ht.getValue(); 
    }
    
    public void changeKind(ValueChangeEvent event){
	     kind=(Integer) event.getNewValue();

	 }
    
    public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public Integer getMonth(){
		return month;
	}
	
	public void setMonth(Integer month){
		this.month=month;
	}
	
	public Integer getYear(){
		return year;
	}
	
	public void setYear(Integer year){
		this.year=year;
	}
	
	public Integer getKind(){
		return kind;
	}
	
	public void setKind(Integer kind){
		this.kind=kind;
	}
	
	public Integer getCnt(){
		return cnt;
	}
	
	public void search(ActionEvent event) {
	
		getLstHolidays();
		
	}
	
	public void clearSearch(ActionEvent event) {
		year=null;
		month=null;
		name=null;
		kind=null;
		getLstHolidays();
		
	}
	
	public void deleteItem(ActionEvent event) {
		Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("holidayid"));
		if (itemid!=null){
			hserv.deleteHolidays(itemid.intValue());
			getLstHolidays();
		}
	}
	
	
}
