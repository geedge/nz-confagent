package com.nis.service;

import java.util.List;
import java.util.Map;

import com.nis.entity.ConfEvent;

public interface ConfEventService {
	
	public Map<String,Integer> queryConfEventMap();
	
	/**
	 * 
	 */
	public boolean compareData(Map<String,Integer> map);
}
