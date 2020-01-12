package com.nis.dao;

import java.util.List;

import com.nis.entity.AlertRule;

public interface AlertRuleDao {
	
	List<AlertRule> selectList();
	
	List<AlertRule> selectBulidInRule();
}
