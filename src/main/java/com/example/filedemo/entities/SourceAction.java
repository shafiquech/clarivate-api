package com.example.filedemo.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the source_action database table.
 * 
 */
@Entity
@Table(name="source_action")
@NamedQuery(name="SourceAction.findAll", query="SELECT s FROM SourceAction s")
public class SourceAction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="action_id", unique=true, nullable=false)
	private int actionId;

	@Column(name="step_seq", nullable=false)
	private int stepSeq;

	@Column(nullable=false, length=100)
	private String url;

	//bi-directional many-to-one association to MasterSource
	@ManyToOne
	@JoinColumn(name="master_id", nullable=false)
	private MasterSource masterSource;

	public SourceAction() {
	}

	public int getActionId() {
		return this.actionId;
	}

	public void setActionId(int actionId) {
		this.actionId = actionId;
	}

	public int getStepSeq() {
		return this.stepSeq;
	}

	public void setStepSeq(int stepSeq) {
		this.stepSeq = stepSeq;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MasterSource getMasterSource() {
		return this.masterSource;
	}

	public void setMasterSource(MasterSource masterSource) {
		this.masterSource = masterSource;
	}

}