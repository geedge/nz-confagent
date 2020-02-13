/**

 *

 *

 */

package com.nis.util;

import org.apache.ibatis.mapping.DatabaseIdProvider;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

/**
 * 常量
 *

 */
public class Constant {
	
	/** 超级管理员ID */
	public static final int SUPER_ADMIN = 1;
    /** 数据权限过滤 */
	public static final String SQL_FILTER = "sql_filter";
    /**
     * 当前页码
     */
    public static final String PAGE = "pageNo";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "pageSize";
    /**
     * 每页显示条数
     */
    public static final long PAGESIZE = 10;
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";
    
    /**
     * 当前数据库类型
     */
    public static final String DB_TYPE;
    
    public static final String ORACLE = "oracle";
    public static final String MYSQL = "mysql";
    public static final String POSTGRESQL = "postgresql";
    
    static {
    	DatabaseIdProvider databaseIdProvider = SpringContextUtils.getBean("getDatabaseIdProvider", DatabaseIdProvider.class);
        DataSource dataSource = SpringContextUtils.getBean("dataSource",DataSource.class);
        String databaseId = null;
		try {
			databaseId = databaseIdProvider.getDatabaseId(dataSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DB_TYPE = databaseId;
    }
}
