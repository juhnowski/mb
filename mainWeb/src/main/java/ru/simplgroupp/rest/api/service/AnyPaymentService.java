package ru.simplgroupp.rest.api.service;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.plugins.payment.PayonlineAcquiringPluginConfig;
import ru.simplgroupp.ejb.plugins.payment.YandexAcquiringPluginConfig;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.interfaces.plugins.payment.PayonlineAcquiringBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.QiwiAcquiringBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.YandexAcquiringBeanLocal;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.persistence.ProlongEntity;
import ru.simplgroupp.persistence.RefinanceEntity;
import ru.simplgroupp.rest.api.data.payment.PaymentSubmitData;
import ru.simplgroupp.rest.api.data.payment.RepayData;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;
import ru.simplgroupp.util.ProductKeys;
import ru.simplgroupp.workflow.PluginExecutionContext;

public class AnyPaymentService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(AnyPaymentService.class.getName());

    private static final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();

    static {
        decimalFormatSymbols.setDecimalSeparator('.');
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00", decimalFormatSymbols);

    private Credit credit;

    @EJB
    private KassaBeanLocal kassa;

    @EJB
    private CreditBeanLocal creditBean;

    @EJB
    private CreditDAO creditDAO;

    @EJB
    private PaymentService paymentService;

    @EJB
    private WorkflowBeanLocal workflowBean;

    @EJB
    private PeopleBeanLocal peopleBean;

    @EJB
    private RulesBeanLocal rulesBean;

    @EJB
    private CreditCalculatorBeanLocal creditCalc;

    @EJB
    private PeopleDAO peopleDAO;

    @EJB
    private ProductBeanLocal productBean;

    /**
     * сумма возврата
     */
    private Double sumBack;
    /**
     * сумма возврата минимальная
     */
    private Double sumBackMin;
    /**
     * сумма, которую можем заплатить бонусами
     */
    private Double sumBonusPay=0d;
    /**
     * сумма, которую можем заплатить бонусами от минимальной
     */
    private Double sumBonusPayMin=0d;

    private String paymentType;

    private String description;

    private Integer paymentId;

    private String qiwiPayUrl;

    private String qiwiPhone;

    private Long yandexShopId;

    private Long yandexScid;

    private String yandexUrl;

    private String payonlineUrl;

    private Integer payonlineMerchantId;

    private String payonlinePrivateSecurityKey;

    private Map<String, Object> limits;

    private PeopleMain people;

    @EJB
    private transient ActionProcessorBeanLocal actionProcessor;

    private transient QiwiAcquiringBeanLocal qiwiAcquiringBean;

    @Inject
    private AiService aiCtl;

    @Inject
    private OverviewService overCtl;

    @Inject
    private UserService userServ;
    @Inject
    private HttpServletRequest request;

    private ProlongEntity prolong;
    private RefinanceEntity refinance;

    public RepayData initCtl() throws KassaException {
        int peopleId = userServ.getUser().getPeopleMain().getId();
        people = peopleDAO.getPeopleMain(peopleId, Utils.setOf(PeopleMain.Options.INIT_PEOPLE_CONTACT));
        overCtl.init();
        aiCtl.init();
        sumBackMin = null;
        credit = creditBean.findCreditActive(peopleId, Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST, BaseCredit.Options.INIT_PAYMENTS));
        if (credit != null) {
            Map<String, Object> prolongConfig = productBean.getCreditReturnPayment(credit.getProduct().getId());
            Integer usePercent = Convertor.toInteger(prolongConfig.get(ProductKeys.CREDIT_USE_PERCENT_MIN_SUM));
            if ((usePercent != null) && (usePercent == ActiveStatus.ACTIVE)) {
                if (aiCtl.getSumForPay() != null) {
                    sumBackMin = (aiCtl.getSumForPay().getFrom() == null) ? 0d : aiCtl.getSumForPay().getFrom().doubleValue();
                }
            } else {
                Double minSum = Convertor.toDouble(prolongConfig.get(ProductKeys.CREDIT_PAY_MIN_SUM));
                if (minSum != null) {
                    sumBackMin = minSum;
                }
            }

        }
            if (aiCtl.getSumForPay() != null) {
                sumBack = (aiCtl.getSumForPay().getTo() == null) ? 0d : aiCtl.getSumForPay().getTo().doubleValue();
                if (sumBackMin == null) {
                    sumBackMin = (aiCtl.getSumForPay().getFrom() == null)?0d:aiCtl.getSumForPay().getFrom().doubleValue();
                }
            }
            if (aiCtl.getSumForPayBonus() != null) {
                sumBonusPay = (aiCtl.getSumForPayBonus().getTo() == null) ? 0d : aiCtl.getSumForPayBonus().getTo().doubleValue();
                sumBonusPayMin = (aiCtl.getSumForPayBonus().getFrom() == null) ? 0d : aiCtl.getSumForPayBonus().getFrom().doubleValue();
            }
        if (sumBackMin == null) {
            sumBackMin = 0d;
        }
        if (credit != null) {

            ActionContext actionContext = actionProcessor.createActionContext(null, true);
            qiwiAcquiringBean = actionContext.getPlugins().getQiwiAcquiring();
            qiwiPhone = peopleBean.findContactClient(PeopleContact.CONTACT_CELL_PHONE,
                    userServ.getUser().getPeopleMain().getId());

            PluginExecutionContext yandexExecutionContext = PluginExecutionContext.createExecutionContext(actionContext,
                    YandexAcquiringBeanLocal.SYSTEM_NAME);

            YandexAcquiringPluginConfig yandexPluginConfig = (YandexAcquiringPluginConfig) yandexExecutionContext
                    .getPluginConfig();
            yandexShopId = yandexPluginConfig.getShopId();
            yandexScid = yandexPluginConfig.getScid();
            yandexUrl = yandexPluginConfig.isUseWork() ? yandexPluginConfig.getWorkUrl() : yandexPluginConfig
                    .getTestUrl();

            PluginExecutionContext payonlineExecutionContext = PluginExecutionContext.createExecutionContext
                    (actionContext,
                            PayonlineAcquiringBeanLocal.SYSTEM_NAME);

            PayonlineAcquiringPluginConfig payonlinePluginConfig = (PayonlineAcquiringPluginConfig)
                    payonlineExecutionContext
                            .getPluginConfig();
            payonlineUrl = payonlinePluginConfig.getBankCardUrl();
            payonlineMerchantId = payonlinePluginConfig.getMerchantId();
            payonlinePrivateSecurityKey = payonlinePluginConfig.getPrivateSecurityKey();

        }

        RepayData data = new RepayData();
        data.setCanPay(aiCtl.getCanPay());
        data.setMsgCanPay(aiCtl.getMsgCanPay());

        if (credit != null) {
            data.setHasCredit(true);
            data.setCredit_creditAccount(credit.getCreditAccount());
            if(sumBack != null)
            {
                data.setPayonlineSecurityKey(getPayonlineSecurityKey(sumBack));
                data.setFormattedSumBack(this.getFormattedSumBack());
            }
            if (data.isCanPay()) {
                if (!credit.getPaymentSuccessPaid()) {
                    data.setCanPay(credit.getPaymentSuccessPaid());
                    data.setMsgCanPay("Погашение недоступно");
                }
            }
        }
        else
            data.setHasCredit(false);

        data.setDescription(description);


        data.setHasProlong(prolong != null);

        data.setPaymentId(paymentId);
        data.setPaymentType(paymentType);
        data.setPayonlineFailUrl(getPayonlineFailUrl());
        data.setPayonlineMerchantId(payonlineMerchantId);
        data.setPayonlineReturnUrl(getPayonlineReturnUrl());
        data.setPayonlineUrl(payonlineUrl);
        if (people.getEmail() != null) {
            data.setPeople_email(people.getEmail().getValue());
        }
        data.setPeople_id(peopleId);
        data.setQiwiPayUrl(qiwiPayUrl);
        data.setQiwiPhone(qiwiPhone);
        data.setSumBack(sumBack);
        data.setSumBackMin(sumBackMin);
        data.setSumBonusPay(sumBonusPay);
        data.setSumBonusPayMin(sumBonusPayMin);
        data.setYandexScid(yandexScid);
        data.setYandexShopId(yandexShopId);
        data.setYandexUrl(yandexUrl);

        return data;

    }

    public Map<String, String> qiwiPayment(Double outsumBack) {
        Map<String, String> result = new HashMap<>();

        paymentId = paymentService.createPayment(credit.getId(), Account.QIWI_TYPE, Payment.SUM_FROM_CLIENT, outsumBack,
                Payment.TO_SYSTEM, Partner.QIWI).getId();

        try {

            ActionContext actionContext = actionProcessor.createActionContext(null, true);
            PluginExecutionContext qiwiExecutionContext = PluginExecutionContext.createExecutionContext(actionContext,
                    QiwiAcquiringBeanLocal.SYSTEM_NAME);
            String trx = qiwiAcquiringBean.doOrder(Convertor.fromMask(qiwiPhone), paymentId, qiwiExecutionContext);
            qiwiPayUrl = qiwiAcquiringBean.getPayUrl(qiwiExecutionContext) + "&transaction=" + trx + "&iframe=true";
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("paymentId", paymentId);
            workflowBean.repaymentReceivedQiwi(credit.getCreditRequest().getId(), data);
            result.put("url", qiwiPayUrl);
            return result;
        } catch (Exception e) {
            paymentType = null;
            logger.severe("Не удалось провести платеж киви "+e.getMessage());
            throw new RuntimeException("Не удалось провести платеж киви ");
        }
    }

    public Map<String, String> yandexPayment(Double outsumBack) {
        Map<String, String> result = new HashMap<>();

        paymentId = paymentService.createPayment(credit.getId(), Account.YANDEX_TYPE, Payment.SUM_FROM_CLIENT, outsumBack,
                Payment.TO_SYSTEM, Partner.YANDEX).getId();

        result.put("shopId", Long.toString(yandexShopId));
        result.put("scid", Long.toString(yandexScid));
        result.put("customerNumber", Long.toString(this.people.getId()));
        result.put("sum", Double.toString(outsumBack));
        result.put("orderNumber", Long.toString(paymentId));
        if (people.getEmail() != null) {
            result.put("cps_email", people.getEmail().getValue());
        }

        return result;
    }

    public Map<String, String> payonlinePayment(Double outsumBack) {
        Map<String, String> result = new HashMap<>();

        paymentId = paymentService.createPayment(credit.getId(), Account.CARD_TYPE, Payment.SUM_FROM_CLIENT, outsumBack,
                Payment.TO_SYSTEM, Partner.PAYONLINE).getId();

        result.put("MerchantId", Integer.toString(payonlineMerchantId));
        result.put("OrderId", Integer.toString(paymentId));
        result.put("Amount", getFormattedSumBack(outsumBack));
        result.put("Currency", "RUB");
        result.put("SecurityKey", getPayonlineSecurityKey(outsumBack));
        result.put("ReturnUrl", getPayonlineReturnUrl());
        result.put("FailUrl", getPayonlineFailUrl());
        result.put("url", payonlineUrl);

        return result;
    }

    public String getPayonlineSecurityKey(Double outsumBack) {
        String data = "MerchantId=" + payonlineMerchantId + "&OrderId=" + paymentId + "&Amount=" + getFormattedSumBack(outsumBack)
                + "&Currency=RUB" + "&PrivateSecurityKey=" + payonlinePrivateSecurityKey;

        return DigestUtils.md5Hex(data);
    }

    public String getPayonlineReturnUrl() {
        return getServerUrl() + "/main/client/payonline-complete-success.shtml";
    }

    public String getPayonlineFailUrl() {
        return getServerUrl() + "/main/client/payonline-complete-fail.shtml";
    }

    public String getFormattedSumBack() {
        return getFormattedSumBack(sumBack);
    }

    public String getFormattedSumBack(Double sum) {
        return decimalFormat.format(sum);
    }

    private String getServerUrl() {
        return "http://" + request.getServerName() + (request.getServerPort() == 80 ? "" : request.getServerPort());
    }

    public Map<String, String> submitPayment(PaymentSubmitData paymentData) throws KassaException {
        Map<String, String> result = new HashMap<>();

        initCtl();

        if (this.credit == null) {
            throw new IllegalStateException("Credit does not found");
        }
        if (this.credit.getCreditAccount() == null) {
            throw new IllegalStateException("Empty account number");
        }
        if (!this.credit.getCreditAccount().equals(paymentData.getAccountNumber())) {
            throw new IllegalArgumentException("Incorrect account number");
        }

        switch (paymentData.getType()) {
            case PAYONLINE:
                return payonlinePayment(paymentData.getPaymentSum());
            case QIWI:
                return qiwiPayment(paymentData.getPaymentSum());
            case YANDEX:
                return yandexPayment(paymentData.getPaymentSum());
            default:
                throw new AssertionError("Illigal payment type");
        }
    }
}
