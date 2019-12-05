package com.nis.entity;

import java.io.Serializable;

public class AlertRule implements Serializable{
	
	private Integer id;
	/**
	 * 告警名称
	 */
	private String alertName;
	/**
	 * 运算表达式
	 */
	private String expr;
	/**
	 * 持续时间,单位s
	 */
	private Integer last;
	/**
	 * 告警级别
	 */
	private String severity;
	/**
	 * 告警摘要
	 */
	private String summary;
	/**
	 * 告警详细描述
	 */
	private String description;
	/**
	 * 通知用户组
	 */
	private Integer receiver;
	/**
	 * 告警类型
	 */
	private Integer type;
	/**
	 * 关联id
	 */
	private Integer linkId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAlertName() {
		return alertName;
	}
	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	public Integer getLast() {
		return last;
	}
	public void setLast(Integer last) {
		this.last = last;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getReceiver() {
		return receiver;
	}
	public void setReceiver(Integer receiver) {
		this.receiver = receiver;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getLinkId() {
		return linkId;
	}
	public void setLinkId(Integer linkId) {
		this.linkId = linkId;
	}
}
