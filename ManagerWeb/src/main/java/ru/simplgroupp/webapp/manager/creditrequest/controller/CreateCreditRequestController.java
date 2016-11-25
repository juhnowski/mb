package ru.simplgroupp.webapp.manager.creditrequest.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.facade.AdditionalDataDto;
import ru.simplgroupp.facade.CreditRequestCreationFacade;
import ru.simplgroupp.facade.EmploymentDataDto;
import ru.simplgroupp.facade.PeopleFacade;
import ru.simplgroupp.facade.PersonalDataDto;
import ru.simplgroupp.facade.RegistrationAddressDataDto;
import ru.simplgroupp.facade.ResidentialAddressDataDto;
import ru.simplgroupp.interfaces.PeopleBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.UsersBeanLocal;
import ru.simplgroupp.persistence.AccountEntity;
import ru.simplgroupp.persistence.AddressEntity;
import ru.simplgroupp.persistence.CreditRequestEntity;
import ru.simplgroupp.persistence.PeopleContactEntity;
import ru.simplgroupp.persistence.UsersEntity;
import ru.simplgroupp.services.CreditDataDto;
import ru.simplgroupp.transfer.Account;
import ru.simplgroupp.transfer.FiasAddress;
import ru.simplgroupp.transfer.PeopleContact;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.fias.controller.ViewAddressController;
import ru.simplgroupp.webapp.manager.people.controller.ListPeopleQuickController;
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.ejb.ObjectNotFoundException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Create credit request controller
 */
@ManagedBean
@ViewScoped
public class CreateCreditRequestController extends AbstractSessionController implements Serializable {

    private static final long serialVersionUID = 2305913883629067646L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCreditRequestController.class);

    @Inject
    private CreditRequestCreationFacade creditRequestCreationFacade;

    @Inject
    private PeopleFacade peopleFacade;

    @Inject
    private PeopleBeanLocal peopleBean;

    @Inject
    private UsersBeanLocal userBean;

    @Inject
    private ReferenceBooksLocal referenceBooks;

    @ManagedProperty("#{listPQ}")
    private ListPeopleQuickController searchPeopleController;

    @ManagedProperty("#{conAddrReg}")
    private ViewAddressController registrationAddressController;

    @ManagedProperty("#{conAddrRes}")
    private ViewAddressController residentialAddressController;

    private Integer creditRequestId;

    private CreditDataDto creditData = new CreditDataDto();

    private Integer peopleId;

    private boolean isNewPeople = false;

    private PersonalDataDto personalData = new PersonalDataDto();

    private AdditionalDataDto additionalData = new AdditionalDataDto();

    private EmploymentDataDto employmentData = new EmploymentDataDto();

    private RegistrationAddressDataDto registrationAddressData = new RegistrationAddressDataDto();

    private ResidentialAddressDataDto residentialAddressData = new ResidentialAddressDataDto();

    private Account account = new Account(new AccountEntity());

    private int step = 0;

    public void newPeople() {
        setPeopleId(null);
        nextStep();
    }

    public void selectPeople(Integer peopleId) {
        setPeopleId(peopleId);
        nextStep();
    }

    public void searchLsn(ActionEvent event) {
    	searchPeopleController.searchLsn(event);
    }
    
    public void savePersonalData() {
        try {
            if (creditRequestId == null) {
                CreditRequestEntity creditRequest = creditRequestCreationFacade.createCreditRequest(creditData,
                        peopleId, personalData, isNewPeople);
                this.creditRequestId = creditRequest.getId();
                this.peopleId = creditRequest.getPeopleMainId().getId();
            } else {
                peopleFacade.savePersonalData(peopleId, personalData, isNewPeople);
            }
            nextStep();
        } catch (Exception e) {
            LOGGER.error("Credit request has not created.", e);
            FacesContext facesCtx = FacesContext.getCurrentInstance();
            JSFUtils.handleAsError(facesCtx, null, new KassaException("Не удалось сохранить данные, ошибка " + e.getMessage()));
        }
    }

    public void saveAdditionalData() throws ObjectNotFoundException {
        peopleFacade.saveAdditionalData(peopleId, additionalData, isNewPeople);
        nextStep();
    }

    public void saveEmploymentData() throws ObjectNotFoundException {
        peopleFacade.saveEmploymentData(peopleId, employmentData, isNewPeople);
        nextStep();
    }

    public void saveAddressData() throws ObjectNotFoundException {
        FacesContext context = FacesContext.getCurrentInstance();
        boolean hasError = false;
        if (!registrationAddressController.getIsCompleted()) {
            JSFUtils.handleAsError(context, "registrationAddress", new Exception("Заполните адрес"));
            hasError = true;
        }
        if (!registrationAddressData.isSameResidentalAddress() && !residentialAddressController.getIsCompleted()) {
            JSFUtils.handleAsError(context, "residentialAddress", new Exception("Заполните адрес"));
            hasError = true;
        }

        if (hasError) {
            return;
        }
        registrationAddressController.levelsToAddress();
        residentialAddressController.levelsToAddress();
        registrationAddressData.setAddress((FiasAddress) registrationAddressController.getAddress());
        residentialAddressData.setAddress((FiasAddress) residentialAddressController.getAddress());
        peopleFacade.saveAddressData(peopleId, registrationAddressData, residentialAddressData, isNewPeople);
        nextStep();
    }

    public void saveAccount() {
        peopleBean.changeAccount(account, peopleId);
        nextStep();
    }

    public String finish() {
        try {
            creditRequestCreationFacade.activateCreditRequest(creditRequestId, userCtl.getUser().getEntity());
            return "edit.xhtml?faces-redirect=true&ccRequestId=" + creditRequestId;
        } catch (Exception e) {
            JSFUtils.handleAsError(FacesContext.getCurrentInstance(), null, e);
            LOGGER.error("Credit request has not created.", e);
            return null;
        }
    }

    public CreditDataDto getCreditData() {
        return creditData;
    }

    public void setCreditData(CreditDataDto creditData) {
        this.creditData = creditData;
    }

    public void setPeopleId(Integer peopleId) {
        if (peopleId == null) {
            isNewPeople = true;
            personalData.clear();
            additionalData.clear();
            employmentData.clear();

            registrationAddressData.clear();
            residentialAddressData.clear();
        } else if (!Objects.equals(peopleId, this.peopleId)) {
            isNewPeople = false;
            personalData = peopleFacade.getPersonalData(peopleId);
            additionalData = peopleFacade.getAdditionalData(peopleId);
            employmentData = peopleFacade.getEmploymentData(peopleId);

            registrationAddressData = peopleFacade.getRegistrationAddressData(peopleId);
            if (registrationAddressData.getAddress() != null) {
                registrationAddressController.setAddress(registrationAddressData.getAddress());
            }

            residentialAddressData = peopleFacade.getResidentialAddressData(peopleId);
            if (residentialAddressData.getAddress() != null) {
                residentialAddressController.setAddress(residentialAddressData.getAddress());
            }
        }

        if (registrationAddressController.getAddress() == null) {
            FiasAddress address = new FiasAddress(new AddressEntity());
            registrationAddressData.setAddress(address);
            registrationAddressController.setAddress(address);
        }
        if (residentialAddressController.getAddress() == null) {
            FiasAddress address = new FiasAddress(new AddressEntity());
            residentialAddressData.setAddress(address);
            residentialAddressController.setAddress(address);
        }
        account.setAccountType(referenceBooks.getAccountType(Account.CARD_TYPE));
        this.peopleId = peopleId;
    }

    public List<SelectItem> getAccountTypes() {
        Reference cardType = referenceBooks.getAccountType(Account.CARD_TYPE);
        return Collections.singletonList(new SelectItem(cardType, cardType.getName()));
    }

    public void changeAccountType(ValueChangeEvent event) {
        Reference type = (Reference) event.getNewValue();
        account.setAccountType(type);
        account.setAccountNumber(null);
        account.setCardNumber(null);
        account.setCardName(null);
    }

    public PersonalDataDto getPersonalData() {
        return personalData;
    }

    public AdditionalDataDto getAdditionalData() {
        return additionalData;
    }

    public EmploymentDataDto getEmploymentData() {
        return employmentData;
    }

    public ListPeopleQuickController getSearchPeopleController() {
        return searchPeopleController;
    }

    public void setSearchPeopleController(ListPeopleQuickController searchPeopleController) {
        this.searchPeopleController = searchPeopleController;
    }

    public boolean getPeopleWorking() {
        if (employmentData.getOccupationCode() == null) {
            return false;
        }

        return 0 == employmentData.getOccupationCode()
                || 1 == employmentData.getOccupationCode()
                || 3 == employmentData.getOccupationCode()
                || 5 == employmentData.getOccupationCode()
                || 9 == employmentData.getOccupationCode();
    }

    public ViewAddressController getRegistrationAddressController() {
        return registrationAddressController;
    }

    public void setRegistrationAddressController(ViewAddressController registrationAddressController) {
        this.registrationAddressController = registrationAddressController;
    }

    public ViewAddressController getResidentialAddressController() {
        return residentialAddressController;
    }

    public void setResidentialAddressController(ViewAddressController residentialAddressController) {
        this.residentialAddressController = residentialAddressController;
    }

    public RegistrationAddressDataDto getRegistrationAddressData() {
        return registrationAddressData;
    }

    public ResidentialAddressDataDto getResidentialAddressData() {
        return residentialAddressData;
    }

    public Account getAccount() {
        return account;
    }

    public int getStep() {
        return step;
    }

    public void nextStep() {
        step++;
    }

    public void previousStep() {
        step--;
    }

    public void validateJobFieldNotNull(FacesContext context, UIComponent component, Object value) {
        if (employmentData.isWorking()) {
            if (value == null) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Заполните поле", "Заполните поле"));
            }
        }
    }

    public void validateJobFieldNotBlank(FacesContext context, UIComponent component, String value) {
        if (employmentData.isWorking()) {
            if (StringUtils.isBlank(value)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Заполните поле", "Заполните поле"));
            }
        }
    }

    public void validatePhone(FacesContext context, UIComponent component, String phone) {
        PeopleContactEntity contactPhone = peopleBean.findPeopleByContactClient(PeopleContact.CONTACT_CELL_PHONE, phone);
        if (contactPhone != null) {
            if (peopleId != null && contactPhone.getPeopleMainId().getId().equals(peopleId)) {
                return;
            }
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Телефон принадлежит другому пользователю", "Телефон принадлежит другому пользователю"));
        }
    }

    public void validateEmail(FacesContext context, UIComponent component, String email) {
        PeopleContactEntity contactEmail = peopleBean.findPeopleByContactClient(PeopleContact.CONTACT_CELL_PHONE, email);
        if (contactEmail != null) {
            if (peopleId != null && contactEmail.getPeopleMainId().getId().equals(peopleId)) {
                return;
            }
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Email принадлежит другому пользователю", "Email принадлежит другому пользователю"));
        }
    }
}
