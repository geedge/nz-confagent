package com.nis.entity;

public enum AlertManagerEnum {
	Alert_api("alert_api"), 
	Alert_path_prefix("alert_path_prefix"); 
    private final String paramkey; 
    private AlertManagerEnum(String paramkey) { 
           this.paramkey = paramkey; 
    } 
    public String getValue() { 
       return paramkey; 
    } 
}
