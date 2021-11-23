package com.seeyon.apps.test.download.controller;

import com.seeyon.apps.test.download.service.OpinionListManager;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.Strings;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 意见controller
 */
public class OpinionController extends BaseController {

    //日志
    private static final Log log = CtpLogFactory.getLog(OpinionController.class);

    @Autowired
    OpinionListManager opinionListManager;

    /**
     * 进行页面查看的跳转
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView findOpinion(HttpServletRequest request, HttpServletResponse response) {
        log.info("开始查看意见的时间--------start-------: " + System.currentTimeMillis());
        ModelAndView modelAndView = new ModelAndView("edoc/opinionList");
        return modelAndView;
    }

    /**
     * 进行页面修改的跳转
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView updateOpinion(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("edoc/updateOpinion");
        return modelAndView;
    }

    /**
     * 修改意见
     * @param request
     * @param response
     * @return
     */
    public ModelAndView updateOpinionData(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        Map map = new HashMap<>();
        //意见id
        long opinionId = Strings.isBlank(request.getParameter("id")) ? -1 : Long.parseLong(request.getParameter("id"));
        //修改的意见内容
        String opinionContent = Strings.isBlank(request.getParameter("opinion")) ? "" : request.getParameter("opinion");
        try{
            opinionListManager.updateOpinionData(opinionId, opinionContent);
            //返回json
            map.put("message", 0);
        }catch (Exception e){
            map.put("message", 1);
            log.error("修改意见出现异常:", e);
        }
        return new ModelAndView(new MappingJackson2JsonView(), map);
    }

}
