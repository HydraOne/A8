package com.seeyon.apps.test.download.dao.impl;

import com.seeyon.apps.test.download.dao.FtpDao;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.JDBCAgent;
import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author sheHaiTao
 * @data 2021/11/11 - 18:33
 * FTP上传文件写入第三方数据库数据
 */
public class FtpDaoImpl implements FtpDao {

    //日志
    private static final Log log = CtpLogFactory.getLog(FtpDaoImpl.class);


    @Override
    public Map findCtpContentAll(String edocId) {
        String sql = "select * from ctp_content_all where module_id = '" + edocId + "' and content_type = 41";
        Map resultSetToMap = new HashMap();
        JDBCAgent jdbcAgent = null;
        try {
            jdbcAgent = new JDBCAgent();
            jdbcAgent.execute(sql);
            resultSetToMap = jdbcAgent.resultSetToMap();
        } catch (Exception e) {
            log.error("Ftp文件上传功能查询数据时出现异常:",e);
        } finally {
            jdbcAgent.close();
        }
        return resultSetToMap;
    }

    @Override
    public List findCtpAttAll(String edocId) {
        String sql = "select * from ctp_attachment where att_reference = '" + edocId + "'";
        List resultSetToList = new ArrayList();
        JDBCAgent jdbcAgent = null;
        try {
            jdbcAgent = new JDBCAgent(false, false);
            jdbcAgent.execute(sql);
            resultSetToList = jdbcAgent.resultSetToList();
        } catch (Exception e) {
            log.error("Ftp文件上传功能查询数据时出现异常:",e);
        } finally {
            jdbcAgent.close();
        }
        return resultSetToList;
    }
}
