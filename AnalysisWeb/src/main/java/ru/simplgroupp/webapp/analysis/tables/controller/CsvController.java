package ru.simplgroupp.webapp.analysis.tables.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.io.FileUtils;

import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.ModelBeanLocal;
import ru.simplgroupp.toolkit.common.FileUtil;
import ru.simplgroupp.toolkit.common.ZipUtils;
import ru.simplgroupp.transfer.AIModel;
import ru.simplgroupp.util.MimeTypeKeys;
import ru.simplgroupp.util.ModelUtils;
import ru.simplgroupp.util.XmlUtils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class CsvController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2592641778856023204L;

	private static Logger logger = Logger.getLogger(CsvController.class.getName());
	
	@EJB
	KassaBeanLocal kassaBean;
	
	@EJB
	ModelBeanLocal modelBean;
	
	protected String table="all";
	protected String where;
	protected String header;
	protected String columns;
	protected List<AIModel> models=new ArrayList<AIModel>(0);
	protected Integer selectedModelId;
	protected String modelWhere;
	protected String modelColumns;
	protected String modelHeader;
	
	@PostConstruct
	public void init() {
		super.init();
		models = modelBean.findModelsWithResults();
	}
	
	public void uploadModelToCsv() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		if (selectedModelId == null) {
			return;
		}
		
		//откуда берем данные
		String viewName = ModelUtils.genViewName(selectedModelId);
	    String st=kassaBean.execCopyToCsv(modelWhere, viewName,modelHeader, modelColumns);
	
	    try {
		
	       File file =File.createTempFile(viewName, ".csv");
	       FileUtils.write(file, st);
	      
	           JSFUtils.downloadFile(file, MimeTypeKeys.TEXT);
	        
	    } catch (Exception e){
		   JSFUtils.handleAsError(facesCtx, null, e);
	   }		
	}
		
	public void uploadToCsv(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		//выгружаем все
		if (table.equalsIgnoreCase("all")) {
			List<File> lstFile=new ArrayList<File>(35);
		
			try {
				logger.info("Каталог для tmp файлов "+FileUtils.getTempDirectoryPath());
				setPropertiesForAddress();
				File file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"address.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "address",header,columns));
				lstFile.add(file);
				setPropertiesForBlacklist();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"blacklist.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "blacklist",header,columns));
				lstFile.add(file);
				setPropertiesForCredit();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"credit.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "credit",header,columns));
				lstFile.add(file);
				setPropertiesForCreditstatus();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"creditstatus.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "creditstatus",header,columns));
				lstFile.add(file);
				setPropertiesForCreditRequest();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"creditrequest.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "creditrequest",header,columns));
				lstFile.add(file);
				setPropertiesForDebt();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"debt.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "debt",header,columns));
				lstFile.add(file);
				setPropertiesForDeviceInfo();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"device_info.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "device_info",header,columns));
				lstFile.add(file);
				setPropertiesForDocument();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"document.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "document",header,columns));
				lstFile.add(file);
				setPropertiesForEmployment();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"employment.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "employment",header,columns));
				lstFile.add(file);
				setPropertiesForEventlog();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"eventlog.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "eventlog",header,columns));
				lstFile.add(file);
				setPropertiesForEventcode();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"eventcode.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "eventcode",header,columns));
				lstFile.add(file);
				setPropertiesForEventtype();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"eventtype.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "eventtype",header,columns));
				lstFile.add(file);
				setPropertiesForMisc();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"misc.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "misc",header,columns));
				lstFile.add(file);
				setPropertiesForNegative();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"negative.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "negative",header,columns));
				lstFile.add(file);
				setPropertiesForPayment();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"payment.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "payment",header,columns));
				lstFile.add(file);
				setPropertiesForProlong();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"prolong.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "prolong",header,columns));
				lstFile.add(file);
				setPropertiesForPeoplepersonal();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"peoplepersonal.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "peoplepersonal",header,columns));
				lstFile.add(file);
				setPropertiesForPeoplecontact();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"peoplecontact.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "peoplecontact",header,columns));
				lstFile.add(file);
				setPropertiesForPeoplemisc();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"peoplemisc.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "peoplemisc",header,columns));
				lstFile.add(file);
				setPropertiesForPeoplemain();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"peoplemain.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "peoplemain",header,columns));
				lstFile.add(file);
				setPropertiesForPhonepay();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"phonepay.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "phonepay",header,columns));
				lstFile.add(file);
				setPropertiesForPhonepaysummary();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"phonepaysummary.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "phonepaysummary",header,columns));
				lstFile.add(file);
				setPropertiesForQarequest();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"qarequest.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "qarequest",header,columns));
				lstFile.add(file);
				setPropertiesForQuestion();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"question.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "question",header,columns));
				lstFile.add(file);
				setPropertiesForQuestionvariant();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"questionvariant.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "questionvariant",header,columns));
				lstFile.add(file);
				setPropertiesForRefBlacklist();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"ref_blacklist.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "ref_blacklist",header,columns));
				lstFile.add(file);
				setPropertiesForRefheader();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"ref_header.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "ref_header",header,columns));
				lstFile.add(file);
				setPropertiesForRegions();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"regions.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "regions",header,columns));
				lstFile.add(file);
				setPropertiesForRegionsNew();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"regionsnew.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "regionsnew",header,columns));
				lstFile.add(file);
				setPropertiesForReference();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"reference.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "reference",header,columns));
				lstFile.add(file);
				setPropertiesForRefusereason();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"refusereason.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "refusereason",header,columns));
				lstFile.add(file);
				setPropertiesForRefNegative();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"ref_negative.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "ref_negative",header,columns));
				lstFile.add(file);
				setPropertiesForScoring();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"scoring.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "scoring",header,columns));
				lstFile.add(file);
				setPropertiesForSpouse();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"spouse.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "spouse",header,columns));
				lstFile.add(file);
				setPropertiesForVerification();
				file=new File(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"verification.csv");
				FileUtils.write(file, kassaBean.execCopyToCsv(where, "verification",header,columns));
				lstFile.add(file);
				
				File resFile= File.createTempFile("all-csv", ".zip");
				ZipUtils.ZipMultiFiles(lstFile,resFile);
				JSFUtils.downloadFile(resFile, "application/zip");
			} catch (IOException e) {
				logger.severe("Произошла ошибка "+e.getMessage());
				JSFUtils.handleAsError(facesCtx, null, e);
			}
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"address.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"blacklist.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"credit.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"creditstatus.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"creditrequest.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"debt.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"device_info.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"document.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"employment.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"eventlog.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"eventcode.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"eventtype.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"misc.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"negative.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"payment.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"peoplepersonal.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"peoplemisc.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"peoplemain.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"peoplecontact.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"phonepay.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"prolong.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"phonepaysummary.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"qarequest.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"question.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"questionvariant.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"reference.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"ref_header.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"regions.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"regionsnew.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"ref_negative.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"ref_blacklist.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"refusereason.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"scoring.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"spouse.csv");
			FileUtil.deleteFile(XmlUtils.checkSlash(FileUtils.getTempDirectoryPath())+"verification.csv");
		} else {
			//выгружаем по одной таблице
		    String st=kassaBean.execCopyToCsv(where, table,header,columns);
		
		    try {
			
		       File file =File.createTempFile(table, ".csv");
		       FileUtils.write(file, st);
   	      
   	           JSFUtils.downloadFile(file, MimeTypeKeys.TEXT);
   	        
		    } catch (Exception e){
			   JSFUtils.handleAsError(facesCtx, null, e);
		   }
		}
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}
		
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public void changeTable(ValueChangeEvent event){
		table=(String) event.getNewValue();
		header="";
		where="";
		if (table.equalsIgnoreCase("address")){
			setPropertiesForAddress();
		}
		if (table.equalsIgnoreCase("blacklist")){
			setPropertiesForBlacklist();
		}
		if (table.equalsIgnoreCase("credit")){
			setPropertiesForCredit();
		}
		if (table.equalsIgnoreCase("creditstatus")){
			setPropertiesForCreditstatus();
		}
		if (table.equalsIgnoreCase("creditrequest")){
		    setPropertiesForCreditRequest();
		}
		if (table.equalsIgnoreCase("debt")){
			setPropertiesForDebt();  
		}
		if (table.equalsIgnoreCase("device_info")){
			setPropertiesForDeviceInfo();  
		}
		if (table.equalsIgnoreCase("document")){
			setPropertiesForDocument();
		} 
		if (table.equalsIgnoreCase("employment")){
			setPropertiesForEmployment();
		}
		if (table.equalsIgnoreCase("eventlog")){
			setPropertiesForEventlog();
		}
		if (table.equalsIgnoreCase("eventcode")){
			setPropertiesForEventcode();
		}
		if (table.equalsIgnoreCase("eventtype")){
			setPropertiesForEventtype();
		}
		if (table.equalsIgnoreCase("misc")){
			setPropertiesForMisc();
		}
		if (table.equalsIgnoreCase("negative")){
			setPropertiesForNegative(); 
	    }
		if (table.equalsIgnoreCase("payment")){
		    setPropertiesForPayment();	
		}
		if (table.equalsIgnoreCase("peoplepersonal")){
			setPropertiesForPeoplepersonal(); 
		}
		if (table.equalsIgnoreCase("peoplecontact")){
			setPropertiesForPeoplecontact(); 
		}
		if (table.equalsIgnoreCase("peoplemisc")){
			setPropertiesForPeoplemisc(); 
		}
		if (table.equalsIgnoreCase("peoplemain")){
			setPropertiesForPeoplemain(); 
		}
		if (table.equalsIgnoreCase("phonepay")){
			setPropertiesForPhonepay();  
		}
		if (table.equalsIgnoreCase("phonepaysummary")){
		    setPropertiesForPhonepaysummary();
		}
		if (table.equalsIgnoreCase("prolong")){
		    setPropertiesForProlong();	
		}
		if (table.equalsIgnoreCase("qarequest")){
			setPropertiesForQarequest(); 
		}
		if (table.equalsIgnoreCase("question")){
			setPropertiesForQuestion(); 
		}
		if (table.equalsIgnoreCase("questionvariant")){
			setPropertiesForQuestionvariant(); 
		}
		if (table.equalsIgnoreCase("ref_blacklist")){
			setPropertiesForRefBlacklist();
		}
		if (table.equalsIgnoreCase("reference")){
			setPropertiesForReference(); 
		}
		if (table.equalsIgnoreCase("ref_header")){
			setPropertiesForRefheader(); 
		}
		if (table.equalsIgnoreCase("refusereason")){
			setPropertiesForRefusereason(); 
		}
		if (table.equalsIgnoreCase("regions")){
			setPropertiesForRegions(); 
		}
		if (table.equalsIgnoreCase("regionsnew")){
			setPropertiesForRegionsNew(); 
		}
		if (table.equalsIgnoreCase("ref_negative")){
			setPropertiesForRefNegative(); 
		}
		if (table.equalsIgnoreCase("scoring")){
		    setPropertiesForScoring();
		}
		if (table.equalsIgnoreCase("spouse")){
		    setPropertiesForSpouse();
		}
		if (table.equalsIgnoreCase("verification")){
		    setPropertiesForVerification();
		}
	  
	}
	
		
	public void setPropertiesForAddress(){
		  header="id|peoplemain_id|creditrequest_id|index|address_text|country|region_short|region_name|region|area|area_name|place|place_name|city|city_name|district|district_name|street|street_name|house|corpus|building|flat|addrtype|issame|partners_id|isactive|databeg|dataend|area_ext|place_ext|city_ext|street_ext";
		  columns="id,peoplemain_id,creditrequest_id,index,address_text,country,region_short,region_name,region,area,area_name,place,place_name,city,city_name,district,district_name,street,street_name,house,corpus,building,flat,addrtype,issame,partners_id,isactive,databeg,dataend,area_ext,place_ext,city_ext,street_ext";
	}
	
	public void setPropertiesForBlacklist(){
		  header="id|reason_id|peoplemain_id|databeg|dataend|comment";
		  columns="id,reason_id,peoplemain_id,databeg,dataend,comment";
	
	}
	
	public void setPropertiesForCredit(){
		  header="id|creditrequest_id|partners_id|accounttype_id|peoplemain_id|bankname|creditdatabeg|creditdataend|isover|credittype_id|issameorg|creditdataendfact|creditstatus_id|id_currency|bik|maxdelay|creditsum|creditsumdebt|creditcardlimit|creditpercent|creditsumback|id_credit|creditrelation_id|creditlong|delay5|delay30|delay60|delay90|delay120|delaymore|maxoverduedebt|creditfreqpayment_id|currentdebt|currentoverduedebt|delayindays|creditlimitunused|creditmoneyback|datelastupdate|overduestate_id|creditaccount|paydiscipline|creditsumpaid|creditmonthlypayment|creditorganization_id|creditdatedebt|datestatus|isactive";
		  columns="id,creditrequest_id,partners_id,accounttype_id,peoplemain_id,bankname,creditdatabeg,creditdataend,isover,credittype_id,issameorg,creditdataendfact,creditstatus_id,id_currency,bik,maxdelay,creditsum,creditsumdebt,creditcardlimit,creditpercent,creditsumback,id_credit,creditrelation_id,creditlong,delay5,delay30,delay60,delay90,delay120,delaymore,maxoverduedebt,creditfreqpayment_id,currentdebt,currentoverduedebt,delayindays,creditlimitunused,creditmoneyback,datelastupdate,overduestate_id,creditaccount,paydiscipline,creditsumpaid,creditmonthlypayment,creditorganization_id,creditdatedebt,datestatus,isactive";
			 
	}
	
	public void setPropertiesForCreditstatus(){
		 header="id|name";
		 columns="id,name";
	}
	
	public void setPropertiesForCreditRequest(){
		  header="id|rejectreason_id|status_id|contest|datecontest|creditdays|comment|accepted|acceptedcredit_id|contest_asp|contest_pd|contest_cb|creditsum|peoplemain_id|nomer|uniquenomer|stake|account_id|smscode|datestatus|confirmed|datesign|clienttimezone|region|city|datefill";
		  columns="id,rejectreason_id,status_id,contest,datecontest,creditdays,comment,accepted,acceptedcredit_id,contest_asp,contest_pd,contest_cb,creditsum,peoplemain_id,nomer,uniquenomer,stake,account_id,smscode,datestatus,confirmed,datesign,clienttimezone,region,city,datefill";
	
	}
	
	public void setPropertiesForDebt(){
		  header="id|creditrequest_id|peoplemain_id|amount|typedebt|namedebt|datedebt|authoritycode|authority_id|authorityname|partners_id";
		  columns="id,creditrequest_id,peoplemain_id,amount,typedebt,namedebt,datedebt,authoritycode,authority_id,authorityname,partners_id";
 	}
	
	public void setPropertiesForDeviceInfo(){
		  header="id|creditrequest_id|info_date|resolution_w|resolution_h|device_platform|user_agent";
		  columns="id,creditrequest_id,info_date,resolution_w,resolution_h,device_platform,user_agent";
	}
	
	public void setPropertiesForDocument(){
	      header="id|documenttype_id|peoplemain_id|partners_id|creditrequest_id|series|number|docdate|docenddate|docorg|docorgcode|comment|isactive|reasonend_id|isuploaded|datechange";
	      columns="id,documenttype_id,peoplemain_id,partners_id,creditrequest_id,series,number,docdate,docenddate,docorg,docorgcode,comment,isactive,reasonend_id,isuploaded,datechange";
	}
	
	public void setPropertiesForEmployment(){
		 header="id|peoplemain_id|partners_id|current|duration_id|typework_id|profession_id|profession_text|salaryfreq_id|placework|creditrequest_id|occupation|education_id|databeg|dataend|salary|extsalary|datestartwork|experience|extsalary_id";
		 columns="id,peoplemain_id,partners_id,current,duration_id,typework_id,profession_id,profession_text,salaryfreq_id,placework,creditrequest_id,occupation,education_id,databeg,dataend,salary,extsalary,datestartwork,experience,extsalary_id";
		 
	}
	
	public void setPropertiesForEventlog(){
		  header="id|creditrequest_id|users_id|peoplemain_id|eventcode_id|eventtype_id|eventtime|description|ipaddress|browser|useragent|provider|geoplace|os|credit_id";
		  columns="id,creditrequest_id,users_id,peoplemain_id,eventcode_id,eventtype_id,eventtime,description,ipaddress,browser,useragent,provider,geoplace,os,credit_id";
    }
	
	public void setPropertiesForEventcode(){
		 header="id|name";
		 columns="id,name";
	}
	
	public void setPropertiesForEventtype(){
		 header="id|name|shortname";
		 columns="id,name,shortname";
	}
	
	public void setPropertiesForMisc(){
		  header="classname|classid|name|format|value";
		  columns="classname,classid,name,format,value";
	}
	
	public void setPropertiesForNegative(){
		 header="id|peoplemain_id|partners_id|creditrequest_id|article_id|yeararticle|module";
		 columns="id,peoplemain_id,partners_id,creditrequest_id,article_id,yeararticle,module";
	}
	
	public void setPropertiesForPayment(){
		  header="id|credit_id|createdate|paymenttype_id|processdate|ispaid|amount|partners_id|paysum_id|status|externalid|account_id|accounttype_id";
		  columns="id,credit_id,createdate,paymenttype_id,processdate,ispaid,amount,partners_id,paysum_id,status,externalid,account_id,accounttype_id";
	}
	
	public void setPropertiesForPeoplepersonal(){
		 header="id|peoplemain_id|partners_id|surname|name|midname|gender|birthdate|birthplace|databeg|citizen|dataend|isactive|creditrequest_id|maidenname|isuploaded";
		 columns="id,peoplemain_id,partners_id,surname,name,midname,gender,birthdate,birthplace,databeg,citizen,dataend,isactive,creditrequest_id,maidenname,isuploaded";
	}
	
	public void setPropertiesForPeoplecontact(){
		 header="id|peoplemain_id|partners_id|contact_id|value|dateactual|isactive|available|region|operator|region_short";
		 columns="id,peoplemain_id,partners_id,contact_id,value,dateactual,isactive,available,region,operator,region_short";
	}
	
	public void setPropertiesForPeoplemain(){
		 header="id|inn|snils";
		 columns="id,inn,snils";
	}
	
	public void setPropertiesForPeoplemisc(){
		 header="id|peoplemain_id|marriage_id|children|dependants|databeg|dataend|realty_id|partners_id|isactive|realtydate|creditrequest_id|car|regdate";
		 columns="id,peoplemain_id,marriage_id,children,dependants,databeg,dataend,realty_id,partners_id,isactive,realtydate,creditrequest_id,car,regdate";
	}
	
	public void setPropertiesForSpouse(){
		 header="id|peoplemain_id|peoplemainspouse_id|databeg|dataend|isactive|spouse_id|typework_id";
		 columns="id,peoplemain_id,peoplemainspouse_id,databeg,dataend,isactive,spouse_id,typework_id";
	}
	
	public void setPropertiesForPhonepay(){
		header="id|summary_id|paymenttype|paymentcount|paymentsum|periodnumber|periodfirstdate";
		columns="id,summary_id,paymenttype,paymentcount,paymentsum,periodnumber,periodfirstdate";
	}
	
	public void setPropertiesForPhonepaysummary(){
		  header="id|peoplemain_id|partners_id|creditrequest_id|paymenttype|paymentperiodtype|periodcount";
		  columns="id,peoplemain_id,partners_id,creditrequest_id,paymenttype,paymentperiodtype,periodcount";
	}
	
	public void setPropertiesForProlong(){
		  header="id|credit_id|longdate|longdays|longstake|longamount|smscode|isactive|uniquenomer";
		  columns="id,credit_id,longdate,longdays,longstake,longamount,smscode,isactive,uniquenomer";
	}
	
	
	public void setPropertiesForQarequest(){
		 header="question_id|creditrequest_id|answerstatus|answervaluenumber|answervaluestring|answervaluedate|answervaluemoney|answervalueref";
		 columns="question_id,creditrequest_id,answerstatus,answervaluenumber,answervaluestring,answervaluedate,answervaluemoney,answervalueref";
	}
	
	public void setPropertiesForQuestion(){
		 header="id|questioncode|questiontext|answertype";
		 columns="id,questioncode,questiontext,answertype";
	}
	
	public void setPropertiesForQuestionvariant(){
		 header="id|question_id|answervalue|answertext";
		 columns="id,question_id,answervalue,answertext";
	}
	
	public void setPropertiesForRefBlacklist(){
		  header="id|surname|name|midname|maidenname|birthdate|databeg|dataend|isactive|series|number|reason_id|mobilephone";
		  columns="id,surname,name,midname,maidenname,birthdate,databeg,dataend,isactive,series,number,reason_id,mobilephone";
	}
	
	public void setPropertiesForRefusereason(){
		 header="id|reason_id|name|namefull|constantname";
		 columns="id,reason_id,name,namefull,constantname";
	}
	
	public void setPropertiesForRefNegative(){
		 header="id|article|groupid|subgroupid|name|lasts|country";
		 columns="id,article,groupid,subgroupid,name,lasts,country";
	}
	
	public void setPropertiesForRefheader(){
		 header="id|partners_id|name";
		 columns="id,partners_id,name";
	}
	
	public void setPropertiesForRegions(){
		 header="code|name|codereg";
		 columns="code,name,codereg";
	}
	
	public void setPropertiesForRegionsNew(){
		 header="code|name|codereg";
		 columns="code,name,codereg";
	}
	
	public void setPropertiesForReference(){
		 header="id|ref_header_id|name|code|codeinteger|isactive|codemain|codeintegermain|ref_header_idmain";
		 columns="id,ref_header_id,name,code,codeinteger,isactive,codemain,codeintegermain,ref_header_idmain";
	}
	
	public void setPropertiesForScoring(){
		  header="id|partners_id|creditrequest_id|score_model_id|peoplemain_id|score|score_error|text_error|reason_error|comment";
		  columns="id,partners_id,creditrequest_id,score_model_id,peoplemain_id,score,score_error,text_error,reason_error,comment";
	}
	
	public void setPropertiesForVerification(){
		  header="id|peoplemain_id|partners_id|creditrequest_id|verification_score|validation_score|verification_text|validation_text|verification_mark|validation_mark|verify_personal|verify_document|verify_phone|verify_address|verify_inn";
		  columns="id,peoplemain_id,partners_id,creditrequest_id,verification_score,validation_score,verification_text,validation_text,verification_mark,validation_mark,verify_personal,verify_document,verify_phone,verify_address,verify_inn";
	  
	}

	public List<AIModel> getModels() {
		return models;
	}

	public Integer getSelectedModelId() {
		return selectedModelId;
	}

	public void setSelectedModelId(Integer selectedModelId) {
		this.selectedModelId = selectedModelId;
	}

	public String getModelWhere() {
		return modelWhere;
	}

	public void setModelWhere(String modelWhere) {
		this.modelWhere = modelWhere;
	}

	public String getModelColumns() {
		return modelColumns;
	}

	public void setModelColumns(String modelColumns) {
		this.modelColumns = modelColumns;
	}

	public String getModelHeader() {
		return modelHeader;
	}

	public void setModelHeader(String modelHeader) {
		this.modelHeader = modelHeader;
	}
}
