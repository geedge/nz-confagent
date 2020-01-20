package com.nis.entity;

import java.io.Serializable;
import java.util.Date;

public class Promserver implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer idcId;
	private String host;
	private Integer port;
	private Integer type;
	private Boolean status;
	private Date lastUpdate;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}	
}
