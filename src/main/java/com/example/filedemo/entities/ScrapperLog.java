package com.example.filedemo.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the scrapper_log database table.
 * 
 */
@Entity
@Table(name="scrapper_log")
@NamedQuery(name="ScrapperLog.findAll", query="SELECT s FROM ScrapperLog s")
public class ScrapperLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="scrapper_id", unique=true, nullable=false)
	private int scrapperId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date datetime;

	@Column(nullable=false, length=200)
	private String description;

	@Column(name="file_name")
	private String fileName;

	@Column(nullable=false, length=50)
	private String path;
	
	@Column(name="generate_diff_file")
	private String generateDiffFile;

	//bi-directional many-to-one association to MasterSource
	@ManyToOne
	@JoinColumn(name="master_id", nullable=false)
	private MasterSource masterSource;

	public ScrapperLog() {
	}

	public int getScrapperId() {
		return this.scrapperId;
	}

	public void setScrapperId(int scrapperId) {
		this.scrapperId = scrapperId;
	}

	public Date getDatetime() {
		return this.datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public MasterSource getMasterSource() {
		return this.masterSource;
	}

	public void setMasterSource(MasterSource masterSource) {
		this.masterSource = masterSource;
	}

	public String getGenerateDiffFile() {
		return generateDiffFile;
	}

	public void setGenerateDiffFile(String generateDiffFile) {
		this.generateDiffFile = generateDiffFile;
	}

}