package ru.simplgroupp.rest.api.data.firstcreditrequest;

import ru.simplgroupp.rest.api.data.SocialData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class InitialInfoData implements Serializable {
    private List<ReferenceData> marriageTypes;
    private List<ReferenceData> realtyTypes;
    private List<ReferenceData> educationTypes;
    private List<ReferenceData> employTypes;
    private List<ReferenceData> extSalaryTypes;
    private List<ReferenceData> banksList;
    private List<ReferenceData> creditTypes;
    private List<ReferenceData> currencyTypes;
    private List<ReferenceData> сreditOverdueTypes;
    private List<ReferenceData> professions;
    private List<ReferenceData> professionTypes;
    private List<ReferenceData> creditPurposes;
    private Map<String, Integer> accountTypesMap;
    private SocialData socialData;
    private DataHolder prevData;
    private ValidationDicts validationDicts;

    public List<ReferenceData> getMarriageTypes() {
        return marriageTypes;
    }

    public void setMarriageTypes(List<ReferenceData> marriageTypes) {
        this.marriageTypes = marriageTypes;
    }

    public List<ReferenceData> getRealtyTypes() {
        return realtyTypes;
    }

    public void setRealtyTypes(List<ReferenceData> realtyTypes) {
        this.realtyTypes = realtyTypes;
    }

    public List<ReferenceData> getEducationTypes() {
        return educationTypes;
    }

    public void setEducationTypes(List<ReferenceData> educationTypes) {
        this.educationTypes = educationTypes;
    }

    public List<ReferenceData> getEmployTypes() {
        return employTypes;
    }

    public void setEmployTypes(List<ReferenceData> employTypes) {
        this.employTypes = employTypes;
    }

    public List<ReferenceData> getExtSalaryTypes() {
        return extSalaryTypes;
    }

    public void setExtSalaryTypes(List<ReferenceData> extSalaryTypes) {
        this.extSalaryTypes = extSalaryTypes;
    }

    public List<ReferenceData> getBanksList() {
        return banksList;
    }

    public void setBanksList(List<ReferenceData> banksList) {
        this.banksList = banksList;
    }

    public List<ReferenceData> getCreditTypes() {
        return creditTypes;
    }

    public void setCreditTypes(List<ReferenceData> creditTypes) {
        this.creditTypes = creditTypes;
    }

    public List<ReferenceData> getCurrencyTypes() {
        return currencyTypes;
    }

    public void setCurrencyTypes(List<ReferenceData> currencyTypes) {
        this.currencyTypes = currencyTypes;
    }

    public List<ReferenceData> getСreditOverdueTypes() {
        return сreditOverdueTypes;
    }

    public void setСreditOverdueTypes(List<ReferenceData> сreditOverdueTypes) {
        this.сreditOverdueTypes = сreditOverdueTypes;
    }

    public List<ReferenceData> getProfessions() {
        return professions;
    }

    public void setProfessions(List<ReferenceData> professions) {
        this.professions = professions;
    }

    public List<ReferenceData> getCreditPurposes() {
        return creditPurposes;
    }

    public void setCreditPurposes(List<ReferenceData> creditPurposes) {
        this.creditPurposes = creditPurposes;
    }

    public Map<String, Integer> getAccountTypesMap() {
        return accountTypesMap;
    }

    public void setAccountTypesMap(Map<String, Integer> accountTypesMap) {
        this.accountTypesMap = accountTypesMap;
    }

    public List<ReferenceData> getProfessionTypes() {
        return professionTypes;
    }

    public void setProfessionTypes(List<ReferenceData> professionTypes) {
        this.professionTypes = professionTypes;
    }

    public DataHolder getPrevData() {
        return prevData;
    }

    public void setPrevData(DataHolder prevData) {
        this.prevData = prevData;
    }

    public ValidationDicts getValidationDicts() {
        return validationDicts;
    }

    public void setValidationDicts(ValidationDicts validationDicts) {
        this.validationDicts = validationDicts;
    }

    public SocialData getSocialData() {
        return socialData;
    }

    public void setSocialData(SocialData socialData) {
        this.socialData = socialData;
    }
}
