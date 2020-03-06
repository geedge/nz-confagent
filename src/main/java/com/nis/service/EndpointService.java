package com.nis.service;

import java.util.List;

import com.nis.entity.Endpoint;
import com.nis.entity.MetricsDto;
import com.nis.entity.Promserver;

public interface EndpointService {

	/**
	 * 根据idc信息查询exporter数量信息
	 */
	public List<Endpoint> queryExporterInfos(Integer idcId);
	
	/**
	 * 自定义指标当前采集endpoint个数
	 */
	List<MetricsDto> queryEndpointMetrics();
	
	/**
	 * 查询endpoint和promserver对应关系
	 */
	List<Endpoint> queryPromServerInfo(Integer promserverId);
}
