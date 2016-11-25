package ru.simplgroupp.webapp.manager.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.richfaces.event.ItemChangeEvent;
import org.richfaces.component.UIPanelMenu;
import org.richfaces.component.UIPanelMenuItem;

import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.workflow.WorkflowObjectStateDef;

public class ViewProcessStatesController extends AbstractSessionController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2862148149418496235L;

	@EJB
	private WorkflowBeanLocal workflowServ;
	
	private UIPanelMenu pnmLeft;
	
	private String currentStateName = "stateAll";
	
//	@PostConstruct
	public void init() {
		reloadMenuLeft();
	}

	private void reloadMenuLeft() {
		if (pnmLeft == null) {
			return;
		}
		
		if (pnmLeft.getChildCount() > 0) {
			return;			
		}
		
		List<WorkflowObjectStateDef> wfStates = workflowServ.getCreditProcessStatesForRequest();
		
		Properties prop = new Properties();
		try {
			InputStream stm = this.getClass().getClassLoader().getResourceAsStream("/crStates.properties"); 
			if (stm != null) {
				try {
					prop.load(stm);
				} finally {
					stm.close();
				}
			}
		} catch (IOException e) {
			// TODO log
		}
		
	    FacesContext facesCtx = FacesContext.getCurrentInstance();
	    Application facesApp = facesCtx.getApplication();
	    
	    for (WorkflowObjectStateDef sdef: wfStates) {
	    	
			UIPanelMenuItem mi = (UIPanelMenuItem) facesApp.createComponent(facesCtx, UIPanelMenuItem.COMPONENT_TYPE, "org.richfaces.PanelMenuItemRenderer");
			mi.setName(sdef.getName());
			mi.setId("pmi" + mi.getName());
			String slabel = prop.getProperty(sdef.getName());
			if (slabel == null) {
				mi.setLabel(sdef.getName());
			} else {
				mi.setLabel(slabel);
			}
			mi.setRendered(true);
			mi.setDisabled(false);
			pnmLeft.getChildren().add(mi);
		}
		
	}

	public String getCurrentStateName() {
		return currentStateName;
	}

	public void setCurrentStateName(String currentStateName) {
		if ( ! Utils.equalsNull(currentStateName, this.currentStateName) ) {
			this.currentStateName = currentStateName;
			afterEvent("state.changed");
		}
	}
	
	public void psChangedLsn(ItemChangeEvent event) {
		setCurrentStateName( event.getNewItemName() );
	}

	public UIPanelMenu getPnmLeft() {
		return pnmLeft;
	}

	public void setPnmLeft(UIPanelMenu pnmLeft) {
		this.pnmLeft = pnmLeft;
	}
}
