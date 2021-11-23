package com.seeyon.apps.test.contractEvents.dao;

import java.util.Map;

/**
 * @author sheHaiTao
 * @data 2021/10/27 - 16:32
 * @Description 通用数据获取接口
 */
public interface CommonDao {

    /**
     * 自定义sql,表数据查询
     * @param tableName 表名
     * @param formId 表id
     * @return 返回结果集
     */
    Map getData(String tableName, Long formId);

}
