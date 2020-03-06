package com.nis.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthyController{
	
	@GetMapping("/healthy")
	public Map healthy() {
		HashMap<String, Object> map =new HashMap<String,Object>();
		map.put("msg", "success");
		map.put("code", 200);
		return map;
	}
}
