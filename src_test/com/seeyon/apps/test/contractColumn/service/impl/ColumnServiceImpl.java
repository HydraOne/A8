package com.seeyon.apps.test.contractColumn.service.impl;

import com.seeyon.apps.test.contractColumn.dao.ColumnMapper;
import com.seeyon.apps.test.contractColumn.pojo.RegulatoryContract;
import com.seeyon.apps.test.contractColumn.service.ColumnService;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ocip.common.org.OrgUnit;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ColumnServiceImpl implements ColumnService {

    //日志
    private static final Log log = CtpLogFactory.getLog(ColumnServiceImpl.class);

    //注入dao层
    @Autowired
    private ColumnMapper columnMapper;

    @Override
    public ArrayList<RegulatoryContract> getColumnFormData(String regulatoryContract) {
        //获取分页对象
        FlipInfo flipInfo = new FlipInfo();
        //设置页数
        int pageSize = 0;
        try{
            pageSize = Integer.parseInt(regulatoryContract);
        }catch (Exception e){
            log.error("获取栏目数据时,传入对象无法转换为int",e);
            return null;
        }
        flipInfo.setSize(pageSize);
        //pojo集合
        ArrayList<RegulatoryContract> regulatoryContracts = new ArrayList<>();
        //查询监管合同详情
        FlipInfo flipInfoData = columnMapper.queryContractInformation(flipInfo);
        List<Map<String, Object>> data = flipInfoData.getData();
        for (Map<String, Object> map : data) {
            RegulatoryContract contract = new RegulatoryContract();
            contract.setField0001(String.valueOf(map.get("field0001")));
            contract.setField0002(String.valueOf(map.get("field0002")));
            contract.setField0003((Date) map.get("field0003"));
            contract.setField0004(String.valueOf(map.get("field0004")));
            contract.setField0005(String.valueOf(map.get("field0005")));
            contract.setField0006(String.valueOf(map.get("field0006")));
            contract.setField0033(String.valueOf(map.get("field0033")));
            contract.setField0036(String.valueOf(map.get("field0036")));
            regulatoryContracts.add(contract);
        }
        return regulatoryContracts;
    }
}
