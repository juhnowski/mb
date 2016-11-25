package ru.simplgroupp.webapp.analysis.crypto.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.admnkz.crypto.app.ICryptoService;
import org.admnkz.crypto.data.Certificate;
import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import ru.simplgroupp.toolkit.common.DateRange;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.exception.WebAppException;
import ru.simplgroupp.webapp.util.JSFUtils;

public class ListCertificatesController extends AbstractListController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7395485841068989413L;

	@EJB
	protected ICryptoService crypto;

	/**
	 * вид сертификата
	 */
	protected Integer prmSubjectTypeId;
	/**
	 * на кого выпущен сертификат
	 */
	protected String prmSubjectCN;
	/**
	 * провайдер
	 */
	protected String prmProviderName;
	/**
	 * провайдер
	 */
	protected String providerName;
	/**
	 * название в системе
	 */
	protected String prmSettingsName;
	/**
	 * кто подписывает
	 */
	protected String prmSignerId;
	/**
	 * статус сертификата
	 */
	protected Integer prmStatusId;
	/**
	 * сертификат
	 */
	protected byte[] cert;
	/**
	 * закрытый ключ
	 */
	protected byte[] certKey;
	/**
	 * secure random
	 */
	protected byte[] certRand;
	/**
	 * название сертификата в системе
	 */
	protected String settingsName;
	/**
	 * дата начала
	 */
	protected DateRange prmDateStart=new DateRange(null, null);
	/**
	 * дата окончания
	 */
	protected DateRange prmDateFinish=new DateRange(null, null);
		
	@PostConstruct
	public void init() {
		super.init();
	}	
	
	@Override
	public void initData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StdRequestDataModel<Certificate> createModel() {
		return new CertificatesDataModel();
	}
	
    protected void nullIfEmpty() {
		
		if (StringUtils.isBlank(prmProviderName)) {
			prmProviderName = null;
		}
		if (StringUtils.isBlank(prmSettingsName)) {
			prmSettingsName = null;
		}
		if (StringUtils.isBlank(prmSignerId)) {
			prmSignerId = null;
		}
		if (StringUtils.isBlank(prmSubjectCN)) {
			prmSubjectCN = null;
		}
		
		prmStatusId=null;
	}	
	
	public void clearLsn(ActionEvent event) {
		prmSignerId=null;
		prmSubjectTypeId=null;
		prmStatusId=null;
		prmSettingsName=null;
		prmSubjectCN=null;
		prmProviderName= null;
		prmDateStart=new DateRange(null, null);
		prmDateFinish=new DateRange(null, null);
		refreshSearch();
	}	

	
	public class CertificatesDataModel extends StdRequestDataModel<Certificate>{

		public CertificatesDataModel(){
			super();
		}
		
		@Override
		protected List<Certificate> internalList(SequenceRange seqRange,
				SortCriteria[] sorting) throws WebAppException {
			nullIfEmpty();
			List<Certificate> lst=crypto.listCertificates(seqRange.getFirstRow(), seqRange.getRows(), prmSubjectCN, prmSubjectTypeId, prmStatusId, prmProviderName, prmSettingsName, prmDateStart,prmDateFinish,prmSignerId, null, Utils.setOf(Certificate.Options.INIT_SETTINGS));
			return lst;
		}

		@Override
		protected int internalRowCount() throws WebAppException {
			nullIfEmpty();
			return crypto.countCertificates(prmSubjectCN, prmSubjectTypeId, prmStatusId, prmProviderName, prmSettingsName, prmDateStart,prmDateFinish,prmSignerId);
		}

		@Override
		public Certificate getRowData() {
			if (rowKey == null) {
				return null;
			} else {
			  return crypto.getCertificate(rowKey.toString(), Utils.setOf(Certificate.Options.INIT_SETTINGS));
			}
		}
		
	}


	public Integer getPrmSubjectTypeId() {
		return prmSubjectTypeId;
	}

	public void setPrmSubjectTypeId(Integer prmSubjectTypeId) {
		this.prmSubjectTypeId = prmSubjectTypeId;
	}

	public String getPrmSubjectCN() {
		return prmSubjectCN;
	}

	public void setPrmSubjectCN(String prmSubjectCN) {
		this.prmSubjectCN = prmSubjectCN;
	}

	public String getPrmProviderName() {
		return prmProviderName;
	}

	public void setPrmProviderName(String prmProviderName) {
		this.prmProviderName = prmProviderName;
	}

	public String getPrmSettingsName() {
		return prmSettingsName;
	}

	public void setPrmSettingsName(String prmSettingsName) {
		this.prmSettingsName = prmSettingsName;
	}

	public String getPrmSignerId() {
		return prmSignerId;
	}

	public void setPrmSignerId(String prmSignerId) {
		this.prmSignerId = prmSignerId;
	}

	public Integer getPrmStatusId() {
		return prmStatusId;
	}

	public void setPrmStatusId(Integer prmStatusId) {
		this.prmStatusId = prmStatusId;
	}
	
	public DateRange getPrmDateStart() {
		return prmDateStart;
	}

	public void setPrmDateStart(DateRange prmDateStart) {
		this.prmDateStart = prmDateStart;
	}

	public DateRange getPrmDateFinish() {
		return prmDateFinish;
	}

	public void setPrmDateFinish(DateRange prmDateFinish) {
		this.prmDateFinish = prmDateFinish;
	}

	public String addCertificate(){
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
			
		if (cert!=null){
			try {
				Certificate certificate=null;
				//если нет ключа и рандома
				if (certKey==null&&certRand==null){
				    certificate=crypto.addCertificate(null, providerName, cert);
				} else if (certKey!=null&&certRand!=null){
					certificate=crypto.addCertificate(null, providerName, cert, certKey, certRand, null);
				} else if (certKey!=null&&certRand==null){
					certificate=crypto.addCertificate(null, providerName, cert, certKey, null, null);
				} else {
					JSFUtils.handleAsError(facesCtx, null, new Exception("Необходимо либо добавить закрытый ключ, либо убрать secure random"));
				}
			    if (certificate!=null){
			    	crypto.addSettings(certificate,settingsName);
			    	String id=certificate.getId();
			    	return "edit?faces-redirect=true&reqid="+id;
			    }
			} catch (Exception e) {
				JSFUtils.handleAsError(facesCtx, null, e);
			}
		}
		return null;
	}
	
	public byte[] getCert() {
		return cert;
	}

	public void setCert(byte[] cert) {
		this.cert = cert;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public void uploadListener(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getUploadedFile();
		if (item.getName().toUpperCase().endsWith("CER")||item.getName().toUpperCase().endsWith("CRT")||item.getName().toUpperCase().endsWith("CERT")){
		  cert=item.getData();
		}
		if (item.getName().toUpperCase().endsWith("KEY")){
		  certKey=item.getData();
		}
		if (item.getName().toUpperCase().endsWith("ZIP")){
		  certRand=item.getData();
		}
	}

	public void changeProvider(ValueChangeEvent event){
		providerName=(String) event.getNewValue();
	}

	public String getSettingsName() {
		return settingsName;
	}

	public void setSettingsName(String settingsName) {
		this.settingsName = settingsName;
	}

	public byte[] getCertKey() {
		return certKey;
	}

	public void setCertKey(byte[] certKey) {
		this.certKey = certKey;
	}

	public byte[] getCertRand() {
		return certRand;
	}

	public void setCertRand(byte[] certRand) {
		this.certRand = certRand;
	}

	public void deleteItem(ActionEvent event) {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		String certID = (String) event.getComponent().getAttributes().get("reqid");
		
		if (certID!=null){
		  try {
			  crypto.removeCertificate(certID, true);
			  refreshSearch();
		  } catch (Exception ex) {
			  JSFUtils.handleAsError(facesCtx, null, ex);
		  }
		}
	}		
	
}
