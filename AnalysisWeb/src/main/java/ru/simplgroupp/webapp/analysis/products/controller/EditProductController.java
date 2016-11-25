package ru.simplgroupp.webapp.analysis.products.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.dao.interfaces.ProductDAO;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Products;
import ru.simplgroupp.transfer.RefHeader;
import ru.simplgroupp.transfer.Reference;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class EditProductController extends AbstractSessionController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6692745897151773734L;

	@EJB
	protected ProductDAO productDAO;
	@EJB
	protected ReferenceBooksLocal refBooks;
	
	@EJB
	protected CreditRequestDAO creditRequestDAO;
	
	protected Integer productId;
	protected Products product;
	protected List<Reference> listConfig;
	
	@PostConstruct
	public void init()	{
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
			if (productId == null) {
				Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
				if (prms.containsKey("prodid")) {
					productId = Convertor.toInteger(prms.get("prodid"));
				}
			}
			if (productId != null) {
				reloadItem(facesCtx, productId);
			}	
		}
	}
	
	private void reloadItem(FacesContext facesCtx, Integer productId){
		product=productDAO.getProduct(productId, Utils.setOf(Products.Options.INIT_CONFIG,
				Products.Options.INIT_MESSAGES,Products.Options.INIT_RULES));
		listConfig=refBooks.listReference(RefHeader.PRODUCT_CONFIG_TYPE);
	}

	public String save(){
		productDAO.saveProduct(product.getEntity());
		return "close";
	}
	
	public String close(){
		return "close";
	}
	
	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Products getProduct() {
		return product;
	}

	public void setProduct(Products product) {
		this.product = product;
	}

	public List<Reference> getListConfig() {
		return listConfig;
	}
	
	
	
}
