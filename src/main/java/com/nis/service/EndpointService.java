package com.nis.service;

import java.util.List;

import com.nis.entity.Endpoint;

public interface EndpointService {

	/**
	 * 根据idc信息查询exporter数量信息
	 */
	public List<Endpoint> queryExporterInfos(Integer idcId);
	
}
