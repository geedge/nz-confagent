package com.nis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nis.dao.EndpointDao;
import com.nis.entity.Endpoint;
import com.nis.entity.MetricsDto;
import com.nis.entity.Promserver;
import com.nis.service.EndpointService;
@Service
public class EndpointServiceImpl implements EndpointService{
	@Autowired
	private EndpointDao endpointDao;
	
	@Override
	public List<Endpoint> queryExporterInfos(Integer idcId) {
		return endpointDao.selectExporterInfos(idcId);
	}

	@Override
	public List<MetricsDto> queryEndpointMetrics() {
		return endpointDao.selectEndpointMetrics();
	}

	@Override
	public List<Endpoint> queryPromServerInfo(Integer promserverId) {
		return endpointDao.selectPromServerInfo(promserverId);
	}

}
