package com.example.filedemo.vo;

public class SourceActionVO {
	
	public SourceActionVO() {
	}
	
	public SourceActionVO(int actionId, int stepSeq, String url) {
		super();
		this.actionId = actionId;
		this.stepSeq = stepSeq;
		this.url = url;
	}

	private int actionId;
	private int stepSeq;
	private String url;

	public int getActionId() {
		return actionId;
	}

	public void setActionId(int actionId) {
		this.actionId = actionId;
	}

	public int getStepSeq() {
		return stepSeq;
	}

	public void setStepSeq(int stepSeq) {
		this.stepSeq = stepSeq;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
