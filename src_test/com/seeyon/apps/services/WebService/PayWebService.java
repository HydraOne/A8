package com.seeyon.apps.services.WebService;


/**
 * @author sheHaiTao
 * @data 2021/11/2 - 10:22
 * @Description 支付的web服务
 */
public interface PayWebService {
    /**
     * 更新金额
     * @param dataXml
     * @return
     */
    public String updateMoney(String dataXml);
}
