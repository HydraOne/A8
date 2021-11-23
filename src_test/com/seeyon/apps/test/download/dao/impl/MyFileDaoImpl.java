package com.seeyon.apps.test.download.dao.impl;

import com.seeyon.apps.test.download.dao.MyFileDao;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.JDBCAgent;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MyFileDaoImpl implements MyFileDao {

    //日志
    private static final Log log = CtpLogFactory.getLog(MyFileDaoImpl.class);


    @Override
    public List getFile(Long id) throws BusinessException {

        String sql = "select * from ctp_attachment where SUB_REFERENCE = " + id;
        List list = jdbcSelectFile(sql);
        return list;
    }

    /**
     * 传入sql返回多个数据集合
     *
     * @param sql
     */
    public List jdbcSelectFile(String sql) {
        JDBCAgent jdbcAgent = new JDBCAgent(false, false);
        List list = null;
        try {
            jdbcAgent.execute(sql);
            list = jdbcAgent.resultSetToList();
        } catch (BusinessException | SQLException e) {
            log.error("文件相关JDBC查询错误:", e);
        }finally {
            jdbcAgent.close();
        }
        return list;
    }


    @Override
    public String getFileType(Long id) throws BusinessException {
        String sql = "select * from ctp_attachment where SUB_REFERENCE = " + id;
        Map map = jdbcSelectFileType(sql);
        String mimeType = (String) map.get("mime_type");
        String[] split = mimeType.split("/");
        return split[1];
    }


    /**
     * 传入sql返回Map集合
     *
     * @param sql
     */
    public Map jdbcSelectFileType(String sql) {
        JDBCAgent jdbcAgent = new JDBCAgent(false, false);
        Map map = null;
        try {
            jdbcAgent.execute(sql);
            map = jdbcAgent.resultSetToMap();
        } catch (BusinessException | SQLException e) {
            log.error("文件类型JDBC查询错误:", e);
        }finally {
            jdbcAgent.close();
        }
        return map;
    }




}
