package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeopleDopDocsData implements Serializable {
	
	private String innDocFile;
	private String snilsDocFile;
	private boolean hasDriverDoc;
	private String driverDocFile;
	private String passportDocFile;
	private boolean hasAuto;

	public String getInnDocFile() {
		return innDocFile;
	}
	public void setInnDocFile(String innDocFile) {
		this.innDocFile = innDocFile;
	}
	public String getSnilsDocFile() {
		return snilsDocFile;
	}
	public void setSnilsDocFile(String snilsDocFile) {
		this.snilsDocFile = snilsDocFile;
	}
	public boolean isHasDriverDoc() {
		return hasDriverDoc;
	}
	public void setHasDriverDoc(boolean hasDriverDoc) {
		this.hasDriverDoc = hasDriverDoc;
	}
	public String getDriverDocFile() {
		return driverDocFile;
	}
	public void setDriverDocFile(String driverDocFile) {
		this.driverDocFile = driverDocFile;
	}
	public String getPassportDocFile() {
		return passportDocFile;
	}
	public void setPassportDocFile(String passportDocFile) {
		this.passportDocFile = passportDocFile;
	}
	public boolean isHasAuto() {
		return hasAuto;
	}
	public void setHasAuto(boolean hasAuto) {
		this.hasAuto = hasAuto;
	}
}
