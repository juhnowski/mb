package ru.simplgroupp.webapp.analysis.reference.controller;

import ru.simplgroupp.interfaces.VerificationReferenceBeanLocal;
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
import java.util.Iterator;
import java.util.Map;

public class EditCreditCompareController extends AbstractSessionController implements Serializable {
    private static final long serialVersionUID = 5130867646662360724L;

    private Integer referenceId;

    private VerificationReference reference;

    @EJB
    private VerificationReferenceBeanLocal verificationReference;


    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            if (referenceId == null) {
                Map<String, String> parameters = facesCtx.getExternalContext().getRequestParameterMap();
                if (parameters.containsKey("referenceId")) {
                    referenceId = Convertor.toInteger(parameters.get("referenceId"));
                }
            }
            if (referenceId != null) {
                reference = verificationReference.getReference(referenceId,
                        Utils.setOf(VerificationReference.Options.INIT_CONFIGURATIONS));
            }
        }
    }

    public String save() {
        if (reference.getConfigurations().size() == 0) {
            FacesContext facesCtx = FacesContext.getCurrentInstance();
            JSFUtils.handleAsError(facesCtx, null, new Exception("Должна быть как минимум одна конфигурация"));
            return null;
        }

        verificationReference.saveReference(reference);
        return "success";
    }

    public String cancel() {
        return "canceled";
    }

    public void addConfig() {
        VerificationConfig newConfig = new VerificationConfig();
        newConfig.setDiff(0d);
        newConfig.setRating(0d);
        reference.getConfigurations().add(newConfig);
    }

    public void removeConfig(ActionEvent event) {
        if (reference.getConfigurations().size() == 1) {
            FacesContext facesCtx = FacesContext.getCurrentInstance();
            JSFUtils.handleAsError(facesCtx, null, new Exception("Должна быть как минимум одна конфигурация"));
            return;
        }

        VerificationConfig configRemove = (VerificationConfig) event.getComponent().getAttributes().get("config");

        Iterator<VerificationConfig> iterator = reference.getConfigurations().iterator();
        while (iterator.hasNext()) {
            VerificationConfig config = iterator.next();
            if (config.equals(configRemove)) {
                iterator.remove();
            }
        }
    }

    public VerificationReference getReference() {
        return reference;
    }
}
