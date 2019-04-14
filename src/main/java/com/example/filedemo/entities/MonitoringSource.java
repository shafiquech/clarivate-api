package com.example.filedemo.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the monitoring_source database table.
 * 
 */
@Entity
@Table(name="monitoring_source")
@NamedQuery(name="MonitoringSource.findAll", query="SELECT m FROM MonitoringSource m")
public class MonitoringSource implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="log_id", unique=true, nullable=false)
	private int logId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date datetime;

	@Column(nullable=false, length=200)
	private String error;

	@Column(nullable=false, length=5)
	private String status;

	//bi-directional many-to-one association to MasterSource
	@ManyToOne
	@JoinColumn(name="master_id", nullable=false)
	private MasterSource masterSource;

	public MonitoringSource() {
	}

	public int getLogId() {
		return this.logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public Date getDatetime() {
		return this.datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MasterSource getMasterSource() {
		return this.masterSource;
	}

	public void setMasterSource(MasterSource masterSource) {
		this.masterSource = masterSource;
	}

}