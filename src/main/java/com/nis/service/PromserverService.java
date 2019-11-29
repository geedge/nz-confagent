package com.nis.service;

import java.util.List;

import com.nis.entity.Promserver;

public interface PromserverService {
	
	public List<Promserver> queryList();
	
	/**
	 * 根据ip查询
	 * @param ip
	 * @return
	 */
	public Promserver queryRoleByIp(String ip);
	
	/**
	 * 查询sub信息 根据idcid及type
	 * @param promserver
	 * @return
	 */
	public List<Promserver> querySubInfos(Promserver promserver);
	
	/**
	 * 查询所有sub信息
	 */
	public List<Promserver> querySubInfosByType(Integer type);
}
