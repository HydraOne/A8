package com.seeyon.apps.test.payFormEvents.service;

import com.seeyon.apps.collaboration.event.CollaborationFinishEvent;
import com.seeyon.apps.collaboration.po.ColSummary;
import com.seeyon.apps.test.contractEvents.controller.ContractApi4cap4;
import com.seeyon.apps.test.contractEvents.dao.CommonDao;
import com.seeyon.apps.test.util.HttpHelper;
import com.seeyon.apps.test.util.SOAPUtil;
import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.cap4.form.bean.FormBean;
import com.seeyon.cap4.form.bean.FormTableBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.annotation.ListenEvent;
import org.apache.commons.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.util.Map;

/**
 * @author sheHaiTao
 * @data 2021/11/2 - 10:22
 * @Description 监听合同付款表流程
 */
public class PayFormEventsListener {

    //日志
    private static final Log log = CtpLogFactory.getLog(PayFormEventsListener.class);

    @Autowired
    private FormApi4Cap4 formApi4Cap4;

    @Autowired
    private ContractApi4cap4 contractApi4cap4;

    /**
     * 监听器方法,监听流程结束
     *
     * @param event 被监听的对象
     */
    @ListenEvent(event = CollaborationFinishEvent.class, async = true)
    public void eventListenerFinish(CollaborationFinishEvent event) {
        //获取summary对象
        ColSummary summary = event.getSummary();
        //用summary对象获取监听对象的表单模板id和具体记录的id
        Long formAppid = summary.getFormAppid();
        Long formRecordid = summary.getFormRecordid();
        FormBean form = formApi4Cap4.getForm(formAppid);
        FormTableBean masterTableBean = null;
        if (form != null){
            masterTableBean = form.getMasterTableBean();
        }
        if(masterTableBean != null){
            //对比监听对象的表名与实际表名
            String tableName = masterTableBean.getTableName();
            String formCode = AppContext.getSystemProperty("test.paySheetProcess");
            try {
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
                log.error("表单bean获取异常:",e);
            }
            log.info("监听到合同支付完成,进行金额累加");
            //通用mapper查询表数据
            Map data = contractApi4cap4.getData(tableName, formRecordid);
            String contractNo = String.valueOf(data.get(masterTableBean.getFieldBeanByDisplay("合同编号").getName()));
            String contractMoney = String.valueOf(data.get(masterTableBean.getFieldBeanByDisplay("已付款金额合计").getName()));
            //封装xml,传入工具类.调用webService接口进行金额累加
            String webServiceUrl = AppContext.getSystemProperty("test.webservice");
            Document document = DocumentHelper.createDocument();
            //添加一个根节点
            Element rootElement = document.addElement("formExport");
            rootElement.addAttribute("version", "2.0");
            Element valuesElement = rootElement.addElement("values");
            valuesElement.addElement("column").addAttribute("name", "合同编号").addElement("value").addText(contractNo != null ? contractNo : "");
            valuesElement.addElement("column").addAttribute("name", "付款金额").addElement("value").addText(contractMoney != null ? contractMoney : "");
            String  requestXML = document.asXML();
            String method = "updateMoney";
            HttpHelper.sendRPCClient(webServiceUrl, method, requestXML);
            //SOAP方式尝试失败
//            try {
//                SOAPUtil.soapXML(requestXML);
//            } catch (IOException | SOAPException e) {
//                log.error("soap方式发送失败",e);
//            }
        }
    }
}
