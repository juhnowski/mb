package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;
import java.util.Date;

public class PeopleBehaviorData implements Serializable {

	private String clientIp;

    private String clientIpAttr;
    
    private Integer clientTimezone;

    /*device info*/
    
    private int resW;
    private int resH;
    private String userAgent;
    private Date infoDate;
    
    /*Google analytics*/
    
    private String source_from;
    private String visit_count;
    private String ga_visitor_id;
    private String first_vizit_date;
    
    
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getClientIpAttr() {
		return clientIpAttr;
	}
	public void setClientIpAttr(String clientIpAttr) {
		this.clientIpAttr = clientIpAttr;
	}
	public Integer getClientTimezone() {
		return clientTimezone;
	}
	public void setClientTimezone(Integer clientTimezone) {
		this.clientTimezone = clientTimezone;
	}
	public int getResW() {
		return resW;
	}
	public void setResW(int resW) {
		this.resW = resW;
	}
	public int getResH() {
		return resH;
	}
	public void setResH(int resH) {
		this.resH = resH;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public Date getInfoDate() {
		return infoDate;
	}
	public void setInfoDate(Date infoDate) {
		this.infoDate = infoDate;
	}
	public String getSource_from() {
		return source_from;
	}
	public void setSource_from(String source_from) {
		this.source_from = source_from;
	}
	public String getVisit_count() {
		return visit_count;
	}
	public void setVisit_count(String visit_count) {
		this.visit_count = visit_count;
	}
	public String getGa_visitor_id() {
		return ga_visitor_id;
	}
	public void setGa_visitor_id(String ga_visitor_id) {
		this.ga_visitor_id = ga_visitor_id;
	}
	public String getFirst_vizit_date() {
		return first_vizit_date;
	}
	public void setFirst_vizit_date(String first_vizit_date) {
		this.first_vizit_date = first_vizit_date;
	}
}
