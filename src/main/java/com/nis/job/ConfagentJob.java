package com.nis.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.alibaba.fastjson.JSON;
import com.nis.entity.AlertManagerEnum;
import com.nis.entity.AlertRule;
import com.nis.entity.Endpoint;
import com.nis.entity.Promserver;
import com.nis.entity.SysConfig;
import com.nis.service.AlertRuleService;
import com.nis.service.ConfEventService;
import com.nis.service.EndpointService;
import com.nis.service.PromserverService;
import com.nis.service.SysConfigService;
import com.nis.util.ConfigUtil;
import com.nis.util.Constant;
import com.nis.util.PropertyPlaceholder;
import com.nis.util.YamlUtil;

/**
 * confagent job任务相关 处理同步prometheus配置信息
 * @author Th
 *
 */
public class ConfagentJob extends QuartzJobBean {
	private Logger logger = LoggerFactory.getLogger(ConfagentJob.class);
	private static Map<String,Integer> confEvents=null;
	@Autowired
	private PromserverService promserverService;
	@Autowired
	private ConfEventService confEventService;
	@Autowired
	private EndpointService endpointService;
	@Autowired
	private AlertRuleService alertRuleService;
	@Autowired
	private SysConfigService sysConfigService;
	@Autowired
    private ConfigUtil configUtil;
	@Value("${confagent.ipaddr}")
	private String ipaddr;
	@Value("${confagent.ymlPath}")
	private String ymlPath;
	@Value("${confagent.ruleYmlPath}")
	private String ruleYmlPath;
	@Value("${confagent.prometheusPort}")
	private	String prometheusPort;
	@Value("${confagent.metricsPath}")
	private String metricsPath;
	@Autowired
	private RedisTemplate<Object,Object> redisTemplate;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.info("task start");
		//获取本机ip地址 获取本机所属角色信息
		logger.info("The local IP address is {}",ipaddr);
		
		Promserver promserver = promserverService.queryRoleByIp(ipaddr);
		logger.info("The local role info {}",JSON.toJSON(promserver));
		
		logger.info("The local ymlPath info {}",JSON.toJSON(ymlPath));
		
		logger.info("The local ruleYmlPath info {}",JSON.toJSON(ruleYmlPath));
		
		savePromeserverInfoToRedis();
		
		if(promserver!=null) {
			// 判断本次表格数据与之前数据是否有变化判断是否执行操作
			boolean changeFlag = confEventService.compareData(confEvents);
			//先判断conf_event表中是否有数据变化  程序初始进入map数据为空 则全量配置更新同步
			logger.debug("before confEvents data {} ",JSON.toJSON(confEvents));
			if(confEvents==null || changeFlag ) {
				logger.debug("data had change confEvents data {}",JSON.toJSON(confEvents));
				logger.debug("data had change flag {}",changeFlag);
				if(promserver.getType()==1) {
					Map centerResult = centerHandle();
					logger.debug("center result info {} ",JSON.toJSON(centerResult));
					if(centerResult!=null) {
						//获取yml文件信息替换对应内容
						try {
							boolean handleResult = YamlUtil.centerYmlHandle(centerResult,ymlPath,ruleYmlPath);
							logger.info("yamlHanlde center result {}",JSON.toJSON(handleResult));
						} catch (Exception e) {
							logger.error("yamlHanlde center error");
							e.printStackTrace();
						}
					}
				}else if(promserver.getType()==2) {
					List result = subHandle(promserver);
					logger.debug("subHandle list info {} ",JSON.toJSON(result));
					if(result!=null) {
						//获取yml文件信息替换对应内容
						try {
							boolean handleResult = YamlUtil.subYmlHandle(result,ymlPath);
							logger.info("yamlHanlde sub result {}",JSON.toJSON(handleResult));
						} catch (Exception e) {
							logger.error("yamlHanlde sub error");
							e.printStackTrace();
						}
					}
				}
				//处理完信息配置后 将confEvent数据保存 留待下个周期比较判断
				confEvents=confEventService.queryConfEventMap();
			}else {
				logger.info("confEvents list info {}",JSON.toJSON(confEvents));
				logger.info("changeflag is {}",changeFlag);
				// 如果表中数据无更新则本次无操作
				logger.info("There is no change in the table data. This task is completed");
			}
		}
		logger.info("Synchronization configurator end");
	}

	/**
	 * 处理sub相关逻辑
	 */
	public List subHandle(Promserver promserver) {
		//查询同一个idc下相同角色sub的数量以及exporter的数量信息
		List<Promserver> subInfos = promserverService.querySubInfos(promserver);
		logger.info("subHandle : There are {} sub in total",subInfos.size());
		logger.info("subHandle : There are all subInfos : {}",JSON.toJSON(subInfos));
		
		List<Endpoint> endpointInfos = endpointService.queryExporterInfos(promserver.getIdcId());
		logger.info("subHandle : There are {} exporter in total",endpointInfos.size());
		logger.info("subHandle : There are all endpointInfos : {} ",JSON.toJSON(endpointInfos));
		//获取当前sub排序信息
		Integer index=null;
		if(!subInfos.isEmpty()) {
			for(int i=0;i<subInfos.size();i++) {
				if(subInfos.get(i).getHost().equals(promserver.getHost())) {
					index=i;
				}
			}
		}
		List result = null;
		//两者模运算均匀分配
		if(index!=null && !endpointInfos.isEmpty()) {
			result=new ArrayList();
			for(int m=0;m<endpointInfos.size();m++) {
				if(m%subInfos.size()==index) {
					LinkedHashMap job = new LinkedHashMap();
//					job.put("job_name", endpointInfos.get(m).getModule().getName()+endpointInfos.get(m).getHost());
					job.put("job_name", "ed_"+endpointInfos.get(m).getId());
					job.put("metrics_path", endpointInfos.get(m).getPath());
					List list=new ArrayList();
					list.add(endpointInfos.get(m).getHost()+":"+endpointInfos.get(m).getPort());
					List list2=new ArrayList();
					Map job2 = new HashMap();
					job2.put("targets",list);
					Map job3 = new HashMap();
					job3.put("dc", endpointInfos.get(m).getIdc().getName());
					job3.put("project", endpointInfos.get(m).getProject().getName());
					job3.put("asset",endpointInfos.get(m).getAsset().getHost());
					job3.put("module",endpointInfos.get(m).getModule().getName());
//					job3.put("job", "ed_"+endpointInfos.get(m).getId());
//					job3.put("host", endpointInfos.get(m).getAsset().getHost());
					job2.put("labels", job3);
					list2.add(job2);
					job.put("static_configs", list2);
					
					//处理endpoint中param字段json串 请求参数
					String param = endpointInfos.get(m).getParam();
					Map params=null;
					if(param!=null && !param.isEmpty()) {
						params=new HashMap();
						Map<String,String> json = (Map) JSON.parse(param);
						for(Map.Entry<String, String> a:json.entrySet()){
							List paramList =new ArrayList();
							paramList.add(a.getValue());
							params.put(a.getKey(),paramList);
						}
					}
					if(params!=null) {
						job.put("params",params);
					}
					result.add(job);
				}
			}
		}
		logger.info("subHandle : result : {} ",JSON.toJSON(result));
		return result;
	}
	
	/**
	 * 处理center相关逻辑
	 */
	public Map centerHandle() {
		Map result=new HashMap();
		//查询sys_config表获取数据
		List<SysConfig> sysConfigs = sysConfigService.queryList();
		//获取所有sub信息 配置联邦
		List<Promserver> subInfos = promserverService.querySubInfosByType(2);
		if(subInfos==null||subInfos.size()==0) {
			logger.info("promserver is null");
			return null;
		}
		logger.info("centerHandle : There are all subInfos : {}",JSON.toJSON(subInfos));
		
		//配置rule相关文件信息 目前固定为rule.yml
		List ruleFile =new ArrayList();
		ruleFile.add("rule.yml");
		result.put("rule_files", ruleFile);
		
		// 配置组装联邦信息开始
		List l = new ArrayList();
		Map m1 = new LinkedHashMap();
		m1.put("job_name", "federate");
		
//		m1.put("scrape_interval", "15s");
		m1.put("honor_labels", true);
		m1.put("metrics_path", "/federate");
				
		List l2 =new ArrayList();
		List l3 =new ArrayList();
		//获取默认端口号
//		String prometheusPort = (String)PropertyPlaceholder.getProperty("prometheusPort");
		for(Promserver subInfo:subInfos) {
			l3.add(subInfo.getHost()+":"+subInfo.getPort());
		}
		Map m2 =new HashMap();
		m2.put("targets", l3);
		l2.add(m2);
		m1.put("static_configs", l2);
		
		Map m3 = new HashMap();
		List l4=new ArrayList();
//		l4.add("{job=\"prometheus\"}");
		l4.add("{dc=~\".+\"}");
		m3.put("match[]", l4);
		m1.put("params", m3);
		
		l.add(m1);
		
		
		
		//配置自监控相关任务
		Map nezhaMetrics = new HashMap();
		Map nezhaMetrics2 = new HashMap();
		nezhaMetrics.put("job_name","nezha_metrics");
		
		Map confagentMetrics = new HashMap();
		Map confagentMetrics2 = new HashMap();
		confagentMetrics.put("job_name","confagent_metrics");
		
		List nezhaMetricsList =new ArrayList();
		List nezhaMetricsList2 =new ArrayList();
		List confagentMetricsList =new ArrayList();
		List confagentMetricsList2 =new ArrayList();
		
		nezhaMetrics.put("static_configs", nezhaMetricsList);
		confagentMetrics.put("static_configs", confagentMetricsList);
		
		if(!sysConfigs.isEmpty()) {
			for(SysConfig sysconfig : sysConfigs) {
				if(sysconfig.getParamKey().equals(AlertManagerEnum.ALERT_API.getValue())) {
					nezhaMetricsList2.add(sysconfig.getParamValue());
				}else if(sysconfig.getParamKey().equals(AlertManagerEnum.ALERT_PATH_PREFIX.getValue())){
					nezhaMetrics.put("metrics_path", sysconfig.getParamValue()+metricsPath);
				}else if(sysconfig.getParamKey().equals(AlertManagerEnum.CONFAGENT_API.getValue())) {
					confagentMetricsList2.add(sysconfig.getParamValue());
				}else if(sysconfig.getParamKey().equals(AlertManagerEnum.CONFAGENT_PATH.getValue())) {
					confagentMetrics.put("metrics_path", sysconfig.getParamValue()+metricsPath);
				}
			}
		}
		nezhaMetrics2.put("targets", nezhaMetricsList2);
		confagentMetrics2.put("targets", confagentMetricsList2);
		nezhaMetricsList.add(nezhaMetrics2);
		confagentMetricsList.add(confagentMetrics2);
		l.add(nezhaMetrics);
		l.add(confagentMetrics);
		
		result.put("scrape_configs", l);
		
		logger.info("centerHandle : scrape_configs Info : {}",JSON.toJSON(l));
		//配置联邦信息结束
		
		//配置rule
		
		//查询所有rule配置信息
		List<AlertRule> alertRules = alertRuleService.queryList();
		
		Map toCheck = new HashMap();
		
		List groups = new ArrayList();
		
		Map group = new HashMap();
		groups.add(group);
		
		List rules = new ArrayList();
		group.put("name", "alertRule");
		group.put("rules", rules);

		for(AlertRule alertRule : alertRules) {
			Map labels = new HashMap();
			labels.put("severity", alertRule.getSeverity());
			labels.put("type", alertRule.getType());
			labels.put("linkId", alertRule.getLinkId());
			Map annotations = new HashMap();
			annotations.put("summary", alertRule.getSummary());
			annotations.put("description", alertRule.getDescription());
			Map rule = new HashMap();
			rule.put("alert", alertRule.getId());
			rule.put("expr", alertRule.getExpr());
			rule.put("for", alertRule.getLast() + "s");
			rule.put("labels", labels);
			rule.put("annotations", annotations);
			rules.add(rule);
		}
		result.put("groups", groups);
		
		logger.info("centerHandle : alert rule Info : {}",JSON.toJSON(groups));
		//配置alertManager
		
		
		Map alertManager =new HashMap();
		List staticConfigList=new ArrayList();
		LinkedHashMap staticConfigs =new LinkedHashMap();
		staticConfigList.add(staticConfigs);
		List staticConfig =new ArrayList();
		Map targets =new HashMap();
		List target =new ArrayList();
		List alertData =new ArrayList();
		if(!sysConfigs.isEmpty()) {
			for(SysConfig sysconfig : sysConfigs) {
				if(sysconfig.getParamKey().equals(AlertManagerEnum.ALERT_API.getValue())) {
					alertData.add(sysconfig.getParamValue());
				}else if(sysconfig.getParamKey().equals(AlertManagerEnum.ALERT_PATH_PREFIX.getValue())){
					staticConfigs.put("path_prefix", sysconfig.getParamValue());
				}
			}
		}
		if(staticConfigs.get("path_prefix")==null) {
			staticConfigs.put("path_prefix","/");
		}
		alertManager.put("alertmanagers",staticConfigList);
		staticConfigs.put("static_configs", staticConfig);
		staticConfig.add(targets);
		targets.put("targets", alertData);
		result.put("alerting", alertManager);
		logger.info("centerHandle : alert manager Info : {}",JSON.toJSON(alertManager));
		return result;
	}
	
	/**
	 * 定时任务中将promserver信息储存到redis中
	 */
	public void savePromeserverInfoToRedis() {
		// 每次同步操作先将之前缓存内容清空然后更新
		HashOperations opsForHash = redisTemplate.opsForHash();
		Set<Object> keys = redisTemplate.opsForHash().keys(Constant.ENDPOINT_PROM);
		logger.info("endpoint_prom keys :{}",JSON.toJSON(keys));
		if(!keys.isEmpty()) {
			for(Object key:keys) {
				opsForHash.delete(Constant.ENDPOINT_PROM, key);
			}
			logger.info("delete "+Constant.ENDPOINT_PROM+" all keys ");
		}
		List<Endpoint> endpointPromInfos = endpointService.queryPromServerInfo();
		if(endpointPromInfos!=null && endpointPromInfos.size()>0) {
			for(Endpoint endpointPromInfo : endpointPromInfos) {
				logger.info("savePromeserverInfoToRedis"+endpointPromInfo.getId()+"--->"+JSON.toJSONString(endpointPromInfo.getPromserver()));
				redisTemplate.boundHashOps(Constant.ENDPOINT_PROM).put(endpointPromInfo.getId().toString(), endpointPromInfo.getPromserver());
			}
		}
	}
}
