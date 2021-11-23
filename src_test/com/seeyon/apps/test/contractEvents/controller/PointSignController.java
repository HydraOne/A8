package com.seeyon.apps.test.contractEvents.controller;

import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Base64;
import com.seeyon.ctp.util.annotation.NeedlessCheckLogin;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

/**
 * @author sheHaiTao
 * @data 2021/10/28 - 10:37
 * @Description 单点登录
 */
public class PointSignController extends BaseController {

    //日志
    private static final Log log = CtpLogFactory.getLog(PointSignController.class);

    //注入组织架构管理类
    @Autowired
    private OrgManager orgManager;

    /**
     * 单点登录
     * @param request 请求
     * @param response 响应
     */
    @NeedlessCheckLogin
    public void pointSign(HttpServletRequest request, HttpServletResponse response){
        //从请求获得url传入的LoginId
        String loginId = request.getParameter("LoginId");
        String loginName = null;
        try {
            //根据登录的id查询用户的id,根据用户id获取登录名称
            V3xOrgMember memberById = orgManager.getMemberById(Long.valueOf(loginId));
            loginName = memberById.getLoginName();
            if (loginName != null){
                //url需要使用随机的ticket,防止失效
                log.info("单点登录时,获得用户名:"+loginName);
                String url = "/seeyon/login/sso?from=oasso&ticket=" + Base64.encodeString(loginName+","+new Random().nextInt());
                //重定向资源
                response.sendRedirect(url);
            }
        } catch (BusinessException | IOException e) {
            log.error("单点登录异常:", e);
        }
    }
}
