package com.seeyon.apps.test.download.service;

import com.seeyon.ctp.common.exceptions.BusinessException;

import java.util.List;


public interface DownloadManager {
    public List getFileUrlList(Long along) throws BusinessException;

    String getFileType(Long id) throws  BusinessException;
}
