package com.seeyon.apps.test.getAccount;

import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author sheHaiTao
 * @data 2021/11/13 - 15:07
 * 根据用户id获取类名的接口
 */
public class UserAccount extends BaseController {
    @Autowired
    OrgManager orgManager;

    private static final Log log = CtpLogFactory.getLog(UserAccount.class);

    @AjaxAccess
    public ModelAndView selectUserAccount(HttpServletRequest request , HttpServletResponse response){
        String id = request.getParameter("id");
        String loginName= null;
        try {
            V3xOrgMember memberById = orgManager.getMemberById(Long.valueOf(id));
            loginName = memberById.getLoginName();
        } catch (BusinessException e) {
            log.error("用户查找出现异常:",e);
        }
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("account",loginName);
        return new ModelAndView(new MappingJackson2JsonView()).addAllObjects(resultMap);
    }
}
