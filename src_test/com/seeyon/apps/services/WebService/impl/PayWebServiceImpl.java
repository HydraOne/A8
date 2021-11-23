package com.seeyon.apps.services.WebService.impl;

import com.seeyon.apps.services.WebService.PayWebService;
import com.seeyon.apps.test.payFormEvents.dao.PayFormDao;
import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.cap4.form.bean.FormTableBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * @author sheHaiTao
 * @data 2021/11/2 - 14:43
 * @Description 监听合同付款表流程
 */
public class PayWebServiceImpl implements PayWebService {

    //日志
    private static final Log log = CtpLogFactory.getLog(PayWebServiceImpl.class);

    @Autowired
    private PayFormDao payFormDao;

    @Autowired
    private FormApi4Cap4 formApi4Cap4;

    @Override
    public String updateMoney(String dataXml) {
        Map map = new HashMap();

        //解析xml，存到数据库
        //dom方式解析
        try {
            //创建DocumentBuilderFactory的对象
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            //通过DocumentBuilderFactory对象获取DocumentBuilder对象
            DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
            //DocumentBuilder 对象的parse 方法可以传入String 类型的URI来进行xml的解析，从而返回Document 对象
            StringReader sr = new StringReader(dataXml);
            InputSource is = new InputSource(sr);
            Document doc = docBuilder.parse(is);
            NodeList valueList = doc.getElementsByTagName("column");
            log.info("一共有 " + valueList.getLength() + " 个value 节点");
            for (int i = 0; i < valueList.getLength(); i++) {
                log.info("开始遍历第 " + (i + 1) + "个节点 : ");
                Element classpath = (Element) valueList.item(i);
                String name2 = classpath.getAttribute("name");
                log.info("-------属性名：name ,属性值：" + name2);
                //子节点
                NodeList childNodes = valueList.item(i).getChildNodes();
                for (int k = 0; k < childNodes.getLength(); k++) {
                    Node subNode = childNodes.item(k);
                    if (subNode.getNodeType() == Element.ELEMENT_NODE) {
                        log.info(subNode.getNodeName() + "," + name2 + "==" + subNode.getTextContent());
                        map.put(name2, subNode.getTextContent());
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析错误-------------------" , e);
        }

        //通过合同编号查出该付款信息
        //替换//
        String htNum = (String) map.get("合同编号");
        Map map2 = null;
        map2 = payFormDao.getData(htNum);
        //将累计已付金额加上
        FormTableBean masterTableBean = null;
        try {
            masterTableBean = formApi4Cap4.getFormByFormCode(AppContext.getSystemProperty("test.contractSupervisionBottom"))
                    .getMasterTableBean();
        } catch (BusinessException e) {
            log.error("获取表单异常",e);
        }
        //获取累计已付金额
        String moneyName = masterTableBean.getFieldBeanByDisplay("累计已付金额").getName();
        BigDecimal money1 = (BigDecimal)map2.get(moneyName) ;
        //替换//
        BigDecimal money2= new BigDecimal(Integer.parseInt((String) map.get("付款金额")));
        money1 =  money1.add(money2);
        String num2 = String.valueOf(money1);
        //保存更新数据库，监听底表中
        //替换//
        payFormDao.payUpdate(num2, htNum);
        return "";
    }
}
