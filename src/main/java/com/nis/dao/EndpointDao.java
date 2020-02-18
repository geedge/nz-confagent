package com.nis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.nis.entity.Endpoint;
import com.nis.entity.MetricsDto;

@Mapper
public interface EndpointDao {
	
	List<Endpoint> selectExporterInfos(Integer idcId);
	
	List<MetricsDto> selectEndpointMetrics();
	
	List<Endpoint> selectPromServerInfo();
}
