package com.seeyon.apps.test.formDataEvents.service;

import com.seeyon.cap4.form.bean.FormDataMasterBean;
import com.seeyon.cap4.form.modules.event.FormDataBeforeSubmitEvent;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.annotation.ListenEvent;
import org.apache.commons.logging.Log;

import java.util.Date;
import java.util.Map;

/**
 * @author sheHaiTao
 * @data 2021/10/30 - 17:22
 * @Description 监管合同档案修改
 */
public class FormDataEventListener {
    //日志
    private static final Log log = CtpLogFactory.getLog(FormDataEventListener.class);

    /**
     *监听合同修改
     * @param event
     */
    @ListenEvent(event = FormDataBeforeSubmitEvent.class ,async = true)
    public void monitorContractSupervisionModificationEvents(FormDataBeforeSubmitEvent event) {
        FormDataMasterBean formDataMasterBean = (FormDataMasterBean) event.getSource();
        Map<String, Object> allDataMap = formDataMasterBean.getAllDataMap();
        String contractNo = String.valueOf(allDataMap.get(formDataMasterBean.formTable.getFieldBeanByDisplay("合同编号").getName()));
        String contractTitle = String.valueOf(allDataMap.get(formDataMasterBean.formTable.getFieldBeanByDisplay("合同名称").getName()));
        log.info("合同监管修改事件：时间："+new Date()+"合同编号："+contractNo+" 合同名称："+contractTitle);
    }
}
