package com.nis.dao;

import java.util.List;

import com.nis.entity.Endpoint;

public interface EndpointDao {
	
	List<Endpoint> selectExporterInfos(Integer idcId);
	
}
