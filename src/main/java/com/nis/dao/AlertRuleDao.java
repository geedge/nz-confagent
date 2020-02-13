package com.nis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.nis.entity.AlertRule;

@Mapper
public interface AlertRuleDao {
	
	List<AlertRule> selectList();
	
	List<AlertRule> selectBulidInRule();
}
