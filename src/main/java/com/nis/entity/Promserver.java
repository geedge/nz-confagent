package com.nis.entity;

import java.io.Serializable;

public class Promserver implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer idcId;
	private String host;
	private String relabel;
	private Integer type;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getIdcId() {
		return idcId;
	}
	public void setIdcId(Integer idcId) {
		this.idcId = idcId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getRelabel() {
		return relabel;
	}
	public void setRelabel(String relabel) {
		this.relabel = relabel;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	
}
