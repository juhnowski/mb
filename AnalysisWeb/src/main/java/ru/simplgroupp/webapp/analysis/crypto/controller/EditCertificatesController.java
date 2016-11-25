package ru.simplgroupp.webapp.analysis.crypto.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import org.admnkz.crypto.app.ICryptoService;
import org.admnkz.crypto.data.Certificate;
import org.admnkz.crypto.data.Settings;

import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

public class EditCertificatesController extends AbstractSessionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6553395439384134720L;
	
	@EJB
	protected ICryptoService crypto;
	protected String reqId;
	protected Certificate certificate;
	protected Settings settings;
	protected String settingsId;
	
	 @PostConstruct
	 public void init()	{
			FacesContext facesCtx = FacesContext.getCurrentInstance();
			if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()) {
				if (reqId == null) {
					Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
					if (prms.containsKey("reqid")) {
						reqId = prms.get("reqid");
					}
				}
				if (reqId != null) {
					reloadItem(facesCtx, reqId);
				}
			}
		}
	 
	   protected void reloadItem(FacesContext facesCtx, String refId) {
			certificate=crypto.getCertificate(refId, Utils.setOf(Certificate.Options.INIT_SETTINGS));
			if (certificate!=null){
				settings=certificate.getSettings().get(0);
				settingsId=settings.getID();
			}
		}
	   
    public String close(){
	   return "close";
	}

    public String save(){
       settings=crypto.saveSettings(settings);		
       certificate=crypto.saveCertificate(certificate);	
       if (!settingsId.equals(settings.getID())){
    	   crypto.removeSettings(settingsId);
       }
 	   return "save";
 	}

    public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public String getSettingsId() {
		return settingsId;
	}

	public void setSettingsId(String settingsId) {
		this.settingsId = settingsId;
	}
	   
	
}
