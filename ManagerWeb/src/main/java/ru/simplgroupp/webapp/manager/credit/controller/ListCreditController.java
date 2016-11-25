package ru.simplgroupp.webapp.manager.credit.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.interfaces.CreditBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.toolkit.common.DateRange;
import ru.simplgroupp.toolkit.common.MoneyRange;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.BaseCredit;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.controller.IEventListener;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListCreditController extends AbstractListController implements Serializable, IEventListener{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	protected KassaBeanLocal kassa;
	
	 @EJB
	 protected CreditBeanLocal creditBean;
	 
    @EJB
	protected CreditDAO creditDAO;
	 
	protected Boolean searchFull = false;
	/**
	 * номер
	 */
	protected String prmNomer;
	/**
	 * дата начала в кратком поиске
	 */
	protected DateRange prmDatabeg1 = new DateRange(null, null);	
	/**
	 * дата окончания по графику в кратком поиске
	 */
	protected DateRange prmDataend1 = new DateRange(null, null);	
	/**
	 * дата окончания фактическая в кратком поиске
	 */
	protected DateRange prmDataendfact1 = new DateRange(null, null);	
	/**
	 * сумма кредита в кратком поиске
	 */
	protected MoneyRange prmCreditsum1 = new MoneyRange(null, null);
	/**
	 * дата начала в расширенном поиске
	 */
	protected DateRange prmDatabeg2 = new DateRange(null, null);	
	/**
	 * дата окончания по графику в расширенном поиске
	 */
	protected DateRange prmDataend2 = new DateRange(null, null);	
	/**
	 * дата окончания фактическая в расширенном поиске
	 */
	protected DateRange prmDataendfact2 = new DateRange(null, null);	
	/**
	 * сумма кредита в расширенном поиске
	 */
	protected MoneyRange prmCreditsum2 = new MoneyRange(null, null);
	
	protected DateRange prmDatabeg = new DateRange(null, null);	
	protected DateRange prmDataend = new DateRange(null, null);	
	protected DateRange prmDataendfact = new DateRange(null, null);	
	protected MoneyRange prmCreditsum = new MoneyRange(null, null);
	protected String prmSurname;
	/**
	 * фамилия в кратком поиске
	 */
	protected String prmSurname1;
	/**
	 * фамилия в расширенном поиске
	 */
	protected String prmSurname2;
	/**
	 * статус кредита
	 */
	protected Integer prmStatusId;
	/**
	 * завершен ли кредит
	 */
	protected Integer prmIsOver;
	/**
	 * рабочее состояние
	 */
	protected Integer prmIsActive;
	/**
	 * рабочее состояние в кратком поиске
	 */
	protected Integer prmIsActive1;
	/**
	 * рабочее состояние в расширенном поиске
	 */
	protected Integer prmIsActive2;
    /**
     * выдан свойе организацией
     */
	protected Integer prmIsSameorg;
	//по умолчанию показываем кредиты только свои
	protected Integer prmPartnerId=Partner.SYSTEM;
	/**
	 * вид кредита
	 */
	protected Integer prmTypeId;
	/**
	 * ставка в день
	 */
	protected MoneyRange prmCreditstake = new MoneyRange(null, null);
	
	@PostConstruct
	public void init() {
		super.init();
	}
	
	public class CreditDataModel extends StdRequestDataModel<Credit>{
		
		public CreditDataModel() {
			super();
			multipleSorting = true;
		}

		@Override
		protected List<Credit> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			Integer pplmainId=null;
			Integer accId=null;
			prmSurname=StringUtils.isEmpty(prmSurname1)?prmSurname2:prmSurname1;
			prmDatabeg=(prmDatabeg1.getFrom()==null&&prmDatabeg1.getTo()==null)?prmDatabeg2:prmDatabeg1;
			prmDataend=(prmDataend1.getFrom()==null&&prmDataend1.getTo()==null)?prmDataend2:prmDataend1;
			prmDataendfact=(prmDataendfact1.getFrom()==null&&prmDataendfact1.getTo()==null)?prmDataendfact2:prmDataendfact1;
			prmCreditsum=(prmCreditsum1.getFrom()==null&&prmCreditsum1.getTo()==null)?prmCreditsum2:prmCreditsum1;
			prmIsActive=(prmIsActive1==null)?prmIsActive2:prmIsActive1;
			return creditBean.listCredit(seqRange.getFirstRow(), seqRange.getRows(), sorting, null, pplmainId, accId, prmPartnerId, prmDatabeg, 
					prmDataend, prmTypeId, Utils.triStateToBoolean( prmIsOver), prmNomer, Utils.triStateToBoolean( prmIsSameorg ), prmDataendfact, 
					prmStatusId, prmCreditsum, prmCreditstake,prmSurname,prmIsActive);
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			Integer pplmainId=null;
			Integer accId=null;
			prmSurname=StringUtils.isEmpty(prmSurname1)?prmSurname2:prmSurname1;
			prmDatabeg=(prmDatabeg1.getFrom()==null&&prmDatabeg1.getTo()==null)?prmDatabeg2:prmDatabeg1;
			prmDataend=(prmDataend1.getFrom()==null&&prmDataend1.getTo()==null)?prmDataend2:prmDataend1;
			prmDataendfact=(prmDataendfact1.getFrom()==null&&prmDataendfact1.getTo()==null)?prmDataendfact2:prmDataendfact1;
			prmCreditsum=(prmCreditsum1.getFrom()==null&&prmCreditsum1.getTo()==null)?prmCreditsum2:prmCreditsum1;
			prmIsActive=(prmIsActive1==null)?prmIsActive2:prmIsActive1;
			return creditBean.countCredit(pplmainId, accId, prmPartnerId,prmDatabeg, prmDataend, prmTypeId, 
					Utils.triStateToBoolean( prmIsOver ), prmNomer, Utils.triStateToBoolean( prmIsSameorg), prmDataendfact, 
					prmStatusId, prmCreditsum, prmCreditstake,prmSurname,prmIsActive);
		}

		@Override
		public Credit getRowData() {
			if (rowKey == null)
				return null;
			else {
				Credit cred = creditDAO.getCredit(((Number) rowKey).intValue(), Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST,PeopleMain.Options.INIT_PEOPLE_PERSONAL,PeopleMain.Options.INIT_DOCUMENT));
				return cred;
	
			}
		}
	}
	
	@Override
	public void eventFired(String eventName, Object eventSource)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel<Credit> createModel() {
		return new CreditDataModel();
	}
	
	public Boolean getSearchFull() {
		return searchFull;
	}

	public void setSearchFull(Boolean searchFull) {
		this.searchFull = searchFull;
	}

	public DateRange getPrmDatabeg() {
		return prmDatabeg;
	}

	public void setPrmDatabeg(DateRange prmDatabeg) {
		this.prmDatabeg = prmDatabeg;
	}

	public DateRange getPrmDataend() {
		return prmDataend;
	}

	public void setPrmDataend(DateRange prmDataend) {
		this.prmDataend = prmDataend;
	}
	
	public DateRange getPrmDataendfact() {
		return prmDataendfact;
	}

	public void setPrmDataendfact(DateRange prmDataendfact) {
		this.prmDataendfact = prmDataendfact;
	}
	
	public MoneyRange getPrmCreditsum() {
		return prmCreditsum;
	}

	public void setPrmCreditsum(MoneyRange prmCreditsum) {
		this.prmCreditsum = prmCreditsum;
	}
	
	
	public DateRange getPrmDatabeg1() {
		return prmDatabeg1;
	}

	public void setPrmDatabeg1(DateRange prmDatabeg1) {
		this.prmDatabeg1 = prmDatabeg1;
	}

	public DateRange getPrmDataend1() {
		return prmDataend1;
	}

	public void setPrmDataend1(DateRange prmDataend1) {
		this.prmDataend1 = prmDataend1;
	}
	
	public DateRange getPrmDataendfact1() {
		return prmDataendfact1;
	}

	public void setPrmDataendfact1(DateRange prmDataendfact1) {
		this.prmDataendfact1 = prmDataendfact1;
	}
	
	public MoneyRange getPrmCreditsum1() {
		return prmCreditsum1;
	}

	public void setPrmCreditsum1(MoneyRange prmCreditsum1) {
		this.prmCreditsum1 = prmCreditsum1;
	}

	public DateRange getPrmDatabeg2() {
		return prmDatabeg2;
	}

	public void setPrmDatabeg2(DateRange prmDatabeg2) {
		this.prmDatabeg2 = prmDatabeg2;
	}

	public DateRange getPrmDataend2() {
		return prmDataend2;
	}

	public void setPrmDataend2(DateRange prmDataend2) {
		this.prmDataend2 = prmDataend2;
	}
	
	public DateRange getPrmDataendfact2() {
		return prmDataendfact2;
	}

	public void setPrmDataendfact2(DateRange prmDataendfact2) {
		this.prmDataendfact2 = prmDataendfact2;
	}
	
	public MoneyRange getPrmCreditsum2() {
		return prmCreditsum2;
	}

	public void setPrmCreditsum2(MoneyRange prmCreditsum2) {
		this.prmCreditsum2 = prmCreditsum2;
	}

	public String getPrmNomer() {
		return prmNomer;
	}

	public void setPrmNomer(String prmNomer) {
		this.prmNomer = prmNomer;
	}
	
	public Integer getPrmStatusId() {
		return prmStatusId;
	}

	public void setPrmStatusId(Integer prmStatusId) {
		this.prmStatusId = prmStatusId;
	}

	public Integer getPrmPartnerId() {
		return prmPartnerId;
	}

	public void setPrmPartnerId(Integer prmPartnerId) {
		this.prmPartnerId = prmPartnerId;
	}
	
	public Integer getPrmTypeId() {
		return prmTypeId;
	}

	public void setPrmTypeId(Integer prmTypeId) {
		this.prmTypeId = prmTypeId;
	}
	
	public Integer getPrmIsOver() {
		return prmIsOver;
	}

	public void setPrmIsOver(Integer prmIsOver) {
		this.prmIsOver = prmIsOver;
	}
	
	public Integer getPrmIsSameorg() {
		return prmIsSameorg;
	}

	public void setPrmIsSameorg(Integer prmIsSameorg) {
		this.prmIsSameorg = prmIsSameorg;
	}
	
	public MoneyRange getPrmCreditstake() {
		return prmCreditstake;
	}

	public void setPrmCreditstake(MoneyRange prmCreditstake) {
		this.prmCreditstake = prmCreditstake;
	}
	
	public String getPrmSurname() {
		return prmSurname;
	}

	public void setPrmSurname(String prmSurname) {
		this.prmSurname = prmSurname;
	}
	
	public String getPrmSurname1() {
		return prmSurname1;
	}

	public void setPrmSurname1(String prmSurname1) {
		this.prmSurname1 = prmSurname1;
	}
	
	public String getPrmSurname2() {
		return prmSurname2;
	}

	public void setPrmSurname2(String prmSurname2) {
		this.prmSurname2 = prmSurname2;
	}
	
	public Integer getPrmIsActive() {
		return prmIsActive;
	}

	public void setPrmIsActive(Integer prmIsActive) {
		this.prmIsActive = prmIsActive;
	}

	public String dummy() {
		return null;
	}
	
	public Integer getPrmIsActive1() {
		return prmIsActive1;
	}

	public void setPrmIsActive1(Integer prmIsActive1) {
		this.prmIsActive1 = prmIsActive1;
	}

	public Integer getPrmIsActive2() {
		return prmIsActive2;
	}

	public void setPrmIsActive2(Integer prmIsActive2) {
		this.prmIsActive2 = prmIsActive2;
	}

	public void clearLsn(ActionEvent event) {
		prmStatusId = null;
		prmPartnerId = Partner.SYSTEM;
		prmTypeId = null;
		prmNomer = null;
		prmDatabeg = new DateRange(null, null);
		prmDataend = new DateRange(null, null);
		prmDataendfact = new DateRange(null, null);
		prmSurname=null;
		prmSurname1=null;
		prmSurname2=null;
		prmDatabeg1 = new DateRange(null, null);
		prmDataend1 = new DateRange(null, null);
		prmDataendfact1 = new DateRange(null, null);
		prmDatabeg2 = new DateRange(null, null);
		prmDataend2 = new DateRange(null, null);
		prmDataendfact2 = new DateRange(null, null);
		prmCreditsum = new MoneyRange(null, null);
		prmCreditsum1 = new MoneyRange(null, null);	
		prmCreditsum2 = new MoneyRange(null, null);
		prmCreditstake = new MoneyRange(null, null);				
		prmIsOver = null;
		prmIsSameorg = null;
		prmIsActive=null;
		prmIsActive1=null;
		prmIsActive2=null;
		refreshSearch();
	}
	
	public void saveCondLsn(ActionEvent event) {
		
	}
	
    public void loadCondLsn(ActionEvent event) {
		
	}

	protected void nullIfEmpty() {
	
		if (JSFUtils.NULL_INT_VALUE.equals(prmStatusId)) {
			prmStatusId = null;
		}
		if (JSFUtils.NULL_INT_VALUE.equals(prmTypeId)) {
			prmTypeId = null;
		}
		if (StringUtils.isBlank(prmNomer)) {
			prmNomer = null;
		}
	
		if (StringUtils.isBlank(prmSurname)) {
			prmSurname = null;
		}
		
		if (StringUtils.isBlank(prmSurname1)) {
			prmSurname1 = null;
		}
		
		if (StringUtils.isBlank(prmSurname2)) {
			prmSurname2 = null;
		}
		if (JSFUtils.NULL_INT_VALUE.equals(prmIsSameorg)) {
			prmIsSameorg = null;
		}	
		if (JSFUtils.NULL_INT_VALUE.equals(prmIsOver)) {
			prmIsOver = null;
		}	
		
		if (JSFUtils.NULL_INT_VALUE.equals(prmIsActive1)) {
			prmIsActive1 = null;
		}	
		
		if (JSFUtils.NULL_INT_VALUE.equals(prmIsActive2)) {
			prmIsActive2 = null;
		}	
		
		if (prmCreditsum != null && prmCreditsum.getFrom() != null && prmCreditsum.getFrom().intValue() == 0) {
			prmCreditsum.setFrom(null);
		}
		if (prmCreditsum != null && prmCreditsum.getTo() != null && prmCreditsum.getTo().intValue() == 0) {
			prmCreditsum.setTo(null);
		}		
		
		if (prmCreditsum1 != null && prmCreditsum1.getFrom() != null && prmCreditsum1.getFrom().intValue() == 0) {
			prmCreditsum1.setFrom(null);
		}
		if (prmCreditsum1 != null && prmCreditsum1.getTo() != null && prmCreditsum1.getTo().intValue() == 0) {
			prmCreditsum1.setTo(null);
		}	
		
		if (prmCreditsum2 != null && prmCreditsum2.getFrom() != null && prmCreditsum2.getFrom().intValue() == 0) {
			prmCreditsum2.setFrom(null);
		}
		if (prmCreditsum2 != null && prmCreditsum2.getTo() != null && prmCreditsum2.getTo().intValue() == 0) {
			prmCreditsum2.setTo(null);
		}		
		if (prmCreditstake != null && prmCreditstake.getFrom() != null && prmCreditstake.getFrom().intValue() == 0) {
			prmCreditstake.setFrom(null);
		}
		if (prmCreditstake != null && prmCreditstake.getTo() != null && prmCreditstake.getTo().intValue() == 0) {
			prmCreditstake.setTo(null);
		}		
		
	}	
}
