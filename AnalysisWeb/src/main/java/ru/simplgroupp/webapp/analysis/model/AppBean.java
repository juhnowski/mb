package ru.simplgroupp.webapp.analysis.model;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang3.StringUtils;

import ru.simplgroupp.interfaces.QuestionBeanLocal;
import ru.simplgroupp.persistence.QuestionEntity;
import ru.simplgroupp.persistence.QuestionVariantEntity;
import ru.simplgroupp.toolkit.common.NameValuePair;

@Stateless
public class AppBean {
	
	@EJB
	QuestionBeanLocal questBean;	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveQuestionVariants(QuestionEntity question, List<NameValuePair> variants) {
		List<Integer> lstRem = new ArrayList<Integer>(question.getVariants().size());
		List<Integer> lstEx = new ArrayList<Integer>(question.getVariants().size());
		for (QuestionVariantEntity qvar: question.getVariants()) {
			int n = findNVByValue(qvar.getAnswerValue(), variants);
			if (n >= 0) {
				qvar.setAnswerText(variants.get(n).getName());
				lstEx.add(n);
			} else {
				lstRem.add(qvar.getId());
			}
		}
		// добавляем новые варианты из списка
		for (int i = 0; i < variants.size(); i++) {
			NameValuePair pair = variants.get(i);
			if ( (! StringUtils.isBlank(pair.getValue().toString())) && lstEx.indexOf(new Integer(i)) < 0) {
				QuestionVariantEntity qvar = new QuestionVariantEntity();
				qvar.setQuestionId(question);
				qvar.setAnswerValue(pair.getValue().toString());
				qvar.setAnswerText(pair.getName());
				question.getVariants().add(qvar);
			}
		}
		questBean.saveQuestion(question);	
		
		// удаляем ненужные
		for (Integer varId: lstRem) {
			questBean.removeQuestionVariant(varId);
		}
	}
	
	private int findNVByValue(String code, List<NameValuePair> variants) {
		for (int i = 0; i < variants.size(); i++) {
			NameValuePair pair = variants.get(i);
			if (code.equals(pair.getValue())) {
				return i;
			}
		}
		return -1;
	}	
}
