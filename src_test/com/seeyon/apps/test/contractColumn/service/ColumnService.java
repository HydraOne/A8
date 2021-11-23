package com.seeyon.apps.test.contractColumn.service;

import com.seeyon.apps.test.contractColumn.pojo.RegulatoryContract;

import java.util.ArrayList;

/**
 * @author sheHaiTao
 * @data 2021/10/29 - 18:37
 * @Description 栏目service层
 */
public interface ColumnService {
    /**
     * 获取栏目表数据
     * @return 返回pojo类型的集合
     */
    ArrayList<RegulatoryContract> getColumnFormData(String regulatoryContract);
}
