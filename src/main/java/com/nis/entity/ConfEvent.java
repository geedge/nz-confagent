package com.nis.entity;

import java.io.Serializable;

public class ConfEvent implements Serializable{
	
	private String table;
	private Integer value;
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	
}
