package ru.simplgroupp.rest;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.service.OrganizationService;
import ru.simplgroupp.persistence.BankEntity;
import ru.simplgroupp.transfer.Bank;
import ru.simplgroupp.transfer.Organization;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * RESTful функции для работы со справочниками
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 22.04.14.
 */
@Stateless
@Path("/reference")
public class ReferenceController {

    @EJB
    protected ReferenceBooksLocal referenceBean;

    @EJB
    OrganizationService orgService;
    
    /**
     * Возвращает справочник семейных положений
     * @return
     */
    @POST
    @Path("/maryed")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getMaried() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getMarriageTypes();
        return getReferenceList(list);
    }

    /**
     * Возвращает справочник оснований занятия жилого помещения
     * @return
     */
    @POST
    @Path("/grounds")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getGrounds() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getRealtyTypes();
        return getReferenceList(list);
    }

    /**
     * Возвращает справочник образования
     * @return
     */
    @POST
    @Path("/educations")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getEducations() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getEducationTypes();
        return getReferenceList(list);
    }

    /**
     * Возвращает справочник типов просроченной задолженности по кредитам
     * @return
     */
    @POST
    @Path("/overdueTypes")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getCreditOverdueTypes() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getCreditOverdueTypes();

        StringBuilder builder = new StringBuilder();
        for (Iterator<ru.simplgroupp.transfer.Reference> i = list.iterator(); i.hasNext(); ) {
            ru.simplgroupp.transfer.Reference o = i.next();
            builder.append("<i><span class='title'>" + o.getName() + "</span><span class='guid'>" + o.getCode() + "</span></i>");
        }
        return builder.toString();
    }

    /**
     * Возвращает справочник доп источников дохода
     * @return
     */
    @POST
    @Path("/extsalarysource")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getExtSalarySource() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getExtSalaryTypes();
        return getReferenceList(list);
    }

    /**
     * Возвращает справочник занятости
     * @return
     */
    @POST
    @Path("/employments")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getEmployment() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getEmployTypes();
        return getReferenceList(list);
    }

    /**
     * Возвращает справочник видов деятельности
     * @return
     */
    @POST
    @Path("/professions")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getProfession() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getOrganizationTypes();
        return getReferenceList(list);
    }

    /**
     * Возвращает справочник частоты получения доходов
     * @return
     */
    @POST
    @Path("/salarydates")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getSalarydate() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getIncomeFreqTypes();
        return getReferenceList(list);
    }

    /**
     * Возвращает справочник банков
     * @return
     */
    @POST
    @Path("/creditbiks")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getCreditbiks() {
        List<Bank> list = referenceBean.getBanksList();
        StringBuilder builder = new StringBuilder();
        for (Iterator<Bank> i = list.iterator(); i.hasNext(); ) {
            Bank o = i.next();
            builder.append("<i><span class='title'>" + o.getName() + "</span><span class='guid'>" + o.getBik() + "</span></i>");
        }
        return builder.toString();
    }

    /**
     * Подсказка биков
     * @return
     */
    @GET
    @Path("/bik-autocomplete")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<BikAutocomplete> bikAutocomplete(@QueryParam("term") String bikPattern) {
        List<BankEntity> banks = referenceBean.findBankByBik(bikPattern, 5);
        List<BikAutocomplete> bikAutocompleteList = new ArrayList<>(banks.size());
        for(BankEntity bank: banks) {
            bikAutocompleteList.add(new BikAutocomplete(bank.getBik(), bank.getBik(), bank.getCorAccount(), bank.getName()));
        }

        return bikAutocompleteList;
    }

    private final class BikAutocomplete {
        private String value;

        private String label;

        private String correspondentAccount;

        private String bankName;

        private BikAutocomplete(String value, String label, String correspondentAccount, String bankName) {
            this.value = value;
            this.label = label;
            this.correspondentAccount = correspondentAccount;
            this.bankName = bankName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getCorrespondentAccount() {
            return correspondentAccount;
        }

        public void setCorrespondentAccount(String correspondentAccount) {
            this.correspondentAccount = correspondentAccount;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }
    }

    /**
     * Возвращает справочник типов кредита
     * @return
     */
    @POST
    @Path("/credittypes")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getCredittypes() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getCreditTypes();
        return getReferenceList(list);
    }

    /**
     * Возвращает справочник валюты кредита
     * @return
     */
    @POST
    @Path("/currencytypes")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getCurrencytype() {
        List<ru.simplgroupp.transfer.Reference> list = referenceBean.getCurrencyTypes();
        return getReferenceList(list);
    }

    /**
     * Возвращает справочник валюты кредита
     *
     * @return
     */
    @POST
    @Path("/credit-organizations")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getCreditOrganizations() {
        List<Organization> list = orgService.getCreditOrganizations();
        StringBuilder builder = new StringBuilder();
        for (Organization organization : list) {
            builder.append("<i><span class='title'>" + organization.getName() + "</span><span class='guid'>"
                    + organization.getId() + "</span></i>");
        }
        return builder.toString();
    }

    /**
     * Возвращает объекты справочника
     * @param list
     * @return
     */
    private String getReferenceList(List<ru.simplgroupp.transfer.Reference> list){
        StringBuilder builder = new StringBuilder();
        for (Iterator<ru.simplgroupp.transfer.Reference> i = list.iterator(); i.hasNext(); ) {
            ru.simplgroupp.transfer.Reference o = i.next();
            builder.append("<i><span class='title'>" + o.getName() + "</span><span class='guid'>" + o.getCodeInteger() + "</span></i>");
        }
        return builder.toString();
    }
}
