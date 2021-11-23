package com.seeyon.apps.test.download.service;

import com.seeyon.ctp.util.FlipInfo;

import java.util.List;
import java.util.Map;


/**
 * @author sheHaiTao
 * @data 2021/11/11 - 17:55
 * FTP上传文件写入第三方数据库数据
 */
public interface EdocOpinionManager {
    /**
     * 按照条件查询公文意见
     * @param sql
     * @param map
     * @param fi
     * @return
     */
    public List<Map<String, Object>> findOpinionList(String sql, Map map, FlipInfo fi);
}
