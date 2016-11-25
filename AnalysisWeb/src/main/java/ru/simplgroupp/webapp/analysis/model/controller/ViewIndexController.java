package ru.simplgroupp.webapp.analysis.model.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.richfaces.component.SortOrder;

import ru.simplgroupp.persistence.AIModelEntity;
import ru.simplgroupp.dao.interfaces.AIModelDAO;
import ru.simplgroupp.exception.ModelException;
import ru.simplgroupp.interfaces.ModelBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.AIModel;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ViewIndexController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4026277042074464185L;

	@EJB
	ModelBeanLocal modelBean;
	
	@EJB
	AIModelDAO modelDAO;
	
	protected List<AIModel> activeModels;
	protected Map<Integer,Boolean> markVersions = new HashMap<Integer,Boolean>(20);
	protected String searchContent;
	protected Map <String, SortOrder> sortOrders = new HashMap<String, SortOrder>(3);
	protected List<String> priorityList = new ArrayList<String>(3);
	
	@PostConstruct
	public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
        	reloadModels();
        }
	}
	
	private void reloadModels() {
		activeModels = modelBean.getActiveModels();
		markVersions.clear();
		
		sortOrders.put("isActive", SortOrder.unsorted);
		sortOrders.put("version", SortOrder.unsorted);
		sortOrders.put("dateChange", SortOrder.unsorted);
		priorityList.add("isActive");
		priorityList.add("version");
		priorityList.add("dateChange");
	}
	
	public List<AIModel> getActiveModels() {
		return activeModels;
	}
	
	public void createDraftLsn(ActionEvent event) {	
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Integer modelId = Convertor.toInteger(event.getComponent().getAttributes().get("modelid"));
		if (modelId!=null) {
		    AIModelEntity draft = modelBean.createDraftFrom(modelId);
		    modelDAO.copyModelParams(modelId, null, draft.getId(), null);
		    facesCtx.getApplication().getNavigationHandler().handleNavigation(facesCtx, null, "edit?faces-redirect=true&modelid=" + draft.getId().toString());
		}
	}
	
	public void makeActiveLsn(ActionEvent event) {
		Integer modelId = Convertor.toInteger(event.getComponent().getAttributes().get("modelid"));
		if (modelId!=null) {
		    modelBean.makeActive(modelId);
		    reloadModels();
		}
	}
	
	public void removeModelsLsn(ActionEvent event) {
		for (Map.Entry<Integer, Boolean> ent: markVersions.entrySet()) {
			if (ent.getKey() != null && ent.getValue() != null && ent.getValue().booleanValue()) {
				modelBean.removeModel(ent.getKey());
			}
		}
	}
	
	public void searchModelLsn(ActionEvent event) {
		if (StringUtils.isBlank(searchContent)) {
			return;
		}
		
		String modelTarget = Convertor.toString(event.getComponent().getAttributes().get("modelTarget"));
		
		for (Integer ikey: markVersions.keySet()) {
			markVersions.put(ikey, new Boolean(false));
		}
		List<AIModelEntity> lstFound = modelBean.listModels(modelTarget, null, null, searchContent.trim(),null, null);
		for (AIModelEntity mdl: lstFound) {
			markVersions.put(mdl.getId(), new Boolean(true));
		}
		// TODO
	}
	
	public void removeModelLsn(ActionEvent event) {
		Integer modelId = Convertor.toInteger(event.getComponent().getAttributes().get("modelid"));
		if (modelId!=null) {
		    modelBean.removeModel(modelId);
		}
	}
	
	public void removeModelDraftLsn(ActionEvent event) {
		modelBean.removeDraftModels();
	}
	
	public void deleteModelLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		Integer modelId = Convertor.toInteger(event.getComponent().getAttributes().get("modelid"));
		try {
			if (modelId!=null) {
			    modelBean.removeActiveModel(modelId);
			}
			reloadModels();
		} catch (ModelException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
		}
	}	
	
	public Map<String, List> getModelVersions() {
		return new Map<String, List>() {

			@Override
			public int size() {
				return activeModels.size();
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
			public List get(Object key) {
				List<AIModel> lstMod =  modelBean.getModelVersions(key.toString(), false);
				if (lstMod != null) {
					for (AIModel mdl: lstMod) {
						if (! markVersions.containsKey(mdl.getId())) {
							markVersions.put(mdl.getId(), new Boolean(false));
						}
					}
				}
				return lstMod;
			}

			@Override
			public List put(String key, List value) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List remove(Object key) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void putAll(Map<? extends String, ? extends List> m) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void clear() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Set<String> keySet() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Collection<List> values() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<java.util.Map.Entry<String, List>> entrySet() {
				// TODO Auto-generated method stub
				return null;
			}
			
		
		};
	}

	public String getSearchContent() {
		return searchContent;
	}

	public void setSearchContent(String searchContent) {
		this.searchContent = searchContent;
	}

	public Map<Integer, Boolean> getMarkVersions() {
		return markVersions;
	}

	public Map<String, SortOrder> getSortOrders() {
		return sortOrders;
	}

	public List<String> getPriorityList() {
		return priorityList;
	}

}
