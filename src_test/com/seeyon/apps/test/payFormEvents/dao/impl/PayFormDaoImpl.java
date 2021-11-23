package com.seeyon.apps.test.payFormEvents.dao.impl;

import com.seeyon.apps.test.payFormEvents.dao.PayFormDao;
import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.cap4.form.bean.FormTableBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.JDBCAgent;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class PayFormDaoImpl implements PayFormDao {

    //日志
    private static final Log log = CtpLogFactory.getLog(PayFormDaoImpl.class);

    @Autowired
    private FormApi4Cap4 formApi4Cap4;

    @Override
    public Map getData(String contractNum) {
        FormTableBean table = null;
        try {
            table = formApi4Cap4.getFormByFormCode(AppContext.getSystemProperty("test.contractSupervisionBottom")).getMasterTableBean();
        } catch (BusinessException e) {
            log.error("接口获取表单bean出现异常:",e);
        }
        String contractCode = table.getFieldBeanByDisplay("合同编号").getName();
        String sql = "select * from " + table.getTableName() + " where "+ contractCode +" = '" + contractNum +"'";
        JDBCAgent agent = new JDBCAgent(false, false);
        Map map2 = new HashMap();
        try {
            agent.execute(sql);
            map2 = agent.resultSetToMap();
        } catch (Exception e) {
            log.error("查询付款信息错误:", e);
        } finally {
            agent.close();
        }
        return map2;
    }

    @Override
    public void payUpdate(String num, String contractNum) {
        FormTableBean table = null;
        try {
            table = formApi4Cap4.getFormByFormCode(AppContext.getSystemProperty("test.contractSupervisionBottom")).getMasterTableBean();
        } catch (BusinessException e) {
            log.error("获取表单bean异常:",e);
        }
        String contractCode = table.getFieldBeanByDisplay("合同编号").getName();
        String amount = table.getFieldBeanByDisplay("累计已付金额").getName();
        String sql = "update " + table.getTableName() + " set "+ amount + " = " + num + " where "+ contractCode +" = '" + contractNum + "'";
        JDBCAgent agent = null ;
        try{
            agent = new JDBCAgent(false, false);
            agent.execute(sql);
        } catch (Exception e) {
            log.error("添加累计金额错误:", e);
        }finally {
            agent.close();
        }
    }
}
