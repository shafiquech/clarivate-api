package com.example.filedemo.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the source_notification database table.
 * 
 */
@Entity
@Table(name="source_notification")
@NamedQuery(name="SourceNotification.findAll", query="SELECT s FROM SourceNotification s")
public class SourceNotification implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="notification_id", unique=true, nullable=false)
	private int notificationId;

	@Column(nullable=false, length=50)
	private String emailaddress;

	//bi-directional many-to-one association to MasterSource
	@ManyToOne
	@JoinColumn(name="master_id", nullable=false)
	private MasterSource masterSource;

	public SourceNotification() {
	}

	public int getNotificationId() {
		return this.notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public String getEmailaddress() {
		return this.emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public MasterSource getMasterSource() {
		return this.masterSource;
	}

	public void setMasterSource(MasterSource masterSource) {
		this.masterSource = masterSource;
	}

}