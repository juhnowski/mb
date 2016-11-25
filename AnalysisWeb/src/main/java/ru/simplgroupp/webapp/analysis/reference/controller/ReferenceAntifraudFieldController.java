package ru.simplgroupp.webapp.analysis.reference.controller;

import ru.simplgroupp.interfaces.AntifraudBeanLocal;
import ru.simplgroupp.transfer.AntifraudFieldType;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

public class ReferenceAntifraudFieldController extends AbstractSessionController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5435436111004324347L;

    @EJB
    protected AntifraudBeanLocal antifraudBean;

    protected Integer id;
    protected String type;
    protected String description;
    protected List<AntifraudFieldType> antifraudFieldTypeList;

    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            getAntifraudFieldTypeList();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<AntifraudFieldType> getAntifraudFieldTypeList() {
        antifraudFieldTypeList = antifraudBean.getAntifraudFieldTypes();
        return antifraudFieldTypeList;
    }

    public void addItem(ActionEvent event) {
        antifraudBean.addAntifraudFieldType(type, description);
        getAntifraudFieldTypeList();
    }

    public void saveItems(ActionEvent event) {
        antifraudBean.saveAntifraudFieldTypes(antifraudFieldTypeList);
        getAntifraudFieldTypeList();
    }
}
