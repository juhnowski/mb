package ru.simplgroupp.webapp.analysis.reference.controller;

import ru.simplgroupp.dao.interfaces.RefAntifraudRulesDAO;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.RefAntifraudRules;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

public class ReferenceAntifraudRulesController extends AbstractSessionController implements Serializable {
    @EJB
    private RefAntifraudRulesDAO refAntifraudRulesDAO;

    private List<RefAntifraudRules> antifraudRulesList;


    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            refreshRules();
        }
    }

    public void refreshRules() {
        antifraudRulesList = refAntifraudRulesDAO.findAllRules(null);
    }

    public void deleteRule(ActionEvent event) {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        Integer id = Convertor.toInteger(event.getComponent().getAttributes().get("ruleId"));
        if (id != null) {
            if (refAntifraudRulesDAO.removeRule(id)) {
                refreshRules();
            } else {
                JSFUtils.addAsError(facesCtx, false, "Ошбика удаления");
            }
        }
    }

    public List<RefAntifraudRules> getAntifraudRulesList() {
        return antifraudRulesList;
    }
}
