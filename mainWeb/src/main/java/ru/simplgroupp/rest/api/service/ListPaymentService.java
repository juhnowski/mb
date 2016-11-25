package ru.simplgroupp.rest.api.service;

import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.CreditBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.rest.api.data.CreditData;
import ru.simplgroupp.rest.api.data.CreditRequestData;
import ru.simplgroupp.rest.api.data.RepaymentScheduleData;
import ru.simplgroupp.rest.api.data.payment.ListPaymentData;
import ru.simplgroupp.rest.api.data.payment.PaymentData;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.*;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class ListPaymentService implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @EJB
    private CreditBeanLocal creditBean;
    @EJB
    private PaymentService paymentService;
    @EJB
    private PaymentDAO payDAO;
    @EJB
    private KassaBeanLocal kassa;
    @EJB
    private CreditDAO creditDAO;

    @Inject
    private UserService userServ;
    @Inject
    private HttpServletRequest request;


    /**
     * Платежи по займу
     *
     * @return - ListPaymentData
     */
    public ListPaymentData getLstPay() throws KassaException {
        Users user = userServ.getUser();
        Credit credit = creditBean.findCreditActive(user.getPeopleMain().getId(), Utils.setOf());
        ListPaymentData lst = new ListPaymentData();
        lst.setHasCredit(credit != null);

        List<PaymentData> payl = new ArrayList<PaymentData>();
        if (credit != null) {
            for (Payment pay : payDAO.listPayments(credit.getId())) {
                PaymentData payNew = new PaymentData();
                payNew.setAmount(pay.getAmount());
                payNew.setCreateDate(pay.getCreateDate());
                payNew.setProcessDate(pay.getProcessDate());
                payNew.setPartner_realName(pay.getPartner().getRealName());
                payl.add(payNew);
            }
        }
        lst.setLstPay(payl);
        return lst;
    }


    /**
     * График платежей
     *
     * @return - List<RepaymentScheduleWeb>
     */
    public List<RepaymentScheduleData> getLstSched() throws KassaException {
        Users user = userServ.getUser();
        Credit credit = creditBean.findCreditActive(user.getPeopleMain().getId(), Utils.setOf());

        List<RepaymentScheduleData> result = new ArrayList<>();
        if (credit != null) {
            List<RepaymentSchedule> repaymentSchedules = payDAO.listSchedules(credit.getId());
            for (RepaymentSchedule repaymentSchedule : repaymentSchedules) {
                result.add(new RepaymentScheduleData(repaymentSchedule));
            }
        }
        return result;
    }


    /**
     * Итория займов и заявок
     *
     * @return - на выходе MAP с сключами
     * credit - лист истории кредитов
     * creditReq - лист истории заявок
     */
    public Map<String, Object> getHistoryCredits() throws KassaException {
        Users user = userServ.getUser();
        List<Credit> lstCredit = creditBean.listCredit(0, 20, null, null, user.getPeopleMain().getId(), null, Partner.SYSTEM, null, null, null, true, null, true, null, null, null, null, null, null);
        List<CreditRequest> lstCRequest = kassa.listCreditRequestWithoutCredits(user.getPeopleMain().getId());
        List<CreditData> creditWebs = new ArrayList<>();
        List<CreditRequestData> creditRequestWebs = new ArrayList<>();
        for (Credit credit : lstCredit) {
            CreditData creditData = new CreditData(credit);

            List<PaymentData> paymentDataList = new ArrayList<>();
            for (Payment payment : payDAO.listPayments(credit.getId())) {
                PaymentData paymentData = new PaymentData();
                paymentData.setAmount(payment.getAmount());
                paymentData.setCreateDate(payment.getCreateDate());
                paymentData.setProcessDate(payment.getProcessDate());
                paymentData.setPartner_realName(payment.getPartner().getRealName());

                paymentDataList.add(paymentData);
            }

            creditData.setPayments(paymentDataList);
            creditWebs.add(creditData);
        }
        for (CreditRequest creditRequest : lstCRequest) {
            creditRequestWebs.add(new CreditRequestData(creditRequest));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("credit", creditWebs);
        map.put("creditReq", creditRequestWebs);
        return map;
    }


    public CreditData getCreditData(Integer creditId) throws KassaException {
        Credit credit = creditDAO.getCredit(creditId, Utils.setOf());

        return new CreditData(credit);
    }
}
