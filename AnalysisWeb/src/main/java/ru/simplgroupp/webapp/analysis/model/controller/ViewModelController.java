package ru.simplgroupp.webapp.analysis.model.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.interfaces.ModelBeanLocal;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.AIModel;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class ViewModelController extends AbstractSessionController implements Serializable {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1880097311478930121L;

	@EJB
	ModelBeanLocal modelBean;
	
	protected Integer prmId;
	protected AIModel model;
	
	@PostConstruct
	public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed())
        {
            if (prmId == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("modelid")) {
                	prmId = Convertor.toInteger( prms.get("modelid").toString() );
                }
            }
            if (prmId != null) {
                reloadModel(facesCtx, prmId);
            }
        }	
	}

	protected void reloadModel(FacesContext facesCtx, Integer prmId2) {
		model = modelBean.getModel(prmId2, Utils.setOf());
	}


	public Integer getPrmId() {
		return prmId;
	}

	public void setPrmId(Integer prmId) {
		this.prmId = prmId;
	}

	public AIModel getModel() {
		return model;
	}
	
	public String close() {
		return "closed";
	}
}
