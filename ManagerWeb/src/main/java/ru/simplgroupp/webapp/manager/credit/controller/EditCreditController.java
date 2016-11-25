package ru.simplgroupp.webapp.manager.credit.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.time.DateUtils;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.ejb.EnvironmentSnapshot;
import ru.simplgroupp.ejb.PaymentFilter;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.CreditBeanLocal;
import ru.simplgroupp.interfaces.CreditCalculatorBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.interfaces.ProductBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.rules.AbstractContext;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.BaseCredit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.RefHeader;
import ru.simplgroupp.transfer.RefSystemFeature;
import ru.simplgroupp.util.AppUtil;
import ru.simplgroupp.util.DatesUtils;
import ru.simplgroupp.util.ProductKeys;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.WorkflowObjectState;

/**
 * Контроллер просмотра кредита и расчета разных сумм
 */
public class EditCreditController extends AbstractSessionController implements Serializable {

   
    /**
	 * 
	 */
	private static final long serialVersionUID = -4188596552729808627L;

	@EJB
    protected KassaBeanLocal kassa;

    @EJB
    protected CreditBeanLocal creditBean;
    
    @EJB
    protected CreditDAO creditDAO;
    
    @EJB
    protected PaymentService paymentService;

    @EJB
    protected RulesBeanLocal rules;
    
    @EJB
    protected PeopleBeanLocal people;
    
    @EJB
    protected PeopleDAO peopleDAO;
    
    @EJB
    protected CreditCalculatorBeanLocal creditCalc;
    
    @EJB
    private WorkflowBeanLocal workflowBean;
    
	@EJB
	AppServiceBeanLocal appServ;    
    
	@EJB
	ProductBeanLocal productBean;
   
	@EJB
	ReferenceBooksLocal refBook;
	
    protected Integer prmCreditId;
    /**
     * что считаем
     * 1 - сумма обычная
     * 2 - сумма для продления
     */
    protected Integer calc=1;
    /**
     * общая сумма
     */
    protected Double sumAll;
    /**
     * сумма основного долга
     */
    protected Double sumMain;
    /**
     * сумма процентов
     */
    protected Double sumPercent;
    /**
     * сумма процентов к выплате
     */
    protected Double sumPercentPay;
    /**
     * сумма процентов без штрафа
     */
    protected Double sumPercentWithoutPenalty;
    /**
     * сумма штрафа
     */
    protected Double sumPenalty=new Double(0);
    /**
     * сумма комиссии
     */
    protected Double sumComission=new Double(0);
    /**
     * дата расчета
     */
    protected Date dateCalc;
    /**
     * дата продления максимальная
     */
    protected Date dateLong;
    /**
     * дата, до которой продляем
     */
    protected Date dateNew;
    /**
     * дней просрочки
     */
    protected Integer daysOverdue=0;
    /**
     * есть продление из просрочки
     */
    protected Integer hasProlongFromOverdue=0;
    /**
     * кредит
     */
    protected Credit credit;
    /**
     * платежи от клиента
     */
    protected List<Payment> pays;
    /**
     * платежи клиенту
     */
    protected List<Payment> paysSystem;
    /**
     * константы стандартные
     */
    protected Map<String,Object> newLimits;
    /**
     * константы для продления
     */
    protected Map<String,Object> prolongLimits;
    /**
     * константы для просрочки
     */
    protected Map<String,Object> overdueLimits;
    
    protected EnvironmentSnapshot snapshot;
	protected List<WorkflowObjectState> actionStates;
	protected List<WorkflowObjectState> actionTaskStates;    

    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            if (prmCreditId == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("creditid")) {
                    String ss = prms.get("creditid");
                    prmCreditId = Convertor.toInteger(ss);
                }
            }

            if (prmCreditId != null) {
                reloadCredit(facesCtx, prmCreditId);
            }
        }
    }

    protected void reloadCredit(FacesContext facesCtx, Integer prmId) {
        try {
            credit = creditDAO.getCredit(prmId, Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST,
                    PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_DOCUMENT,
                    BaseCredit.Options.INIT_PROLONGS,BaseCredit.Options.INIT_OFFICIAL_DOCUMENT));
            if (credit!=null){
            	if (credit.getProduct()!=null){           	
            	    newLimits = productBean.getNewRequestProductConfig(credit.getProduct().getId());
            	    prolongLimits=productBean.getProlongProductConfig(credit.getProduct().getId());
            	    overdueLimits=productBean.getOverdueProductConfig(credit.getProduct().getId());
            	    hasProlongFromOverdue=Convertor.toInteger(prolongLimits.get(ProductKeys.CREDIT_HAS_PROLONG_FROM_OVERDUE));
            	}
            }
            PaymentFilter filter = new PaymentFilter();
            filter.setCreditId(prmId);
            filter.setIsPaid(true);
            filter.setPaymentTypeId(refBook.findByCodeIntegerEntity(RefHeader.PAYMENT_TYPE,Payment.TO_SYSTEM).getId());
            pays = paymentService.findPayments(-1, -1, new SortCriteria[]{new SortCriteria("createDate", true)}, null,
                    filter);
            PaymentFilter filterSystem = new PaymentFilter();
            filterSystem.setCreditId(prmId);
            filterSystem.setPaymentTypeId(refBook.findByCodeIntegerEntity(RefHeader.PAYMENT_TYPE,Payment.FROM_SYSTEM).getId());
            paysSystem = paymentService.findPayments(-1, -1, new SortCriteria[]{new SortCriteria("createDate", true)}, null,
                    filterSystem);
            reloadActionStates();
        } catch (Exception e) {
            JSFUtils.handleAsError(facesCtx, null, e);
        }

    }
    
	private void reloadActionStates() {
		AbstractContext context = AppUtil.getDefaultContext(credit, Credit.class.getName());
		context.setCurrentUser(userCtl.getUser());
		
		snapshot = appServ.getSnapshot(context, CreditRequest.class.getName(), true, true);
		actionStates = snapshot.getStates();
//		actionStates = workflow.getProcWfActions(CreditRequest.class.getName(), creditRequest.getId(), null, true);
		actionTaskStates = new ArrayList<WorkflowObjectState>(actionStates.size());
		for (WorkflowObjectState stat: actionStates) {
			if (stat.getTask() != null) {
				actionTaskStates.add(stat);
			}
		}
	}    

    // TODO убрать совсем
    public String makePayment(){
    	 FacesContext facesCtx = FacesContext.getCurrentInstance();
  	 Integer paymentId = paymentService.createPayment(credit.getId(), Account.YANDEX_TYPE, Payment.SUM_FROM_CLIENT, new Double(1000),
                 Payment.TO_SYSTEM, Partner.YANDEX).getId();
    	   Map<String, Object> data = new HashMap<String, Object>();
           data.put("paymentId", paymentId);
           data.put("date", new Date());
    	try {
			workflowBean.repaymentReceivedYandex(credit.getCreditRequest().getId(), data);
		} catch (WorkflowException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
		}
    	return null;
    }
    
    public Credit getCredit() {
        return credit;
    }

    public Integer getPrmCreditId() {
        return prmCreditId;
    }

    public void setPrmCreditId(Integer prmCreditId) {
        this.prmCreditId = prmCreditId;
    }

    public Integer getCalc() {
        return calc;
    }

    public void setCalc(Integer calc) {
        this.calc = calc;
    }
    
    public Integer getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(Integer daysOverdue) {
        this.daysOverdue = daysOverdue;
    }
    
    public Double getSumAll() {
        return sumAll;
    }

    public void setSumAll(Double sumAll) {
        this.sumAll = sumAll;
    }
    
    public Double getSumMain() {
        return sumMain;
    }

    public void setSumMain(Double sumMain) {
        this.sumMain = sumMain;
    }
    
    public Double getSumPercent() {
        return sumPercent;
    }

    public void setSumPercent(Double sumPercent) {
        this.sumPercent = sumPercent;
    }
    
    public Double getSumPercentWithoutPenalty() {
		return sumPercentWithoutPenalty;
	}

	public void setSumPercentWithoutPenalty(Double sumPercentWithoutPenalty) {
		this.sumPercentWithoutPenalty = sumPercentWithoutPenalty;
	}

	public Double getSumPercentPay() {
        return sumPercentPay;
    }

    public void setSumPercentPay(Double sumPercentPay) {
        this.sumPercentPay = sumPercentPay;
    }
    
    public Double getSumPenalty() {
        return sumPenalty;
    }

    public void setSumPenalty(Double sumPenalty) {
        this.sumPenalty = sumPenalty;
    }
    
    public Double getSumComission() {
        return sumComission;
    }

    public void setSumComission(Double sumComission) {
        this.sumComission = sumComission;
    }
    
    public Date getDateCalc(){
    	return dateCalc;
    }
    
    public void setDateCalc(Date dateCalc){
    	this.dateCalc=dateCalc;
    }
    
    public Date getDateNew(){
    	return dateNew;
    }
    
    public void setDateNew(Date dateNew){
    	this.dateNew=dateNew;
    }
    
    public Date getDateLong(){
    	return dateLong;
    }
    
    public void setDateLong(Date dateLong){
    	this.dateLong=dateLong;
    }
    public String cancel() {
        return "canceled";
    }

    public List<Payment> getPays() {
        return pays;
    }
    
    public List<Payment> getPaysSystem() {
		return paysSystem;
	}

	public Map<String, Object> getNewLimits() {
		return newLimits;
	}

    public Map<String, Object> getProlongLimits() {
		return prolongLimits;
	}
    
    public Map<String, Object> getOverdueLimits() {
		return overdueLimits;
	}
    
    public void changeCalcType(ValueChangeEvent event){
    	
    	calc=Convertor.toInteger(event.getNewValue());
    	sumAll=new Double(0);
    	sumMain=new Double(0);
    	sumPercent=new Double(0);
    	sumPenalty=new Double(0);
    	//если это продление
    	if (calc==2){
    	
    	 // рассчитываем параметры продления
         Map<String, Object> cpparams = creditCalc.calcProlongDays(credit, credit.getCreditDataEnd());
         Integer longdays=Convertor.toInteger(cpparams.get(CreditCalculatorBeanLocal.PROLONG_DAYS));
         log.info("Дней продления "+longdays);
         dateLong=DateUtils.addDays(credit.getCreditDataEnd(),longdays);
         log.info("Дата продления "+dateLong);
    	}
    }
    
    public void changeDate(ValueChangeEvent event){
		dateCalc=(Date) event.getNewValue();
	}
    
    public void changeDateNew(ValueChangeEvent event){
		dateNew=(Date) event.getNewValue();
	}    
    
    public void recalc(ActionEvent event){
    	    	
    	switch(calc){
    	  //сумма к возврату
    	  case 1:{
    		  Map<String, Object> resCalc = creditCalc.calcCredit(prmCreditId, dateCalc);
    		  sumAll = Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_BACK));
    		  sumMain = Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_MAIN));
    		  log.info("Дата для расчета "+dateCalc);
    		  log.info("Сумма общая "+sumAll);
    		  sumPenalty =  Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_PENALTY));
    		  sumPercent = Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_PERCENT));
    		  sumPercentWithoutPenalty=sumPercent-sumPenalty;
    		  
    		  log.info("Сумма процентов "+sumPercent);
    		  log.info("Сумма штрафов "+sumPenalty);
    		  daysOverdue = Convertor.toInteger(resCalc.get(CreditCalculatorBeanLocal.DAYS_OVERDUE));
    		  log.info("Сумма обычных процентов при просрочке "+sumPercentWithoutPenalty);
    		    	    		
    		  break;
    	  }
    	  //сумма к возврату - продление
    	  case 2:{
    		  sumPenalty=new Double(0);
    		    		  
    		  //продление возможно только из льготного периода просрочки или при установленном продлении из просрочки
    		  if (hasProlongFromOverdue==1||(daysOverdue<=Convertor.toInteger(overdueLimits.get(ProductKeys.CREDIT_DAYS_MAX_LGOT)))){
    			// есть возможность продления - не все дни продления выбраны 
    		  //  if (dateLong!=credit.getCreditDataEnd()){
    			  //дата расчета не может быть больше максимальной даты продления
    			  if (hasProlongFromOverdue==1){
    				  if (dateCalc.after(dateNew)){
    					  dateCalc=dateNew;
    				  }
    			  } else if (dateCalc.after(dateLong)){
    				dateCalc=dateLong;
    			  }
    		     
    			  Map<String, Object> resCalc = creditCalc.calcCredit(prmCreditId, dateCalc);
    			  sumPercentPay = Convertor.toDouble(resCalc.get(CreditCalculatorBeanLocal.SUM_PERCENT));
    			  daysOverdue = Convertor.toInteger(resCalc.get(CreditCalculatorBeanLocal.DAYS_OVERDUE));
    			  
	              // рассчитываем параметры кредита
    			  Map<String, Object> crparams = creditCalc.calcCreditInitial(credit.getSumMainRemain(), credit.getCreditPercent(), DatesUtils.daysDiff(dateNew,dateCalc),null);    
	              sumPercent= Convertor.toDouble(crparams.get(CreditCalculatorBeanLocal.SUM_PERCENT));
    			  sumMain=credit.getSumMainRemain(); 
    			  sumAll=((sumPercent==null)?0:sumPercent)+((sumMain==null)?0:sumMain);
  			
    		//    }
    		  }
    		  
    		  break;
    	  }
    	}
    }

	public List<WorkflowObjectState> getActionStates() {
		return actionStates;
	}

	public List<WorkflowObjectState> getActionTaskStates() {
		return actionTaskStates;
	}

	public EnvironmentSnapshot getSnapshot() {
		return snapshot;
	}

	public Integer getHasProlongFromOverdue() {
		return hasProlongFromOverdue;
	}

	public void setHasProlongFromOverdue(Integer hasProlongFromOverdue) {
		this.hasProlongFromOverdue = hasProlongFromOverdue;
	}
	
	public boolean getCanViewContacts() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.PE_VIEW_CONTACT}, false);
	}	
}
