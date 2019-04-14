package com.example.filedemo.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the master_source database table.
 * 
 */
@Entity
@Table(name="master_source")
@NamedQuery(name="MasterSource.findAll", query="SELECT m FROM MasterSource m")
public class MasterSource implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="master_id", unique=true, nullable=false)
	private int masterId;

	@Temporal(TemporalType.DATE)
	@Column(name="e_date", nullable=false)
	private Date eDate;

	@Column(nullable=false, length=20)
	private String frequency;

	@Temporal(TemporalType.DATE)
	@Column(name="s_date", nullable=false)
	private Date sDate;

	@Column(nullable=false)
	private String time;

	@Column(nullable=false, length=50)
	private String title;

	@Column(nullable=false, length=10)
	private String type;

	@Column(nullable=false, length=100)
	private String url;
	
	@Column(nullable=false, length=50)
	private String country;
	
	private String execution;
	
	@Column(name="keywords")
	private String keywords;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_date")
	private Date updatedDate;

	public String getExecution() {
		return execution;
	}

	public void setExecution(String execution) {
		this.execution = execution;
	}

	//bi-directional many-to-one association to MonitoringSource
	@OneToMany(mappedBy="masterSource")
	private List<MonitoringSource> monitoringSources;

	//bi-directional many-to-one association to ScrapperLog
	@OneToMany(mappedBy="masterSource")
	private List<ScrapperLog> scrapperLogs;

	//bi-directional many-to-one association to SourceAction
	@OneToMany(mappedBy="masterSource",fetch = FetchType.EAGER)
	private List<SourceAction> sourceActions;

	//bi-directional many-to-one association to SourceNotification
	@OneToMany(mappedBy="masterSource")
	private List<SourceNotification> sourceNotifications;

	public MasterSource() {
	}

	public int getMasterId() {
		return this.masterId;
	}

	public void setMasterId(int masterId) {
		this.masterId = masterId;
	}

	public Date getEDate() {
		return this.eDate;
	}

	public void setEDate(Date eDate) {
		this.eDate = eDate;
	}

	public String getFrequency() {
		return this.frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Date getSDate() {
		return this.sDate;
	}

	public void setSDate(Date sDate) {
		this.sDate = sDate;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<MonitoringSource> getMonitoringSources() {
		return this.monitoringSources;
	}

	public void setMonitoringSources(List<MonitoringSource> monitoringSources) {
		this.monitoringSources = monitoringSources;
	}

	public MonitoringSource addMonitoringSource(MonitoringSource monitoringSource) {
		getMonitoringSources().add(monitoringSource);
		monitoringSource.setMasterSource(this);

		return monitoringSource;
	}

	public MonitoringSource removeMonitoringSource(MonitoringSource monitoringSource) {
		getMonitoringSources().remove(monitoringSource);
		monitoringSource.setMasterSource(null);

		return monitoringSource;
	}

	public List<ScrapperLog> getScrapperLogs() {
		return this.scrapperLogs;
	}

	public void setScrapperLogs(List<ScrapperLog> scrapperLogs) {
		this.scrapperLogs = scrapperLogs;
	}

	public ScrapperLog addScrapperLog(ScrapperLog scrapperLog) {
		getScrapperLogs().add(scrapperLog);
		scrapperLog.setMasterSource(this);

		return scrapperLog;
	}

	public ScrapperLog removeScrapperLog(ScrapperLog scrapperLog) {
		getScrapperLogs().remove(scrapperLog);
		scrapperLog.setMasterSource(null);

		return scrapperLog;
	}

	public List<SourceAction> getSourceActions() {
		return this.sourceActions;
	}

	public void setSourceActions(List<SourceAction> sourceActions) {
		this.sourceActions = sourceActions;
	}

	public SourceAction addSourceAction(SourceAction sourceAction) {
		getSourceActions().add(sourceAction);
		sourceAction.setMasterSource(this);

		return sourceAction;
	}

	public SourceAction removeSourceAction(SourceAction sourceAction) {
		getSourceActions().remove(sourceAction);
		sourceAction.setMasterSource(null);

		return sourceAction;
	}

	public List<SourceNotification> getSourceNotifications() {
		return this.sourceNotifications;
	}

	public void setSourceNotifications(List<SourceNotification> sourceNotifications) {
		this.sourceNotifications = sourceNotifications;
	}

	public SourceNotification addSourceNotification(SourceNotification sourceNotification) {
		getSourceNotifications().add(sourceNotification);
		sourceNotification.setMasterSource(this);

		return sourceNotification;
	}

	public SourceNotification removeSourceNotification(SourceNotification sourceNotification) {
		getSourceNotifications().remove(sourceNotification);
		sourceNotification.setMasterSource(null);

		return sourceNotification;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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