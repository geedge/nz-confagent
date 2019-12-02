package com.nis.entity;

import java.io.Serializable;

public class Idc implements Serializable{
	private Integer id;
	private String name;
	private String location;
	private String tel;
	private Integer principal;
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Integer getPrincipal() {
		return principal;
	}
	public void setPrincipal(Integer principal) {
		this.principal = principal;
	}
	
	
}
