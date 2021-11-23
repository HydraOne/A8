package com.seeyon.ctp.rest.resources;

import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.cap4.form.api.FormDataApi4Cap4;
import com.seeyon.cap4.form.bean.FormBean;
import com.seeyon.cap4.form.bean.FormDataBean;
import com.seeyon.cap4.form.bean.FormDataMasterBean;
import com.seeyon.cap4.form.bean.FormTableBean;
import com.seeyon.cap4.form.service.CAP4FormManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.content.mainbody.MainbodyManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.po.content.CtpContentAll;
import com.seeyon.ctp.form.modules.engin.base.formData.FormDataDAO;
import com.seeyon.ctp.util.annotation.RestInterfaceAnnotation;
import com.seeyon.ctp.util.json.JSONUtil;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import www.seeyon.com.utils.UUIDUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sheHaiTao
 * @data 2021/10/26 - 9:54
 * @Description rest接口类
 */
@Path("RegulationContractResources")
@Consumes({"application/json", "application/xml"})
@Produces({"application/json"})
public class RegulationContractResources extends BaseResource {
    //日志
    private static final Log log = CtpLogFactory.getLog(RegulationContractResources.class);

    //注入FormApi4cap4
    private final FormApi4Cap4 formApi4Cap4 = (FormApi4Cap4) AppContext.getBean("formApi4Cap4");

    //注入formDateDao  注意层级调用关系
//    private final FormDataDAO formDataDAO = (FormDataDAO) AppContext.getBean("formDataDAO");

    //注入主表manager
    private final MainbodyManager mainbodyManager = (MainbodyManager) AppContext.getBean("ctpMainbodyManager");

    /**
     * 自定义的rest接口,监听器触发事件调用该接口
     * @param formJsonString 接口传入的表单数据
     * @throws BusinessException 抛出异常(FormApi4Cap4查询表单接口)
     */
    @POST
    @Path("postContract")
    @Produces(MediaType.APPLICATION_JSON)
    public void postContract(Map<String, Object> formJsonString) {
        if (formJsonString == null){
            log.info("传入的表单数据为空");
            return;
        }
        //先获得主键id
        long uuid = UUIDUtil.getUUIDLong();
        //获取监管合同表单编号准备存入
        String formNumber = AppContext.getSystemProperty("test.contractSupervisionBottom");
        //通过表单编号查询表单(FormApi4Cap4的方法)
        FormBean formBean = null;
        try {
            formBean = formApi4Cap4.getFormByFormCode(formNumber);
        } catch (BusinessException e) {
            log.error("rest接口内获取formBean出现异常:",e);
        }
        //初始化表名
        String tableName = null;
        //查询出的表单如果不为空则继续操作
        if (formBean != null) {
            //获取主表的bean
            FormTableBean masterTableBean = formBean.getMasterTableBean();
            //设置表名
            tableName = masterTableBean.getTableName();
            if (masterTableBean != null) {
                //创建数据使用的集合
                Map<String, Object> sqlMap = new HashMap<>();
                sqlMap.put("id", uuid);
                sqlMap.put("state", "1");
                sqlMap.put("start_member_id", formJsonString.get("经办人"));
                sqlMap.put("start_date", new Date());
                sqlMap.put("approve_member_id", formJsonString.get("经办人"));
                sqlMap.put("approve_date", new Date());
                sqlMap.put("sort", 2);
                sqlMap.put("modify_date", new Date());
                sqlMap.put(masterTableBean.getFieldBeanByDisplay("经办人").getName(), formJsonString.get("经办人"));
                sqlMap.put(masterTableBean.getFieldBeanByDisplay("经办部门").getName(), formJsonString.get("经办部门"));
                sqlMap.put(masterTableBean.getFieldBeanByDisplay("申请日期").getName(), formJsonString.get("申请日期"));
                sqlMap.put(masterTableBean.getFieldBeanByDisplay("合同编号").getName(), formJsonString.get("合同编号"));
                sqlMap.put(masterTableBean.getFieldBeanByDisplay("合同名称").getName(), formJsonString.get("合同名称"));
                sqlMap.put(masterTableBean.getFieldBeanByDisplay("合同金额").getName(), formJsonString.get("合同金额"));
                sqlMap.put(masterTableBean.getFieldBeanByDisplay("累计已付金额").getName(), formJsonString.get("累计已付金额"));
                sqlMap.put(masterTableBean.getFieldBeanByDisplay("单点登录").getName(), formJsonString.get("单点登录"));
                ArrayList<Map<String, Object>> list = new ArrayList<>();
                list.add(sqlMap);
                //设置CtpContentAll表关系（关联）固定写法不可修改
                CtpContentAll contentAll = new CtpContentAll();
                contentAll.setContentDataId(uuid);
                contentAll.setModuleId(uuid);
                contentAll.setModuleTemplateId(formBean.getMasterTableBean().getId());
                contentAll.setNewId();
                contentAll.setModuleType(42);
                contentAll.setContentType(20);
                contentAll.setSort(0);
                contentAll.setModifyId(-1L);
                contentAll.setCreateDate(new Date());
                contentAll.setModifyDate(new Date());
                contentAll.setContentTemplateId(formBean.getMasterTableBean().getFormId());
                contentAll.setTitle("保存合同至底单");
                //存入表关系
                try {
                    mainbodyManager.saveOrUpdateContentAll(contentAll);
                } catch (BusinessException e) {
                    log.error("主表manager存入数据错误:",e);
                }
                //存入数据库
                try {
                    //formDataDAO对象调用insertData方法,传入表名,数据集,设置是否立即更新为false
                    //接口之间通信需要调用api层
                    FormDataMasterBean formDataMasterBean = new FormDataMasterBean(sqlMap, masterTableBean, null);
                    formApi4Cap4.saveOrUpdateFormData(formDataMasterBean, formBean.getId(), true);
//                    formDataDAO.insertData(tableName, list, false);
                    //log对象写入日志
                    log.info("此处是向表中插入的数据:"+list);
                } catch (BusinessException | SQLException e) {
                    log.error("数据插入"+tableName+"时失败:",e);
                }
            }
        }
    }
}
