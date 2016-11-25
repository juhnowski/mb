package ru.simplgroupp.webapp.manager.ejb;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.exception.KassaRuntimeException;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.persistence.PeopleMainEntity;
import ru.simplgroupp.transfer.ActiveStatus;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.transfer.PeopleContact;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.Products;


@Stateless
@LocalBean
public class AppBeanMgr {
	
	@EJB
	PeopleBeanLocal peopleServ;
	
	@EJB
	PeopleDAO peopleDAO;
	
	@EJB
	KassaBeanLocal kassaBean;
	
	@EJB
	UsersBeanLocal userBean;
	
	@EJB
	WorkflowBeanLocal wfBean;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)	
	public CreditRequest createCRNewPeople(double creditSum, int creditDays, String surname, String name, String midname, Date dateBirth, String paspSeries, String paspNumber, String snils, String inn, String email, String phone) {
		try {
		
			PeopleMainEntity peopleMain=peopleServ.savePeopleMain(null, inn, snils);
		    PeopleMain pmain=new PeopleMain(peopleMain);
		    if (StringUtils.isNotEmpty(surname)&&StringUtils.isNotEmpty(name)){
		        peopleServ.newPeoplePersonal(null,peopleMain.getId(), null, Partner.CLIENT, surname, name, midname, 
				    null, dateBirth, null, null, new Date(), ActiveStatus.ACTIVE);
		    }
		    if (StringUtils.isNotEmpty(phone)){
		        peopleServ.setPeopleContactClient(peopleMain, PeopleContact.CONTACT_CELL_PHONE, phone, false);
		    }
		    if (StringUtils.isNotEmpty(paspSeries)&&StringUtils.isNotEmpty(paspNumber)){
		        peopleServ.newDocument(null, peopleMain.getId(), null, Partner.CLIENT, paspSeries, paspNumber, null, null, null, ActiveStatus.ACTIVE);
		    }
		  
		    if (StringUtils.isNotEmpty(email)){
		        peopleServ.setPeopleContactClient(peopleMain, PeopleContact.CONTACT_EMAIL, email, false);
		        userBean.addUserClient(peopleMain, email);
	  
		    }
			CreditRequest cr = kassaBean.createOffline(creditSum, creditDays, pmain, Products.ZAEM_CODE,null);
			
			wfBean.startOrFindProcCROffline(cr.getId(), null);
			return cr;
		} catch (Exception ex) {
			throw new KassaRuntimeException(ex);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)	
	public CreditRequest createCR(double creditSum, int creditDays, int peopleId) {
		try {
			PeopleMain pmain = peopleDAO.getPeopleMain(peopleId, null);
			CreditRequest cr = kassaBean.createOffline(creditSum, creditDays, pmain, Products.ZAEM_CODE,null);
			wfBean.startOrFindProcCROffline(cr.getId(),null);
			return cr;
		} catch (Exception ex) {
			throw new KassaRuntimeException(ex);
		}
	}	
	
	
}
