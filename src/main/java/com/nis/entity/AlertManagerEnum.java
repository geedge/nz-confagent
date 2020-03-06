package com.nis.entity;

public enum AlertManagerEnum {
	ALERT_API("alert_api"), 
	ALERT_PATH_PREFIX("alert_path_prefix"),
	CONFAGENT_PORT("confagent_port"),
	CONFAGENT_PATH("confagent_path");
    private final String paramkey; 
    private AlertManagerEnum(String paramkey) { 
           this.paramkey = paramkey; 
    } 
    public String getValue() { 
       return paramkey; 
    } 
}
