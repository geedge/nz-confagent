package com.nis.entity;

import java.io.Serializable;
import java.util.Date;

public class Asset implements Serializable{
	private Integer id;
	private String sn;
	private Integer modelId;
	private Integer idcId;
	private Integer cabinetId;
	private String host;
	private Integer state;
	private Date purchaseDate;
	private Date createAt;
	private Date updateAt;
	private Integer createBy;
	private Integer updateBy;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getModelId() {
		return modelId;
	}
	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}
	public Integer getIdcId() {
		return idcId;
	}
	public void setIdcId(Integer idcId) {
		this.idcId = idcId;
	}
	public Integer getCabinetId() {
		return cabinetId;
	}
	public void setCabinetId(Integer cabinetId) {
		this.cabinetId = cabinetId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	public Integer getCreateBy() {
		return createBy;
	}
	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}
	public Integer getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(Integer updateBy) {
		this.updateBy = updateBy;
	}
	
	
}
