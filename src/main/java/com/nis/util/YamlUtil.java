package com.nis.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.platform.commons.util.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.alibaba.fastjson.JSON;



public class YamlUtil {
	
	private static Yaml y;
	
	static {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		y = new Yaml(options);
	}

	public static boolean subYmlHandle(List param) throws Exception {
		boolean flag=false;
		String ymlPath = (String)PropertyPlaceholder.getProperty("ymlPath");
		if(!param.isEmpty() && StringUtils.isNotBlank(ymlPath)) {
			File file = new File(ymlPath);
			FileInputStream fileInputStream  = new FileInputStream(file);
			Map map = y.loadAs(fileInputStream, Map.class);
			map.put("scrape_configs", param);
			OutputStream out = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			y.dump(map,osw);
//			RuntimeUtil.run(null, null, new String[]{"kill -hup `ps -ef |grep prometheus|grep -v grep|awk '{print $2}'`"});
			flag = true;
		}
		return flag;
	}
	
	public static boolean centerYmlHandle(Map param) throws Exception {
		boolean flag=false;
		String ymlPath = (String)PropertyPlaceholder.getProperty("ymlPath");
		if(!param.isEmpty() && StringUtils.isNotBlank(ymlPath)) {
			File file = new File(ymlPath);
			FileInputStream fileInputStream  = new FileInputStream(file);
			Map map = y.loadAs(fileInputStream, Map.class);
			map.put("scrape_configs", param.get("scrape_configs"));
			map.put("groups", param.get("groups"));
			map.put("alerting", param.get("alerting"));
			OutputStream out = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			y.dump(map,osw);
//			RuntimeUtil.run(null, null, new String[]{"kill -hup `ps -ef |grep prometheus|grep -v grep|awk '{print $2}'`"});
			flag = true;
		}
		return flag;
	}
	
	
	public static void main(String[] args) throws Exception {
//		File file = new File("F:/workspace/nz-web/nz-admin/tmp/promRuleCheck1574925561786.yml");
		File file = new File("F:/prometheus.yml");
		FileInputStream fileInputStream  = new FileInputStream(file);
		Map map = y.loadAs(fileInputStream, Map.class);

		/**
		List l = new ArrayList();
		Map m1 = new LinkedHashMap();
		m1.put("job_name", "federate");
		
		
		m1.put("scrape_interval", "15s");
		m1.put("honor_labels", true);
		m1.put("metrics_path", "/federate");
		
		
		List l2 =new ArrayList();
		List l3 =new ArrayList();
		l3.add("192.168.1.1:9090");
		l3.add("192.168.2.2:9090");
		Map m2 =new HashMap();
		m2.put("targets", l3);
		l2.add(m2);
		m1.put("static_configs", l2);
		
		Map m3 = new HashMap();
		List l4=new ArrayList();
		l4.add("{job=\"prometheus\"}");
		l4.add("{__name__=~\"job:.*\"}");
		m3.put("match[]", l4);
		m1.put("params", m3);
		
		
		l.add(m1);
		map.put("scrape_configs", l);
		**/
		
		/*Map toCheck = new HashMap();
		
		List groups = new ArrayList();
		
		Map group = new HashMap();
		groups.add(group);
		
		List rules = new ArrayList();
		group.put("name", "example");
		group.put("rules", rules);
		
		for(int i=0;i<3;i++) {
			Map labels = new HashMap();
			labels.put("severity", "warning");
			Map annotations = new HashMap();
			annotations.put("summary", "hhh");
			annotations.put("description", "1111");
			Map rule = new HashMap();
			rule.put("alert", "tttt");
			rule.put("expr", "up == 0");
			rule.put("for", "70s");
			rule.put("labels", labels);
			rule.put("annotations", annotations);
			rules.add(rule);
		}
		map.put("groups", groups);*/
		
		Map alertManager =new HashMap();
		List staticConfigList=new ArrayList();
		Map staticConfigs =new HashMap();
		staticConfigList.add(staticConfigs);
		List staticConfig =new ArrayList();
		Map targets =new HashMap();
		List target =new ArrayList();
		List alertData =new ArrayList();
		alertData.add("192.168.41.134:8008");
		alertManager.put("alertmanagers",staticConfigList);
		staticConfigs.put("static_configs", staticConfig);
		staticConfig.add(targets);
		targets.put("targets", alertData);
		map.put("alerting", alertManager);
		OutputStream out = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
		y.dump(map,osw);
	}
}
