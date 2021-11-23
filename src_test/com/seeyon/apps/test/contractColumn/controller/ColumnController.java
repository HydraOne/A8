package com.seeyon.apps.test.contractColumn.controller;

import com.alibaba.fastjson.JSONArray;
import com.seeyon.apps.test.contractColumn.pojo.RegulatoryContract;
import com.seeyon.apps.test.contractColumn.service.ColumnService;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.FlipInfo;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author sheHaiTao
 * @data 2021/10/29 - 18:51
 * @Description 栏目controller层
 */
public class ColumnController extends BaseController {

    //日志
    private static final Log log = CtpLogFactory.getLog(ColumnController.class);

    //注入service
    @Autowired
    private ColumnService columnService;

    /**
     *请求数据
     * @param request
     * @param response
     * @AjaxAccess 该接口如果想被ajax访问到,就要加这个注解
     */
    public void getColumnData(HttpServletRequest request, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        //获取监管合同参数
        String regulatoryContract = request.getParameter("regulatoryContract");
        //调用service处理
        ArrayList<RegulatoryContract> columnFormData = columnService.getColumnFormData(regulatoryContract);
        //转换成json
        Object toJSON = JSONArray.toJSON(columnFormData);
        //打印输出流
        try {
            response.getWriter().print(toJSON);
        } catch (IOException e) {
            log.error("打印输出流时出现异常:",e);
        }
    }

    //加载页面
    public ModelAndView loadPage(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        FlipInfo flipInfo = new FlipInfo();
        //获取分页条数
        String sectionCount = request.getParameter("sectionCount");
        flipInfo.setSize(Integer.parseInt(sectionCount));
        return new ModelAndView("/testContract/testContract");
    }
}
