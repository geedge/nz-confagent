package com.nis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.nis.entity.SysConfig;

@Mapper
public interface SysConfigDao {
	
	List<SysConfig> selectList();
	
}
