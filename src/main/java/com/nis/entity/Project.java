package com.nis.entity;

import java.io.Serializable;

public class Project implements Serializable{
	private Integer id;
	private String name;
	private String remark;
	private Boolean buildIn;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Boolean getBuildIn() {
		return buildIn;
	}
	public void setBuildIn(Boolean buildIn) {
		this.buildIn = buildIn;
	}
}
