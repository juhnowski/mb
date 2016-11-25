package ru.simplgroupp.webapp.manager.callback.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;

import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.DateRange;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.transfer.ActiveStatus;
import ru.simplgroupp.transfer.CallBack;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;

public class ListCallbackController extends AbstractListController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1783011936904833892L;
	@EJB
	protected PeopleBeanLocal peopleBean;
	@EJB
	protected PeopleDAO peopleDAO;
	/**
	 * дата подачи заявки на обратный звонок
	 */
	protected DateRange prmDateRequest = new DateRange(null, null);	
	
	/**
	 * дата взятия в работу
	 */
	protected DateRange prmDateCall = new DateRange(null, null);
	/**
	 * фамилия
	 */
	protected String prmSurname;
	/**
	 * телефон
	 */
	protected String prmPhone;
	/**
	 * email
	 */
	protected String prmEmail;
	/**
	 * список новых заявок
	 */
	protected List<CallBack> newList;
	/**
	 * тип подавшего заявку
	 */
	protected Integer prmType;
	
	protected Integer newCount;
	
	@PostConstruct
	public void init() {
		super.init();
		
	}
	
	@Override
	public void initData() throws Exception {
		reloadList();
		newCount=newList.size();
	}

	protected List<CallBack> reloadList(){
		newList= peopleBean.listCallBack(-1, -1, null, null, null, null, null, null, null, ActiveStatus.DRAFT,null);
		return newList;
	}

	public List<CallBack> getNewList() {
		return newList;
	}
	
	
	public Integer getNewCount() {
		return newCount;
	}

	public String takeCallback(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
		if (prms.containsKey("callid")) {
			Integer id= Convertor.toInteger(prms.get("callid"));
			CallBack callBack=peopleDAO.getCallBack(id);
			if (callBack!=null){
				peopleBean.saveCallBackWithUser(callBack, userCtl.getUser().getId());
				return "view?faces-redirect=true&callid="+id;
			}
		}
		return null;
	}
	
	@Override
	public StdRequestDataModel<CallBack> createModel() {
		return new CallbackDataModel();
	}

	public class CallbackDataModel extends StdRequestDataModel<CallBack>{
		
		public CallbackDataModel() {
			super();
			multipleSorting = true;
		}

		@Override
		protected List<CallBack> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			
			return peopleBean.listCallBack(seqRange.getFirstRow(), seqRange.getRows(), null, prmSurname, null, prmPhone, prmEmail, prmDateRequest, prmDateCall, ActiveStatus.ACTIVE,prmType);
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			
			return peopleBean.countCallBack(prmSurname, null, prmPhone, prmEmail, prmDateRequest, prmDateCall, ActiveStatus.ACTIVE,prmType);
		}

		@Override
		public CallBack getRowData() {
			if (rowKey == null)
				return null;
			else {
				return peopleDAO.getCallBack(((Number) rowKey).intValue());
			}
		}
		
	}
	
	public void clearLsn(ActionEvent event) {
		prmDateCall=new DateRange(null,null);
		prmDateRequest=new DateRange(null,null);
		prmPhone=null;
		prmSurname=null;
		prmEmail=null;
		prmType=null;
		refreshSearch();
	}
	public DateRange getPrmDateRequest() {
		return prmDateRequest;
	}

	public void setPrmDateRequest(DateRange prmDateRequest) {
		this.prmDateRequest = prmDateRequest;
	}

	public DateRange getPrmDateCall() {
		return prmDateCall;
	}

	public void setPrmDateCall(DateRange prmDateCall) {
		this.prmDateCall = prmDateCall;
	}

	public String getPrmSurname() {
		return prmSurname;
	}

	public void setPrmSurname(String prmSurname) {
		this.prmSurname = prmSurname;
	}

	public String getPrmPhone() {
		return prmPhone;
	}

	public void setPrmPhone(String prmPhone) {
		this.prmPhone = prmPhone;
	}

	public String getPrmEmail() {
		return prmEmail;
	}

	public void setPrmEmail(String prmEmail) {
		this.prmEmail = prmEmail;
	}

	public Integer getPrmType() {
		return prmType;
	}

	public void setPrmType(Integer prmType) {
		this.prmType = prmType;
	}

	public String dummy() {
		return null;
	}
	
}
