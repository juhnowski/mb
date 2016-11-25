package ru.simplgroupp.rest.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.ProductDAO;
import ru.simplgroupp.ejb.ApplicationAction;
import ru.simplgroupp.ejb.EnvironmentSnapshot;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.rules.CreditRequestContext;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.MoneyRange;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.workflow.*;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequestScoped
public class AiService implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(AiService.class.getName());

    @EJB
    private RulesBeanLocal rulesBean;

    @EJB
    private WorkflowBeanLocal workflow;

    @EJB
    private AppServiceBeanLocal appServ;

    @EJB
    private KassaBeanLocal kassaBean;

    @EJB
    private ProductBeanLocal productBean;

    @EJB
    private ProductDAO productDAO;

    @Inject
    private OverviewService viewOC;

    @EJB
    private CreditBeanLocal creditBean;

    //может ли быть продление
    private boolean bCanProlong;
    private String msgCanProlong;

    //может ли быть рефинансирование
    private boolean bCanRefinance;
    private String msgCanRefinance;

    //может ли быть запущено рефинансирование
    private boolean bCanRefinanceRun;
    private String msgCanRefinanceRun;


    //может ли быть отмена продления
    private boolean bCanProlongCancel;

    //может ли быть погашение
    private boolean bCanComplete;
    private String msgCanComplete;

    //может ли быть добавление новой заявки
    private boolean bCanAdd;
    private String msgCanAdd;

    //может ли быть подписание оферты
    private boolean bCanSign;
    private String msgCanSign;

    // возможен ли платёж
    private boolean bCanPay;
    private String msgCanPay;
    //суммы для оплаты по кредиту
    private MoneyRange sumForPay;
    //суммы оплаты бонусами
    private MoneyRange sumForPayBonus;

    /**
     * вид платежа (кредит, продление, рефинансирование)
     */
    private Integer sumIdPay;

    /**
     * может ли быть отказ клиента
     */
    private boolean bCanClientReject;

    /**
     * можно ли редактировать данные профиля
     */
    private boolean canEdit;

    private EnvironmentSnapshot snapshot;
    @Inject
    private UserService userServ;
    @Inject
    private HttpServletRequest request;

    public void init() {
        if (this.userServ.getUser() != null) {
            reloadData();
        }
    }

    /*
    private void addResErrors(BooleanRuleResult res) {
        for (RuleMessage msg: res.getMessages()) {
            if (msg.getSeverity().equals(RuleMessage.Level.INFO)) {
                facesCtx.addMessage(null, new FacesMessage());
                // TODO
            }
        }
    }
    */
    private boolean reloadActions(CreditRequestContext ctx) {

        snapshot = appServ.getSnapshot(ctx);

        boolean bExcl = false;
        String msgExcl = "Выполняется: ";
        for (ApplicationAction act : snapshot.getRunningActions()) {
            if (act.isExclusive()) {
                bExcl = true;
                msgExcl = msgExcl + " " + act.getDescription();
            }
        }
        if (bExcl) {
//			msgCanProlongPay = msgExcl;
            msgCanProlong = msgExcl;
            msgCanComplete = msgExcl;
            msgCanAdd = msgExcl;
            msgCanSign = msgExcl;
            msgCanRefinance = msgExcl;
//			msgCanRefinancePay=msgExcl;
        }
        return bExcl;
    }

    private void reloadData() {
/*		
        bCanProlongPay = false;
		msgCanProlongPay = null;
		bCanRefinancePay = false;
		msgCanRefinancePay = null;		
*/
        bCanProlongCancel = false;
        bCanProlong = false;
        msgCanProlong = null;
        bCanRefinance = false;
        msgCanRefinance = null;
        bCanComplete = false;
        msgCanComplete = null;
        bCanAdd = false;
        msgCanAdd = null;
        bCanSign = false;
        msgCanSign = null;
        bCanClientReject = false;
        bCanPay = false;
        msgCanPay = null;
        sumForPay = null;

        CreditRequestContext ctx = new CreditRequestContext(userServ.getUser().getPeopleMain(), viewOC.getCreditRequest());
        ctx.setCredit(viewOC.getCredit());
		/*CreditRequestContext ctx =null;
  	    if (viewOC.getCreditRequest()!=null){
		    ctx= (CreditRequestContext) AppUtil.getDefaultContext(viewOC.getCreditRequest(), CreditRequest.class.getName());	
		} else {
			ctx= (CreditRequestContext) AppUtil.getDefaultContext(viewOC.getUserCtl().getUser().getPeopleMainData(), PeopleMain.class.getName());
		}*/
        ctx.setCurrentUser(userServ.getUser());
        //добавляем константы для рассчета разных действий
        Map<String, Object> limits = null;
        if (ctx.getCredit() != null) {
            limits = productBean.getAllProductConfig(ctx.getCredit().getProduct().getId());
        } else {
            if (productDAO.getProductDefault() != null) {
                limits = productBean.getAllProductConfig(productDAO.getProductDefault().getId());
            } else {
                logger.error("Не найден продукт по умолчанию!");
            }
        }
        ctx.getParams().putAll(limits);
        limits = null;

        CreditRequest lastCreditRequest = viewOC.getLastRefusedCreditRequest();
        if (lastCreditRequest != null) {
            ctx.setLastRefusedCreditRequest(lastCreditRequest);
        }

        if (reloadActions(ctx)) {
            return;
        }

        //ПРОДЛЕНИЕ
        //если нет текущего кредита
        if (viewOC.getCredit() == null) {
            bCanProlong = false;
            msgCanProlong = "У Вас нет активных займов";
        } else {
            WorkflowObjectActionDef def = snapshot.getAction(ProcessKeys.MSG_PROLONG);
            bCanProlong = (def != null && def.isEnabled());
            msgCanProlong = (def == null) ? null : def.getComment();

            if (bCanProlong) {
                Credit crd = viewOC.getCredit();
                if (!crd.getPaymentSuccessPaid()) {
                    bCanProlong = crd.getPaymentSuccessPaid();
                    msgCanProlong = "Продление недоступно";
                }
            }

            def = snapshot.getAction(ProcessKeys.MSG_PROLONG_CANCEL);
            bCanProlongCancel = (def != null && def.isEnabled());
        }

        //РЕФИНАНСИРОВАНИЕ
        {
            WorkflowObjectActionDef def = snapshot.getAction(ProcessKeys.MSG_REFINANCE);
            bCanRefinance = (def != null && def.isEnabled());
            msgCanRefinance = (def == null) ? null : def.getComment();

            def = snapshot.getAction(ProcessKeys.MSG_REFINANCE_RUN);
            bCanRefinanceRun = (def != null && def.isEnabled());
            msgCanRefinanceRun = (def == null) ? null : def.getComment();

        }

        //ПОДПИСАНИЕ ОФЕРТЫ
        {
            WorkflowObjectState stateSign = snapshot.getState(new StateRef(ProcessKeys.DEF_CREDIT_REQUEST, "*", ProcessKeys.TASK_SIGN_OFERTA));
            if (stateSign == null) {
                bCanSign = false;
                msgCanSign = "Вы не можете подписать оферту";
            } else {
                bCanSign = true;
                msgCanSign = null;
            }
        }


        // оплата
        {
            List<Integer> listSumIds = rulesBean.getPaymentToOrder();

            // определяем первый по приоритету платёж
            Map<String, Object> varsPay = Utils.mapOfSO(ProcessKeys.VAR_PAYMENT_FORM + ".type", Payment.TO_SYSTEM);
            List<WorkflowObjectState> lstStates = snapshot.getStates(varsPay);
            List<WorkflowObjectActionDef> lstDefs = snapshot.getActions(varsPay);
            WorkflowObjectState state = null;
            WorkflowObjectActionDef def = null;
            for (Integer csumIdPay : listSumIds) {
                for (WorkflowObjectState st : lstStates) {
                    Number nsumId = (Number) st.getDefinition().getVariablesLocal().get(ProcessKeys.VAR_PAYMENT_FORM + ".sumId");
                    if (csumIdPay.intValue() == nsumId.intValue()) {
                        state = st;
                        break;
                    }
                }
                for (WorkflowObjectActionDef df : lstDefs) {
                    if (!df.isEnabled()) {
                        continue;
                    }
                    Number nsumId = (Number) df.getVariables().get(ProcessKeys.VAR_PAYMENT_FORM + ".sumId");
                    if (csumIdPay.intValue() == nsumId.intValue()) {
                        def = df;
                        break;
                    }
                }

                if (state != null || def != null) {
                    break;
                }
            }

            bCanPay = (def != null) || (state != null);

            if (bCanPay) {
                if (state != null) {
                    sumIdPay = Convertor.toInteger(state.getDefinition().getVariablesLocal().get(ProcessKeys.VAR_PAYMENT_FORM + ".sumId"));
                } else if (def != null) {
                    sumIdPay = Convertor.toInteger(def.getVariables().get(ProcessKeys.VAR_PAYMENT_FORM + ".sumId"));
                }
                if (sumIdPay == null) {
                    sumIdPay = RefPaymentTarget.RETURN;
                }
                switch (sumIdPay) {
                    case RefPaymentTarget.RETURN:
                        msgCanPay = "Оплатить заем";
                        break;
                    case RefPaymentTarget.PROLONG:
                        msgCanPay = "Оплатить проценты перед продлением";
                        break;
                    case RefPaymentTarget.REFINANCE:
                        msgCanPay = "Оплатить рефинансирование";
                        break;
                }

                sumForPay = new MoneyRange();
                if (state != null) {
                    Number nSumFrom = (Number) state.getDefinition().getVariablesLocal().get(ProcessKeys.VAR_PAYMENT_FORM + ".sumFrom");
                    Number nSumTo = (Number) state.getDefinition().getVariablesLocal().get(ProcessKeys.VAR_PAYMENT_FORM + ".sumTo");
                    if (nSumFrom != null) {
                        sumForPay.setFrom(new BigDecimal(nSumFrom.doubleValue()));
                    }
                    if (nSumTo != null) {
                        sumForPay.setTo(new BigDecimal(nSumTo.doubleValue()));
                    }

                } else if (def != null) {
                    Number nSumFrom = (Number) def.getVariables().get(ProcessKeys.VAR_PAYMENT_FORM + ".sumFrom");
                    Number nSumTo = (Number) def.getVariables().get(ProcessKeys.VAR_PAYMENT_FORM + ".sumTo");
                    if (nSumFrom != null) {
                        sumForPay.setFrom(new BigDecimal(nSumFrom.doubleValue()));
                    }
                    if (nSumTo != null) {
                        sumForPay.setTo(new BigDecimal(nSumTo.doubleValue()));
                    }

                }
                sumForPayBonus = new MoneyRange();
                if (state != null) {
                    Number nSumFrom = (Number) state.getDefinition().getVariablesLocal().get(ProcessKeys.VAR_PAYMENT_FORM + ".bonus.sumFrom");
                    Number nSumTo = (Number) state.getDefinition().getVariablesLocal().get(ProcessKeys.VAR_PAYMENT_FORM + ".bonus.sumTo");
                    if (nSumFrom != null) {
                        sumForPayBonus.setFrom(new BigDecimal(nSumFrom.doubleValue()));
                    }
                    if (nSumTo != null) {
                        sumForPayBonus.setTo(new BigDecimal(nSumTo.doubleValue()));
                    }

                } else if (def != null) {
                    Number nSumFrom = (Number) def.getVariables().get(ProcessKeys.VAR_PAYMENT_FORM + ".bonus.sumFrom");
                    Number nSumTo = (Number) def.getVariables().get(ProcessKeys.VAR_PAYMENT_FORM + ".bonus.sumTo");
                    if (nSumFrom != null) {
                        sumForPayBonus.setFrom(new BigDecimal(nSumFrom.doubleValue()));
                    }
                    if (nSumTo != null) {
                        sumForPayBonus.setTo(new BigDecimal(nSumTo.doubleValue()));
                    }

                }
            } else {
                msgCanPay = "Погашение недоступно";
            }

        }

        // отказ клиента
        {
            WorkflowObjectActionDef def = snapshot.getAction(SignalRef.toString(ProcessKeys.DEF_CREDIT_REQUEST, null, ProcessKeys.MSG_CLIENT_REJECT));
            bCanClientReject = (def != null && def.isEnabled());
        }

        //НОВАЯ ЗАЯВКА
        {

            WorkflowObjectActionDef def = snapshot.getAction(ProcessKeys.MSG_ADD_CREDIT_REQUEST);
            bCanAdd = (def != null && def.isEnabled());
            if (def == null) {
                msgCanAdd = null;
            } else {
                msgCanAdd = def.getComment();
            }

        }


        // редактирование профиля
        // редактировать можно когда нет активного кредита или активной заявки
        PeopleMain peopleMain = userServ.getUser().getPeopleMain();
        Credit activeCredit = creditBean.creditActive(peopleMain.getId());
        boolean creditRequestActive = true;
        try {
            CreditRequest creditRequest = kassaBean.findLastCreditRequestClosed(peopleMain.getId(), Utils.setOf());
            if ((creditRequest.getStatus().getId() == CreditStatus.CLIENT_REFUSE ||
                    creditRequest.getStatus().getId() == CreditStatus.REJECTED ||
                    creditRequest.getStatus().getId() == CreditStatus.CLOSED)) {
                creditRequestActive = false;
            }
        } catch (KassaException e) {
            logger.error("ошибка при запросе активной кредитной заявки " + e);
        }

        canEdit = !creditRequestActive;
    }

    public boolean getCanClientReject() {
        return bCanClientReject;
    }

    public boolean getCanProlong() {
        return bCanProlong;
    }

    public boolean getCanRefinance() {
        return bCanRefinance;
    }

    public boolean getCanRefinanceRun() {
        return bCanRefinanceRun;
    }

    public boolean getCanProlongCancel() {
        return bCanProlongCancel;
    }

    public boolean getCanPay() {
        return bCanPay;
    }

    public String getMsgCanPay() {
        return msgCanPay;
    }

    public MoneyRange getSumForPay() {
        return sumForPay;
    }

    public boolean getCanComplete() {
        return bCanComplete;
    }

    public boolean getCanAdd() {
        return bCanAdd;
    }

    public boolean getCanSign() {
        return bCanSign;
    }

    public String getMsgCanAdd() {
        return msgCanAdd;
    }

    public String getMsgCanComplete() {
        return msgCanComplete;
    }

    public String getMsgCanSign() {
        return msgCanSign;
    }

    public String getMsgCanProlong() {
        return msgCanProlong;
    }

    public String getMsgCanRefinance() {
        return msgCanRefinance;
    }

    public String getMsgCanRefinanceRun() {
        return msgCanRefinance;
    }

    public Integer getSumIdPay() {
        return sumIdPay;
    }

    public void setSumIdPay(Integer sumIdPay) {
        this.sumIdPay = sumIdPay;
    }

    public MoneyRange getSumForPayBonus() {
        return sumForPayBonus;
    }

    public boolean isCanEdit() {
        return canEdit;
    }
}
