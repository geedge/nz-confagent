package com.nis.entity;

import java.io.Serializable;

public class Endpoint implements Serializable{
	
	private Integer id;
	private Integer moduleId;
	private Integer assetId;
	private String host;
	private Integer port;
	private String param;
	private String path;
	private Module module;
	private Asset asset;
	private Idc idc;
	private Integer idcId;
	private Project project;
	private Promserver promserver;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getModuleId() {
		return moduleId;
	}
	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}
	public Integer getAssetId() {
		return assetId;
	}
	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Module getModule() {
		return module;
	}
	public void setModule(Module module) {
		this.module = module;
	}
	public Asset getAsset() {
		return asset;
	}
	public void setAsset(Asset asset) {
		this.asset = asset;
	}
	public Idc getIdc() {
		return idc;
	}
	public void setIdc(Idc idc) {
		this.idc = idc;
	}
	public Integer getIdcId() {
		return idcId;
	}
	public void setIdcId(Integer idcId) {
		this.idcId = idcId;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public Promserver getPromserver() {
		return promserver;
	}
	public void setPromserver(Promserver promserver) {
		this.promserver = promserver;
	}
}

