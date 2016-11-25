package ru.simplgroupp.webapp.analysis.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.admnkz.crypto.data.Certificate;

import ru.simplgroupp.dao.interfaces.BizActionDAO;
import ru.simplgroupp.interfaces.BizActionBeanLocal;
import ru.simplgroupp.interfaces.ModelBeanLocal;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.ActiveStatus;
import ru.simplgroupp.transfer.BizActionType;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.Prolong;
import ru.simplgroupp.transfer.Question;
import ru.simplgroupp.transfer.UploadStatus;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ReferenceController extends AbstractSessionController implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4138905723971741776L;

	private static Map<String,String> bizClasses ;
	
	static {
		bizClasses = Utils.mapOf(
			CreditRequest.class.getName(), "Заявка",
			Credit.class.getName(), "Кредит",
			PeopleMain.class.getName(), "Клиент",
			Payment.class.getName(), "Платёж",
			Prolong.class.getName(), "Продление"				
		);
	}
	
	@EJB
	ModelBeanLocal modelBean;	
	
	@EJB
	BizActionBeanLocal bizBean;
	
	@EJB
	BizActionDAO bizDAO;

	public List<SelectItem> getScriptTypes() {
		List<String> lstSource = modelBean.getScriptTypesSupported();
		List<SelectItem> lstRes = new ArrayList<SelectItem>(lstSource.size());
		for (String scType: lstSource) {
			lstRes.add(new SelectItem(scType, scType));
		}
		return lstRes;
	}
	
	public List<SelectItem> getAnswerTypes() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(6);
		SelectItem si = new SelectItem();
		si.setValue(null);
		si.setLabel("---любой---");
		lstRes.add(si);
		lstRes.add(new SelectItem(Question.ANSWER_SINGLE, "можно выбрать один вариант"));
		lstRes.add(new SelectItem(Question.ANSWER_MULTI, "можно выбрать несколько вариантов"));
		lstRes.add(new SelectItem(Question.ANSWER_NUMERIC, "Число"));
		lstRes.add(new SelectItem(Question.ANSWER_MONEY, "Деньги"));
		lstRes.add(new SelectItem(Question.ANSWER_DATE, "Дата"));
		lstRes.add(new SelectItem(Question.ANSWER_STRING, "Строка"));
		return lstRes;
	}
	
	public List<SelectItem> getActiveTypes() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(2);
		lstRes.add(new SelectItem(ActiveStatus.ACTIVE, "активный"));
		lstRes.add(new SelectItem(ActiveStatus.ARCHIVE, "архивный"));
		return lstRes;
	}
	
	public List<SelectItem> getDeletedTypes() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(2);
		lstRes.add(new SelectItem(ActiveStatus.ACTIVE, "удалена"));
		lstRes.add(new SelectItem(ActiveStatus.ARCHIVE, "активная"));
		return lstRes;
	}
	
	public String getBusinessObjectClassName(String className) {
		return bizClasses.get(className);
	}
	
	public List<SelectItem> getBusinessObjectClasses() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(bizClasses.size());
		for (Map.Entry<String,String> ent: bizClasses.entrySet()) {
			lstRes.add(new SelectItem(ent.getKey(), ent.getValue()));	
		}
		return lstRes;		
	}
	
	public BizActionType getBizActionType(int id) {
		return bizDAO.getBizActionType(id);
	}
	
	public List<SelectItem> getBizActionTypes() {
		List<BizActionType> lstBiz = bizDAO.getBizActionTypes();
		List<SelectItem> lstRes = new ArrayList<SelectItem>(lstBiz.size());
		for (BizActionType bizt: lstBiz) {
			lstRes.add(new SelectItem(bizt.getId(), bizt.getName()));	
		}
		return lstRes;
	}
	
	public List<SelectItem> getDataTypes() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(4);
		lstRes.add(new SelectItem("C", "строка"));
		lstRes.add(new SelectItem("N", "целое число"));
		lstRes.add(new SelectItem("F", "десятичное число"));
		lstRes.add(new SelectItem("D", "дата гггг-мм-дд"));
		return lstRes;
	}	
	
	public List<SelectItem> getRuleScriptTypes() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(4);
		lstRes.add(new SelectItem(1, "JS скрипт"));
		lstRes.add(new SelectItem(2, "Knowlegde base(java)"));
		return lstRes;
	}	
	
	public List<SelectItem> getCertTypesNull() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(6);
		SelectItem si = new SelectItem();
		si.setValue(null);
		si.setLabel("---любой---");
		lstRes.add(si);
		lstRes.add(new SelectItem(Certificate.SUBJECT_ROOT_CA,"Корневой CA"));
		lstRes.add(new SelectItem(Certificate.SUBJECT_SUB_CA,"Промежуточный CA"));
		lstRes.add(new SelectItem(Certificate.SUBJECT_END_USER,"Конечный сертификат"));
		return lstRes;
	}
	
	public List<SelectItem> getUploadTypesNull() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(6);
		SelectItem si = new SelectItem();
		si.setValue(null);
		si.setLabel("---любой---");
		lstRes.add(si);
		lstRes.add(new SelectItem(UploadStatus.UPLOAD_CREDIT,"Кредиты"));
		lstRes.add(new SelectItem(UploadStatus.UPLOAD_CREDITREQUEST,"Заявки"));
		lstRes.add(new SelectItem(UploadStatus.UPLOAD_CREDIT_CREDITREQUEST,"Кредиты и заявки"));
		lstRes.add(new SelectItem(UploadStatus.UPLOAD_CREDIT_CORRECTION,"Корректировочный файл по 1 кредиту"));
		lstRes.add(new SelectItem(UploadStatus.UPLOAD_CREDIT_DELETE,"Удаление кредита из БД партнера"));
		lstRes.add(new SelectItem(UploadStatus.UPLOAD_CREDIT_CORRECT_ERRORS,"Выгрузка кредитов по номерам"));
		return lstRes;
	}
	
	public List<SelectItem> getProviderNull() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(6);
		SelectItem si = new SelectItem();
		si.setValue(null);
		si.setLabel("---любой---");
		lstRes.add(si);
		lstRes.add(new SelectItem("JCP","Крипто-про"));
		lstRes.add(new SelectItem("BC","Bouncy Castle"));
		lstRes.add(new SelectItem("SC","Сигнал-ком"));
		lstRes.add(new SelectItem("RSA","RSA"));
		lstRes.add(new SelectItem("SUN","SUN"));
		return lstRes;
	}
	
	public List<SelectItem> getTables() {
		List<SelectItem> lstRes = new ArrayList<SelectItem>(36);
		lstRes.add(new SelectItem("all","--все таблицы--"));
		lstRes.add(new SelectItem("address","address"));
		lstRes.add(new SelectItem("blacklist","blacklist"));
		lstRes.add(new SelectItem("credit","credit"));
		lstRes.add(new SelectItem("creditrequest","creditrequest"));
		lstRes.add(new SelectItem("creditstatus","creditstatus"));
		lstRes.add(new SelectItem("debt","debt"));
		lstRes.add(new SelectItem("device_info","device_info"));
		lstRes.add(new SelectItem("document","document"));
		lstRes.add(new SelectItem("employment","employment"));
		lstRes.add(new SelectItem("eventlog","eventlog"));
		lstRes.add(new SelectItem("eventcode","eventcode"));
		lstRes.add(new SelectItem("eventtype","eventtype"));
		lstRes.add(new SelectItem("misc","misc"));
		lstRes.add(new SelectItem("negative","negative"));
		lstRes.add(new SelectItem("payment","payment"));
		lstRes.add(new SelectItem("peoplecontact","peoplecontact"));
		lstRes.add(new SelectItem("peoplemain","peoplemain"));
		lstRes.add(new SelectItem("peoplemisc","peoplemisc"));
		lstRes.add(new SelectItem("peoplepersonal","peoplepersonal"));
		lstRes.add(new SelectItem("phonepay","phonepay"));
		lstRes.add(new SelectItem("phonepaysummary","phonepaysummary"));
		lstRes.add(new SelectItem("prolong","prolong"));
		lstRes.add(new SelectItem("qarequest","qarequest"));
		lstRes.add(new SelectItem("question","question"));
		lstRes.add(new SelectItem("questionvariant","questionvariant"));
		lstRes.add(new SelectItem("ref_header","ref_header"));
		lstRes.add(new SelectItem("regions","regions"));
		lstRes.add(new SelectItem("regionsnew","regionsnew"));
		lstRes.add(new SelectItem("reference","reference"));
		lstRes.add(new SelectItem("ref_blacklist","ref_blacklist"));
		lstRes.add(new SelectItem("ref_negative","ref_negative"));
		lstRes.add(new SelectItem("refusereason","refusereason"));
		lstRes.add(new SelectItem("scoring","scoring"));
		lstRes.add(new SelectItem("spouse","spouse"));
		lstRes.add(new SelectItem("verification","verification"));
		return lstRes;
	}
}
