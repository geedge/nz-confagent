package com.nis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nis.dao.SysConfigDao;
import com.nis.entity.SysConfig;
import com.nis.service.SysConfigService;

@Service
public class SysConfigServiceImpl implements SysConfigService {
	
	@Autowired
	private SysConfigDao sysConfigDao;
	
	@Override
	public List<SysConfig> queryList() {
		return sysConfigDao.selectList();
	}
}
