package com.seeyon.apps.test.timedTask.dao.impl;

import com.seeyon.apps.test.timedTask.dao.TimedTaskDao;
import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.JDBCAgent;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import com.seeyon.cap4.form.bean.FormTableBean;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TimedTaskDaoImpl implements TimedTaskDao {

    //日志
    private static final Log log = CtpLogFactory.getLog(TimedTaskDaoImpl.class);
    //注入cap4
    @Autowired
    private FormApi4Cap4 formApi4Cap4;


    @Override
    public List getData() {
        //返回明细表集合
        List result = null;
        //返回主表集合
        List master = null;
        //主表bean
        FormTableBean masterTableBean = null;
        //当日需提醒付款的数据集合
        List<FormTableBean> current = null;
        //子表bean
        List<FormTableBean> subTableBean = null;
        //获得表
        String systemProperty = AppContext.getSystemProperty("test.contractSupervisionProcess");
        try {
            //获取明细表的付款时间是今天的集合，只要集合有值，就需要拿主表信息去提醒
            subTableBean = formApi4Cap4.getFormByFormCode(systemProperty).getSubTableBean();
        } catch (BusinessException e) {
            log.error("子表获取出现异常:",e);
        }
        //取到数据库与当前日相同数据，使用DATE()获取时间中日期部分，得到每日时间条件下的数据
        FormTableBean subBean = subTableBean.get(0);
        String name = subBean.getFieldBeanByDisplay("预订合同-付款日期").getName();
        String tableName = subBean.getTableName();
        String sql = "SELECT * FROM " + tableName + " WHERE DATE(" + name + ") = DATE(NOW())";
        JDBCAgent jdbcAgent = new JDBCAgent(false, false);
        try {
            jdbcAgent.execute(sql);
            result = jdbcAgent.resultSetToList();
        } catch (BusinessException | SQLException e) {
            log.error("JDBC查询时出现异常:",e);
        }finally{
            jdbcAgent.close();
        }
        //循环拼接当日相同数据
        if(result != null && result.size()>0){
            int size = result.size(),i=0;
            StringBuilder sb  = new StringBuilder();
            for (Object test : result) {
                i++;
                Map temp = (Map) test;
                String formMainId =String.valueOf(temp.get("formmain_id"));
                sb.append("'").append(formMainId).append("'");
                if (i<size){
                    sb.append(",");
                }
            }
            String ids = "(" + sb.toString() + ")";
            //获取主表bean,查询主表中id是当日的数据,返回
            try {
                masterTableBean = formApi4Cap4.getFormByFormCode(systemProperty).getMasterTableBean();
            } catch (BusinessException e) {
                log.error("获取主表Bean出现异常:",e);
            }
            String sql2 = "SELECT * FROM " + masterTableBean.getTableName() + " WHERE ID in "+ids;
            JDBCAgent jdbcAgent1 = new JDBCAgent(false, false);
            try {
                jdbcAgent1.execute(sql2);
                master = jdbcAgent1.resultSetToList();
            } catch (BusinessException | SQLException e) {
                log.error("查询数据出现异常:",e);
            }finally {
                jdbcAgent1.close();
            }
        }
        return master;
    }
}
