package com.seeyon.apps.test.paymentCalendar.service;


import com.seeyon.apps.collaboration.event.CollaborationStartEvent;
import com.seeyon.apps.collaboration.po.ColSummary;
import com.seeyon.apps.test.contractEvents.controller.ContractApi4cap4;
import com.seeyon.apps.test.contractEvents.dao.CommonDao;
import com.seeyon.apps.test.paymentCalendar.dao.FormTimeViewMapper;
import com.seeyon.apps.timeview.po.TimeViewAuth;
import com.seeyon.apps.timeview.po.TimeViewInfo;
import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.cap4.form.bean.FormBean;
import com.seeyon.cap4.form.bean.FormTableBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.annotation.ListenEvent;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import www.seeyon.com.utils.UUIDUtil;

import java.util.Date;
import java.util.Map;

/**
 * @author sheHaiTao
 * @data 2021/11/3 - 9:37
 * @Description 合同流程结束的事件监听，监听合同档案登记表单，获取申请日期
 */
public class ContractEndEventsListener {

    //日志
    private static final Log log = CtpLogFactory.getLog(ContractEndEventsListener.class);

    //视图表单mapper
    @Autowired
    private FormTimeViewMapper formTimeViewMapper;

    @Autowired
    private FormApi4Cap4 formApi4Cap4;
    //组织管理对象
    @Autowired
    private OrgManager orgManager;

    @Autowired
    private ContractApi4cap4 contractApi4cap4;

    //监听合同档案管理开始事件，同步执行
    @ListenEvent(event = CollaborationStartEvent.class,async = true)
    //协同结束立刻执行，异步模式，监听代码出现异常不影响协同发起，但如果协同发起自身事务回滚，监听代码仍然会执行
    public void addEventView(CollaborationStartEvent event){
        //获取summary对象
        ColSummary summary = event.getSummary();
        //获取表单模板id
        Long formAppId = summary.getFormAppid();
        //获取具体该条数据的id
        Long formRecordId = summary.getFormRecordid();
        //通过表单模板id取到具体的表单
        FormBean form = formApi4Cap4.getForm(formAppId);
        if (form!=null) {
            //拿到主表
            FormTableBean masterTableBean = form.getMasterTableBean();
            if (masterTableBean!=null) {
                try {
                    if (!masterTableBean.
                            getTableName().
                            equals(formApi4Cap4.
                                    getFormByFormCode(AppContext.getSystemProperty("test.contractSupervisionProcess")).
                                    getMasterTableBean().
                                    getTableName())) {
                        return;
                    }
                } catch (BusinessException e) {
                    log.error("接口获取formBean出现异常:",e);
                }
                //从数据库获取数据
                Map resultMap = contractApi4cap4.getData(masterTableBean.getTableName(), formRecordId);
                //uuid
                long uuidLong = UUIDUtil.getUUIDLong();
                //时间视图表
                TimeViewInfo timeViewInfo = new TimeViewInfo();
                timeViewInfo.setId(uuidLong);
                timeViewInfo.setAppId(summary.getId());
                timeViewInfo.setAppTitle(summary.getSubject());
                //此处字段为源码中添加的
                timeViewInfo.setAppCategory("register");
                timeViewInfo.setStartTime(new Date());
                timeViewInfo.setEndTime((Date) resultMap.get(masterTableBean.getFieldBeanByDisplay("申请日期").getName()));
                timeViewInfo.setCreateMember(Long.valueOf(
                        String.valueOf(resultMap.get(masterTableBean.getFieldBeanByDisplay("经办人").getName()))));
                timeViewInfo.setCreateTime(new Date());
                timeViewInfo.setUpdateMember(Long.valueOf(
                        String.valueOf(resultMap.get(masterTableBean.getFieldBeanByDisplay("经办人").getName()))));
                timeViewInfo.setUpdateTime(new Date());
                //设置单位id
                timeViewInfo.setAccountId(orgManager.getGroupAdmin().getOrgAccountId());
                timeViewInfo.setAppStatus(3);
                timeViewInfo.setProjectId(-1L);
                timeViewInfo.setRemindTime(0L);
                timeViewInfo.setTimeViewStatus(0);
                formTimeViewMapper.addTimeViewInfo(timeViewInfo);

                TimeViewAuth timeViewAuth = new TimeViewAuth();
                timeViewAuth.setNewId();
                timeViewAuth.setAppId(summary.getId());
                timeViewAuth.setTimeViewId(uuidLong);
                timeViewAuth.setOrgentId(summary.getStartMemberId());
                timeViewAuth.setOrgentType(3);
                timeViewAuth.setAuthType(0);
                timeViewAuth.setRoleType("0");
                formTimeViewMapper.addTimeViewAuth(timeViewAuth);
            }
        }
    }
}
