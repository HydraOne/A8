package com.seeyon.apps.test.paymentCalendar.dao;

import com.seeyon.apps.timeview.po.TimeViewAuth;
import com.seeyon.apps.timeview.po.TimeViewInfo;

/**
 * @author sheHaiTao
 * @data 2021/11/2 - 19:05
 *添加时间视图信息和认证
 */
public interface FormTimeViewMapper {

    /**
     * 添加信息
     * @param timeViewInfo
     */
    void addTimeViewInfo(TimeViewInfo timeViewInfo);

    /**
     * 添加认证
     * @param timeViewAuth
     */
    void addTimeViewAuth(TimeViewAuth timeViewAuth);
}
