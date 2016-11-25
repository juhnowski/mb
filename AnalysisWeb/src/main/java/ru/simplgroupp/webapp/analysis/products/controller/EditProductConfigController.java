package ru.simplgroupp.webapp.analysis.products.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.dao.interfaces.ProductDAO;
import ru.simplgroupp.interfaces.ProductBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.ProductConfig;
import ru.simplgroupp.transfer.ProductMessages;
import ru.simplgroupp.transfer.ProductRules;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class EditProductConfigController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -408940719661834033L;
	private static final Logger logger = LoggerFactory.getLogger(EditProductConfigController.class.getName());
	@EJB
	protected ReferenceBooksLocal refBooks;
	
	@EJB
	ProductDAO productDAO;
	
	@EJB
	ProductBeanLocal productBean;
	
	protected Integer productId;
	protected Integer configId;
    protected List<Reference> lstConfig;
	protected List<ProductConfig> config;
	protected List<ProductRules> rules;
	protected List<ProductMessages> messages;
	protected Date databeg;
	protected String value;
	protected Integer confId;
	protected Integer ruleId;
	
	@PostConstruct
	public void init()	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (productId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("prodid")) {
					productId = Convertor.toInteger(prms.get("prodid"));
				}
				if (prms.containsKey("confid")) {
					configId = Convertor.toInteger(prms.get("confid"));
				}
			}
			if (productId != null&&configId!=null) {
				reloadItem(facesCtx,productId,configId);
			}	
		}
	}
	
	private void reloadItem(FacesContext facesCtx, Integer productId,Integer configId){
		config=productDAO.findProductConfigByType(productId,configId,null);
		rules=productDAO.findProductRulesActiveByType(productId, configId, new Date(), null);
		messages=productDAO.findProductMessagesByType(productId, configId, null);
	}
		
	public Integer getProductId() {
		return productId;
	}

	public Integer getConfigId() {
		return configId;
	}

	public List<Reference> getLstConfig() {
		return lstConfig;
	}

	public void setLstConfig(List<Reference> lstConfig) {
		this.lstConfig = lstConfig;
	}

	public List<ProductConfig> getConfig() {
		return config;
	}

	public List<ProductRules> getRules() {
		return rules;
	}

	public List<ProductMessages> getMessages() {
		return messages;
	}
	
	public String save(){
		if (messages.size()>0){
		    productDAO.saveListProductMessages(getMessages());
		}
		if (config.size()>0){
			productDAO.saveListProductConfig(config);
		}
		if (rules.size()>0){
			productDAO.saveListProductRules(rules);
		}
		return "edit?faces-redirect=true&prodid="+productId;
	}
	
	 public void deleteConfigItem(ActionEvent event) {
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
			Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("confid"));
			if (itemid!=null){
			   try {	
				   ProductConfig cfg=new ProductConfig(productDAO.getProductConfig(itemid));
			       productDAO.deleteProductConfig(itemid);
			       config.remove(cfg);
			   } catch (Exception e){
				   JSFUtils.handleAsError(facesCtx, null,e);
			   }
			}
	 }
   
	 public void addConfigItem(ActionEvent event) {
		ProductConfig cfg=productBean.addProductConfig(productId, configId);
		config.add(cfg);
	 }
	 
	 public void addMessageItem(ActionEvent event) {
		ProductMessages msg=productBean.addProductMessage(productId, configId);
		messages.add(msg);
	 }
	 
	 public void addRuleItem(ActionEvent event) {
		ProductRules rule=productBean.addProductRule(productId, configId);
		rules.add(rule);
	 }
	 
	 public void deleteMessageItem(ActionEvent event) {
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
			Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("mesid"));
			if (itemid!=null){
			   try {	
				   ProductMessages msg=new ProductMessages(productDAO.getProductMessage(itemid));
			       productDAO.deleteProductMessage(itemid);
			       messages.remove(msg);
			   } catch (Exception e){
				   JSFUtils.handleAsError(facesCtx, null,e);
			   }
			}
	 }
	 
	 public void deleteRuleItem(ActionEvent event) {
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
			Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("rulid"));
			if (itemid!=null){
			   try {	
				   ProductRules rule=new ProductRules(productDAO.getProductRule(itemid));
			       productDAO.deleteProductRule(itemid);
			       rules.remove(rule);
			   } catch (Exception e){
				   JSFUtils.handleAsError(facesCtx, null,e);
			   }
			}
	 }
	 
	 public void changeItem() {
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
			if (confId!=null&&confId>0){
				try {
				   productBean.changeProductConfig(confId, databeg, value);
				   confId=0;
				   value="";
				   config=productDAO.findProductConfigByType(productId,configId,null);
			    } catch (Exception e){
				   JSFUtils.handleAsError(facesCtx, null,e);
			    }
			}
			if (ruleId!=null&&ruleId>0){
				try {
				   productBean.changeProductRule(ruleId, databeg, value);
				   ruleId=0;
				   value="";
			    } catch (Exception e){
				   JSFUtils.handleAsError(facesCtx, null,e);
			    }
			}
	 }
	 
	 public void findConfig() {
		logger.info("id конфига "+confId);
		logger.info("id правила "+ruleId);
	 }
	 
	public Date getDatabeg() {
		return databeg;
	}

	public void setDatabeg(Date databeg) {
		this.databeg = databeg;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getConfId() {
		return confId;
	}

	public void setConfId(Integer confId) {
		this.confId = confId;
	}

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}
	 
	 
}

