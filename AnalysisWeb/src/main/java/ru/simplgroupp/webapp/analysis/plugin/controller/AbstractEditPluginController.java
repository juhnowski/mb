package ru.simplgroupp.webapp.analysis.plugin.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.ejb.PluginConfig;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.plugins.PluginSystemLocal;
import ru.simplgroupp.transfer.Partner;
import ru.simplgroupp.util.WorkflowUtil;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

abstract public class AbstractEditPluginController extends AbstractSessionController implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3832235177406534565L;
	protected PluginConfig plugin;
	protected EditPluginExtController extCtl;
	
	protected String modeId;
	protected Long packetInterval;
	protected String packetSchedule;
	protected Long asyncInterval;
	protected String asyncSchedule;
	protected Long retryInterval;
	protected String retrySchedule;
	protected Integer fakeId;
	protected String executionModeId;
	protected String syncModeId;
	protected Integer workEJBType;
	protected String workEJBName;
	protected Integer fakeEJBType;
	protected String fakeEJBName;
	protected Integer processType;
	protected String processName;
	protected String processPacketName;
	protected IEditPluginChanged subscriber;
	protected Partner partner;
	
	
	public void init() {
	}
	
	abstract public ReferenceBooksLocal getRefBooks();
	
	public String getExtName() {
		return plugin.getClass().getSimpleName();
	}

	public void reloadPlugin(FacesContext facesCtx, String prmPluginName2) {
		partner = getRefBooks().findPartnerByName(plugin.getPartnerName());
		
		if (plugin.getIsActive()) {
			executionModeId = plugin.getExecutionMode().toString();
		} else {
			executionModeId = null;
		}
		modeId = plugin.getMode().toString();
		syncModeId = plugin.getSyncMode().toString();
		packetInterval = plugin.getPacketInterval();
		packetSchedule = plugin.getPacketSchedule();
		asyncInterval = plugin.getAsyncInterval();
		asyncSchedule = plugin.getAsyncSchedule();
		retryInterval = plugin.getRetryInterval();
		retrySchedule = plugin.getRetrySchedule();
		if (plugin.isUseFake()) {
			fakeId = 1;
		} else {
			fakeId = 0;
		}
		
		workEJBName = plugin.getWorkEJBName();
		if (StringUtils.isBlank(workEJBName)) {
			workEJBType = 1;
		} else {
			workEJBType = 2;
		}
		fakeEJBName = plugin.getFakeEJBName();
		if (StringUtils.isBlank(fakeEJBName)) {
			fakeEJBType = 1;
		} else {
			fakeEJBType = 2;
		}
		
		processName = plugin.getProcessName();
		processPacketName = plugin.getProcessPacketName();
		
		if (extCtl != null) {
			extCtl.reload();
		}
	}

	public PluginConfig getPlugin() {
		return plugin;
	}

	public List<SelectItem> getModes() {
		ArrayList<SelectItem> lst = new ArrayList<SelectItem>(3); 
		lst.add(new SelectItem(PluginSystemLocal.Mode.SINGLE.toString(), "одиночный"));
		lst.add(new SelectItem(PluginSystemLocal.Mode.PACKET.toString(), "пакетный"));
		return lst;
	}
	
	public List<SelectItem> getSyncModes() {
		ArrayList<SelectItem> lst = new ArrayList<SelectItem>(3); 
		lst.add(new SelectItem(PluginSystemLocal.SyncMode.SYNC.toString(), "синхронный"));
		lst.add(new SelectItem(PluginSystemLocal.SyncMode.ASYNC.toString(), "асинхронный"));
		return lst;
	}	
	
	public List<SelectItem> getExecutionModes() {
		ArrayList<SelectItem> lst = new ArrayList<SelectItem>(3); 
		lst.add(new SelectItem(null, "отключен"));
		lst.add(new SelectItem(PluginSystemLocal.ExecutionMode.AUTOMATIC.toString(), "автоматический"));
		lst.add(new SelectItem(PluginSystemLocal.ExecutionMode.MANUAL.toString(), "ручной"));
		return lst;
	}	
	
	public List<SelectItem> getFakeModes() {
		ArrayList<SelectItem> lst = new ArrayList<SelectItem>(3); 
		lst.add(new SelectItem(0, "рабочий"));
		lst.add(new SelectItem(1, "заглушка"));
		return lst;
	}	
	
	private boolean validateInterval(FacesContext facesCtx, Long interval, String schedule, long minInterval, String idInterval, String idSchedule) {
		boolean bOk = true;
		if (StringUtils.isBlank(schedule)) {
			if (interval == null || interval.longValue() == 0) {
				JSFUtils.handleAsError(facesCtx, idInterval, new Exception("Обязательно требуется или интервал, или расписание"));
				bOk = false;
			} else if (interval.longValue() < minInterval) {
				JSFUtils.handleAsError(facesCtx, idInterval, new Exception("Интервал должен быть не менее " + minInterval + " мс"));
				bOk = false;				
			}
			
		} else {
			try {
				WorkflowUtil.isISOSchedule(schedule);
			} catch (WorkflowException e) {
				JSFUtils.handleAsError(facesCtx, idSchedule, e);
				bOk = false;
			}
		}
		return bOk;		
	}
	
	public boolean validate(FacesContext facesCtx) {
		boolean bOk = true;
		bOk = bOk && validateInterval(facesCtx, packetInterval, packetSchedule, 60*1000, "txtPacketInterval", "txtPacketSchedule");
		bOk = bOk && validateInterval(facesCtx, retryInterval, retrySchedule, 60*1000, "txtRetryInterval", "txtRetrySchedule");
		bOk = bOk && validateInterval(facesCtx, asyncInterval, asyncSchedule, 60*1000, "txtAsyncInterval", "txtAsyncSchedule");
		return bOk;
	}
	
	public String save() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		if (! validate(facesCtx)) {
			return null;
		}
		
		if (StringUtils.isBlank(executionModeId)) {
			plugin.setExecutionMode(null);
		} else {
			plugin.setExecutionMode(PluginSystemLocal.ExecutionMode.valueOf(executionModeId));
		}
		plugin.setMode(PluginSystemLocal.Mode.valueOf(modeId));
		plugin.setSyncMode(PluginSystemLocal.SyncMode.valueOf(syncModeId));
		plugin.setPacketInterval(packetInterval);
		plugin.setPacketSchedule(packetSchedule);
		plugin.setAsyncInterval(asyncInterval);
		plugin.setAsyncSchedule(asyncSchedule);
		plugin.setRetryInterval(retryInterval);
		plugin.setRetrySchedule(retrySchedule);
		plugin.setUseFake(fakeId == 1);
		plugin.setWorkEJBName(workEJBName);
		plugin.setFakeEJBName(fakeEJBName);
		plugin.setProcessName(processName);
		plugin.setProcessPacketName(processPacketName);
		
		if (extCtl != null) {
			extCtl.save();
		}
		
		if (partner != null) {
			getRefBooks().savePartner(partner);
		}
		
		if ( subscriber != null) {
			subscriber.changed(plugin.getPluginName());
		}
		return "success";
	}
	
	public String cancel() {
		return "canceled";
	}

	public String getModeId() {
		return modeId;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}

	public Long getPacketInterval() {
		return packetInterval;
	}

	public void setPacketInterval(Long packetInterval) {
		this.packetInterval = packetInterval;
	}

	public Integer getFakeId() {
		return fakeId;
	}

	public void setFakeId(Integer fakeId) {
		this.fakeId = fakeId;
	}

	public String getWorkEJBName() {
		return workEJBName;
	}

	public void setWorkEJBName(String workEJBName) {
		this.workEJBName = workEJBName;
	}

	public String getFakeEJBName() {
		return fakeEJBName;
	}

	public void setFakeEJBName(String fakeEJBName) {
		this.fakeEJBName = fakeEJBName;
	}

	public Integer getWorkEJBType() {
		return workEJBType;
	}

	public void setWorkEJBType(Integer workEJBType) {
		this.workEJBType = workEJBType;
	}

	public Integer getFakeEJBType() {
		return fakeEJBType;
	}

	public void setFakeEJBType(Integer fakeEJBType) {
		this.fakeEJBType = fakeEJBType;
	}
	
	public void ejbTypeChangedLsn(AjaxBehaviorEvent event) {
		HtmlSelectOneMenu sel = (HtmlSelectOneMenu) event.getComponent();
		workEJBType = (Integer) sel.getValue();
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getExecutionModeId() {
		return executionModeId;
	}

	public void setExecutionModeId(String executionModeId) {
		this.executionModeId = executionModeId;
	}

	public String getSyncModeId() {
		return syncModeId;
	}

	public void setSyncModeId(String syncModeId) {
		this.syncModeId = syncModeId;
	}

	public Integer getProcessType() {
		return processType;
	}

	public void setProcessType(Integer processType) {
		this.processType = processType;
	}

	public Long getAsyncInterval() {
		return asyncInterval;
	}

	public void setAsyncInterval(Long asyncInterval) {
		this.asyncInterval = asyncInterval;
	}

	public String getProcessPacketName() {
		return processPacketName;
	}

	public void setProcessPacketName(String processPacketName) {
		this.processPacketName = processPacketName;
	}

	public Long getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(Long retryInterval) {
		this.retryInterval = retryInterval;
	}

	public EditPluginExtController getExtCtl() {
		return extCtl;
	}

	public void setExtCtl(EditPluginExtController extCtl) {
		this.extCtl = extCtl;
	}

	public String getPacketSchedule() {
		return packetSchedule;
	}

	public void setPacketSchedule(String packetSchedule) {
		this.packetSchedule = packetSchedule;
	}

	public String getAsyncSchedule() {
		return asyncSchedule;
	}

	public void setAsyncSchedule(String asyncSchedule) {
		this.asyncSchedule = asyncSchedule;
	}

	public String getRetrySchedule() {
		return retrySchedule;
	}

	public void setRetrySchedule(String retrySchedule) {
		this.retrySchedule = retrySchedule;
	}
}
