package com.seeyon.apps.test.contractEvents.controller;


import com.seeyon.apps.test.contractEvents.service.ContractManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 对外暴露的api接口
 */
public class ContractApi4cap4 {

    @Autowired
    private ContractManager contractManager;

    /**
     * 用于根据表名和表单id获取数据
     */
    public Map getData(String tableName, Long formId){
        return contractManager.getData(tableName, formId);
    }

}
