package com.seeyon.apps.test.contractEvents.service.impl;

import com.seeyon.apps.test.contractEvents.controller.ContractApi4cap4;
import com.seeyon.apps.test.contractEvents.dao.CommonDao;
import com.seeyon.apps.test.contractEvents.service.ContractManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class ContractManagerImpl implements ContractManager {

    @Autowired
    private ContractApi4cap4 contractApi4cap4;

    @Override
    public Map getData(String tableName, Long formId) {
        return contractApi4cap4.getData(tableName, formId);
    }
}
