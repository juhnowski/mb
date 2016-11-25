package ru.simplgroupp.webapp.managernew.rest;

import org.apache.commons.lang3.StringUtils;
import ru.simplgroupp.ejb.RuleEvent;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.util.RulesKeys;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class AppInitializerManager {
	
	@EJB
	RulesBeanLocal rulesBean;
	
	private Map<String,Object> appConst = new HashMap<String,Object>(2);
	
	@PostConstruct
	public void init() {
		appConst = rulesBean.getMainWebConstant();	
	}
	
	public Map<String,Object> getAppConstant() {
		return appConst;
	}
	
    public String getAppearance() {
    	String sapp = (String) appConst.get("appearance");
    	if (StringUtils.isBlank(sapp)) {
    		sapp = "lovizaim";
    	}
    	return sapp;
    }	
	
    public void consumeEvent(@Observes RuleEvent event) throws InterruptedException {
    	if (event.getRuleName().equals(RulesKeys.AC_MAIN)) {
    		appConst = rulesBean.getMainWebConstant();
    	}
    }
    
	// TODO ловить событие на изменение констант
}
