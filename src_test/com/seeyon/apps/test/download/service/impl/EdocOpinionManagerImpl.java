package com.seeyon.apps.test.download.service.impl;

import com.seeyon.apps.test.download.dao.impl.EdocOpinionDaoImpl;
import com.seeyon.apps.test.download.service.EdocOpinionManager;
import com.seeyon.ctp.util.FlipInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


public class EdocOpinionManagerImpl implements EdocOpinionManager {

    @Autowired
    private EdocOpinionDaoImpl edocOpinionDao;

    @Override
    public List<Map<String, Object>> findOpinionList(String sql, Map map, FlipInfo fi) {
        return edocOpinionDao.findOpinionListByUserId(sql, map, fi);
    }
}
