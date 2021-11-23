package com.seeyon.apps.test.contractEvents.service;

import com.alibaba.fastjson.JSONObject;
import com.seeyon.apps.collaboration.event.CollaborationFinishEvent;
import com.seeyon.apps.collaboration.po.ColSummary;
import com.seeyon.apps.test.contractEvents.controller.ContractApi4cap4;
import com.seeyon.apps.test.contractEvents.dao.CommonDao;
import com.seeyon.apps.test.util.HttpUtils;
import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.cap4.form.bean.FormBean;
import com.seeyon.cap4.form.bean.FormFieldBean;
import com.seeyon.cap4.form.bean.FormTableBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.annotation.ListenEvent;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


/**
 * @author sheHaiTao
 * @data 2021/10/27 - 11:07
 * @Description 监听合同登记流程
 */
public class ContractFormEventListener {

    //日志
    private static final Log log = CtpLogFactory.getLog(ContractFormEventListener.class);


    @Autowired
    private  CommonDao commonDao;
    @Autowired
    private FormApi4Cap4 formApi4Cap4;

    /**
     * 监听器方法,监听流程结束
     * @param event 被监听的对象
     */
    @ListenEvent(event = CollaborationFinishEvent.class, async = true)
    //CollaborationFinishEvent   流程结束监听
    public void eventListenerFinish(CollaborationFinishEvent event){
        //获取summary对象
        ColSummary summary = event.getSummary();
        //用summary对象获取监听对象的表单模板id和具体记录的id
        Long formAppId = summary.getFormAppid();
        Long formRecordId = summary.getFormRecordid();
        FormBean form = formApi4Cap4.getForm(formAppId);
        FormTableBean masterTableBean = null;
        if (form != null){
            masterTableBean = form.getMasterTableBean();
        }
        if (masterTableBean != null){
            //监听对象的表名与实际表名做对比,如果两表一样,则继续,不一样则return
            String tableName = masterTableBean.getTableName();
            try {
                String formCode = AppContext.getSystemProperty("test.contractSupervisionProcess");
                FormBean formByFormCode = formApi4Cap4.getFormByFormCode(formCode);
                if (formByFormCode == null){
                    log.info("模板编号未配置");
                    return;
                }
                String masterTableName = formByFormCode.getMasterTableBean().getTableName();
                if (!tableName.equals(masterTableName)){
                    log.info("配置的表与监听对象表不一致");
                    return;
                }
            } catch (BusinessException e) {
                log.error("获取formBean异常:",e);
            }
            //传入tableName和表记录id,查询表数据
            Map resultMap = commonDao.getData(tableName, formRecordId);
            //将数据存入dateMap集合
            HashMap<String, Object> dateMap = new HashMap<>();
            dateMap.put("经办人", resultMap.get(masterTableBean.getFieldBeanByDisplay("经办人").getName()));
            dateMap.put("经办部门", resultMap.get(masterTableBean.getFieldBeanByDisplay("经办部门").getName()));
            dateMap.put("申请日期", resultMap.get(masterTableBean.getFieldBeanByDisplay("申请日期").getName()));
            dateMap.put("合同编号", resultMap.get(masterTableBean.getFieldBeanByDisplay("合同编号").getName()));
            dateMap.put("合同名称", resultMap.get(masterTableBean.getFieldBeanByDisplay("合同名称").getName()));
            dateMap.put("合同金额", resultMap.get(masterTableBean.getFieldBeanByDisplay("合同金额").getName()));
            dateMap.put("累计已付金额", resultMap.get(masterTableBean.getFieldBeanByDisplay("累计已付金额").getName()));
            String ssoLoginUrl = AppContext.getSystemProperty("test.ssoLoginUrl");
            FormFieldBean name = masterTableBean.getFieldBeanByDisplay("经办人");
            String name1 = name.getName();
            dateMap.put("单点登录", ssoLoginUrl + "?method=pointSign&LoginId=" + resultMap.get(name1));
            //拼接url
            String tokenUrl = AppContext.getSystemProperty("test.getTokenUrl");
            tokenUrl += "/" + AppContext.getSystemProperty("test.restAccount") + "/" + AppContext.getSystemProperty("test.restPassword");
            //用工具类的get方法获取token
            String token = null;
            try {
                token = HttpUtils.sendGet(tokenUrl, "");
            } catch (Exception e) {
                log.error("获取Token出现异常:",e);
            }
            //转换数据类型
            Map<String, Object> map;
            map = JSONObject.parseObject(token);
            String tokenString = (String) map.get("id");
            String formJSONString = JSONObject.toJSONString(dateMap);
            //带上token调rest接口
            String postUrl = AppContext.getSystemProperty("test.postRestUrl");
            postUrl += "?token=" + tokenString;
            //发送post请求
            HttpUtils.sendPost(postUrl, formJSONString);
        }
    }
}
