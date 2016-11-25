package ru.simplgroupp.webapp.analysis.plugin.controller;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.webapp.analysis.plugin.PluginUtils;

public class EditPluginController extends AbstractEditPluginController implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8409132090389716469L;
	protected String prmPluginName;
	protected ViewIndexController plugsCtl;
	
	@EJB
	ReferenceBooksLocal refBooks;	
	
	public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed())
        {
            if (prmPluginName == null) {
                Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
                if (prms.containsKey("pluginName")) {
                	prmPluginName = prms.get("pluginName").toString();
                }
            }
            if (prmPluginName != null) {
                reloadPlugin(facesCtx, prmPluginName);
            }
        }	
	}
	
	public void reloadPlugin(FacesContext facesCtx, String prmPluginName2) {
		if (plugsCtl == null) {
			return;
		}
		plugin = plugsCtl.getSupport().getPluginConfig(prmPluginName2);
		subscriber = plugsCtl;
		
		super.reloadPlugin(facesCtx, prmPluginName2);
		PluginUtils.attachExtCtl(prmPluginName2, this);
	}

	public String getPrmPluginName() {
		return prmPluginName;
	}

	public void setPrmPluginName(String prmPluginName) {
		this.prmPluginName = prmPluginName;
	}

	public ViewIndexController getPlugsCtl() {
		return plugsCtl;
	}

	public void setPlugsCtl(ViewIndexController plugsCtl) {
		this.plugsCtl = plugsCtl;
	}

	@Override
	public ReferenceBooksLocal getRefBooks() {
		return refBooks;
	}	
}
