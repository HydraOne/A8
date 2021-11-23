package com.seeyon.apps.test.payFormEvents.dao;

import java.util.Map;

/**
 * @author sheHaiTao
 * @data 2021/11/2 - 11:06
 * @Description 获取付款金额
 */
public interface PayFormDao {
    /**
     * 查询付款信息
     * @param contractNum 合同编号
     * @return 查询的付款信息集合
     */
    Map getData(String contractNum);

    /**
     *更新付款金额
     * @param num
     * @param contractNum
     */
    void payUpdate(String num, String contractNum);
}
