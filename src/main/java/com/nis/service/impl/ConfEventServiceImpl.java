package com.nis.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nis.dao.ConfEventDao;
import com.nis.entity.ConfEvent;
import com.nis.service.ConfEventService;

@Service
public class ConfEventServiceImpl implements ConfEventService {
	
	@Autowired
	private ConfEventDao confEventDao;
	
	@Override
	public Map<String, Integer> queryConfEventMap() {
		Map<String,Integer> result = new HashMap<String,Integer>();
		List<ConfEvent> datas = confEventDao.selectList();
		if(datas.size()>0) {
			for(ConfEvent data:datas) {
				result.put(data.getTable(), data.getValue());
			}
			return result;
		}else {
			return null;
		}
	}

	@Override
	public boolean compareData(Map<String, Integer> map) {
		boolean flag = false;
		if(map==null) {
			flag = true;
			return flag;
		}
		List<ConfEvent> datas = confEventDao.selectList();
		for(ConfEvent data:datas) {
			if(!(data.getValue()==map.get(data.getTable()))) {
				flag = true;
			}
		}
		return flag;
	}
}
