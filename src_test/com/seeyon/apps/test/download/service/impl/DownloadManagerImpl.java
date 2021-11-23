package com.seeyon.apps.test.download.service.impl;

import com.seeyon.apps.test.download.dao.MyFileDao;
import com.seeyon.apps.test.download.service.DownloadManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DownloadManagerImpl implements DownloadManager {

    @Autowired
    private MyFileDao myFileDao;

    @Override
    public List getFileUrlList(Long along) throws BusinessException {
        return myFileDao.getFile(along);
    }

    @Override
    public String getFileType(Long id) throws BusinessException {
        return myFileDao.getFileType(id);
    }
}
