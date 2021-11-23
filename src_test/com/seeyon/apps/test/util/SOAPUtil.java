package com.seeyon.apps.test.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.soap.SOAPException;

import com.seeyon.ctp.common.AppContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author 作者 : hehaitao
 * @version 创建时间：2020年05月06日 下午5:34:39
 * 类说明
 */

public class SOAPUtil {
    private static final Log logger = LogFactory.getLog(SOAPUtil.class);

    /**
     * 调用SAP组织架构接口，并获取响应报文
     *
     * @param requestXML ：请求报文
     * @return 返回的响应报文
     * @throws IOException
     * @throws SOAPException
     */
    public static String soapXML(String requestXML) throws IOException, SOAPException {
        String systemProperty = "";
        systemProperty = AppContext.getSystemProperty("test.webservice");
        //String replace = systemProperty.replace("|", "&");
        StringBuilder responseXML = new StringBuilder();
        //1：创建服务地址
        URL url = new URL(systemProperty);
        //2：打开到服务地址的一个连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //3：设置连接参数
        //3.1设置发送方式：POST必须大写
        connection.setRequestMethod("POST");
        //3.2设置数据格式：Content-type
        connection.setRequestProperty("Content-type", "text/xml; charset=UTF-8");
//	         connection.setRequestProperty("SOAPAction", "urn:updateMoney");
//	         connection.setRequestProperty("Connection", "Keep-Alive");
        //3.3设置输入输出，新创建的connection默认是没有读写权限的，
//	         String headerKey = "Authorization";
//	         String userInfo = CommonUtil.SAP_USER_NAME+ ":"+ CommonUtil.SAP_PASSWORD;
//	         String headerValue = Base64Util.encode(userInfo);
//	         connection.setRequestProperty(headerKey,"Basic " + headerValue);
        connection.setDoInput(true);
        connection.setDoOutput(true);


        //4：组织SOAP协议数据，发送给服务端
        OutputStream os = connection.getOutputStream();
        byte[] b = new byte[0];
        b = requestXML.getBytes("utf-8");
        os.write(b);

        //5：接收服务端的响应
        //需求6尝试过包括传入值直接用string字符串(dom4j版本过低或者io流的问题);connection设置SOAPAction,Connection;链接地址从127.0.0.1改为本机的192.168.229.1;
        //总结就是用不成
        int responseCode = connection.getResponseCode();
        if (200 != responseCode) {
            logger.error("连接webservice接口异常！！--错误码为：" + responseCode);
            return null;
        }
        if (200 == responseCode) {//表示服务端响应成功
            InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "utf-8");
            BufferedReader br = new BufferedReader(isr);

            String temp = null;

            while (null != (temp = br.readLine())) {
                responseXML.append(temp);
            }
            isr.close();
            br.close();
        }
        return responseXML.toString();

    }

}
