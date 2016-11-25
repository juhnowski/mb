package ru.simplgroupp.webapp.analysis.reference.controller;

import ru.simplgroupp.dao.interfaces.RefAntifraudRulesDAO;
import ru.simplgroupp.interfaces.service.RefAntifraudRulesService;
import ru.simplgroupp.persistence.antifraud.RefAntifraudRulesEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.RefAntifraudRules;
import ru.simplgroupp.transfer.RefAntifraudRulesParams;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.*;

public class EditReferenceAntifraudRulesController extends AbstractSessionController implements Serializable {
    @EJB
    private RefAntifraudRulesDAO refAntifraudRulesDAO;

    private Integer ruleId;

    private RefAntifraudRules rule;

    private List<RefAntifraudRulesParams> paramsList;


    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            if (ruleId == null) {
                Map<String, String> parameters = facesCtx.getExternalContext().getRequestParameterMap();
                if (parameters.containsKey("ruleId")) {
                    ruleId = Convertor.toInteger(parameters.get("ruleId"));
                }
            }
            if (ruleId != null) {
                rule = refAntifraudRulesDAO.getRefAntifraudRule(ruleId, null);
                paramsList = refAntifraudRulesDAO.findParams(ruleId, null);
            } else {
                rule = new RefAntifraudRules();
                paramsList = new ArrayList<>();
            }
        }
    }

    public String save() {
        for (RefAntifraudRulesParams param : paramsList) {
            if (param.getValueString().length() == 0) {
                param.setValueString(null);
            }
            if (param.getValueInteger() == 0) {
                param.setValueInteger(null);
            }
            if (param.getValueFloat() == 0) {
                param.setValueFloat(null);
            }
            if (param.getGroupCondition().length() == 0) {
                param.setGroupCondition(null);
            }
            if (param.getGroupIndex() == 0) {
                param.setGroupIndex(null);
            }
        }


        if (rule.getEntityName().length() == 0) {
            rule.setEntityName(null);
        }
        if (rule.getEntityAlias().length() == 0) {
            rule.setEntityAlias(null);
        }
        if (rule.getResultType().length() == 0) {
            rule.setResultType(null);
        }
        if (rule.getResultTypeAlias().length() == 0) {
            rule.setResultTypeAlias(null);
        }
        if (rule.getResultJoin().length() == 0) {
            rule.setResultJoin(null);
        }

        RefAntifraudRulesEntity savedRule = refAntifraudRulesDAO.saveEntity(rule.getEntity());

        if (ruleId == null) {
            ruleId = savedRule.getId();
        }
        refAntifraudRulesDAO.updateParams(ruleId, paramsList);

        return "success";
    }

    public String cancel() {
        return "canceled";
    }

    public void addParam() {
        RefAntifraudRulesParams params = new RefAntifraudRulesParams();
        params.getEntity().setRulesId(rule.getEntity());
//        rule.getParamList().add(params);
        paramsList.add(params);
    }

    public void removeParam(ActionEvent event) {
        RefAntifraudRulesParams paramRemove = (RefAntifraudRulesParams) event.getComponent().getAttributes().get("ruleParam");
        paramsList.removeAll(Collections.singleton(paramRemove));
//        rule.getParamList().removeAll(Collections.singleton(paramRemove));
    }

    public RefAntifraudRules getRule() {
        return rule;
    }

    public List<RefAntifraudRulesParams> getParamsList() {
        return paramsList;
    }
}
