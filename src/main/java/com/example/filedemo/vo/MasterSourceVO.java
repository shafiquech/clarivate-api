package com.example.filedemo.vo;

import java.util.Date;
import java.util.List;

public class MasterSourceVO {

	private int masterId;
	private Date eDate;
	private String frequency;
	private Date sDate;
	private Date updatedDate;
	private String time;
	private String title;
	private String type;
	private String url;
	private String country;
	private String execution;
	private String keywords;
	private List<String> notificationEmails;
	private List<MonitoringSourceVO> monitoringSources;
	private List<SourceActionVO> sourceActions;
	private List<ScrapperLogVO> scrapperLogs;
	
	public List<SourceActionVO> getSourceActions() {
		return sourceActions;
	}

	public void setSourceActions(List<SourceActionVO> sourceActions) {
		this.sourceActions = sourceActions;
	}

	public List<MonitoringSourceVO> getMonitoringSources() {
		return monitoringSources;
	}

	public void setMonitoringSources(List<MonitoringSourceVO> monitoringSources) {
		this.monitoringSources = monitoringSources;
	}

	public List<String> getNotificationEmails() {
		return notificationEmails;
	}

	public void setNotificationEmails(List<String> notificationEmails) {
		this.notificationEmails = notificationEmails;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date geteDate() {
		return eDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Date getsDate() {
		return sDate;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getMasterId() {
		return masterId;
	}

	public void setMasterId(int masterId) {
		this.masterId = masterId;
	}

	public String getExecution() {
		return execution;
	}

	public void setExecution(String execution) {
		this.execution = execution;
	}

	public List<ScrapperLogVO> getScrapperLogs() {
		return scrapperLogs;
	}

	public void setScrapperLogs(List<ScrapperLogVO> scrapperLogs) {
		this.scrapperLogs = scrapperLogs;
	}
	
	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	

}
