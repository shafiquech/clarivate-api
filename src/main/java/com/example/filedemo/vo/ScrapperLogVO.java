package com.example.filedemo.vo;

import java.util.Date;

public class ScrapperLogVO {

	private int scrapperId;
	private Date datetime;
	private String description;
	private String fileName;
	private String path;
	private int masterId;
	private String generateDiffFile;
	
	public int getScrapperId() {
		return scrapperId;
	}
	public void setScrapperId(int scrapperId) {
		this.scrapperId = scrapperId;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setMasterId(int masterId) {
		this.masterId = masterId;
	}
	public int getMasterId() {
		return masterId;
	}
	public String getGenerateDiffFile() {
		return generateDiffFile;
	}
	public void setGenerateDiffFile(String generateDiffFile) {
		this.generateDiffFile = generateDiffFile;
	}

	
	
}
