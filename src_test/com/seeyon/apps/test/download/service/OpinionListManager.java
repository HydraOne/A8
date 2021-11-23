package com.seeyon.apps.test.download.service;

import com.seeyon.ctp.util.FlipInfo;

import java.util.Map;


/**
 * @author sheHaiTao
 * @data 2021/11/12 - 13:44
 *查询/修改公文列表意见
 */
public interface OpinionListManager {

    /**
     * 查询意见
     * @param fi
     * @param pas
     * @return
     */
    public FlipInfo opinionList(FlipInfo fi, Map pas);

    /**
     * 修改意见
     * @param opinionId
     * @param opinionContent
     */
    public void updateOpinionData(Long opinionId,String opinionContent);
}
