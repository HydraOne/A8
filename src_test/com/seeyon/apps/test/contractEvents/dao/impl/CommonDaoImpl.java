package com.seeyon.apps.test.contractEvents.dao.impl;

import com.seeyon.apps.test.contractEvents.dao.CommonDao;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.rest.resources.RegulationContractResources;
import com.seeyon.ctp.util.JDBCAgent;
import org.apache.commons.logging.Log;

import java.sql.SQLException;
import java.util.Map;

public class CommonDaoImpl implements CommonDao {

    //日志
    private static final Log log = CtpLogFactory.getLog(RegulationContractResources.class);

    @Override
    public Map getData(String tableName, Long formId) {
        //初始化sql
        String sql = null;
        StringBuffer stringBuffer = new StringBuffer();
        sql = String.valueOf(stringBuffer.append("select * from ").append(tableName).append(" where id = ").append(formId));
        //JDBC代理(不使用原始数据库连接构造,不自动关闭连接)
        JDBCAgent jdbcAgent = new JDBCAgent(false, false);
        //执行sql,存入Map
        Map resultMap = null;
        try {
            jdbcAgent.execute(sql);
            resultMap = jdbcAgent.resultSetToMap();
            log.info("通过表名和id查询了" + tableName + "的数据");
        } catch (BusinessException | SQLException e) {
            log.error("JDBC查询出现异常:",e);
        }finally {
            jdbcAgent.close();
        }
        return resultMap;
    }
}
