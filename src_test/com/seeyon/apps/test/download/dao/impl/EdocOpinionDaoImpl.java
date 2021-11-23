package com.seeyon.apps.test.download.dao.impl;

import com.seeyon.apps.test.download.dao.EdocOpinionDao;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.JDBCAgent;
import org.apache.commons.logging.Log;

import java.util.List;
import java.util.Map;

public class EdocOpinionDaoImpl implements EdocOpinionDao {

    //日志
    private static final Log log = CtpLogFactory.getLog(EdocOpinionDaoImpl.class);


    @Override
    public List<Map<String, Object>> findOpinionListByUserId(String sql, Map map, FlipInfo fi) {
        JDBCAgent jdbc = new JDBCAgent();
        try {
            fi = jdbc.findNameByPaging(sql, map, fi);
        } catch (Exception e) {
            log.info("查询意见列表出现异常:",e);
        }finally {
            jdbc.close();//error
        }

        log.info("打印出的sql------"+sql);
        List<Map<String,Object>> data = fi.getData();
        return data;
    }

    @Override
    public void update(Long opinionId, String opinionContent) {
        String sql = "update ctp_comment_all set content = '" + opinionContent + "' where id = " + opinionId;
        JDBCAgent jdbcAgent = null ;
        try {
            jdbcAgent = new JDBCAgent(false,false);
            jdbcAgent.execute(sql);
        } catch (Exception e) {
            log.error("修改意见sql出现异常:",e);
        }finally {
            jdbcAgent.close();
        }
    }
}
