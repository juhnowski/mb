package ru.simplgroupp.webapp.analysis.products.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.dao.interfaces.ProductDAO;
import ru.simplgroupp.interfaces.ProductBeanLocal;
import ru.simplgroupp.persistence.ProductsEntity;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Products;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListProductsController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2914767959965395115L;

	@EJB
	protected ProductDAO productDAO;
	@EJB
	protected ProductBeanLocal productBean;
	@EJB
	protected CreditRequestDAO creditRequestDAO;
	
	protected List<Products> products;
	protected String productName;
	protected String productDescription;
	protected String productCode;
	protected Integer paymentType;
	
	@PostConstruct
	public void init()	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
		    getProducts();
		}
	}
	
	public List<Products> getProducts(){
		return productDAO.getProducts(Utils.setOf(Products.Options.INIT_CONFIG,
				Products.Options.INIT_MESSAGES,Products.Options.INIT_RULES));
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	public String addItem(){
		ProductsEntity product=productBean.addProductHeader(productName, productDescription, productCode, paymentType);
		if (product!=null){
			Integer id=product.getId();
			return "edit?faces-redirect=true&prodid="+id.toString();
		}
		return null;
	}
	
	public void changePaymentType(ValueChangeEvent event){
		paymentType=Convertor.toInteger(event.getNewValue());
	}
	 
	 public void deleteItem(ActionEvent event) {
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
			Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("prodid"));
			if (itemid!=null){
				boolean t=creditRequestDAO.findCreditRequestsWithProduct(itemid);
				if (t){
					JSFUtils.handleAsError(facesCtx, null,new Exception("Этот продукт удалить нельзя, с ним связаны заявки"));
				} else {
					productDAO.deleteProduct(itemid);
				}
			}
	 }
	 
	 public void makeActive(ActionEvent event) {
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
			Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("prodid"));
			if (itemid!=null){
				try {
				    productDAO.makeProductActive(itemid);
				} catch (Exception e){
					JSFUtils.handleAsError(facesCtx, null,new Exception("Не удалось сделать продукт активным, ошибка "+e));
				}
			}
	 }
	 
	 public void makeDefault(ActionEvent event) {
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
			Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("prodid"));
			if (itemid!=null){
				try {
				    productDAO.makeProductDefault(itemid);
				} catch (Exception e){
					JSFUtils.handleAsError(facesCtx, null,new Exception("Не удалось сделать продукт продуктом по умолчанию, ошибка "+e));
				}
			}
	 }
	 
	 public void copyItem(ActionEvent event) {
	    	FacesContext facesCtx = FacesContext.getCurrentInstance();
			Integer itemid = Convertor.toInteger(event.getComponent().getAttributes().get("prodid"));
			if (itemid!=null){
				try {
				    productBean.copyProduct(itemid);
				} catch (Exception e){
					JSFUtils.handleAsError(facesCtx, null,new Exception("Не удалось скопировать продукт, ошибка "+e));
				}
			}
	 }
}
