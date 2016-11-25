package ru.simplgroupp.webapp.manager.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.interfaces.WorkflowEngineBeanLocal;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.transfer.RefSystemFeature;
import ru.simplgroupp.transfer.Roles;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.workflow.WorkflowObjectStateDef;

public class MenuController extends AbstractSessionController  implements Serializable
{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6325966725995105185L;

	private static final Logger logger = LoggerFactory.getLogger(MenuController.class.getName());
	
	@EJB
	WorkflowEngineBeanLocal wfEngine;
	
	@EJB
	RulesBeanLocal rules;
	
	protected AppDataController appDataCtl;
	
	protected List<WorkflowObjectStateDef> taskInfos = new ArrayList<WorkflowObjectStateDef>(3);
	protected List<WorkflowObjectStateDef> taskInfosCR;
	protected List<WorkflowObjectStateDef> taskInfosC;
	protected List<WorkflowObjectStateDef> taskInfosP;
	protected List<WorkflowObjectStateDef> taskInfosCU;
	
	private List<String> roleNames;
	
	@PostConstruct
	public void init() {
		super.init();
		reloadTaskInfos();
//		reloadActionInfos();
	}
	
	public boolean isEnabled_CreditRequest() {
		
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.CR_CREATE, RefSystemFeature.CR_VIEW, RefSystemFeature.CR_EDIT, RefSystemFeature.CR_DELETE, RefSystemFeature.CR_DECISION, RefSystemFeature.CR_VERIFY}, false);
	
	}
	
	public boolean isEnabled_CreditRequest_New() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.CR_CREATE, RefSystemFeature.CR_VIEW, RefSystemFeature.CR_EDIT,  RefSystemFeature.CR_DELETE, RefSystemFeature.CR_DECISION, RefSystemFeature.CR_VERIFY}, false);
	}	
	
	public boolean isEnabled_Credit() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.C_VIEW, RefSystemFeature.C_EDIT, RefSystemFeature.C_DELETE}, false);
	}	
	
	public boolean isEnabled_People() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.PE_VIEW, RefSystemFeature.PE_EDIT, RefSystemFeature.PE_CREATE,RefSystemFeature.PE_DELETE}, false);
	}

	public boolean isEnabled_Collector() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.COLLECTOR_MAIN}, false);
	}

	public boolean isEnabled_CollectorSuperviser() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.COLLECTOR_SUPERVISER_MAIN}, false);
	}
	
	public boolean isEnabled_Payment() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.P_VIEW, RefSystemFeature.P_EDIT, RefSystemFeature.P_DELETE, RefSystemFeature.P_CREATE}, false);
	}
	
	public boolean isEnabled_Messages() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.MESSAGE_MAIN}, false);
	}	
	
	public boolean isEnabled_Admin() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.ADMIN_MAIN}, false);
	}	
	public boolean isEnabled_Admin_User() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.A_USER}, false);
	}	
	public boolean isEnabled_Admin_Role() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.A_ROLE}, false);
	}	
	public boolean isEnabled_Admin_Settings() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.A_SETTINGS}, false);
	}	
	
	public boolean isEnabled_Report() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.REPORT_MAIN}, false);
	}
	public boolean isEnabled_Report_Stat() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.REPORT_STAT}, false);
	}	
	public boolean isEnabled_Report_List() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.REPORT_LIST}, false);
	}
	public boolean isEnabled_Report_Collector() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.REPORT_COLLECTOR}, false);
	}	
	public boolean isEnabled_Report_Template() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.REPORT_TEMPLATE}, false);
	}
	public boolean isEnabled_Report_Simple() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.REPORT_STAT}, false);
	}	
	public boolean isEnabled_Callback() {
		return userCtl.isFeaturesEnabled(new int[] {RefSystemFeature.CALLBACK_MAIN}, false);
	
	}	
/*	
	public boolean calcRendered(String itemName) {
		int mapIndex = appDataCtl.findMenuMap(itemName);
		if (mapIndex < 0) {
			return false;
		}
		
		boolean bRole = false;
		Integer[] roleIds = appDataCtl.getMenuRoles(mapIndex);
		for (Roles role: userCtl.getUser().getRoles() ) {
			
			if ( Arrays.binarySearch(roleIds, role.getId()) >= 0 ) {
				bRole = true;
				return bRole;
			}
		}		

		return bRole;
	}
*/	
	
	private void reloadTaskInfos() {
		taskInfos.clear();
		
		roleNames = new ArrayList<String>(5);
		logger.info("user "+userCtl.getUser());
		if (userCtl.getUser()!=null){
		    for (Roles role: userCtl.getUser().getRoles() ) {
			    roleNames.add(role.getName());
		    }
		    taskInfos = wfEngine.listTaskDefs(null, roleNames, null, null);
	
  		    taskInfosCR = new ArrayList<WorkflowObjectStateDef>(taskInfos.size());
		    taskInfosC = new ArrayList<WorkflowObjectStateDef>(taskInfos.size());
		    taskInfosCU = new ArrayList<WorkflowObjectStateDef>(taskInfos.size());
		    taskInfosP = new ArrayList<WorkflowObjectStateDef>(taskInfos.size());
		    for (WorkflowObjectStateDef wfo: taskInfos) {
			    if (CreditRequest.class.getName().equals(wfo.getBusinessObjectClass())) {
				    taskInfosCR.add(wfo);
			    } else if (Credit.class.getName().equals(wfo.getBusinessObjectClass())) {
				    taskInfosC.add(wfo);
			    } else 	if (Payment.class.getName().equals(wfo.getBusinessObjectClass())) {
				    taskInfosP.add(wfo);
			    } else 	if (PeopleMain.class.getName().equals(wfo.getBusinessObjectClass())) {
				    taskInfosCU.add(wfo);
			    }
		    }	
		}
	}	
	
	public int getTaskInfoCountCR() {
		if (taskInfosCR == null) {
			return 0;
		} else {
			return taskInfosCR.size();
		}
	}
	
	public int getTaskInfoCountC() {
		if (taskInfosC == null) {
			return 0;
		} else {
			return taskInfosC.size();
		}
	}	
	
	public int getTaskInfoCountCU() {
		if (taskInfosCU == null) {
			return 0;
		} else {
			return taskInfosCU.size();
		}
	}
	
	public int getTaskInfoCountP() {
		if (taskInfosP == null) {
			return 0;
		} else {
			return taskInfosP.size();
		}
	}	
	
	public List<WorkflowObjectStateDef> getTaskInfosCR() {
		return taskInfosCR;
	}
	
	public List<WorkflowObjectStateDef> getTaskInfosC() {
		return taskInfosC;
	}	
	
	public List<WorkflowObjectStateDef> getTaskInfosCU() {
		return taskInfosCU;
	}
	
	public List<WorkflowObjectStateDef> getTaskInfosP() {
		return taskInfosP;
	}
	
	public String toHome() {
		return "toHome";
	}
	public String toHomeCR() {
		return "toHomeCR";
	}
	public String toRoles() {
		return "toRoles";
	}	
	public String toListCR() {
		return "toListCR";
	}	
	public String toNewCR() {
		return "toNewCR";
	}	
	public String toHomeC() {
		return "toHomeC";
	}
	public String toListC() {
		return "toListC";
	}	
	public String toHomeP() {
		return "toHomeP";
	}
	public String toListP() {
		return "toListP";
	}
	public String toHomeCU() {
		return "toHomeCU";
	}
	public String toListCU() {
		return "toListCU";
	}
	public String toReports() {
		return "toReports";
	}	
	public String toListU() {
		return "toListU";
	}
	public String toSettings() {
		return "toSettings";
	}
	public String toSets() {
		return "toSets";
	}
	public String toHelp() {
		return "toHelp";
	}

	public String toStat(){
		return "toStat";
	}
	public String toCollector(){
		return "toCollector";
	}
	
	public String toCallback(){
		return "toCallback";
	}

	public String collector() {
		return "collector";
	}

	public String collectorSuperviser() {
		return "collectorSuperviser";
	}
	
	public String toReportSets(){
		return "toReportSets";
	}
	
	public String toSimpleReport(){
		return "toSimpleReport";
	}
	
	public AppDataController getAppDataCtl() {
		return appDataCtl;
	}

	public void setAppDataCtl(AppDataController appDataCtl) {
		this.appDataCtl = appDataCtl;
	}	
}
