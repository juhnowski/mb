package ru.simplgroupp.webapp.manager.admin.controller;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.persistence.UsersEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.EventCode;
import ru.simplgroupp.transfer.EventType;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.Users;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

/**
 * контроллер для поиска и добавления пользователей-сотрудников
 */
public class ListUsersController extends AbstractListController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ListUsersController.class.getName());

	@EJB
	UsersBeanLocal userBean;
	
	@EJB
	KassaBeanLocal kassaBean;
	
	@EJB
	ReferenceBooksLocal refBook;
	
	@EJB 
	EventLogService eventLog;
	
	/**
	 * имя входа специалиста для поиска
	 */
	protected String prmUserName;
	/**
	 * фамилия специалиста для поиска
	 */
	protected String prmSurname;
	protected Integer prmUserType=Users.USER_SPECIALIST;
	/**
	 * id роли для поиска
	 */
	protected Integer prmRole;
	
	@PostConstruct
	public void init() {
		super.init();
	}
	
	public class UsersDataModel extends StdRequestDataModel<Users>{

		public UsersDataModel(){
			super();
			multipleSorting = true;
		}
		
		@Override
		protected List<Users> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			return userBean.listUsers(seqRange.getFirstRow(), seqRange.getRows(), sorting, null, prmUserName, null, prmUserType, prmRole,prmSurname);
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return userBean.countUsers(prmUserName, null, prmUserType, prmRole,prmSurname);
		}

		@Override
		public Users getRowData() {
			if (rowKey == null)
				return null;
			else {
				Users users=userBean.getUser(((Number) rowKey).intValue(), Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL,Users.Options.INIT_ROLES));
				return users;
			}
		
		}
		
	}
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel createModel() {
	  return new UsersDataModel();
	}

	public String getPrmSurname() {
		return prmSurname;
	}

	public void setPrmSurname(String prmSurname) {
		this.prmSurname = prmSurname;
	}
	
	public String getPrmUserName() {
		return prmUserName;
	}

	public void setPrmUserName(String prmUserName) {
		this.prmUserName = prmUserName;
	}
	
	public Integer getPrmUserType() {
		return prmUserType;
	}

	public void setPrmUserType(Integer prmUserType) {
		this.prmUserType = prmUserType;
	}
	
	public Integer getPrmRole() {
		return prmRole;
	}

	public void setPrmRole(Integer prmRole) {
		this.prmRole = prmRole;
	}
	
    protected void nullIfEmpty() {
		
		if (StringUtils.isEmpty(prmSurname)) {
			prmSurname = null;
		}
		
		if (StringUtils.isEmpty(prmUserName)) {
			prmUserName = null;
		}
		
		if (JSFUtils.NULL_INT_VALUE.equals(prmRole)) {
			prmRole = null;
		}	
	    prmUserType=Users.USER_SPECIALIST;
    }
    
    public void clearLsn(ActionEvent event) {
		prmSurname=null;
		prmUserName=null;
		prmRole=null;
		prmUserType=Users.USER_SPECIALIST;
		
		refreshSearch();
	}
    
    public String dummy() {
		return null;
	}
    
    public void deleteItem(ActionEvent event) {
		
	  if ( event.getComponent().getAttributes().get("userid")!=null) {
		  Integer id =Convertor.toInteger(event.getComponent().getAttributes().get("userid"));
  	      userBean.removeUser(id);
  	      logger.info("Удалили пользователя "+id);
		  refreshSearch();
		}
	}
    
    public String addUser(){
    	UsersEntity usr=userBean.addUser(null, "", "", "", Users.USER_SPECIALIST, "user1", "", null);
    	if (usr!=null) {
    		try {
				eventLog.saveLog("localhost", eventLog.getEventType(EventType.INFO).getEntity(), eventLog.getEventCode(EventCode.ADD_USER).getEntity(),
					"Добавление пользователя в систему ", null, usr.getPeopleMainId(), userCtl.getUser().getEntity(), null,"", "", "", "");
			} catch (KassaException e) {
				logger.log(Level.INFO, "Не удалось записать лог", e);
			}
    	    int id=usr.getId();
    	    return "edit?faces-redirect=true&userid="+String.valueOf(id);
    	} else {
    		return null;
    	}
    	
    }
}
