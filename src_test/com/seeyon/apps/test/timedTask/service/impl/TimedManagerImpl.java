package com.seeyon.apps.test.timedTask.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seeyon.apps.test.timedTask.dao.TimedTaskDao;
import com.seeyon.apps.test.util.HttpUtils;
import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.cap4.form.bean.FormBean;
import com.seeyon.cap4.form.bean.FormTableBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.quartz.MutiQuartzJobNameException;
import com.seeyon.ctp.common.quartz.NoSuchQuartzJobBeanException;
import com.seeyon.ctp.common.quartz.QuartzHolder;
import com.seeyon.ctp.common.quartz.QuartzJob;
import org.apache.commons.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimedManagerImpl implements QuartzJob {

    //日志
    private static final Log log = CtpLogFactory.getLog(TimedManagerImpl.class);

    @Autowired
    TimedTaskDao timedTaskDao;

    @Autowired
    private FormApi4Cap4 formApi4Cap4;

    @Override
    public void execute(Map<String, String> parameters) {
        //
        List data = timedTaskDao.getData();
        if (data == null){
            return;
        }
        Map map = null;
        String token = null;
        String tokenString = null;
        //拿到token
        String user = AppContext.getSystemProperty("test.restAccount");
        String pwd = AppContext.getSystemProperty("test.restPassword");
        String url = AppContext.getSystemProperty("test.getTokenUrl");
        String tokenUrl = url + "/" + user + "/" + pwd;
        //get方法发送url获取token
        try {
            token = HttpUtils.sendGet(tokenUrl, "");
        } catch (Exception e) {
            log.error("获取Token出现异常:",e);
        }
        //拼接发送post的url
        Map<String, Object> map1;
        map1 = JSONObject.parseObject(token);
        tokenString = (String) map1.get("id");
        String restUrl = AppContext.getSystemProperty("test.timedPostRestUrl");
        restUrl += "?token=" + tokenString;
        //准备post接口发送的JSON数据
        String systemProperty = AppContext.getSystemProperty("test.contractSupervisionProcess");
        FormTableBean masterTableBean = null;
        try {
            masterTableBean = formApi4Cap4.getFormByFormCode(systemProperty).getMasterTableBean();
        } catch (BusinessException e) {
            log.error("获取主表Bean出现异常:",e);
        }
        for (Object datum : data) {
            map = (Map) datum;
            //获取合同名称 ，合同编号，合同金额
            String contractName = String.valueOf(map.get(masterTableBean.getFieldBeanByDisplay("合同名称").getName()));
            String contractNo = String.valueOf(map.get(masterTableBean.getFieldBeanByDisplay("合同编号").getName()));
            String contractMoney = String.valueOf(map.get(masterTableBean.getFieldBeanByDisplay("合同金额").getName()));
            FormBean formBean = null;
            try {
                formBean = formApi4Cap4.getFormByFormCode(systemProperty);
            } catch (BusinessException e) {
                log.error("获取主表Bean出现异常:",e);
            }
            //document
            Document document = DocumentHelper.createDocument();
            //添加一个节点
            Element rootElement = document.addElement("formExport");
            //添加属性
            rootElement.addAttribute("version", "2.0");
            //添加子节点summary
            Element summaryElement = rootElement.addElement("summary");
            //子节点添加属性
            summaryElement.addAttribute("id", formBean.getId().toString());
            summaryElement.addAttribute("name", formBean.getMasterTableBean().getTableName());
            //添加子节点values
            Element valuesElement = rootElement.addElement("values");
            valuesElement.addElement("column").addAttribute("name", "合同编号").addElement("value").addText(contractNo != null ? contractNo : "");
            valuesElement.addElement("column").addAttribute("name", "合同名称").addElement("value").addText(contractName != null ? contractName : "");
            valuesElement.addElement("column").addAttribute("name", "合同金额").addElement("value").addText(contractMoney != null ? contractMoney : "");
            valuesElement.addElement("subForms");
            HttpUtils.sendPost(restUrl, document.asXML());
        }
    }

    /**
     *
     * 初始化定时器
     */
    public void init() {
        String startTime = AppContext.getSystemProperty("test.timeQuartzJob");
        //初始化删除同名的可能未清除的任务
        if(QuartzHolder.hasQuartzJob("每日付款计划")){
            QuartzHolder.deleteQuartzJob("每日付款计划");
            log.info("已存在的每日付款计划已经删除");
        }
        Map<String, String>  parameters = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date parseTime = null;
        try {
            parseTime = simpleDateFormat.parse(startTime);
        } catch (ParseException e) {
            log.error("配置的日期转换异常",e);
        }
        try {
            //每天定时
            QuartzHolder.newQuartzJobPerDay(null, "每日付款计划", parseTime, "TimedManagerImpl", parameters);
            //测试用20秒循环定时
//            QuartzHolder.newQuartzJob("每日付款计划", parseTime, 1000*20, "TimedManagerImpl", parameters);
        } catch (MutiQuartzJobNameException | NoSuchQuartzJobBeanException e) {
            log.error("定时任务创建异常", e);
        }
    }


//    //timeTask调用的方法
//    public void getTimer() {
//        List data = timedTaskDao.getData();
//        if (data == null){
//            return;
//        }
//        Map map = null;
//        String token = null;
//        String tokenString = null;
//        //拿到token
//        String user = AppContext.getSystemProperty("test.restAccount");
//        String pwd = AppContext.getSystemProperty("test.restPassword");
//        String url = AppContext.getSystemProperty("test.getTokenUrl");
//        String tokenUrl = url + "/" + user + "/" + "/" + pwd;
//        //get方法发送url获取token
//        try {
//            token = HttpUtils.sendGet(tokenUrl, "");
//        } catch (Exception e) {
//            log.error("获取Token出现异常:",e);
//        }
//        //拼接发送post的url
//        Map<String, Object> map1;
//        map1 = JSONObject.parseObject(token);
//        tokenString = (String) map1.get("id");
//        String restUrl = AppContext.getSystemProperty("test.timedPostRestUrl");
//        restUrl += "?token=" + tokenString;
//        //准备post接口发送的JSON数据
//        String systemProperty = AppContext.getSystemProperty("test.contractSupervisionProcess");
//        FormTableBean masterTableBean = null;
//        try {
//            masterTableBean = formApi4Cap4.getFormByFormCode(systemProperty).getMasterTableBean();
//        } catch (BusinessException e) {
//            log.error("获取主表Bean时出现异常:",e);
//        }
//        for (Object datum : data) {
//            map = (Map) datum;
//            //获取合同名称 ，合同编号，合同金额
//            String contractName = String.valueOf(map.get(masterTableBean.getFieldBeanByDisplay("合同名称").getName()));
//            String contractNo = String.valueOf(map.get(masterTableBean.getFieldBeanByDisplay("合同编号").getName()));
//            String contractMoney = String.valueOf(map.get(masterTableBean.getFieldBeanByDisplay("合同金额").getName()));
//            FormBean formBean = null;
//            try {
//                formBean = formApi4Cap4.getFormByFormCode(systemProperty);
//            } catch (BusinessException e) {
//                log.error("获取主表Bean时出现异常:",e);
//            }
//            //document
//            Document document = DocumentHelper.createDocument();
//            //添加一个节点
//            Element rootElement = document.addElement("formExport");
//            rootElement.addAttribute("version", "2.0");
//            Element summaryElement = rootElement.addElement("summary");
//            summaryElement.addAttribute("id", formBean.getId().toString());
//            summaryElement.addAttribute("name", formBean.getMasterTableBean().getTableName());
//            Element valuesElement = rootElement.addElement("values");
//            valuesElement.addElement("column").addAttribute("name", "合同编号").addElement("value").addText(contractNo != null ? contractNo : "");
//            valuesElement.addElement("column").addAttribute("name", "合同名称").addElement("value").addText(contractName != null ? contractName : "");
//            valuesElement.addElement("column").addAttribute("name", "合同金额").addElement("value").addText(contractMoney != null ? contractMoney : "");
//            valuesElement.addElement("subForms");
//            HttpUtils.sendPost(restUrl, document.asXML());
//        }
//    }
//
//    //timeTask方式的init方法
//    public void init() {
//        log.info("timeTask的定时任务init方法已经进入了");
//        //任务定时器
//        Timer timer = new Timer();
//        //参数：
//        //task - 要安排的任务。
//        //delay - 执行任务之前的延迟（以毫秒为单位）。
//        //period - 连续任务执行之间的时间（以毫秒为单位）
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //调用发起流程的方法
//                try {
//                    getTimer();
//                }catch (Exception e) {
//                    log.error("定时任务异常",e);
//                }
//            }
//            //1000 * 60 *60 * 24
//        },1000,1000 * 30);
//        log.info("timeTask的定时任务已经启动了");
//    }
}