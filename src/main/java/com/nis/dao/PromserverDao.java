package com.nis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nis.entity.Promserver;

public interface PromserverDao {
	
	List<Promserver> selectList();
		
	Promserver selectRoleByIp(String ip);
	
	List<Promserver> selectSubInfo(@Param("promserver")Promserver promserver);
	
	List<Promserver> selectSubInfosByType(Integer type);
}
