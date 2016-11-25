package ru.simplgroupp.webapp.analysis.reference.controller;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.RefBonus;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

public class ReferenceBonusController extends AbstractSessionController implements Serializable {
    @EJB
    protected ReferenceBooksLocal referenceBooksLocal;

    protected String name;
    protected Double amount;
    protected Boolean isactive;
    protected Integer codeinteger;

    protected List<RefBonus> listRefBonus;

    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
            updateList();
        }
    }

    public void saveItems() {
        referenceBooksLocal.saveRefBonus(listRefBonus);
        updateList();
        resetForm();
    }

    private void resetForm() {
        name = null;
        amount = null;
    }

    public void updateList() {
        listRefBonus = referenceBooksLocal.getRefBonus();
    }

    public void deleteItem(ActionEvent event) {
        Integer bonusID = Convertor.toInteger(event.getComponent().getAttributes().get("bonusID"));
        if (bonusID != null) {
            referenceBooksLocal.removeRefBonus(bonusID);
            updateList();
        }
    }

    public Integer getMaxCodeInteger() {
        Integer max = 0;
        for (RefBonus bonus : listRefBonus) {
            if (bonus.getCodeinteger() != null && bonus.getCodeinteger() > max) {
                max = bonus.getCodeinteger();
            }
        }
        return max + 1;
    }

    public void addItem(ActionEvent event) {
        RefBonus bonus = new RefBonus();
        bonus.setName(name);
        bonus.setAmount(amount);
        bonus.setIsactive(isactive);
        bonus.setCodeinteger(getMaxCodeInteger());

        referenceBooksLocal.addBonus(bonus);

        updateList();
        resetForm();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<RefBonus> getListRefBonus() {
        return listRefBonus;
    }

    public void setListRefBonus(List<RefBonus> listRefBonus) {
        this.listRefBonus = listRefBonus;
    }

    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }

    public Integer getCodeinteger() {
        return codeinteger;
    }

    public void setCodeinteger(Integer codeinteger) {
        this.codeinteger = codeinteger;
    }
}
