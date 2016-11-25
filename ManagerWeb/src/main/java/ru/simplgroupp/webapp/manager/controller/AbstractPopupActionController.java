package ru.simplgroupp.webapp.manager.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.dao.interfaces.BizActionDAO;
import ru.simplgroupp.dao.interfaces.ProductDAO;
import ru.simplgroupp.persistence.BizActionEntity;
import ru.simplgroupp.ejb.EnvironmentSnapshot;
import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.BizActionBeanLocal;
import ru.simplgroupp.interfaces.BizActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.ProductBeanLocal;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.interfaces.service.AppServiceBeanLocal;
import ru.simplgroupp.rules.AbstractContext;
import ru.simplgroupp.rules.CreditRequestContext;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.util.AppUtil;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

abstract public class AbstractPopupActionController<T> extends AbstractSessionController {
	
	private static Logger logger = Logger.getLogger(AbstractPopupActionController.class.getName());
	
	@EJB
	protected RulesBeanLocal rules;
	
	@EJB
	protected ProductBeanLocal productBean;
	
	@EJB
	protected ProductDAO productDAO;
	
	@EJB
	protected AppServiceBeanLocal appServ;
	
	@EJB
	protected BizActionBeanLocal bizBean;
	
	@EJB
	protected BizActionDAO bizDAO;
	
	@EJB
	protected BizActionProcessorBeanLocal bizProc;	
	
	protected Integer prmBusinessObjectId;
	protected T businessObject;
	protected AppDataController appData;
	protected EnvironmentSnapshot snapshot;
	protected List<ActionInfo> actions = new ArrayList<ActionInfo>(5);

	public T getBusinessObject() {
		return businessObject;
	}
	
	public AppDataController getAppData() {
		return appData;
	}

	public void setAppData(AppDataController appData) {
		this.appData = appData;
	}	
	
	public List<ActionInfo> getActions() {
		return actions;
	}	
	
	protected void reloadActions() {
		AbstractContext ctx = AppUtil.getDefaultContext(businessObject, businessObject.getClass().getName());
		ctx.setCurrentUser(userCtl.getUser());
		ctx.setAutoRun(false);
		
		//добавляем константы для расчета разных действий
		Map<String,Object>  limits =null;
		if (ctx.getCredit()!=null){
		    limits = productBean.getAllProductConfig(ctx.getCredit().getProduct().getId());
		} else {
			if (productDAO.getProductDefault()!=null){
				 limits=productBean.getAllProductConfig(productDAO.getProductDefault().getId());
			}
		}
		ctx.getParams().putAll(limits);
		limits = null;
		
		snapshot = appServ.getSnapshot(ctx, businessObject.getClass().getName(), true, false);
		
		//если найдены действия
		if (snapshot.getObjectActions().size() > 0) {
			  for (WorkflowObjectActionDef def: snapshot.getObjectActions()) {
				  if (! def.isEnabled() ) {
					continue;
				  }
				
				  ActionInfo info = new ActionInfo(appData);
				  info.action = def;
				  info.mapIndex = appData.findActionMap(def);
				  if (info.mapIndex >= 0) {
					  actions.add(info);	
				  }				
			  }				
		}
/*		
		List<WorkflowObjectActionDef> lstAct =new ArrayList<WorkflowObjectActionDef>(0);
		//заявки
		if (businessObject instanceof CreditRequest) {
		   lstAct = rules.getObjectActions(CreditRequest.class.getName(),  true, false);
		}
		//займы
		if (businessObject instanceof Credit) {
		   lstAct = rules.getObjectActions(Credit.class.getName(),  true, false);
		}
		//если найдены действия
		if (lstAct.size()>0) {
		  WorkflowUtil.checkWFActionsByRoles(lstAct, userCtl.getUser().getRoles());
		
		  RuntimeServices runtimeServices = new RuntimeServices(null, rules, null, null);

		  WorkflowUtil.checkWFActionsEnabled(lstAct, runtimeServices, ctx);
		
		  for (WorkflowObjectActionDef def: lstAct) {
			  if (! def.isEnabled() ) {
				continue;
			  }
			
			  ActionInfo info = new ActionInfo();
			  info.action = def;
			  info.mapIndex = appData.findActionMap(def);
			  if (info.mapIndex >= 0) {
				  actions.add(info);	
			  }				
		  }	
		}
*/		
	}

	public Integer getPrmBusinessObjectId() {
		return prmBusinessObjectId;
	}

	public void setPrmBusinessObjectId(Integer prmBusinessObjectId) {
		this.prmBusinessObjectId = prmBusinessObjectId;
	}	
	
	abstract protected void reloadData() throws KassaException;
	
	protected boolean handleStdAction(FacesContext facesCtx, int prmBusinessObjectId, WorkflowObjectActionDef actionDef) throws KassaException {
		return false;
	}
	
	public void setCurrentLsn(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		prmBusinessObjectId = Convertor.toInteger(event.getComponent().getAttributes().get("prmBusinessObjectId"));
		
		try {
			businessObject = null;
			actions.clear();	
			
			if (prmBusinessObjectId != null) {
				reloadData();
				if (businessObject != null) {
					reloadActions();
				}
			}
		} catch (KassaException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
		}
	}	
	
	public void executeLsn(ActionEvent event) {
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		prmBusinessObjectId = Convertor.toInteger(event.getComponent().getAttributes().get("prmBusinessObjectId"));
		WorkflowObjectActionDef actionDef = WorkflowObjectActionDef.valueOf((String) event.getComponent().getAttributes().get("actionDef"));
		logger.info("Выполняем действие " + actionDef.toString());
		
		try {
			if (! handleStdAction(facesCtx, prmBusinessObjectId, actionDef) ) {
				if ( ! actionDef.isForProcess()) {
					BizActionEntity bizAct = bizDAO.findBizObjectAction(actionDef.getBusinessObjectClass(), actionDef.getSignalRef());
					if (bizAct != null) {
						logger.info("Выполняем стандартное бизнес-действие " + actionDef.toString());
						AbstractContext ctx =  new CreditRequestContext();
						ctx.setCurrentUser(this.userCtl.getUser());
						ctx.setAutoRun(false);
						bizProc.executeBizAction(bizAct, prmBusinessObjectId, ctx);
					}
				}
			}
			logger.info("Выполнили действие " + actionDef.toString());
		} catch (KassaException ex) {
			logger.info("Не выполнили действие " + actionDef.toString());
			JSFUtils.handleAsError(facesCtx, null, ex);
		} catch (ActionException ex) {
			logger.info("Не выполнили действие " + actionDef.toString());
			JSFUtils.handleAsError(facesCtx, null, ex);
		}		
	}	
}
