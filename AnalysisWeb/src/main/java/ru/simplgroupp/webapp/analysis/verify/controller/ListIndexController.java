package ru.simplgroupp.webapp.analysis.verify.controller;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.interfaces.QuestionBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.transfer.Question;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListIndexController extends AbstractListController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	QuestionBeanLocal questBean;
	
	protected String prmQuestionCode;
	protected String prmQuestionText;
	protected Integer prmAnswerType;
	protected Integer prmProductId;
	
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public void removeQuestLsn(ActionEvent event) {
		Integer questid = Convertor.toInteger( event.getComponent().getAttributes().get("questid"));
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			questBean.removeQuestion(questid);
			refreshSearch();
		} catch (Exception ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
		}
	}

	public String dummy() {
		return null;
	}
	
	@Override
	public StdRequestDataModel createModel() {
		return new QuestionDataModel();
	}
	
	void nullIfEmpty() {
		if (StringUtils.isBlank(prmQuestionCode)) {
			prmQuestionCode = null;
		} 
			
		if (JSFUtils.NULL_INT_VALUE.equals(prmAnswerType)) {
			prmAnswerType = null;
		}
		
		if (JSFUtils.NULL_INT_VALUE.equals(prmProductId)) {
			prmProductId = null;
		}
		
		if (StringUtils.isBlank(prmQuestionText)) {
			prmQuestionText = null;
		} 
		
	}
		
	public class QuestionDataModel extends StdRequestDataModel<Question> {

		@Override
		protected List<Question> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
	    	return questBean.listQuestion(seqRange.getFirstRow(), seqRange.getRows(), sorting, null, prmQuestionCode, prmQuestionText, prmAnswerType,prmProductId);
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return questBean.countQuestion(prmQuestionCode, prmQuestionText, prmAnswerType,prmProductId);
		}

		@Override
		public Question getRowData() {
			if (rowKey == null)
				return null;
			else {
				Question res = questBean.getQuestion(((Number) rowKey).intValue(), null);
				return res;
			}
		}
		
	}

	public String getPrmQuestionCode() {
		return prmQuestionCode;
	}

	public void setPrmQuestionCode(String prmQuestionCode) {
		this.prmQuestionCode = prmQuestionCode;
	}

	public String getPrmQuestionText() {
		return prmQuestionText;
	}

	public void setPrmQuestionText(String prmQuestionText) {
		this.prmQuestionText = prmQuestionText;
	}

	public Integer getPrmAnswerType() {
		return prmAnswerType;
	}

	public void setPrmAnswerType(Integer prmAnswerType) {
		this.prmAnswerType = prmAnswerType;
	}

	public Integer getPrmProductId() {
		return prmProductId;
	}

	public void setPrmProductId(Integer prmProductId) {
		this.prmProductId = prmProductId;
	}

	public void clearLsn(ActionEvent event) {
		prmQuestionCode = null;
		prmQuestionText = null;
		prmAnswerType = null;
		prmProductId=null;
		refreshSearch();
	}
}
