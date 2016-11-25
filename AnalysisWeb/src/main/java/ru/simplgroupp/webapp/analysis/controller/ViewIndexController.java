package ru.simplgroupp.webapp.analysis.controller;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;

import ru.simplgroupp.dao.interfaces.ProductDAO;
import ru.simplgroupp.dao.interfaces.RulesDAO;
import ru.simplgroupp.interfaces.ModelBeanLocal;
import ru.simplgroupp.interfaces.QuestionBeanLocal;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ViewIndexController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB
	protected RulesDAO rulesDAO;

	@EJB
	protected ModelBeanLocal modelBean;
	@EJB
	protected QuestionBeanLocal questBean;
    @EJB
    protected ProductDAO productDAO;
	
	public int getRulesCount() {
		return rulesDAO.countRules(null, null,null, null);
	}
	
	public int getProductsCount(){
		return productDAO.getProductsActive().size();
	}
	
	public int getQuestionCount(){
		return questBean.countQuestion("", "", null,null);
	}
	
	public Date getModelDate(){
		return modelBean.getActiveModel(null).getDateCreate();
	}
	
	public String getModelVersion(){
		return modelBean.getActiveModel(null).getVersion();
	}
}
