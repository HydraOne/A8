package com.seeyon.apps.test.download.dao;


import com.seeyon.ctp.util.FlipInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author sheHaiTao
 * @data 2021/11/12 - 11:27
 * 查询公文意见
 */
public interface EdocOpinionDao {

    /**
     * 条件查询意见列表
     * @param sql
     * @param map
     * @param fi
     * @return
     */
    List<Map<String, Object>> findOpinionListByUserId(@Param("sql") String sql, @Param("map") Map map, @Param("fi") FlipInfo fi);

    /**
     * 修改公文意见
     * @param opinionId
     * @param opinionContent
     */
    void update(Long opinionId, String opinionContent);
}
