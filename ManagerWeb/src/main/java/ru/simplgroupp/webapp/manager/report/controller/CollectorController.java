package ru.simplgroupp.webapp.manager.report.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;

import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.toolkit.common.DateRange;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class CollectorController extends AbstractSessionController  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 402911688450783216L;
	private static Logger logger = Logger.getLogger(CollectorController.class.getName());
	
	@EJB
	protected KassaBeanLocal kassa;
	
	protected Date dateStatus;
	protected DateRange dateStatusRange = new DateRange(null, null);
	protected Integer creditRequestId;
	protected List<CreditRequest> listForCollector;
	protected Integer cnt;
	
	@PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
            getListForCollector();
            cnt=getListForCollector().size();
        } catch (Exception e){
        	logger.severe("Произошла ошибка "+e.getMessage());
			JSFUtils.handleAsError(facesCtx, null, e);
        }
	}

	public Date getDateStatus() {
		return dateStatus;
	}

	public void setDateStatus(Date dateStatus) {
		this.dateStatus = dateStatus;
	}

	public DateRange getDateStatusRange() {
		return dateStatusRange;
	}

	public void setDateStatusRange(DateRange dateStatusRange) {
		this.dateStatusRange = dateStatusRange;
	}

	public Integer getCreditRequestId() {
		return creditRequestId;
	}

	public void setCreditRequestId(Integer creditRequestId) {
		this.creditRequestId = creditRequestId;
	}

	public Integer getCnt() {
		return cnt;
	}

	public void setCnt(Integer cnt) {
		this.cnt = cnt;
	}

	public List<CreditRequest> getListForCollector() {
		return kassa.findCreditRequestsForCollector(dateStatus, dateStatusRange, creditRequestId);
	}

	public void clearLsn(ActionEvent event) {
		dateStatus=null;
		dateStatusRange=new DateRange(null, null);
		creditRequestId=null;
		getListForCollector();
		cnt=getListForCollector().size();
	}
		
	public void searchLsn(ActionEvent event) {
		getListForCollector();
		cnt=getListForCollector().size();
	}
	
	public void showTextForCollector(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
	          
		try {
			File file=File.createTempFile("collector",".csv");
			String header="surname;name;midname;birthdate;gender;creditsum;creditsumback;creditdatabeg;creditdataend;creditaccount;address_text;raddress_text;"
					+ "mobilephone;homephone;workphone;maxdelay;crregion;aregregion;aresregion;aworkregion;extcreditsum;"
					+ "experience;cntcredits;cntscans";
			String columns="surname,name,midname,birthdate,gender,creditsum,creditsumback,creditdatabeg,creditdataend,creditaccount,address_text,raddress_text,"
					+ "mobilephone,homephone,workphone,maxdelay,crregion,aregregion,aresregion,aworkregion,extcreditsum,"
					+ "experience,cntcredits,cntscans";
			FileUtils.write(file, kassa.execCopyToCsv("", "v_for_collector",header,columns,";"),"Windows-1251");
			JSFUtils.downloadFile(file, "text/plain");
		} catch (IOException e) {
			logger.severe("Произошла ошибка "+e.getMessage());
			JSFUtils.handleAsError(facesCtx, null, e);
		}
	}
}
