package ru.simplgroupp.webapp.analysis.verify.controller;

import java.io.Serializable;

import javax.ejb.EJB;

import ru.simplgroupp.interfaces.QuestionBeanLocal;
import ru.simplgroupp.persistence.QuestionEntity;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class NewQuestController extends AbstractSessionController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	QuestionBeanLocal questBean;	
	
	protected String prmQuestionCode;
	protected Integer productId;
	
	public String create() {
				
		QuestionEntity quest = questBean.createQuestion(prmQuestionCode,productId);
		String surl = "edit?faces-redirect=true&questid=" + quest.getId().toString();
		return surl;
	}

	public String getPrmQuestionCode() {
		return prmQuestionCode;
	}

	public void setPrmQuestionCode(String prmQuestionCode) {
		this.prmQuestionCode = prmQuestionCode;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	
}
