package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.persistence.RefBlacklistEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class EditRefBlacklistController extends AbstractSessionController implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7696626291806205317L;

	@EJB
    protected ReferenceBooksLocal refBook;

	protected String surname;
	protected String name;
	protected String midname;
	protected String series;
	protected String number;
	protected Date birthdate;
	protected Integer reason;
	protected Integer isactive;
	protected Date databeg;
	protected Date dataend;
	protected Integer refId;
	protected String mobilephone;
	protected String email;
	protected String comment;
	protected Integer source;
	protected String employerFullName;
	protected String employerShortName;
	protected String regionName;
	protected String areaName;
	protected String cityName;
	protected String placeName;
	protected String streetName;
	protected String house;
	protected String corpus;
	protected String building;
	protected String flat;
	
	@PostConstruct
	public void init() 
	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (refId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("refid")) {
					String ss = prms.get("refid");
					refId = Convertor.toInteger(ss);
				}
			}
			if (refId != null) {
				reloadItem(facesCtx, refId);
			}
		}
	}
	
	protected void reloadItem(FacesContext facesCtx, Integer refId) {
		RefBlacklistEntity re=refBook.getRefBlacklist(refId, null);
		surname=re.getSurname();
		name=re.getName();
		midname=re.getMidname();
		series=re.getSeries();
		number=re.getNumber();
		birthdate=re.getBirthdate();
		isactive=re.getIsactive();
		if (re.getReasonId()!=null){
		  reason=re.getReasonId().getCodeinteger();
		}
		databeg=re.getDatabeg();
		dataend=re.getDataend();
		mobilephone=re.getMobilePhone();
		email=re.getEmail();
		comment=re.getComment();
		if (re.getSourceId()!=null){
		  source=re.getSourceId().getCodeinteger();
		}
		employerFullName=re.getEmployerFullName();
		employerShortName=re.getEmployerShortName();
		regionName=re.getRegionName();
		areaName=re.getAreaName();
		cityName=re.getCityName();
		placeName=re.getPlaceName();
		streetName=re.getStreetName();
		house=re.getHouse();
		corpus=re.getCorpus();
		building=re.getBuilding();
		flat=re.getFlat();
	}
	
	public String save(){
	  refBook.saveRefBlacklist(refId,surname, name, midname, null, birthdate, 
			  databeg, dataend, reason, series, number, isactive,
			  Convertor.fromMask(mobilephone), email,source,comment,
			  employerFullName,employerShortName,regionName,areaName,cityName,
			  placeName,streetName,house,corpus,building,flat);
	  return "success";
	}
	
	public String close(){
		return "cancel";
	}
	
	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMidname() {
		return midname;
	}
	public void setMidname(String midname) {
		this.midname = midname;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Date getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	public Integer getReason() {
		return reason;
	}
	public void setReason(Integer reason) {
		this.reason = reason;
	}
	public Integer getIsactive() {
		return isactive;
	}
	public void setIsactive(Integer isactive) {
		this.isactive = isactive;
	}
	public Date getDatabeg() {
		return databeg;
	}
	public void setDatabeg(Date databeg) {
		this.databeg = databeg;
	}
	public Date getDataend() {
		return dataend;
	}
	public void setDataend(Date dataend) {
		this.dataend = dataend;
	}
	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public ReferenceBooksLocal getRefBook() {
		return refBook;
	}

	public void setRefBook(ReferenceBooksLocal refBook) {
		this.refBook = refBook;
	}

	public String getEmployerFullName() {
		return employerFullName;
	}

	public void setEmployerFullName(String employerFullName) {
		this.employerFullName = employerFullName;
	}

	public String getEmployerShortName() {
		return employerShortName;
	}

	public void setEmployerShortName(String employerShortName) {
		this.employerShortName = employerShortName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getHouse() {
		return house;
	}

	public void setHouse(String house) {
		this.house = house;
	}

	public String getCorpus() {
		return corpus;
	}

	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getFlat() {
		return flat;
	}

	public void setFlat(String flat) {
		this.flat = flat;
	}
	
}
