package ru.simplgroupp.webapp.analysis.reference.controller;

import ru.simplgroupp.interfaces.BIKUpdaterLocal;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import java.io.Serializable;

public class UpdateReferenceController extends AbstractSessionController implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2858617931250254912L;

	@EJB
	BIKUpdaterLocal BIKUpdater;

	private byte[] bikDB;
		
	public void init(){
		
	}
	
	public void uploadListener(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getUploadedFile();
		if (item.getName().toUpperCase().endsWith("DBF")){
			bikDB = item.getData();
			
		}
	}
	
	public String updateBanks() {
		if(bikDB == null) 
			return null;
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			BIKUpdater.updateBanks(bikDB);
		} catch (Exception e) {
			JSFUtils.handleAsError(facesCtx, null, e);
		}
		return null;
	}
}
