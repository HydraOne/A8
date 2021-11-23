package com.seeyon.apps.test.download.controller;

import com.seeyon.apps.test.contractEvents.controller.ContractApi4cap4;
import com.seeyon.apps.test.download.service.DownloadManager;
import com.seeyon.cap4.form.api.FormApi4Cap4;
import com.seeyon.cap4.form.bean.FormBean;
import com.seeyon.cap4.form.bean.FormFieldBean;
import com.seeyon.cap4.form.bean.FormTableBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.affair.manager.AffairManager;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.po.affair.CtpAffair;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadAttachmentController extends BaseController {

    //日志
    private static final Log log = CtpLogFactory.getLog(DownloadAttachmentController.class);

    //表单管理对象
    @Autowired
    private FormApi4Cap4 formApi4Cap4;
    //事务管理对象
    @Autowired
    private AffairManager affairManager;
    //文件管理对象
    @Autowired
    private FileManager fileManager;
    //导入service层
    @Autowired
    DownloadManager downloadManager;
    //调用需求1包下的暴露接口(用于调用其通用的查询sql)
    @Autowired
    private ContractApi4cap4 contractApi4cap4;



    @AjaxAccess
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
        //获取事务id
        String affairId = request.getParameter("affairId");
        //获取ctp事务对象
        CtpAffair ctpAffair = null;
        try {
            ctpAffair = affairManager.get(Long.valueOf(affairId));
        } catch (BusinessException e) {
            log.error("下载文件时,ctp事务对象获取失败:", e);
        }
        //获取表对象
        Long formAppId = ctpAffair.getFormAppId();
        Long formRecordid = ctpAffair.getFormRecordid();
        FormBean form = formApi4Cap4.getForm(formAppId);
        FormTableBean masterTableBean = form.getMasterTableBean();
        //获取表单
        String fields = "";
        for (FormFieldBean field : masterTableBean.getFields()) {
            String inputType = field.getInputType();
            if (inputType.equals("attachment")) {
                if (!"".equals(fields)) {
                    fields += "," + field.getName();
                } else {
                    fields += field.getName();
                }
            }
        }
        //调用CommonDao查询数据的信息
        Map data = contractApi4cap4.getData(masterTableBean.getTableName(), formRecordid);
        //
        String[] split = fields.split(",");
        ArrayList<Object> list = new ArrayList<>();
        for (String s : split) {
            Object o = data.get(s);
            if (o == null) {
                continue;
            }
            list.add(data.get(s));
        }
        //获取到file对象调用getFile进行下载
        List<File> lll = new ArrayList<>();
        for (Object o : list) {
            if (o != null) {
                Long aLong = Long.valueOf((String) o);
                List list1 = null;
                String fileType = null;
                try {
                    list1 = downloadManager.getFileUrlList(aLong);
                    for (int i = 0; i < list1.size(); i++) {
                        Map map1 = (Map) list1.get(i);
                        File fileUrl1 = fileManager.getFile((Long) map1.get("file_url"));
                        lll.add(fileUrl1);
                    }
                    fileType = downloadManager.getFileType(aLong);
                } catch (BusinessException e) {
                    log.error("获取fileUrl时出现异常:",e);
                }
                getFile(lll, fileType);
            }
        }
    }

    //下载文件
    private void getFile(List<File> srcFile, String type) {
        String desFile = AppContext.getSystemProperty("test.downloadFile");
        for (File file : srcFile) {
            String name = file.getName() + "." + type;
            try {
                FileUtils.copyFile(file, new File(desFile, name));
            } catch (IOException e) {
                logger.error("使用FileUtils下载文件时出现异常:",e);
            }
        }
    }
}
