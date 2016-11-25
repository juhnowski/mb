package ru.simplgroupp.webapp.analysis.rules.controller;

import java.io.Serializable;

import javax.ejb.EJB;

import ru.simplgroupp.dao.interfaces.RulesDAO;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ViewIndexController extends AbstractSessionController implements Serializable{

		
	/**
	 * 
	 */
	private static final long serialVersionUID = -2649615141079007393L;
	@EJB
	protected RulesDAO rulesDAO;	

	public int getRulesCount() {
		return rulesDAO.countRules(null,null, null, null);
	}
	

	
}
