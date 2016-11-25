package ru.simplgroupp.data;

import ru.simplgroupp.rest.api.data.SocialData;
import ru.simplgroupp.transfer.creditrequestwizard.Step0Data;
import ru.simplgroupp.transfer.creditrequestwizard.Step1Data;
import ru.simplgroupp.transfer.creditrequestwizard.Step6Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Get объект для обмена данными с фронтэндом
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 25.04.14
 */
public class Data {
    private boolean status;
    private List<ErrorData> errors = new LinkedList<ErrorData>();
    private Object result;
    private int stepcount;
    private Step0Data step0;
    private Step1Data step1;
    private Step2Data step2;
    private Step3Data step3;
    private Step4Data step4;
    private Step5Data step5;
    private Step6Data step6;
    private Step7Data step7;
    private Step8Data step8;
    private String session_id, request_id;
    private SocialData social;
    private boolean issended;

    public Data() {
        status = false;
        result = null;
        session_id = request_id = null;
        step0 = null;
        step1 = null;
        step2 = null;
        step3 = null;
        step4 = null;
        step5 = null;
        step6 = null;
        step7 = null;
        step8 = null;
        social = null;
        issended = false;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<ErrorData> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorData> errors) {
        this.errors = errors;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Step1Data getStep1() {
        return step1;
    }

    public void setStep1(Step1Data step1) {
        this.step1 = step1;
    }

    public Step2Data getStep2() {
        return step2;
    }

    public void setStep2(Step2Data step2) {
        this.step2 = step2;
    }

    public Step3Data getStep3() {
        return step3;
    }

    public void setStep3(Step3Data step3) {
        this.step3 = step3;
    }

    public Step4Data getStep4() {
        return step4;
    }

    public void setStep4(Step4Data step4) {
        this.step4 = step4;
    }

    public Step5Data getStep5() {
        return step5;
    }

    public void setStep5(Step5Data step5) {
        this.step5 = step5;
    }

    public Step6Data getStep6() {
        return step6;
    }

    public void setStep6(Step6Data step6) {
        this.step6 = step6;
    }

    public Step7Data getStep7() {
        return step7;
    }

    public void setStep7(Step7Data step7) {
        this.step7 = step7;
    }

    public Step8Data getStep8() {
        return step8;
    }

    public void setStep8(Step8Data step8) {
        this.step8 = step8;
    }

    /**
     * Вернет просто положительный статус без данных
     *
     * @return
     */
    public static Data setSuccess() {
        Data ans = new Data();
        ans.setStatus(true);
        return ans;
    }

    /**
     * Вернет просто отрицательный статус без данных
     *
     * @return
     */
    public static Data setError() {
        Data ans = new Data();
        ans.setStatus(false);
        return ans;
    }

    /**
     * Вернет ошибку с типом редиректа
     *
     * @return
     */
    public static Data setErrorRedirect() {
        Data ans = new Data();
        ans.setStatus(false);
        ans.getErrors().add(ErrorData.redirect());
        return ans;
    }

    /**
     * Вернет ошибку с типом редиректа
     *
     * @param url куда редиректим
     * @return
     */
    public static Data setErrorRedirect(String url) {
        Data ans = new Data();
        ans.setStatus(false);
        ans.getErrors().add(ErrorData.redirect(url));
        return ans;
    }

    /**
     * Вернет ошибку с типом алерта
     *
     * @param text текст ошибки для отображения пользователю
     * @return
     */
    public static Data setErrorAlert(String text) {
        Data ans = new Data();
        ans.setStatus(false);
        ans.getErrors().add(ErrorData.alert(text));
        return ans;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public boolean getIssended() {
        return issended;
    }

    public void setIssended(boolean issended) {
        this.issended = issended;
    }

//    public boolean isIssended() {
//        return issended;
//    }

    public Step0Data getStep0() {
        return step0;
    }

    public void setStep0(Step0Data step0) {
        this.step0 = step0;
    }

    public SocialData getSocial() {
        return social;
    }

    public void setSocial(SocialData social) {
        this.social = social;
    }

    public int getStepcount() {
        return stepcount;
    }

    public void setStepcount(int stepcount) {
        this.stepcount = stepcount;
    }
}
