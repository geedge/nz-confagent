package com.nis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.nis.entity.ConfEvent;

@Mapper
public interface ConfEventDao {
	
	List<ConfEvent> selectList();
	
}
