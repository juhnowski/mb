package ru.simplgroupp.webapp.analysis.plugin.controller;

import ru.simplgroupp.ejb.plugins.payment.ContactPayPluginConfig;

public class EditPluginContactPayController extends EditPluginExtController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1195830639676627276L;
	protected boolean useWork;
    private String PPcode;
    private String testUrl;
    private String workUrl;
    private String keyStoreAlias;
    private String keyStorePassword;
    private String keyStoreName;
    private String debetAccount;

    private String senderFakeDocName;
    private String senderFakeName;
    private String senderFakeLastName;
    private String senderFakeSurName;
    private String senderFakeZipCode;
    private String senderFakeRegion;
    private String senderFakeCity;
    private String senderFakeAddress;
    private String senderFakeDocSerNum;
    private String senderFakeDocdate;
    private String senderFakeDocWhom;
    private String senderFakeDocExpDate;
    private String senderFakeBirthday;	
    private long intervalWaitClient;

	@Override
	void reload() {
		ContactPayPluginConfig rcfg = (ContactPayPluginConfig) editCtl.plugin;
		useWork = rcfg.isUseWork();
		PPcode = rcfg.getPPcode();
		testUrl = rcfg.getTestUrl();
		workUrl = rcfg.getWorkUrl();
		keyStoreAlias = rcfg.getKeyStoreAlias();
		keyStorePassword = rcfg.getKeyStorePassword();
		keyStoreName = rcfg.getKeyStoreName();
		senderFakeDocName = rcfg.getSenderFakeDocName();
		senderFakeName = rcfg.getSenderFakeName();
		senderFakeLastName = rcfg.getSenderFakeLastName();
		senderFakeSurName = rcfg.getSenderFakeSurName();
		senderFakeZipCode = rcfg.getSenderFakeZipCode();
		senderFakeRegion = rcfg.getSenderFakeRegion();
		senderFakeCity = rcfg.getSenderFakeCity();
		senderFakeAddress = rcfg.getSenderFakeAddress();
		senderFakeDocSerNum = rcfg.getSenderFakeDocSerNum();
		senderFakeDocdate = rcfg.getSenderFakeDocdate();
		senderFakeDocWhom = rcfg.getSenderFakeDocWhom();
		senderFakeDocExpDate = rcfg.getSenderFakeDocExpDate();
		senderFakeBirthday = rcfg.getSenderFakeBirthday();
		intervalWaitClient = rcfg.getIntervalWaitClient();
		debetAccount=rcfg.getDebetAccount();
		// TODO Auto-generated method stub

	}

	@Override
	void save() {
		ContactPayPluginConfig rcfg = (ContactPayPluginConfig) editCtl.plugin;
		rcfg.setUseWork(useWork);
		rcfg.setPPcode(PPcode);
		rcfg.setTestUrl(testUrl);
		rcfg.setWorkUrl(workUrl);
		rcfg.setKeyStoreAlias(keyStoreAlias);
		rcfg.setKeyStorePassword(keyStorePassword);
		rcfg.setKeyStoreName(keyStoreName);
		rcfg.setSenderFakeDocName(senderFakeDocName);
		rcfg.setSenderFakeName(senderFakeName);
		rcfg.setSenderFakeLastName(senderFakeLastName);
		rcfg.setSenderFakeSurName(senderFakeSurName);
		rcfg.setSenderFakeZipCode(senderFakeZipCode);
		rcfg.setSenderFakeRegion(senderFakeRegion);
		rcfg.setSenderFakeCity(senderFakeCity);
		rcfg.setSenderFakeAddress(senderFakeAddress);
		rcfg.setSenderFakeDocSerNum(senderFakeDocSerNum);
		rcfg.setSenderFakeDocdate(senderFakeDocdate);
		rcfg.setSenderFakeDocWhom(senderFakeDocWhom);
		rcfg.setSenderFakeDocExpDate(senderFakeDocExpDate);
		rcfg.setSenderFakeBirthday(senderFakeBirthday);
		rcfg.setIntervalWaitClient(intervalWaitClient);
		rcfg.setDebetAccount(debetAccount);
		// TODO Auto-generated method stub

	}

	public boolean isUseWork() {
		return useWork;
	}

	public void setUseWork(boolean useWork) {
		this.useWork = useWork;
	}

	public String getPPcode() {
		return PPcode;
	}

	public void setPPcode(String pPcode) {
		PPcode = pPcode;
	}

	public String getTestUrl() {
		return testUrl;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	public String getWorkUrl() {
		return workUrl;
	}

	public void setWorkUrl(String workUrl) {
		this.workUrl = workUrl;
	}

	public String getKeyStoreAlias() {
		return keyStoreAlias;
	}

	public void setKeyStoreAlias(String keyStoreAlias) {
		this.keyStoreAlias = keyStoreAlias;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public String getKeyStoreName() {
		return keyStoreName;
	}

	public void setKeyStoreName(String keyStoreName) {
		this.keyStoreName = keyStoreName;
	}

	public String getSenderFakeDocName() {
		return senderFakeDocName;
	}

	public void setSenderFakeDocName(String senderFakeDocName) {
		this.senderFakeDocName = senderFakeDocName;
	}

	public String getSenderFakeName() {
		return senderFakeName;
	}

	public void setSenderFakeName(String senderFakeName) {
		this.senderFakeName = senderFakeName;
	}

	public String getSenderFakeLastName() {
		return senderFakeLastName;
	}

	public void setSenderFakeLastName(String senderFakeLastName) {
		this.senderFakeLastName = senderFakeLastName;
	}

	public String getSenderFakeSurName() {
		return senderFakeSurName;
	}

	public void setSenderFakeSurName(String senderFakeSurName) {
		this.senderFakeSurName = senderFakeSurName;
	}

	public String getSenderFakeZipCode() {
		return senderFakeZipCode;
	}

	public void setSenderFakeZipCode(String senderFakeZipCode) {
		this.senderFakeZipCode = senderFakeZipCode;
	}

	public String getSenderFakeRegion() {
		return senderFakeRegion;
	}

	public void setSenderFakeRegion(String senderFakeRegion) {
		this.senderFakeRegion = senderFakeRegion;
	}

	public String getSenderFakeCity() {
		return senderFakeCity;
	}

	public void setSenderFakeCity(String senderFakeCity) {
		this.senderFakeCity = senderFakeCity;
	}

	public String getSenderFakeAddress() {
		return senderFakeAddress;
	}

	public void setSenderFakeAddress(String senderFakeAddress) {
		this.senderFakeAddress = senderFakeAddress;
	}

	public String getSenderFakeDocSerNum() {
		return senderFakeDocSerNum;
	}

	public void setSenderFakeDocSerNum(String senderFakeDocSerNum) {
		this.senderFakeDocSerNum = senderFakeDocSerNum;
	}

	public String getSenderFakeDocdate() {
		return senderFakeDocdate;
	}

	public void setSenderFakeDocdate(String senderFakeDocdate) {
		this.senderFakeDocdate = senderFakeDocdate;
	}

	public String getSenderFakeDocWhom() {
		return senderFakeDocWhom;
	}

	public void setSenderFakeDocWhom(String senderFakeDocWhom) {
		this.senderFakeDocWhom = senderFakeDocWhom;
	}

	public String getSenderFakeDocExpDate() {
		return senderFakeDocExpDate;
	}

	public void setSenderFakeDocExpDate(String senderFakeDocExpDate) {
		this.senderFakeDocExpDate = senderFakeDocExpDate;
	}

	public String getSenderFakeBirthday() {
		return senderFakeBirthday;
	}

	public void setSenderFakeBirthday(String senderFakeBirthday) {
		this.senderFakeBirthday = senderFakeBirthday;
	}

	public long getIntervalWaitClient() {
		return intervalWaitClient;
	}

	public void setIntervalWaitClient(long intervalWaitClient) {
		this.intervalWaitClient = intervalWaitClient;
	}

	public String getDebetAccount() {
		return debetAccount;
	}

	public void setDebetAccount(String debetAccount) {
		this.debetAccount = debetAccount;
	}

	
}
