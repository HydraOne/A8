package com.seeyon.apps.test.download.dao;

import java.util.List;


/**
 * @author sheHaiTao
 * @data 2021/11/11 - 18:02
 * 定时任务查询数据
 */
public interface QuartzDao {
    /**
     * 查出流程结束时间是否为3分钟之前的数据
     * @return
     */
    List findOfficialDocument();
}
