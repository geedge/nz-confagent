package com.nis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nis.dao.PromserverDao;
import com.nis.entity.Promserver;
import com.nis.service.PromserverService;

@Service
public class PromserverServiceImpl implements PromserverService {
	
	@Autowired
	private PromserverDao promserverDao;
	
	public List<Promserver> queryList(){
		return promserverDao.selectList();
	}

	@Override
	public Promserver queryRoleByIp(String ip) {
		return promserverDao.selectRoleByIp(ip);
	}

	@Override
	public List<Promserver> querySubInfos(Promserver promserver) {
		return promserverDao.selectSubInfo(promserver);
	}

	@Override
	public List<Promserver> querySubInfosByType(Integer type) {
		return promserverDao.selectSubInfosByType(type);
	}
}
