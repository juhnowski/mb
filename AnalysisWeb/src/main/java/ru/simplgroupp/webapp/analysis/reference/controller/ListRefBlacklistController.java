package ru.simplgroupp.webapp.analysis.reference.controller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.persistence.RefBlacklistEntity;
import ru.simplgroupp.persistence.ReferenceEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.transfer.ActiveStatus;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.util.DatesUtils;
import ru.simplgroupp.util.MimeTypeKeys;
import ru.simplgroupp.util.XmlUtils;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListRefBlacklistController extends AbstractListController{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 464920681686848513L;

	@EJB
    protected ReferenceBooksLocal refBook;
	
	protected String surname;
	protected String name;
	protected String midname;
	protected String series;
	protected String number;
	protected String mobilephone;
	protected Date birthdate;
	protected Integer reason;
	protected Integer source;
	protected String email;
	protected Integer isactive=1;
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

	private BufferedReader file;

	@EJB
	private ReferenceBooksLocal referenceBooksLocal;

	private List<String> errorRecords = new ArrayList<String>();
	private Integer successSize = 0;

	public void uploadFileListener(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getUploadedFile();
		this.file = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(item.getData()), StandardCharsets.UTF_8));
		this.errorRecords.clear();
		this.successSize = 0;
	}

	public void saveToDatabase() throws IOException {
		List<RefBlacklistEntity> entities = new ArrayList<RefBlacklistEntity>();

		String line;
		Map<String, ReferenceEntity> reasonMap = getReasonMap();
		Map<String, ReferenceEntity> sourceMap = getSourceMap();

		while ((line = file.readLine()) != null) {
			try {
				RefBlacklistEntity entity = getRefBlacklistEntity(line, reasonMap, sourceMap);
				if (validate(entity)) {
					successSize++;
					// проверяем есть ли в бд совпадения
					Boolean finded = referenceBooksLocal.findRefBlacklistByBlock(entity);
					if (!finded) {
						entities.add(entity);
					}
				} else {
					// отправляем в список неудачных
					errorRecords.add(line);
				}
			} catch (ParseException ignore) {
				// отправляем в список неудачных
				errorRecords.add(line);
			} catch (ArrayIndexOutOfBoundsException ignore) {
				// отправляем в список неудачных
				errorRecords.add(line);
			}
		}
		referenceBooksLocal.saveBulkRefBlacklist(entities);
		refreshSearch();
	}

	private Map<String, ReferenceEntity> getSourceMap() {
		List<Reference> refs = referenceBooksLocal.getBlackListSourceTypes();
		Map<String, ReferenceEntity> result = new HashMap<String, ReferenceEntity>();
		for (Reference ref : refs) {
			result.put(ref.getName().toLowerCase(), ref.getEntity());
		}
		return result;
	}

	private Map<String, ReferenceEntity> getReasonMap() {
		List<Reference> refs = referenceBooksLocal.getBlackListReasonTypes();
		Map<String, ReferenceEntity> result = new HashMap<String, ReferenceEntity>();
		for (Reference ref : refs) {
			result.put(ref.getName().toLowerCase(), ref.getEntity());
		}
		return result;
	}

	private RefBlacklistEntity getRefBlacklistEntity(String line, Map<String, ReferenceEntity> reasonMap, Map<String, ReferenceEntity> sourceMap) throws ParseException, ParseException {
		DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		String[] datas = line.split(";", -1);

		RefBlacklistEntity entity = new RefBlacklistEntity();
		entity.setSurname(datas[0]);
		entity.setName(datas[1]);
		entity.setMidname(datas[2]);
		if (StringUtils.isNotEmpty(datas[3])) {
			entity.setBirthdate(format.parse(datas[3]));
		}
		entity.setMaidenname(datas[4]);
		entity.setSeries(datas[5]);
		entity.setNumber(datas[6]);
		if (StringUtils.isNotEmpty(datas[7])) {
			entity.setMobilePhone(Convertor.fromMask(datas[7]));
		}
		entity.setEmail(datas[8]);
		entity.setEmployerFullName(datas[9]);
		entity.setEmployerShortName(datas[10]);
		entity.setRegionName(datas[11]);
		entity.setAreaName(datas[12]);
		entity.setCityName(datas[13]);
		entity.setPlaceName(datas[14]);
		entity.setStreetName(datas[15]);
		entity.setHouse(datas[16]);
		entity.setCorpus(datas[17]);
		entity.setBuilding(datas[18]);
		entity.setFlat(datas[19]);

		if (StringUtils.isNotEmpty(datas[20]) && reasonMap.get(datas[20].toLowerCase()) != null) {
			entity.setReasonId(reasonMap.get(datas[20].toLowerCase())); // причина
		} else {
			entity.setReasonId(reasonMap.get("другое"));
		}
		if (StringUtils.isNotEmpty(datas[21]) && sourceMap.get(datas[21].toLowerCase()) != null) {
			entity.setSourceId(sourceMap.get(datas[21].toLowerCase())); // источник
		} else {
			entity.setSourceId(reasonMap.get("другое"));
		}

		if (datas.length == 23) {
			entity.setComment(datas[22]);
		}

		entity.setDatabeg(DatesUtils.makeDate(2010, 01, 01));
		entity.setDataend(DatesUtils.makeDate(2020, 01, 01));
		entity.setIsactive(ActiveStatus.ACTIVE);
		return entity;
	}

	private Boolean validate(RefBlacklistEntity entity) {
		boolean valid = false;
		if (StringUtils.isNotEmpty(entity.getSurname()) && StringUtils.isNotEmpty(entity.getName()) && entity.getBirthdate() != null) { // Блок ФИО + ДР
			valid = true;
		} else if (StringUtils.isNotEmpty(entity.getSeries()) && StringUtils.isNotEmpty(entity.getNumber())) { // Блок серия + номер паспорта
			valid = true;
		} else if (StringUtils.isNotEmpty(entity.getMobilePhone())) { // Блок телефон
			valid = true;
		} else if (StringUtils.isNotEmpty(entity.getEmail())) { // Блок email
			valid = true;
		} else if (StringUtils.isNotEmpty(entity.getEmployerFullName()) || StringUtils.isNotEmpty(entity.getEmployerShortName())) { // Блок работодатель
			valid = true;
		} else if (StringUtils.isNotEmpty(entity.getRegionName()) && StringUtils.isNotEmpty(entity.getHouse())) { // Блок адрес
			valid = true;
		}
		return valid;
	}

	public void downloadLogFile() {
		try {
			File file = File.createTempFile("blacklistLog", ".txt");
			StringBuilder b = new StringBuilder(100);
			for (String s : errorRecords) {
				b.append(s).append("\n");
			}
			FileUtils.write(file, b.toString());
			JSFUtils.downloadFile(file, MimeTypeKeys.TEXT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void downloadCsvFile() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String delim = ";";

		List<RefBlacklistEntity> list = refBook.listRefBlacklist(surname, name, midname, null,
				birthdate, reason, series, number, isactive, mobilephone, email, source,
				employerFullName, employerShortName, regionName, areaName, cityName, placeName,
				streetName, house, corpus, building, flat);
		StringBuilder res = new StringBuilder(list.size());
		String newRow = "";
		for (RefBlacklistEntity ref : list) {
			res.append(newRow);
			res.append(StringUtils.defaultString(ref.getSurname())).append(delim);
			res.append(StringUtils.defaultString(ref.getName())).append(delim);
			res.append(StringUtils.defaultString(ref.getMidname())).append(delim);
			if (ref.getBirthdate() != null) {
				res.append(sdf.format(ref.getBirthdate()));
			}
			res.append(delim);
			res.append(StringUtils.defaultString(ref.getMaidenname())).append(delim);

			res.append(StringUtils.defaultString(ref.getSeries())).append(delim);
			res.append(StringUtils.defaultString(ref.getNumber())).append(delim);

			res.append(StringUtils.defaultString(ref.getMobilePhone())).append(delim);
			res.append(StringUtils.defaultString(ref.getEmail())).append(delim);

			res.append(StringUtils.defaultString(ref.getEmployerFullName())).append(delim);
			res.append(StringUtils.defaultString(ref.getEmployerShortName())).append(delim);

			res.append(StringUtils.defaultString(ref.getRegionName())).append(delim);
			res.append(StringUtils.defaultString(ref.getAreaName())).append(delim);
			res.append(StringUtils.defaultString(ref.getCityName())).append(delim);
			res.append(StringUtils.defaultString(ref.getPlaceName())).append(delim);
			res.append(StringUtils.defaultString(ref.getStreetName())).append(delim);
			res.append(StringUtils.defaultString(ref.getHouse())).append(delim);
			res.append(StringUtils.defaultString(ref.getCorpus())).append(delim);
			res.append(StringUtils.defaultString(ref.getBuilding())).append(delim);
			res.append(StringUtils.defaultString(ref.getFlat())).append(delim);

			if (ref.getReasonId() != null) {
				res.append(ref.getReasonId().getName());
			}
			res.append(delim);
			if (ref.getSourceId() != null) {
				res.append(ref.getSourceId().getName());
			}
			res.append(delim);

			res.append(StringUtils.defaultString(ref.getComment()));
			newRow = "\n";
		}

		try {
			File file = File.createTempFile("blacklistcsv", ".csv");
			FileUtils.write(file, res.toString(), XmlUtils.ENCODING_UTF);
			JSFUtils.downloadFile(file, MimeTypeKeys.CSV);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Integer getProcSize() {
		return errorRecords.size() + successSize;
	}

	public Integer getErrorSize() {
		return errorRecords.size();
	}

	public Integer getSuccessSize() {
		return successSize;
	}

	public void setSuccessSize(Integer successSize) {
		this.successSize = successSize;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
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

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	@PostConstruct
	public void init() {
		super.init();
	}	
	 
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel createModel() {
		return new RefBlacklistDataModel();
	}

	public void clearLsn(ActionEvent event) {
		surname=null;
		name=null;
		midname=null;
		birthdate=null;
		series=null;
		number=null;
		reason=null;
		source=null;
		email=null;
		mobilephone=null;
		regionName=null;
		areaName=null;
		cityName=null;
		placeName=null;
		streetName=null;
		employerFullName=null;
		employerShortName=null;
		isactive=1;
		refreshSearch();
	}
	
	protected void nullIfEmpty() {
		if (StringUtils.isEmpty(surname)){
			surname=null;
		}
		if (StringUtils.isEmpty(name)){
			name=null;
		}	
		if (StringUtils.isEmpty(midname)){
			midname=null;
		}
		if (StringUtils.isEmpty(series)){
			series=null;
		}
		if (StringUtils.isEmpty(number)){
			number=null;
		}
		if (StringUtils.isEmpty(mobilephone)){
			mobilephone=null;
		}
		if (StringUtils.isEmpty(email)){
			email=null;
		}
		
	}
	
	public void deleteItem(ActionEvent event) {
		
		Integer refid = Convertor.toInteger(event.getComponent().getAttributes().get("refid"));
		if (refid!=null){
		    refBook.removeRefBlacklist(refid.intValue());
		}
		refreshSearch();
	}
	
	public String addItem() {
		RefBlacklistEntity re=refBook.addRefBlacklist();
		String surl = "editblacklist?faces-redirect=true&refid=" + re.getId().toString();
		return surl;
	}
	
	public class RefBlacklistDataModel extends StdRequestDataModel<RefBlacklistEntity>{

		public RefBlacklistDataModel(){
			super();
		}
		
		@Override
		protected List<RefBlacklistEntity> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			List<RefBlacklistEntity> lst=refBook.listRefBlacklist(surname, name, midname, null, 
					birthdate, reason, series, number, isactive,mobilephone,email,source,
					employerFullName,employerShortName,regionName,areaName,cityName,placeName,
					streetName,	house,corpus,building,flat);
			return lst;
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return refBook.countRefBlacklist(surname, name, midname, null, 
					birthdate, reason, series, number, isactive,mobilephone,email,source,
					employerFullName,employerShortName,regionName,areaName,cityName,placeName,
					streetName,	house,corpus,building,flat);
		}

		@Override
		public RefBlacklistEntity getRowData() {
			if (rowKey == null) {
				return null;
			} else {
				return refBook.getRefBlacklist(((Number) rowKey).intValue(), null);
			}
		}
		
	}

}
