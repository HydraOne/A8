package com.seeyon.ctp.rest.resources;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.v3x.dee.util.rest.CTPRestClient;
import com.seeyon.v3x.dee.util.rest.CTPServiceClientManager;
import org.apache.commons.logging.Log;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;


@Path("/TimedResources")
@Consumes({"application/json", "application/xml"})
@Produces({"application/json"})
public class TimedResources extends BaseResource{

    //日志
    private static final Log log = CtpLogFactory.getLog(TimedResources.class);

    String url = "http://127.0.0.1:80";
    //rest账号
    String restName = AppContext.getSystemProperty("test.restAccount");
    //rest密码
    String restPassword = AppContext.getSystemProperty("test.restPassword");
    //发起付款流程表单的编号
    String templateCode = AppContext.getSystemProperty("test.paySheetProcess");
    //流程所需参数
    Map<String, Object> params = new HashMap<>();
    Long flowId;

    @POST
    @Path("/toTimer")
    @Produces(MediaType.APPLICATION_JSON)
    public Long toTimer(String dataXml){
        //获取rest动态客户机实例
        CTPServiceClientManager ctpServiceClientManager = CTPServiceClientManager.getInstance(url);
        CTPRestClient restClient = ctpServiceClientManager.getRestClient();
        //rest登录
        restClient.authenticate(restName, restPassword);
        //设置表单模板编号,附件Long对象,发起者登录名,主题描述,传入接口的数据,参数
        params.put("templateCode",templateCode);
        params.put("attachments",new Long[]{});
        params.put("senderLoginName","zhangsan");
        params.put("subject","付款流程");
        params.put("data",dataXml);
        params.put("param","0");
        //发送post请求
        try{
            flowId = restClient.post("flow/" + templateCode, params, Long.class);
        }catch (Exception e){
            log.error("发送定时任务出错:",e);
        }

        return flowId;
    }
}
