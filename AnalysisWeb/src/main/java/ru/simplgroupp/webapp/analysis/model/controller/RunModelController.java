package ru.simplgroupp.webapp.analysis.model.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.PluginConfig;
import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.exception.WorkflowException;
import ru.simplgroupp.interfaces.ActionProcessorBeanLocal;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.interfaces.VerificationBeanLocal;
import ru.simplgroupp.interfaces.WorkflowBeanLocal;
import ru.simplgroupp.interfaces.plugins.PluginSystemLocal.ExecutionMode;
import ru.simplgroupp.interfaces.plugins.PluginSystemLocal.SyncMode;
import ru.simplgroupp.toolkit.common.ExecutionProgress;
import ru.simplgroupp.toolkit.common.FileDataHolder;
import ru.simplgroupp.toolkit.common.ZipUtils;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.util.CreditRequestIteratorDB;
import ru.simplgroupp.util.CreditRequestIteratorXml;
import ru.simplgroupp.util.MimeTypeKeys;
import ru.simplgroupp.webapp.analysis.controller.RunEditPluginController;
import ru.simplgroupp.webapp.analysis.plugin.PluginUtils;
import ru.simplgroupp.webapp.util.JSFUtils;

public class RunModelController extends ViewModelController implements ExecutionProgress {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3113722156909013029L;
	
	protected String codeModified;
	protected Integer prmInSource = 1;
	protected String prmInIds;
	protected List<FileDataHolder> prmInFiles = new ArrayList<>(1);
	protected Integer prmInTarget = 2;
	protected String customizationKey;
	
	protected File resFile;
	List<String> lstXmls = new ArrayList<String>(10);
	protected int currentIdx;
	protected int cntModels;
	protected int cntExcluded;
	protected int cntSuccess;
	protected int cntErrors;
	protected Integer runCommand;
	protected ActionContext actionContext;
	protected Map<String, RunEditPluginController> editCtls = new HashMap<String, RunEditPluginController>(20);
	protected List<RunPluginConfiguration> configs = new ArrayList<RunPluginConfiguration>(2);
	protected String configNewName;
	protected RunPluginConfiguration currentConfig;
	protected EditParamsController editParamsCtl;
	
	@EJB
	WorkflowBeanLocal wfBean;
	
	@EJB
	KassaBeanLocal kassaBean;
	
	@EJB
	ActionProcessorBeanLocal actProc;
	
	@EJB
	RulesBeanLocal rulesBean;
	
	@EJB
	CreditRequestDAO crDAO;
	
	@PostConstruct
	public void init() {
		editParamsCtl = new EditParamsController();
		editParamsCtl.modelBean = modelBean;
		editParamsCtl.setUserCtl(getUserCtl());
		editParamsCtl.init();		
		super.init();
	}
	
	@PreDestroy
	public void close(InitialContext context) {
		if (editParamsCtl != null) {
			editParamsCtl.clearParams();
		}
	}
		
	public void uploadXmlListener(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getUploadedFile();

		if (MimeTypeKeys.XML.equalsIgnoreCase(item.getContentType())) {
			FileDataHolder info = new FileDataHolder();
			info.setName(item.getName());
			info.setMimeType(item.getContentType());
			info.setData(item.getData());
			prmInFiles.add(info);
		}
	}
	
	public void saveCode() {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		modelBean.saveModel(model);
		super.reloadModel(facesCtx, prmId);
	}
	
	public void deleteUploadedListener(ActionEvent event) {
		// TODO
	}
	
	public Integer getPrmInSource() {
		return prmInSource;
	}

	public void setPrmInSource(Integer prmInSource) {
		this.prmInSource = prmInSource;
	}

	public String getPrmInIds() {
		return prmInIds;
	}

	public void setPrmInIds(String prmInIds) {
		this.prmInIds = prmInIds;
	}

	public List<FileDataHolder> getPrmInFiles() {
		return prmInFiles;
	}

	public Integer getPrmInTarget() {
		return prmInTarget;
	}

	public void setPrmInTarget(Integer prmInTarget) {
		this.prmInTarget = prmInTarget;
	}

	public String getCodeModified() {
		return codeModified;
	}

	public void setCodeModified(String codeModified) {
		this.codeModified = codeModified;
	}
	
	private void initExecution() {
		resFile = null;
		lstXmls.clear();
		runCommand = null;
		
		cntExcluded = 0;
		cntSuccess = 0;
		cntErrors = 0;
	}
	
	/**
	 * Запустить модель на расчёт
	 * @return
	 */
	public String execute() {		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		initExecution();
		
		Iterator<CreditRequest> iter = null;
		try {
			if (prmInSource == 1) {
				CreditRequestIteratorDB iter1 = new CreditRequestIteratorDB(prmInIds, crDAO);
				cntModels = iter1.getCount();
				iter = iter1;
			} else if (prmInSource == 2) {
				CreditRequestIteratorXml iter1 = new CreditRequestIteratorXml(prmInFiles, kassaBean);
				cntModels = iter1.getCount();
				iter = iter1;
			}
		} catch (JAXBException ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
			return null;
		}
		
		currentIdx = -1;
		runCommand = 1;
		try {
			modelBean.executeModelsAsync(iter, prmInTarget, actionContext, lstXmls, this, model.getId());
		} catch (ActionException e) {
			JSFUtils.handleAsError(facesCtx, null, e);
			return null;
		}
		
		return null;
	}
	
	public String toStart() {
		return "success";
	}
	
	public void downloadResult() throws IOException {
		if (resFile == null) {
			return;
		}
		
		JSFUtils.downloadFile(resFile, MimeTypeKeys.ZIP);
	
	}
	
	@Override
	protected void reloadModel(FacesContext facesCtx, Integer prmId2) {
		customizationKey = UUID.randomUUID().toString();
		modelBean.checkDefaultModelParams(prmId2, customizationKey);
		
		super.reloadModel(facesCtx, prmId2);
		
		actionContext = actProc.createActionContext(customizationKey, false);
		currentConfig = new RunPluginConfiguration();
		currentConfig.setDescription("текущая конфигурация");
		currentConfig.key = customizationKey;
		currentConfig.bChanged = false;
		
    	PluginConfig plc1 = actionContext.getPlugins().getPluginConfig(VerificationBeanLocal.SYSTEM_NAME);
    	plc1.setExecutionMode(ExecutionMode.AUTOMATIC);
    	plc1.setUseFake(true);
    	plc1.setSyncMode(SyncMode.SYNC);				
		
		String starget = model.getTarget();
		editCtls.clear();
		for (PluginConfig plc: actionContext.getPlugins().getPluginConfigs()) {
			Set<String> mods = plc.getPlugin().getModelTargetsSupported();
			if ( mods.contains(starget) ) {
				RunEditPluginController ctl = new RunEditPluginController();
				ctl.init(plc);
				PluginUtils.attachExtCtl(plc.getPluginName(), ctl);
				ctl.reloadPlugin(facesCtx, plc.getPluginName());
				
				editCtls.put(plc.getPluginName(), ctl);
			}
		}
		
		reloadPluginConfigs();	
		
		editParamsCtl.model = model;
		editParamsCtl.customizationKey = customizationKey;
		editParamsCtl.reloadModel(true);
	}
	
	private void reloadPluginConfigs() {
		configs.clear();
		Map<String, String> mpc = rulesBean.getPluginConfigurations();
		for (Map.Entry<String, String> entry: mpc.entrySet()) {
			if (StringUtils.isBlank(entry.getKey())) {
				continue;
			}
			RunPluginConfiguration cfg = new RunPluginConfiguration();
			cfg.key = entry.getKey();
			cfg.bChanged = false;
			cfg.description = entry.getValue();
			configs.add(cfg);
		}		
	}
	
	public void loadConfigLsn(ActionEvent event) {
		String ckey = (String) event.getComponent().getAttributes().get("configKey");
		if (StringUtils.isBlank(ckey)) {
			return;
		}

		customizationKey = ckey;
		actionContext = actProc.createActionContext(customizationKey, true);
		for (RunPluginConfiguration cfg: configs) {
			if (cfg.getKey().equals(ckey)) {
				currentConfig = cfg;
				break;
			}
		}
		
	}
	
	public void removeConfigLsn(ActionEvent event) {
		String ckey = (String) event.getComponent().getAttributes().get("configKey");
		if (StringUtils.isBlank(ckey)) {
			return;
		}
		
		if (rulesBean.deletePluginOptions(currentConfig.getKey())) {
			reloadPluginConfigs();
		}
	}	
	
	public void saveConfigLsn(ActionEvent event) {
		HashMap<String, Object> mpVals = new HashMap<String, Object>(50);
		for (PluginConfig plc: actionContext.getPlugins().getPluginConfigs()) {
			plc.save(plc.getPluginName(), mpVals);
		}
		rulesBean.setPluginsOptions(currentConfig.getKey(), currentConfig.getDescription(), mpVals);
		reloadPluginConfigs();
	}

	public int getCurrentIdx() {
		return currentIdx;
	}

	@Override
	public void progress(Object indicator) {
		Map<String, Object> mp = (Map<String, Object>) indicator;
		currentIdx = ((Number) mp.get("currentIdx")).intValue();
	}

	@Override
	public void finished() {
		runCommand = null;
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		try {
			if (lstXmls.size() > 0) {
				resFile = File.createTempFile("debug-results", ".zip");
				
				ZipUtils.zipTxtFiles(lstXmls, "debug-results", ".xml", resFile);		
			}
		} catch (IOException ex) {
			JSFUtils.handleAsError(facesCtx, null, ex);
		}
	}
	
	public String cancelExecution() {
		// прервать выполнение
		runCommand = new Integer(2);
		return null;
	}

	public int getCntModels() {
		return cntModels;
	}

	public String getResFileName() {
		if (resFile == null) {
			return null;
		} else {
			return resFile.getPath();
		}
	}

	public boolean getExecutionInProgress() {
		return (runCommand != null);
	}
	
	@Override
	public boolean finishRequest() {
		return (runCommand != null && runCommand.intValue() == 2);
	}

	public int getCntExcluded() {
		return cntExcluded;
	}

	public int getCntSuccess() {
		return cntSuccess;
	}

	public int getCntErrors() {
		return cntErrors;
	}
	
	public List<PluginConfig> getPluginConfigs() {
		if (actionContext == null) {
			return null;
		} else {
			ArrayList<PluginConfig> lst = new ArrayList<PluginConfig>( editCtls.size() );
			for (RunEditPluginController ctl: editCtls.values()) {
				lst.add(ctl.getPlugin());
			}
			return lst;
		}
	}

	public Map<String, RunEditPluginController> getEditCtls() {
		return editCtls;
	}
	
	public List<RunPluginConfiguration> getConfigs() {
		return configs;
	}

	public String getConfigNewName() {
		return configNewName;
	}

	public void setConfigNewName(String configNewName) {
		this.configNewName = configNewName;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}
	
	public class RunPluginConfiguration {
		String key;
		String description;
		boolean bChanged;
		
		public String getKey() {
			return key;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String value) {
			description = value;
		}
		public boolean isChanged() {
			return bChanged;
		}
	}

	public void setCurrentIdx(int currentIdx) {
		this.currentIdx = currentIdx;
	}
	
	public void progressScan() {
		
	}

	public RunPluginConfiguration getCurrentConfig() {
		return currentConfig;
	}
	
	public EditParamsController getEditParamsCtl() {
		return editParamsCtl;
	}

}
