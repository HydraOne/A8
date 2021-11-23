package com.seeyon.apps.test.contractColumn.dao;


import com.seeyon.ctp.util.FlipInfo;

/**
 * @author sheHaiTao
 * @data 2021/10/29 - 18:23
 * @Description 栏目dao层
 */
public interface ColumnMapper {
    /**
     *传入分页数据分页
     * @param flipInfo 分页对象
     * @return
     */
    FlipInfo queryContractInformation(FlipInfo flipInfo);
}
