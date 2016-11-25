package ru.simplgroupp.webapp.analysis.verify.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.interfaces.QuestionBeanLocal;
import ru.simplgroupp.persistence.QuestionVariantEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.NameValuePair;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Question;
import ru.simplgroupp.webapp.analysis.model.AppBean;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class EditQuestController extends AbstractSessionController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	QuestionBeanLocal questBean;
	
	@EJB
	AppBean appBean;
	
	protected Integer prmId;
	protected Question question;
	protected List<NameValuePair> variants = new ArrayList<NameValuePair>(3);

	@PostConstruct
	public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed())
        {
            if (prmId == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("questid")) {
                	prmId = Convertor.toInteger( prms.get("questid").toString() );
                }
            }
            if (prmId != null) {
                reloadQuest(facesCtx, prmId);
            }
        }	
	}
	
	public Map<Integer, NameValuePair> getVariants() {
		return new Map<Integer, NameValuePair>() {

			@Override
			public int size() {
				return question.getEntity().getVariants().size();
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsKey(Object key) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsValue(Object value) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public NameValuePair get(Object key) {
				Integer varIdx = (Integer) key;
				return variants.get(varIdx);
			}

			@Override
			public NameValuePair put(Integer key,
					NameValuePair value) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public NameValuePair remove(Object key) {
				// TODO Auto-generated method stub
							
				return null;
			}

			@Override
			public void putAll(
					Map<? extends Integer, ? extends NameValuePair> m) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void clear() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Set<Integer> keySet() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Collection<NameValuePair> values() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<java.util.Map.Entry<Integer, NameValuePair>> entrySet() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}
	
	public List<Integer> getVariantsIds() {
		ArrayList<Integer> lstRes = new ArrayList<Integer>(variants.size());
		for (int i = 0; i < variants.size(); i++) {
			lstRes.add(new Integer(i));
		}
		return lstRes;
	}
	
	private void reloadQuest(FacesContext facesCtx, Integer prmId2) {
		question = questBean.getQuestion(prmId2, Utils.setOf());
		variants.clear();
		for (QuestionVariantEntity qvar: question.getEntity().getVariants()) {
			NameValuePair pair = new NameValuePair(qvar.getAnswerText(), qvar.getAnswerValue());
			variants.add(pair);
		}
		for (int i = 0; i < 5; i++) {
			NameValuePair pair = new NameValuePair("", "");
			variants.add(pair);	
		}
	}

	public Integer getPrmId() {
		return prmId;
	}

	public void setPrmId(Integer prmId) {
		this.prmId = prmId;
	}

	public Question getQuestion() {
		return question;
	}

	/**
	 * Проверяем, есть ли дублирующие коды вариантов ответа
	 * @return
	 */
	private boolean checkCodes() {
		for (int i = 0; i < variants.size(); i++) {
			NameValuePair pair = variants.get(i);
			if (StringUtils.isBlank(pair.getValue().toString())) {
				continue;
			}
			
			for (int j = i + 1; j < variants.size(); j++) {
				NameValuePair pair2 = variants.get(j);
				if (StringUtils.isBlank(pair2.getValue().toString())) {
					continue;
				}
				if (pair.getValue().equals(pair2.getValue())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private int findNVByValue(String code) {
		for (int i = 0; i < variants.size(); i++) {
			NameValuePair pair = variants.get(i);
			if (code.equals(pair.getValue())) {
				return i;
			}
		}
		return -1;
	}
	
	public String save() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if ((question.getAnswerType() == Question.ANSWER_SINGLE || question.getAnswerType() == Question.ANSWER_MULTI) && checkCodes()) {
			JSFUtils.handleAsError(facesCtx, null, new Exception("Некоторые коды в вариантах ответов повторяются"));
			return null;
		}
		
		if (question.getAnswerType() == Question.ANSWER_SINGLE || question.getAnswerType() == Question.ANSWER_MULTI) {
			appBean.saveQuestionVariants(question.getEntity(), variants);
		} else {
			// удаляем варианты ответов
			questBean.removeQuestionVariants(question.getEntity());
		}
		
		return "success";
	}
	
	public String cancel() {
		return "canceled";
	}
	
	public void addVariantLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		for (int i = 0; i < 5; i++) {
			NameValuePair pair = new NameValuePair("", "");
			variants.add(pair);	
		}
		reloadQuest(facesCtx, prmId);
	}
	
	public String dummy(){
		return null;
	}
	
	public void deleteVariantLsn(ActionEvent event) {
		Integer answerid = Convertor.toInteger(event.getComponent().getAttributes().get("answeridx"));
		
		Integer i=questBean.findQuestionVariantId(prmId, variants.get(answerid).getValue().toString(), variants.get(answerid).getName());
		
		if (i!=null) {
		  variants.remove(answerid);
		  questBean.removeQuestionVariant(i);
		
		}
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		reloadQuest(facesCtx, prmId);
			
	}	
}
