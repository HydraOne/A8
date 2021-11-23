package com.seeyon.apps.test.contractColumn.dao.impl;

import com.seeyon.apps.test.contractColumn.dao.ColumnMapper;
import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.cap4.form.bean.FormBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.JDBCAgent;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

public class ColumnMapperImpl implements ColumnMapper {
    //日志
    private static final Log log = CtpLogFactory.getLog(ColumnMapperImpl.class);
    //注入表单操作类
    @Autowired
    private FormApi4Cap4 formApi4Cap4;

    @Override
    public FlipInfo queryContractInformation(FlipInfo flipInfo) {
        //需要sql语句,表单对象,分页对象,JDBC对象
        String sql="";
        FormBean formBean = null;
        FlipInfo byPaging = null;
        JDBCAgent jdbcAgent = null;
        try {
            //拿到合同底表表名
            String systemProperty = AppContext.getSystemProperty("test.contractSupervisionBottom");
            formBean = formApi4Cap4.getFormByFormCode(systemProperty);
            //拼接sql(查询经办人,经办部门,申请日期,合同编号,合同名称,合同金额,合同已付金额,单点登录链接);
            sql = "select m.`NAME` AS "+
                    formBean.getMasterTableBean().getFieldBeanByDisplay("经办人").getName() + "," +
                    formBean.getMasterTableBean().getFieldBeanByDisplay("经办部门").getName() + "," +
                    formBean.getMasterTableBean().getFieldBeanByDisplay("申请日期").getName() + "," +
                    formBean.getMasterTableBean().getFieldBeanByDisplay("合同编号").getName() + "," +
                    formBean.getMasterTableBean().getFieldBeanByDisplay("合同名称").getName() + "," +
                    formBean.getMasterTableBean().getFieldBeanByDisplay("合同金额").getName() + "," +
                    formBean.getMasterTableBean().getFieldBeanByDisplay("累计已付金额").getName() + "," +
                    formBean.getMasterTableBean().getFieldBeanByDisplay("单点登录").getName() +
                    " from "+
                    formBean.getMasterTableBean().getTableName() +
                    " AS contractName " +
                    " , org_member AS m " +
                    " , org_unit AS u "+
                    " where " +
                    formBean.getMasterTableBean().getFieldBeanByDisplay("经办人").getName() + " = m.ID " +
                    "AND " +
                    formBean.getMasterTableBean().getFieldBeanByDisplay("经办部门").getName() + " = u.ID  ";
            //创建JDBC对象查询
            jdbcAgent = new JDBCAgent();
            //查询后分页显示
            byPaging = jdbcAgent.findByPaging(sql, flipInfo);
            //如果结果不为空就返回分页数据集
            if (byPaging != null){
                return byPaging;
            }
        } catch (BusinessException | SQLException e) {
            log.error("查询登记表数据时出现异常:",e);
        } finally {
            jdbcAgent.close();
        }
        return null;
    }
}
