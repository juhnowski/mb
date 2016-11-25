package ru.simplgroupp.webapp.analysis.plugin.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.PluginsSupport;
import ru.simplgroupp.interfaces.ActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.interfaces.ServiceBeanLocal;
import ru.simplgroupp.interfaces.service.EventLogService;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ViewIndexController extends AbstractSessionController implements Serializable, IEditPluginChanged {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7798899131289691597L;

	@EJB
	RulesBeanLocal rulesBean;
	
	@EJB
	ServiceBeanLocal servBean;
	
	@EJB
	ActionProcessorBeanLocal actProc;
	
	@EJB 
	EventLogService eventLog;	

	protected PluginsSupport support;
	protected Map<String, Object> mpChanged = new HashMap<String, Object>(3);
	
	@PostConstruct
	public void init() {
		reloadPlugins();
	}
	
	private void reloadPlugins() {
		Map<String, Object> opts = rulesBean.getPluginsOptions(null);
		
		support = new PluginsSupport();	
		support.load(null, opts);
	}
	
	public String save() {
	
		Map<String, Object> opts = new HashMap<String, Object>(7);
		support.save(null, opts);
		rulesBean.setPluginsOptions(null, "Рабочие настройки", opts);
		mpChanged.clear();

		ActionContext actionContext = actProc.createActionContext(null, true);
		actProc.savePluginConfigs(actionContext, mpChanged);
		
		return null;

	}
	
	public Map<String, Object> getChanged() {
		return mpChanged;
	}
	
	public String cancel() {
		reloadPlugins();
		return null;
	}
	
	public PluginsSupport getSupport() {
		return support;
	}
	
	public void changed(String name) {
		mpChanged.put(name, true);
	}
}
