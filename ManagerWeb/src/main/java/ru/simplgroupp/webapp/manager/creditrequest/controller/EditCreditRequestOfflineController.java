package ru.simplgroupp.webapp.manager.creditrequest.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.dao.interfaces.ProductDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.CreditBeanLocal;
import ru.simplgroupp.interfaces.CreditCalculatorBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.MailBeanLocal;
import ru.simplgroupp.interfaces.ModelBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.interfaces.ProductBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.persistence.CreditRequestEntity;
import ru.simplgroupp.persistence.DocumentEntity;
import ru.simplgroupp.persistence.PeopleContactEntity;
import ru.simplgroupp.persistence.PeopleMainEntity;
import ru.simplgroupp.persistence.ProductsEntity;
import ru.simplgroupp.persistence.UsersEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.GenUtils;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.AIModel;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.BaseCredit;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.CreditStatus;
import ru.simplgroupp.transfer.Documents;
import ru.simplgroupp.transfer.Employment;
import ru.simplgroupp.transfer.EventCode;
import ru.simplgroupp.transfer.EventType;
import ru.simplgroupp.transfer.FiasAddress;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.transfer.PeopleContact;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.PeopleMisc;
import ru.simplgroupp.transfer.PeoplePersonal;
import ru.simplgroupp.transfer.Products;
import ru.simplgroupp.transfer.RefCreditRequestWay;
import ru.simplgroupp.transfer.RefMarriageType;
import ru.simplgroupp.transfer.RefWorkType;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.transfer.Roles;
import ru.simplgroupp.transfer.Spouse;
import ru.simplgroupp.util.SettingsKeys;
import ru.simplgroupp.util.WorkflowUtil;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.fias.controller.ViewAddressController;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.StateRef;
import ru.simplgroupp.workflow.WorkflowObjectStateDef;

public class EditCreditRequestOfflineController extends AbstractSessionController implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1711390812915693036L;

	@EJB
	protected PeopleBeanLocal peopleBean;
	
	@EJB
	protected UsersBeanLocal userBean;
	
	@EJB
	protected MailBeanLocal mailBean;
	
	@EJB
	protected KassaBeanLocal kassaBean;
	
	@EJB
	protected CreditRequestDAO crDAO;
	
	@EJB
	protected CreditBeanLocal creditBean;
	
	@EJB
	protected ReferenceBooksLocal refBook;
	
	@EJB 
	EventLogService eventLog;
	 
	@EJB
    private WorkflowBeanLocal workflow;
	
	@EJB
    private CreditCalculatorBeanLocal creditCalc;
	
	@EJB
    private RulesBeanLocal rulesBean;
	
	@EJB
	PeopleDAO peopleDAO;
	 
	@EJB
	ProductBeanLocal productBean;
	
	@EJB
	ProductDAO productDAO;
	
	@EJB
	ModelBeanLocal modelBean;		
	
	private static final Logger logger = LoggerFactory.getLogger(EditCreditRequestOfflineController.class.getCanonicalName());
	
	/**
	 * id заявки
	 */
	protected Integer prmCreditRequestId;
	/**
	 * заявка
	 */
	protected CreditRequest creditRequest;
	protected CreditRequestEntity creditRequestEntity;
	/**
	 * человек - заголовок
	 */
	protected PeopleMain peopleMain;
	/**
	 * человек - ПД
	 */
	protected PeoplePersonal peoplePersonal;
	/**
	 * человек - доп.данные
	 */
	protected PeopleMisc peopleMisc;
	/**
	 * паспорт
	 */
	protected Documents passport;
	/**
	 * email
	 */
	protected PeopleContact email;
	/**
	 * сотовый телефон
	 */
	protected PeopleContact cellPhone;
	/**
	 * домашний телефон
	 */
	protected PeopleContact homePhone;
	/**
	 * домашний телефон по месту регистрации
	 */
	protected PeopleContact homePhoneReg;
	/**
	 * рабочий телефон
	 */
	protected PeopleContact workPhone;
	/**
	 * адрес регистрации
	 */
	protected FiasAddress registerAddress;
	/**
	 * адрес проживания
	 */
	protected FiasAddress residentAddress;
	/**
	 * адрес работы
	 */
	protected FiasAddress workAddress;
	/**
	 * занятость
	 */
	protected Employment employment;
	/**
	 * счет
	 */
	protected Account account;
	/**
	 * бывший кредит
	 */
	protected Credit credit;
	/**
	 * панели для показа адреса
	 */
	protected ViewAddressController registerAddressCtl;
	protected ViewAddressController residentAddressCtl;
	protected ViewAddressController workAddressCtl;
	/**
	 * поля для супруга - фио, др, телефон, занятость, дата начала совм.жизни
	 */
	protected String surname;
	protected String name;
	protected String midname;
	protected String phone;
	protected Date birthdate;
	protected Reference typework;
	protected Date databeg;
	/**
	 * супруг
	 */
	protected Spouse sp;
	/**
	 * вид незаполненного счета по умолчанию - карта
	 */
	protected Integer actype=Account.CARD_TYPE;
	
	protected String password;
	/**
	 * показываем ли кредит
	 */
	protected Integer showCredit=0;
	protected String spasp;
	protected String npasp;
	
	protected Integer loginByEmail=0;
	
	protected List<Map> lastCalcValues;
	protected AIModel lastModel;	
	
	@PostConstruct
	public void init() {
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (prmCreditRequestId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("ccRequestId")) {
					prmCreditRequestId = Convertor.toInteger(prms.get("ccRequestId"));
				}		
				if (prms.containsKey("prmBusinessObjectId")) {
					prmCreditRequestId = Convertor.toInteger(prms.get("prmBusinessObjectId"));
				}	
			}
			if (prmCreditRequestId != null) {
				reloadCreditRequest(facesCtx, prmCreditRequestId);
			}			
		}
		
	}

	public void saveCR(ActionEvent event){
	    saveAll();
	}
	
	public String save(){
	  saveAll();	
	  return "edit.xhtml?faces-redirect=true&ccRequestId="+creditRequest.getId();		
	}
	
	public void changeShowCredit(ActionEvent event){
	    if (showCredit==0){
	    	showCredit=1;
	    } else {
	    	showCredit=0;
	    }
	}
	
	public void saveAll(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
        try {
        	        	
			Date dateChange = new Date();
			
			PeopleMainEntity pmain=peopleDAO.getPeopleMainEntity(peopleMain.getId());
			//сохраняем персональные данные
			if (JSFUtils.isNullReference(peopleMisc.getRealty())) {
				peopleMisc.setRealty(null);
			}
			if (JSFUtils.isNullReference(peopleMisc.getMarriage())) {
				peopleMisc.setMarriage(null);
			}
			if (JSFUtils.isNullReference(peoplePersonal.getGender())) {
				peoplePersonal.setGender(null);
			}
			if (peopleMisc.getRealtyDate()==null){
				peopleMisc.setRealtyDate(peopleMisc.getRegDate());
			}
			peopleBean.changePeopleData(peoplePersonal, peopleMisc, passport, peopleMain, Partner.CLIENT, dateChange);
			log.info("Сохранили ПД по заявке "+prmCreditRequestId);
			//сохраняем занятость
			if (JSFUtils.isNullReference(employment.getDuration())) {
				employment.setDuration(null);
			}
			if (JSFUtils.isNullReference(employment.getEducation())) {
				employment.setEducation(null);
			}
			if (JSFUtils.isNullReference(employment.getProfession())) {
				employment.setProfession(null);
			}
			if (JSFUtils.isNullReference(employment.getTypeWork())) {
				employment.setTypeWork(null);
			}
			if (JSFUtils.isNullReference(employment.getExtSalaryId())) {
				employment.setExtSalaryId(null);
			}
			peopleBean.changeEmployment(employment, peopleMain.getId(), dateChange);
			log.info("Сохранили занятость по заявке "+prmCreditRequestId);
			//сохраняем адрес регистрации
			if (registerAddressCtl.getIsCompleted()) {
			  registerAddressCtl.levelsToAddress();
			  peopleBean.changeAddress(registerAddress, peopleMain.getId(), Partner.CLIENT, dateChange);
			}
			
			//сохраняем адрес проживания
			if (residentAddressCtl.getIsCompleted()) {
			  residentAddressCtl.levelsToAddress();
			  peopleBean.changeAddress(residentAddress, peopleMain.getId(), Partner.CLIENT, dateChange);
			}
			
			//сохраняем блок с партнером
			if (StringUtils.isNotEmpty(surname)) {
				Integer itype=null;
				if (typework!=null) {
					if (typework.getCodeInteger()>0){
					  itype=typework.getCodeInteger();
					} 
				}
				peopleBean.addSpouse(pmain, surname, name, midname, birthdate, Convertor.fromMask(phone), Spouse.CODE_SPOUSE, databeg,itype);
			}
			else if (sp!=null) {
				peopleBean.closeSpouse(pmain, new Date());
			}
			log.info("Сохранили адреса по заявке "+prmCreditRequestId);
			//сохраняем контакты
			if (StringUtils.isNotEmpty(email.getValue())) {
				PeopleContactEntity contactEmail = peopleBean.findPeopleByContactClient(PeopleContact.CONTACT_EMAIL, email.getValue().toLowerCase());
				if (contactEmail!=null&&contactEmail.getPeopleMainId().getId()!=peopleMain.getId()){
					JSFUtils.handleAsError(facesCtx, "email", new Exception("Пользователь с email "+email.getValue()+" уже есть в системе"));
				}
				peopleBean.changeContact(email, peopleMain.getId(), Partner.CLIENT);
			}
			if (StringUtils.isNotEmpty(cellPhone.getValue())) {
				PeopleContactEntity contactPhone = peopleBean.findPeopleByContactClient(PeopleContact.CONTACT_CELL_PHONE, Convertor.fromMask(cellPhone.getValue()));
			    if (contactPhone != null&&contactPhone.getPeopleMainId().getId()!=peopleMain.getId()) {
			    	JSFUtils.handleAsError(facesCtx, "cellphone", new Exception("Пользователь с телефоном "+cellPhone.getValue()+" уже есть в системе"));
			    }
				peopleBean.changeContact(cellPhone, peopleMain.getId(), Partner.CLIENT);
			}
			if (StringUtils.isNotEmpty(homePhone.getValue())) {
				peopleBean.changeContact(homePhone, peopleMain.getId(), Partner.CLIENT);
			}
			if (StringUtils.isNotEmpty(homePhoneReg.getValue())) {
				peopleBean.changeContact(homePhoneReg, peopleMain.getId(), Partner.CLIENT);
			}
			if (StringUtils.isNotEmpty(workPhone.getValue())) {
				peopleBean.changeContact(workPhone, peopleMain.getId(), Partner.CLIENT);
			}
			log.info("Сохранили контакты по заявке "+prmCreditRequestId);
			//сохраняем кредит
			if (credit.getCreditSum()!=null&&credit.getCreditSum()>0d){
			    creditBean.changeCredit(credit, peopleMain, prmCreditRequestId, Partner.CLIENT);
			}
			log.info("Сохранили кредит по заявке "+prmCreditRequestId);
			//сохраняем счет
			Account  changedAccount = null;
			if (StringUtils.isNotEmpty(account.getAccountNumber())||account.getAccountType().getCodeInteger()>Account.BANK_TYPE){
				changedAccount = peopleBean.changeAccount(account, peopleMain.getId());
				//если поменяли счет, надо сохранить заявку
				creditRequest.setAccount(changedAccount);
				kassaBean.saveCreditRequestWithAccount(creditRequest);
				log.info("Сохранили заявку со счетом "+prmCreditRequestId);	
			}
					
						                
            //сохраняем данные заявки
            Map<String,Object> limits =null;
            if (creditRequest.getProduct()!=null){
               limits = productBean.getNewRequestProductConfig(creditRequest .getProduct().getId());
            } else {
               ProductsEntity product=productDAO.getProductDefault();	
               limits = productBean.getNewRequestProductConfig(product.getId());
               creditRequest.setProduct(new Products(product));
            }
          
            double stake = creditCalc.calcInitialStake(creditRequest.getCreditSum(), creditRequest.getCreditDays(), limits);
            Integer i=kassaBean.findMaxCreditRequestNumber(new Date());
            reloadCreditRequest(facesCtx, prmCreditRequestId);
            
            creditRequestEntity=kassaBean.newCreditRequest(creditRequestEntity,
            		creditRequest.getPeopleMain().getId(),CreditStatus.FILLED,
                	true,true,true,true,true,Partner.SYSTEM,null,new Date(),new Date(),new Date(),
                	i,creditRequest.getCreditSum(), creditRequest.getCreditDays(),
            		stake,"",null,GenUtils.genUniqueNumber(new Date(), i, stake * 100),null,null,RefCreditRequestWay.DIRECT,null);
            creditRequest=new CreditRequest(creditRequestEntity);
         
            generateAgreement(new Date());
            //сохранили данные
            int id = kassaBean.saveCreditRequest(creditRequest, "127.0.0.1", "");
			
			log.info("Сохранили заявку "+prmCreditRequestId);
			//запишем пользователя
		    String login=Convertor.fromMask(cellPhone.getValue().toLowerCase());
		    if (loginByEmail==1&&StringUtils.isNotEmpty(email.getValue())){
		      	login=email.getValue().toLowerCase();
		    }
			UsersEntity user = userBean.findUserByLogin(login);
			if (user==null){
				user=userBean.findUserByPeopleId(creditRequest.getPeopleMain().getId());
				//если у человека нет логина
				if (user==null){
				    user=userBean.addUserClient(pmain, login);
				    userBean.removeTempLink(user.getId());
				} else {
					userBean.updateUsername(user.getUsername(), login);
				}
			}
			//если заявка заполнена, можно идти дальше
			if (creditRequest.getStatus().getId()==CreditStatus.FILLED){
				
				List<WorkflowObjectStateDef> lstActO = workflow.getProcCROfflineWfActions(id, null, true);
				WorkflowObjectStateDef taskDef = WorkflowUtil.findWFState(lstActO, new StateRef("*","*","taskFillCROffline"));
				if (taskDef != null) {
					String msgMore = lstActO.get(0).getActions().get(0).getSignalRef();   
					workflow.goProcCROffline(id, msgMore, null);
				}
			}
			//пишем лог
			CreditRequestEntity crequest=crDAO.getCreditRequestEntity(creditRequest.getId());
			if (crequest!=null){
			    eventLog.saveLog("localhost",  eventLog.getEventType(EventType.INFO).getEntity(), eventLog.getEventCode(EventCode.MANAGER_CALL).getEntity(), "редактирование заявки менеджером ", crequest, null, userCtl.getUser().getEntity(),null,"","","","");
			}
		
			
        } catch (Exception ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
			
		}
	}
	
	public String cancel(){
		return "canceled";
	}
	
		
	private PeopleContact initPeopleContact(int codeInteger) {
		PeopleContact pc=peopleBean.initPeopleContact(peopleMain, codeInteger);
		return pc;
	}

	private PeopleMisc initPeopleMisc(){
		PeopleMisc pm=peopleBean.initPeopleMisc(peopleMain);
		if (pm.getMarriage()==null){
		    pm.setMarriage(refBook.getMarriageType(RefMarriageType.BACHELOR));
		}
		return pm;
	}
	
	private FiasAddress initAddress(int addrType){
		FiasAddress fa=peopleBean.initAddress(peopleMain, addrType);
	    return fa;
	}	
	
	private Documents initDocument(){
		Documents dc=peopleBean.initDocument(peopleMain);
		return dc;
	}
	
	private PeoplePersonal initPeople(){
		PeoplePersonal people=peopleBean.initPeoplePersonal(peopleMain);
		return people;
	}
	
	private Employment initEmployment(){
		Employment emp=peopleBean.initEmployment(peopleMain);
		if (emp.getTypeWork()==null){
		    emp.setTypeWork(refBook.getEmployType(RefWorkType.PERMANENT));
		}
		return emp;
	}
	
	private Credit initCredit(){
		Credit cr=creditBean.initCredit(peopleMain, prmCreditRequestId);
		cr.setCreditRelation(refBook.getCreditRelationType(BaseCredit.CREDIT_OWNER));
		return cr;
	}
	
	private Account initAccount(){
		Account ac=peopleBean.initAccount(peopleMain, Account.CARD_TYPE);
		return ac;
	}
	
	public void changeAccountType(ValueChangeEvent event){
		
		Reference i=(Reference) event.getNewValue();
		account.setAccountType(i);
		account.setAccountNumber(null);
		account.setCardNumber(null);
		account.setCardName(null);
		
	}
	
    public void changeMarriageType(ValueChangeEvent event){
		
		Reference i=(Reference) event.getNewValue();
		peopleMisc.setMarriage(i);
			
	}
	
    public void changeWorkType(ValueChangeEvent event){
		
		Reference i=(Reference) event.getNewValue();
		employment.setTypeWork(i);
			
	}
 
	public void changeEmail(ValueChangeEvent event){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		String emailNew=(String) event.getNewValue();
		if (StringUtils.isNotEmpty(emailNew)){
			PeopleContactEntity contactEmail = peopleBean.findPeopleByContactClient(PeopleContact.CONTACT_EMAIL, emailNew.toLowerCase());
			if (contactEmail!=null&&contactEmail.getPeopleMainId().getId()!=peopleMain.getId()){
				JSFUtils.handleAsError(facesCtx, "email", new Exception("Пользователь с email "+emailNew+" уже есть в системе"));
			}
		}
		
	}
	
	public void changePhone(ValueChangeEvent event){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		String phoneNew=(String) event.getNewValue();
		if (StringUtils.isNotEmpty(phoneNew)){
			PeopleContactEntity contactPhone = peopleBean.findPeopleByContactClient(PeopleContact.CONTACT_CELL_PHONE, Convertor.fromMask(phoneNew));
	        if (contactPhone != null&&contactPhone.getPeopleMainId().getId()!=peopleMain.getId()) {
				JSFUtils.handleAsError(facesCtx, "cellphone", new Exception("Пользователь с телефоном "+phoneNew+" уже есть в системе"));
			}
		}
		
	}
	
	public void changePassportSeries(ValueChangeEvent event){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		spasp=(String) event.getNewValue();
		if (StringUtils.isNotEmpty(spasp)&&StringUtils.isNotEmpty(npasp)){
			 DocumentEntity existingPasport=peopleBean.findDocument(Documents.PASSPORT_RF,spasp, npasp);
		     if (existingPasport!=null){
		    	UsersEntity usr=userBean.findUserByPeopleId(existingPasport.getPeopleMainId().getId());
		    	if (usr!=null&&usr.getPeopleMainId().getId()!=peopleMain.getId()){
				    JSFUtils.handleAsError(facesCtx, "ndoc", new Exception("Пользователь с паспортом "+spasp+" "+npasp+" уже есть в системе"));
		    	}
			}
		}
		
	}
	
	public void changePassportNumber(ValueChangeEvent event){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		npasp=(String) event.getNewValue();
		if (StringUtils.isNotEmpty(spasp)&&StringUtils.isNotEmpty(npasp)){
			 DocumentEntity existingPasport=peopleBean.findDocument(Documents.PASSPORT_RF,spasp, npasp);
		     if (existingPasport!=null){
		    	UsersEntity usr=userBean.findUserByPeopleId(existingPasport.getPeopleMainId().getId());
		    	if (usr!=null&&usr.getPeopleMainId().getId()!=peopleMain.getId()){
				    JSFUtils.handleAsError(facesCtx, "ndoc", new Exception("Пользователь с паспортом "+spasp+" "+npasp+" уже есть в системе"));
		    	}
			}
		}
		
	}
	
	public void sameChangedLsn(AjaxBehaviorEvent event) {
		HtmlSelectBooleanCheckbox chb = (HtmlSelectBooleanCheckbox) event.getComponent();
		Boolean bValue = (Boolean) chb.getValue();
		if (registerAddress != null) {
			registerAddress.setIsSame(bValue);
		}
	}
	
	protected void reloadCreditRequest(FacesContext facesCtx, Integer prmId) {
		try {
			//ищем как логинимся
			Map<String, Object> login_const=rulesBean.getLoginConstants();
		    String login_way = (String) login_const.get(SettingsKeys.LOGIN_WAY);
		    if (login_way.equalsIgnoreCase(SettingsKeys.LOGIN_EMAIL)){
		    	loginByEmail=1;
		    }
			creditRequest = crDAO.getCreditRequest(prmId, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_CONTACT,PeopleMain.Options.INIT_ADDRESS,PeopleMain.Options.INIT_DOCUMENT,PeopleMain.Options.INIT_PEOPLE_PERSONAL,PeopleMain.Options.INIT_PEOPLE_MISC));
			creditRequestEntity=crDAO.getCreditRequestEntity(prmId);
			Integer peopleId=creditRequest.getPeopleMain().getId();
			peopleMain = peopleDAO.getPeopleMain(peopleId, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_MISC, PeopleMain.Options.INIT_SPOUSE, PeopleMain.Options.INIT_PEOPLE_CONTACT,PeopleMain.Options.INIT_DOCUMENT,PeopleMain.Options.INIT_ADDRESS,PeopleMain.Options.INIT_ACCOUNTACTIVE,PeopleMain.Options.INIT_EMPLOYMENT,PeopleMain.Options.INIT_CREDIT));
			//инициализируем ПД
			peoplePersonal=peopleMain.getPeoplePersonalActive();
			if (peoplePersonal==null){
				peoplePersonal=initPeople();
			}
			//инициализируем контакты
			email=peopleMain.getEmail();
			if (email==null) {
				email=initPeopleContact(PeopleContact.CONTACT_EMAIL);
			}
			cellPhone=peopleMain.getCellPhone();
			if (cellPhone==null) {
				cellPhone=initPeopleContact(PeopleContact.CONTACT_CELL_PHONE);
			}
			//домашний телефон
			homePhone=peopleMain.getHomePhone();
			if (homePhone==null) {
				homePhone=initPeopleContact(PeopleContact.CONTACT_HOME_PHONE);
			}
			//телефон по адресу регистрации
			homePhoneReg=peopleMain.getHomePhoneReg();
			if (homePhoneReg==null) {
				homePhoneReg=initPeopleContact(PeopleContact.CONTACT_HOME_REGISTER_PHONE);
			}
			//рабочий телефон
			workPhone=peopleMain.getWorkPhone();
			if (workPhone==null) {
				workPhone=initPeopleContact(PeopleContact.CONTACT_WORK_PHONE);
			}
			//инициализируем доп.данные
			peopleMisc=peopleMain.getPeopleMiscActive();
			if (peopleMisc==null) {
				peopleMisc=initPeopleMisc();
			}
			//инициализируем занятость
			employment=peopleMain.getCurrentEmployment();
			if (employment==null) {
				employment=initEmployment();
			}
			//инициализируем документ
			passport=peopleMain.getActivePassport();
			if (passport==null) {
				passport=initDocument();
			}
		    //инициализируем супруга
			sp=peopleMain.getSpouseActive();
			if (sp!=null) {
				int pmain=sp.getPeopleMainSpouse().getId();
				PeopleMain spPmain=peopleDAO.getPeopleMain(pmain, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_CONTACT));
				surname=spPmain.getPeoplePersonalActive().getSurname();
				name=spPmain.getPeoplePersonalActive().getName();
				midname=spPmain.getPeoplePersonalActive().getMidname();
				birthdate=spPmain.getPeoplePersonalActive().getBirthDate();
				if (spPmain.getCellPhone()!=null){
				  phone=spPmain.getCellPhone().getValue();
				}
				typework=sp.getTypeWork();
				databeg=sp.getDatabeg();
			}
			//инициализируем кредит
			credit=peopleMain.getClientCredit();
			if (credit==null) {
				credit=initCredit();
			}
			//инициализируем счет
			if (creditRequest.getAccount()!=null){
				account=creditRequest.getAccount();
			} else if (peopleMain.getAccountsActive().size()>0){
				account=peopleMain.getAccountsActive().get(0);
			}  else {
				account=initAccount();
			}

			//инициализируем адрес регистрации
			registerAddress=peopleMain.getRegisterAddress();
			
			if (registerAddress == null) {
				registerAddress = initAddress(FiasAddress.REGISTER_ADDRESS);
			}
			
			registerAddressCtl.setAddress(registerAddress);

			//инициализируем рабочий адрес
			workAddress=peopleMain.getWorkAddress();
		
			if (workAddress == null) {
				workAddress = initAddress(FiasAddress.WORKING_ADDRESS);
			}
			workAddressCtl.setAddress(workAddress);
			
			//инициализируем адрес проживания
			residentAddress = peopleMain.getResidentAddress();
			if (residentAddress == null) {
				residentAddress = initAddress(FiasAddress.RESIDENT_ADDRESS);
			}		
			residentAddressCtl.setAddress(residentAddress);
			
			// инициализируем переменные из стратегии
			lastCalcValues = modelBean.listLastParamValues(CreditRequest.class.getName(), prmId);
			if (lastCalcValues.size() > 0) {
				lastModel = modelBean.getLastModel((Integer) (lastCalcValues.get(0).get("aiModelParamId")));
			} else {
				lastModel = null;
			}			
		} catch (Exception e) {
			JSFUtils.handleAsError(facesCtx, null, e);
		}

	}
	
	public PeoplePersonal getPeoplePersonal(){
		return peoplePersonal;
	}
	
	
	public PeopleMisc getPeopleMisc(){
		return peopleMisc;
	}
	
	public Documents getPassport(){
		return passport;
	}
	
	public Spouse getSp(){
		return sp;
	}
	
	public Account getAccount(){
		return account;
	}
	
	public Credit getCredit(){
		return credit;
	}
	
	public PeopleContact getEmail(){
		return email;
	}
	
	public PeopleContact getCellPhone(){
		return cellPhone;
	}
	
	public PeopleContact getHomePhone(){
		return homePhone;
	}
	
	public PeopleContact getHomePhoneReg(){
		return homePhoneReg;
	}
	
	public PeopleContact getWorkPhone(){
		return workPhone;
	}
	
	public Employment getEmployment(){
		return employment;
	}
	
	public String getSurname(){
		return surname;
	}
	
	public void setSurname(String surname){
		this.surname=surname;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getMidname(){
		return midname;
	}
	
	public void setMidname(String midname){
		this.midname=midname;
	}
	
	public String getPhone(){
		return phone;
	}
	
	public void setPhone(String phone){
		this.phone=phone;
	}
	
	public Date getBirthdate(){
		return birthdate;
	}
	
	public void setBirthdate(Date birthdate){
		this.birthdate=birthdate;
	}
	
	public Date getDatabeg(){
		return databeg;
	}
	
	public void setDatabeg(Date databeg){
		this.databeg=databeg;
	}
	
	public Reference getTypework(){
		return typework;
	}
	
	public void setTypework(Reference typework){
		this.typework=typework;
	}
	
	public CreditRequest getCreditRequest() {
		return creditRequest;
	}

	public Integer getPrmCreditRequestId() {
		return prmCreditRequestId;
	}

	public void setPrmCreditRequestId(Integer prmCreditRequestId) {
		this.prmCreditRequestId = prmCreditRequestId;
	}
	
	public FiasAddress getRegisterAddress(){
		return registerAddress;
	}
	
	public void setRegisterAddress(FiasAddress registerAddress){
		this.registerAddress=registerAddress;
	}
	
	public void setResidentAddress(FiasAddress residentAddress){
		this.residentAddress=residentAddress;
	}
	
	public void setWorkAddress(FiasAddress workAddress){
		this.workAddress=workAddress;
	}
	
	public FiasAddress getResidentAddress(){
		return residentAddress;
	}
	
	public FiasAddress getWorkAddress(){
		return workAddress;
	}
	
	public ViewAddressController getRegisterAddressCtl() {
		return registerAddressCtl;
	}

	public void setRegisterAddressCtl(ViewAddressController registerAddressCtl) {
		this.registerAddressCtl = registerAddressCtl;
	}

	public ViewAddressController getResidentAddressCtl() {
		return residentAddressCtl;
	}

	public void setResidentAddressCtl(ViewAddressController residentAddressCtl) {
		this.residentAddressCtl = residentAddressCtl;
	}
	
	public ViewAddressController getWorkAddressCtl() {
		return workAddressCtl;
	}

	public void setWorkAddressCtl(ViewAddressController workAddressCtl) {
		this.workAddressCtl = workAddressCtl;
	}
	
	public Integer getActype(){
		return actype;
	}
	
	public void setActype(Integer actype){
		this.actype=actype;
	}
	
		
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password){
		this.password=password;
	}
	
	 private void generateAgreement(Date dateOferta) throws KassaException {
	    String agreement = kassaBean.generateAgreement(creditRequest, dateOferta,0);
	    creditRequest.setAgreement(agreement);
	 }

	 public boolean getCanEditCreditRequest() {
		 return userCtl.hasRole(Roles.ROLE_ADMIN);
	 }

	public CreditRequestEntity getCreditRequestEntity() {
		return creditRequestEntity;
	}

	public void setCreditRequestEntity(CreditRequestEntity creditRequestEntity) {
		this.creditRequestEntity = creditRequestEntity;
	}

	public Integer getShowCredit() {
		return showCredit;
	}

	public void setShowCredit(Integer showCredit) {
		this.showCredit = showCredit;
	}

	public String getSpasp() {
		return spasp;
	}

	public void setSpasp(String spasp) {
		this.spasp = spasp;
	}

	public String getNpasp() {
		return npasp;
	}

	public void setNpasp(String npasp) {
		this.npasp = npasp;
	}

	public Integer getLoginByEmail() {
		return loginByEmail;
	}

	public void setLoginByEmail(Integer loginByEmail) {
		this.loginByEmail = loginByEmail;
	}

	
	
}
