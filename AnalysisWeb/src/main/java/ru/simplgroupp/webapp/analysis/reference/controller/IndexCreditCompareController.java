package ru.simplgroupp.webapp.analysis.reference.controller;

import ru.simplgroupp.interfaces.VerificationReferenceBeanLocal;
import ru.simplgroupp.persistence.VerificationReferenceEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.VerificationConfig;
import ru.simplgroupp.transfer.VerificationReference;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

public class IndexCreditCompareController extends AbstractSessionController implements Serializable {
    private static final long serialVersionUID = 5130867646662360724L;

    @EJB
    private VerificationReferenceBeanLocal verificationReference;

    private List<VerificationReference> referenceList;

    private VerificationReference newReference;


    @PostConstruct
    public void init() {
        VerificationConfig newConfig = new VerificationConfig();
        newConfig.setDiff(0d);
        newConfig.setRating(0d);
        newReference = new VerificationReference();
        newReference.getConfigurations().add(newConfig);

        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            refreshItems();
        }
    }

    public void refreshItems() {
        referenceList = verificationReference.findAllReference(Utils.setOf(VerificationReference.Options.INIT_CONFIGURATIONS));
    }

    public String create() {
        VerificationReferenceEntity newReferenceEntity = verificationReference.saveReference(newReference);
        if (newReferenceEntity != null) {
            return "edit_creditcompare?faces-redirect=true&referenceId=" + newReferenceEntity.getId().toString();
        } else {
            FacesContext facesCtx = FacesContext.getCurrentInstance();
            JSFUtils.handleAsError(facesCtx, null, new Exception("Ошибка сохранения справочника для сравнения кредитов"));
            return null;
        }
    }

    public void deleteItem(ActionEvent event) {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        Integer id = Convertor.toInteger(event.getComponent().getAttributes().get("referenceId"));
        if (id != null) {
            if (verificationReference.removeReference(id)) {
                refreshItems();
            } else {
                JSFUtils.addAsError(facesCtx, false, "Ошбика удаления");
            }
        }
    }

    public List<VerificationReference> getReferenceList() {
        return referenceList;
    }

    public VerificationReference getNewReference() {
        return newReference;
    }
}
