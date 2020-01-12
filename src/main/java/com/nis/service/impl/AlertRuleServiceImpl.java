package com.nis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nis.dao.AlertRuleDao;
import com.nis.entity.AlertRule;
import com.nis.service.AlertRuleService;

@Service
public class AlertRuleServiceImpl implements AlertRuleService {
	
	@Autowired
	private AlertRuleDao alertRuleDao;
	
	@Override
	public List<AlertRule> queryList() {
		return alertRuleDao.selectList();
	}

	@Override
	public AlertRule queryBulidInRule() {
		List<AlertRule> results = alertRuleDao.selectBulidInRule();
		if(results!=null && results.size()>0) {
			return results.get(0);
		}else {
			return null;
		}
	}
}
