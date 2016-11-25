package ru.simplgroupp.webapp.manager.admin.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.PeopleBonus;
import ru.simplgroupp.transfer.RefBonus;
import ru.simplgroupp.transfer.RefSystemFeature;
import ru.simplgroupp.util.RulesKeys;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.controller.SecurityFeatures;
import ru.simplgroupp.webapp.util.JSFUtils;

@SecurityFeatures(value = {RefSystemFeature.ADMIN_MAIN}, all = false)
public class EditSettingsController extends AbstractSessionController implements Serializable{

    @EJB
    private ReferenceBooksLocal refBooks;
    @EJB
    private RulesBeanLocal rulesBean;
    private static final Logger logger = LoggerFactory.getLogger(EditSettingsController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<RefBonus> bonusTypes;
	private Double bonusrate;
	private Double bonuspayprocent;
	private Double bonuspaymaxsum;
	private Boolean isactive;
	
	@PostConstruct
	public void init() {
		super.init();
		reloadSettings();
	}

	protected void reloadSettings(){
		
		bonusTypes = new ArrayList<RefBonus>();
		
		for(RefBonus bon : refBooks.getBonusTypeList())
		{
			if(bon.getCodeinteger() != PeopleBonus.BONUS_CODE_MANUAL)
				bonusTypes.add(bon);
		}
		
		bonusrate = Convertor.toDouble(rulesBean.getBonusConstants().get("bonus.rate"));
		bonuspayprocent = Convertor.toDouble(rulesBean.getBonusConstants().get("bonus.pay.procent"));
		bonuspaymaxsum = Convertor.toDouble(rulesBean.getBonusConstants().get("bonus.pay.max.sum"));
		isactive = Utils.triStateToBoolean(Convertor.toInteger(rulesBean.getBonusConstants().get("bonus.isactive")));
	}
	
	public void saveBonusSettings() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try	{
			
			if (bonusrate!=null)
				rulesBean.updateConstantValue(RulesKeys.LIMITS_BONUS, "bonus.rate", bonusrate.toString());
			if (bonuspayprocent!=null)
				rulesBean.updateConstantValue(RulesKeys.LIMITS_BONUS, "bonus.pay.procent", bonuspayprocent.toString());
			if (bonuspaymaxsum!=null)
				rulesBean.updateConstantValue(RulesKeys.LIMITS_BONUS, "bonus.pay.max.sum", bonuspaymaxsum.toString());
			if (isactive!=null)
				rulesBean.updateConstantValue(RulesKeys.LIMITS_BONUS, "bonus.isactive", 
						Utils.booleanToTriState(isactive).toString());
			
			for(RefBonus bon : bonusTypes)
			{
				refBooks.updateStatusBonusType(bon);
			}
			
		} catch(Exception ex){
			JSFUtils.handleAsError(facesCtx, null, ex);
		}
	}
	

	public List<RefBonus> getBonusTypes() {
		return bonusTypes;
	}

	public void setBonusTypes(List<RefBonus> bonusTypes) {
		this.bonusTypes = bonusTypes;
	}

	public Double getBonusrate() {
		return bonusrate;
	}

	public void setBonusrate(Double bonusrate) {
		this.bonusrate = bonusrate;
	}

	public Double getBonuspayprocent() {
		return bonuspayprocent;
	}

	public void setBonuspayprocent(Double bonuspayprocent) {
		this.bonuspayprocent = bonuspayprocent;
	}

	public Double getBonuspaymaxsum() {
		return bonuspaymaxsum;
	}

	public void setBonuspaymaxsum(Double bonuspaymaxsum) {
		this.bonuspaymaxsum = bonuspaymaxsum;
	}

	public Boolean getIsactive() {
		return isactive;
	}

	public void setIsactive(Boolean isactive) {
		this.isactive = isactive;
	}
}
