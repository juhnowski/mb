package ru.simplgroupp.rest.api.data.firstcreditrequest;

import ru.simplgroupp.rest.api.FiasController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHolder implements Serializable {
    private static final long serialVersionUID = 7724049973461006043L;
    private FirstCreditRequestData data;
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean skipRegisterPhone;
    private boolean skipLivePhone;
    private boolean additProfit;
    private boolean additOutcome;
    private ReferenceData creditOrganisationReference;
    private Map<String, List<FiasController.AddressRest>> fiasMap = new HashMap<String, List<FiasController.AddressRest>>();
    private Integer activeStep;

    public DataHolder() {

    }

    public DataHolder(FirstCreditRequestData data) {
        this.data = data;
    }

    public FirstCreditRequestData getData() {
        return data;
    }

    public void setData(FirstCreditRequestData data) {
        this.data = data;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public boolean isSkipRegisterPhone() {
        return skipRegisterPhone;
    }

    public void setSkipRegisterPhone(boolean skipRegisterPhone) {
        this.skipRegisterPhone = skipRegisterPhone;
    }

    public boolean isSkipLivePhone() {
        return skipLivePhone;
    }

    public void setSkipLivePhone(boolean skipLivePhone) {
        this.skipLivePhone = skipLivePhone;
    }

    public boolean isAdditProfit() {
        return additProfit;
    }

    public void setAdditProfit(boolean additProfit) {
        this.additProfit = additProfit;
    }

    public boolean isAdditOutcome() {
        return additOutcome;
    }

    public void setAdditOutcome(boolean additOutcome) {
        this.additOutcome = additOutcome;
    }

    public ReferenceData getCreditOrganisationReference() {
        return creditOrganisationReference;
    }

    public void setCreditOrganisationReference(ReferenceData creditOrganisationReference) {
        this.creditOrganisationReference = creditOrganisationReference;
    }

    public Map<String, List<FiasController.AddressRest>> getFiasMap() {
        return fiasMap;
    }

    public void setFiasMap(Map<String, List<FiasController.AddressRest>> fiasMap) {
        this.fiasMap = fiasMap;
    }

    public Integer getActiveStep() {
        return activeStep;
    }

    public void setActiveStep(Integer activeStep) {
        this.activeStep = activeStep;
    }
}
