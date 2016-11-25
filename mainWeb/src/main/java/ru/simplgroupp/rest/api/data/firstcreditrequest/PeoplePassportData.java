package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeoplePassportData implements Serializable {
	private String nomer;
	private String seria;
	private String dateGive;
	private String byGive;
	private String kodByGive;
	public String getNomer() {
		return nomer;
	}
	public void setNomer(String nomer) {
		this.nomer = nomer;
	}
	public String getSeria() {
		return seria;
	}
	public void setSeria(String seria) {
		this.seria = seria;
	}
	public String getDateGive() {
		return dateGive;
	}
	public void setDateGive(String dateGive) {
		this.dateGive = dateGive;
	}
	public String getByGive() {
		return byGive;
	}
	public void setByGive(String byGive) {
		this.byGive = byGive;
	}
	public String getKodByGive() {
		return kodByGive;
	}
	public void setKodByGive(String kodByGive) {
		this.kodByGive = kodByGive;
	}
}
